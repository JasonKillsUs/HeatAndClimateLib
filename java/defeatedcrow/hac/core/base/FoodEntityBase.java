package defeatedcrow.hac.core.base;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.IHopper;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IClimate;
import defeatedcrow.hac.api.placeable.IItemDropEntity;
import defeatedcrow.hac.api.placeable.IRapidCollectables;
import defeatedcrow.hac.config.CoreConfigDC;

public abstract class FoodEntityBase extends Entity implements IItemDropEntity, IRapidCollectables {

	private int totalAge = 0;
	private int cookingAge = 0;
	private int count = 0;
	private int cookingTime = 32; // 2~16 sec 焼く
	private int burntTime = 64;
	private byte sideInt = 0;

	private static final DataParameter<Boolean> RAW = EntityDataManager.<Boolean> createKey(FoodEntityBase.class,
			DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> BURNT = EntityDataManager.<Boolean> createKey(FoodEntityBase.class,
			DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> AGE = EntityDataManager.<Integer> createKey(FoodEntityBase.class,
			DataSerializers.VARINT);
	private static final DataParameter<EnumFacing> SIDE = EntityDataManager.<EnumFacing> createKey(
			FoodEntityBase.class, DataSerializers.FACING);

	private final Random rand = new Random();

	/* コンストラクタ */

	public FoodEntityBase(World worldIn) {
		super(worldIn);
		this.setSize(0.4F, 0.4F);
		this.setSide(EnumFacing.DOWN);
	}

	public FoodEntityBase(World worldIn, double posX, double posY, double posZ) {
		this(worldIn);
		this.setPosition(posX, posY, posZ);
	}

	public FoodEntityBase(World worldIn, double posX, double posY, double posZ, @Nullable EntityPlayer player) {
		this(worldIn, posX, posY, posZ);
		if (player != null)
			this.rotationYaw = player.rotationYaw;
	}

	/* update処理 */

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (this.posY < -16.0D) {
			this.setDead();
		}

		BlockPos pos = new BlockPos(MathHelper.floor_double(this.posX), MathHelper.floor_double(this
				.getEntityBoundingBox().minY), MathHelper.floor_double(this.posZ));

		if (!this.worldObj.isRemote && this.count++ == 20) {
			this.count = 0;
			this.totalAge++;

			if (this.getSide().getIndex() != this.sideInt) {
				this.dataManager.set(SIDE, EnumFacing.getFront(sideInt));
			}

			if (getRaw()) {
				IClimate clm = ClimateAPI.calculator.getClimate(worldObj, pos, new int[] {
						2,
						1,
						1 });
				if (this.canCookingClimate(clm) > 0) {
					int age = this.getAge() + 1;
					int add = this.canCookingClimate(clm);
					this.setAGE(age);
					this.cookingAge += add;
					if (cookingAge >= cookingTime + this.rand.nextInt(3)) {
						// 4 sec 経過したので
						this.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 1.0F, 1.0F);
						this.setRAW(false);
					}
				}
			} else if (CoreConfigDC.burntFood && !getBURNT()) {
				IClimate clm = ClimateAPI.calculator.getClimate(worldObj, pos, new int[] {
						2,
						1,
						1 });
				if (this.canCookingClimate(clm) > 2) {
					int age = this.getAge() + 1;
					int add = this.canCookingClimate(clm);
					this.setAGE(age);
					this.cookingAge += add;
					if (cookingAge >= burntTime + this.rand.nextInt(3)) {
						// 4 sec 経過したので
						this.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 1.0F, 1.0F);
						this.setBURNT(true);
					}
				}
			}

		}

