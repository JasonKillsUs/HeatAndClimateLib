package defeatedcrow.hac.api.climate;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

/** ItemStackのようなBlock、metaのセットが欲しかったので作成 */
public class BlockSet {

	public final Block block;
	public final int meta;

	public BlockSet(Block i, int j) {
		block = i;
		meta = j;
	}

	public IBlockState getState() {
		return block.getStateFromMeta(meta);
	}

	/**
	 * metaにはwildcard指定可能
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof BlockSet) {
			BlockSet p = (BlockSet) obj;
			return p.block == block && (meta == 32767 || p.meta == 32767 || p.meta == meta);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		int i = block.getUnlocalizedName().hashCode() + meta;
		return i;
	}
}