		if (this.getRaw() && this.getAge() > 0 && this.rand.nextInt(4) == 0) {
			this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX, this.posY + 0.5D, this.posZ, 0.0D,
					0.0D, 0.0D, new int[0]);
		}

		// 動作
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.moveEntity(this.motionX, this.motionY, this.motionZ);

		if (this.isFallable()) {

			this.motionY -= 0.04D;
			this.handleWaterMovement();
			float f = 0.98F;

			IBlockState in = worldObj.getBlockState(pos);
			IBlockState under = worldObj.getBlockState(pos.down());

			if (in.getBlock() == Blocks.HOPPER || under.getBlock() == Blocks.HOPPER) {
				this.dropAndDeath(null);
			} else if (worldObj.getTileEntity(pos.down()) != null
					&& worldObj.getTileEntity(pos.down()) instanceof IHopper) {
				this.dropAndDeath(null);
			}

			// 真下チェック
			if (this.onGround) {
				f = under.getBlock().slipperiness * 0.98F;
			}

			this.motionX *= f;
			this.motionY *= 0.95D;
			this.motionZ *= f;

			if (this.onGround) {
				this.motionY *= -0.25D;
			}

			// 水中
			if (this.inWater && this.isFloatOnWater() && this.checkInWater()) {
				this.motionY += 0.035D;
				if (this.motionY > 0.05D) {
					this.motionY = 0.05D;
				}
				this.motionX *= 0.93D;
				this.motionZ *= 0.93D;
			}

			this.doBlockCollisions();

			// 進路方向の接触チェック
			Vec3d checkX = new Vec3d(MathHelper.floor_double(this.posX + this.motionX),
					MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
			BlockPos posX = new BlockPos(checkX);
			IBlockState stateX = worldObj.getBlockState(posX);
			if (stateX.getMaterial() != Material.AIR) {
				AxisAlignedBB aabbX = stateX.getCollisionBoundingBox(this.worldObj, posX);

				if (aabbX != Block.NULL_AABB && aabbX.offset(posX).isVecInside(checkX)) {
					this.motionX *= -0.5D;
				}
			}

			Vec3d checkZ = new Vec3d(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY),
					MathHelper.floor_double(this.posZ + this.motionZ));
			BlockPos posZ = new BlockPos(checkZ);
			IBlockState stateZ = worldObj.getBlockState(posZ);
			if (stateX.getMaterial() != Material.AIR) {
				AxisAlignedBB aabbZ = stateZ.getCollisionBoundingBox(this.worldObj, posZ);

				if (aabbZ != Block.NULL_AABB && aabbZ.offset(posZ).isVecInside(checkZ)) {
					this.motionZ *= -0.5D;
				}
			}

			if (this.motionX * this.motionX < 0.0005D) {
				this.motionX = 0.0D;
			}
			if (this.motionY * this.motionY < 0.0005D) {
				this.motionY = 0.0D;
			}
			if (this.motionZ * this.motionZ < 0.0005D) {
				this.motionZ = 0.0D;
			}

		}
	}

	/* レシピ */

	/**
	 * Tierによって焼ける早さが異なる
	 */
	public int canCookingClimate(IClimate climate) {
		if (climate != null && climate.getHumidity() != DCHumidity.UNDERWATER) {
			switch (climate.getHeat().getTier()) {
			case 2:
				return 2;
			case 3:
				return 4;
			case 4:
				return 16;
			}
		}
		return 0;
	}

	@Override
	public ItemStack getDropItem() {
		boolean raw = this.getRaw();
		if (raw) {
			return drops()[0];
		} else {
			return drops()[1];
		}
	}

	protected abstract ItemStack[] drops();

	protected void dropAndDeath(@Nullable BlockPos pos) {
		// 焦げているとなにも得られない
		if (getBURNT())
			dropFoods(pos);
		this.setDead();
	}

	protected void dropFoods(@Nullable BlockPos pos) {
		if (pos == null) {
			dropFoods(posX, posY + 0.25D, posZ);
		} else {
			dropFoods(pos.getX() + 0.5D, pos.getY() + 0.25D, pos.getZ() + 0.5D);
		}
	}

	protected void dropFoods(double x, double y, double z) {
		if (!worldObj.isRemote && this.getDropItem() != null) {
			ItemStack item = this.getDropItem();
			EntityItem drop = new EntityItem(worldObj, x, y, z, item);
			drop.motionY = 0.025D;
			worldObj.spawnEntityInWorld(drop);
		}
	}

	/* 動き */

	@Override
	@Nullable
	public AxisAlignedBB getCollisionBox(Entity entityIn) {
		return entityIn.getEntityBoundingBox();
	}

	@Override
	@Nullable
	public AxisAlignedBB getCollisionBoundingBox() {
		return this.getEntityBoundingBox();
	}

	@Override
	public boolean canBePushed() {
		return true;
	}

	@Override
	public boolean canBeCollidedWith() {
		return !this.isDead;
	}

	@Override
	public boolean handleWaterMovement() {
		if (this.worldObj.handleMaterialAcceleration(this.getEntityBoundingBox(), Material.WATER, this)) {
			if (!this.inWater && !this.firstUpdate) {
				this.resetHeight();
			}

			this.inWater = true;

		} else {
			this.inWater = false;
		}

		return this.inWater;
	}

	// 水没判定
	protected boolean checkInWater() {
		AxisAlignedBB foodAABB = this.getEntityBoundingBox();
		int i = MathHelper.floor_double(foodAABB.minX);
		int j = MathHelper.ceiling_double_int(foodAABB.maxX);
		int k = MathHelper.floor_double(foodAABB.minY);
		int l = MathHelper.ceiling_double_int(foodAABB.minY + 0.001D);
		int i1 = MathHelper.floor_double(foodAABB.minZ);
		int j1 = MathHelper.ceiling_double_int(foodAABB.maxZ);
		boolean flag = false;
		BlockPos.PooledMutableBlockPos pool = BlockPos.PooledMutableBlockPos.retain();

		try {
			for (int k1 = i; k1 < j; ++k1) {
				for (int l1 = k; l1 < l; ++l1) {
					for (int i2 = i1; i2 < j1; ++i2) {
						pool.set(k1, l1, i2);
						IBlockState iblockstate = this.worldObj.getBlockState(pool);

						if (iblockstate.getMaterial() == Material.WATER) {
							float f = getLiquidHeight(iblockstate, this.worldObj, pool);
							flag |= foodAABB.minY < f;
						}
					}
				}
			}
		} finally {
			pool.release();
		}

		return flag;
	}

	public static float getBlockLiquidHeight(IBlockState state, IBlockAccess world, BlockPos pos) {
		int i = state.getValue(BlockLiquid.LEVEL).intValue();
		return (i & 7) == 0 && world.getBlockState(pos.up()).getMaterial() == Material.WATER ? 1.0F
				: 1.0F - BlockLiquid.getLiquidHeightPercent(i);
	}

	public static float getLiquidHeight(IBlockState state, IBlockAccess world, BlockPos pos) {
		return pos.getY() + getBlockLiquidHeight(state, world, pos);
	}

	@Override
	protected void dealFireDamage(int amount) {
		this.attackEntityFrom(DamageSource.inFire, amount);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (this.isEntityInvulnerable(source)) {
			return false;
		} else if (source.isFireDamage() || source.isMagicDamage()) {
			return false;
		} else {
			this.setBeenAttacked();
			this.dropAndDeath(null);
			return false;
		}
	}

	@Override
	public boolean processInitialInteract(EntityPlayer player, @Nullable ItemStack stack, EnumHand hand) {
		if (!this.worldObj.isRemote) {
			this.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);
			this.dropAndDeath(player.getPosition());
		}
		return true;
	}

	// @Override
	// public void onCollideWithPlayer(EntityPlayer entity) {
	// if (!this.worldObj.isRemote && entity != null && entity.isSneaking()) {
	// this.dropAndDeath();
	// }
	// }

	/* IRapidCollectable */
	@Override
	public boolean isCollectable(@Nullable ItemStack item) {
		return item != null && item.getItem() instanceof ItemSpade;

	}

	@Override
	public int getCollectArea(@Nullable ItemStack item) {
		return 2;
	}

	@Override
	public boolean doCollect(World world, BlockPos pos, IBlockState state, EntityPlayer player, ItemStack tool) {
		if (!worldObj.isRemote && this.getDropItem() != null) {
			this.dropAndDeath(player.getPosition());
			return true;
		}
		return false;
	}

	/* パラメータ各種 */

	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	protected boolean isFallable() {
		return true;
	}

	protected boolean isFloatOnWater() {
		return true;
	}

	@Override
	protected void entityInit() {
		this.dataManager.register(RAW, Boolean.valueOf(false));
		this.dataManager.register(BURNT, Boolean.valueOf(false));
		this.dataManager.register(AGE, 0);
		this.dataManager.register(SIDE, EnumFacing.DOWN);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) {
		this.totalAge = tag.getInteger("dcs.entityage");
		this.cookingAge = tag.getInteger("dcs.entitycook");
		this.cookingTime = tag.getInteger("dcs.entitylimit");
		this.burntTime = tag.getInteger("dcs.entityburnt");
		this.sideInt = tag.getByte("dcs.entityside");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {
		tag.setInteger("dcs.entityage", totalAge);
		tag.setInteger("dcs.entitycook", cookingAge);
		tag.setInteger("dcs.entitylimit", cookingTime);
		tag.setInteger("dcs.entityburnt", burntTime);
		tag.setByte("dcs.entityside", sideInt);
	}

	public void setRotation(float f) {
		this.rotationYaw = f;
	}

	public void setSide(EnumFacing side) {
		this.dataManager.set(SIDE, side);
		this.sideInt = (byte) side.getIndex();
	}

	public void setRAW(boolean b) {
		this.dataManager.set(RAW, b);
	}

	public void setBURNT(boolean b) {
		this.dataManager.set(BURNT, b);
	}

	public void setAGE(int age) {
		this.dataManager.set(AGE, age);
	}

	public void setCookFinishTime(int sec) {
		this.cookingTime = sec;
	}

	public EnumFacing getSide() {
		return this.dataManager.get(SIDE);
	}

	public boolean getRaw() {
		return this.dataManager.get(RAW);
	}

	public boolean getBURNT() {
		return this.dataManager.get(BURNT);
	}

	public int getAge() {
		return this.dataManager.get(AGE);
	}

	public int getTotalAge() {
		return this.totalAge;
	}

}