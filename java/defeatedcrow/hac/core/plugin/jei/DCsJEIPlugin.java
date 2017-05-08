package defeatedcrow.hac.core.plugin.jei;

import java.util.ArrayList;
import java.util.List;

import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.cultivate.CropAPI;
import defeatedcrow.hac.api.cultivate.IClimateCrop;
import defeatedcrow.hac.api.recipe.RecipeAPI;
import defeatedcrow.hac.core.DCInit;
import defeatedcrow.hac.core.climate.recipe.ClimateSmelting;
import defeatedcrow.hac.core.climate.recipe.FluidCraftRecipe;
import defeatedcrow.hac.core.climate.recipe.ReactorRecipe;
import defeatedcrow.hac.core.plugin.DCsJEIPluginLists;
import defeatedcrow.hac.core.plugin.jei.ingredients.AirflowHelper;
import defeatedcrow.hac.core.plugin.jei.ingredients.AirflowRenderer;
import defeatedcrow.hac.core.plugin.jei.ingredients.HeatTierHelper;
import defeatedcrow.hac.core.plugin.jei.ingredients.HeatTierRenderer;
import defeatedcrow.hac.core.plugin.jei.ingredients.HumidityHelper;
import defeatedcrow.hac.core.plugin.jei.ingredients.HumidityRenderer;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import net.minecraft.item.ItemStack;

@JEIPlugin
public class DCsJEIPlugin implements IModPlugin {

	private IJeiHelpers helper;

	@Override
	public void register(IModRegistry registry) {
		helper = registry.getJeiHelpers();

		registry.addRecipeCategories(new ClimateEffectiveCategory(helper.getGuiHelper()),
				new ClimateSmeltingCategory(helper.getGuiHelper()), new MillRecipeCategory(helper.getGuiHelper()),
				new FluidRecipeCategory(helper.getGuiHelper()), new ReactorRecipeCategory(helper.getGuiHelper()),
				new ClimateCropCategory(helper.getGuiHelper()));
		registry.addRecipeHandlers(new ClimateEffectiveHandler(), new ClimateSmeltingHandler(),
				new ClimateRecipeHandler(), new MillRecipeHandler(), new FluidRecipeHandler(),
				new ReactorRecipeHandler(), new ClimateCropHandler());

		registry.addRecipes(DCsJEIPluginLists.climate);

		List<ClimateSmelting> list = new ArrayList<ClimateSmelting>();
		list.addAll((List<ClimateSmelting>) RecipeAPI.registerSmelting.getRecipeList(DCHeatTier.ABSOLUTE));
		list.addAll((List<ClimateSmelting>) RecipeAPI.registerSmelting.getRecipeList(DCHeatTier.FROSTBITE));
		list.addAll((List<ClimateSmelting>) RecipeAPI.registerSmelting.getRecipeList(DCHeatTier.COLD));
		list.addAll((List<ClimateSmelting>) RecipeAPI.registerSmelting.getRecipeList(DCHeatTier.COOL));
		list.addAll((List<ClimateSmelting>) RecipeAPI.registerSmelting.getRecipeList(DCHeatTier.NORMAL));
		list.addAll((List<ClimateSmelting>) RecipeAPI.registerSmelting.getRecipeList(DCHeatTier.WARM));
		list.addAll((List<ClimateSmelting>) RecipeAPI.registerSmelting.getRecipeList(DCHeatTier.HOT));
		list.addAll((List<ClimateSmelting>) RecipeAPI.registerSmelting.getRecipeList(DCHeatTier.OVEN));
		list.addAll((List<ClimateSmelting>) RecipeAPI.registerSmelting.getRecipeList(DCHeatTier.KILN));
		list.addAll((List<ClimateSmelting>) RecipeAPI.registerSmelting.getRecipeList(DCHeatTier.SMELTING));
		list.addAll((List<ClimateSmelting>) RecipeAPI.registerSmelting.getRecipeList(DCHeatTier.UHT));
		list.addAll((List<ClimateSmelting>) RecipeAPI.registerSmelting.getRecipeList(DCHeatTier.INFERNO));

		// List<ClimateRecipe> list2 = new ArrayList<ClimateRecipe>();
		// list2.addAll((List<ClimateRecipe>) RecipeAPI.registerRecipes.getRecipeList(DCHeatTier.ABSOLUTE));
		// list2.addAll((List<ClimateRecipe>) RecipeAPI.registerRecipes.getRecipeList(DCHeatTier.FROSTBITE));
		// list2.addAll((List<ClimateRecipe>) RecipeAPI.registerRecipes.getRecipeList(DCHeatTier.COLD));
		// list2.addAll((List<ClimateRecipe>) RecipeAPI.registerRecipes.getRecipeList(DCHeatTier.COOL));
		// list2.addAll((List<ClimateRecipe>) RecipeAPI.registerRecipes.getRecipeList(DCHeatTier.NORMAL));
		// list2.addAll((List<ClimateRecipe>) RecipeAPI.registerRecipes.getRecipeList(DCHeatTier.WARM));
		// list2.addAll((List<ClimateRecipe>) RecipeAPI.registerRecipes.getRecipeList(DCHeatTier.HOT));
		// list2.addAll((List<ClimateRecipe>) RecipeAPI.registerRecipes.getRecipeList(DCHeatTier.OVEN));
		// list2.addAll((List<ClimateRecipe>) RecipeAPI.registerRecipes.getRecipeList(DCHeatTier.KILN));
		// list2.addAll((List<ClimateRecipe>) RecipeAPI.registerRecipes.getRecipeList(DCHeatTier.SMELTING));
		// list2.addAll((List<ClimateRecipe>) RecipeAPI.registerRecipes.getRecipeList(DCHeatTier.UHT));
		// list2.addAll((List<ClimateRecipe>) RecipeAPI.registerRecipes.getRecipeList(DCHeatTier.FROSTBITE));

		List<FluidCraftRecipe> list3 = new ArrayList<FluidCraftRecipe>();
		list3.addAll((List<FluidCraftRecipe>) RecipeAPI.registerFluidRecipes.getRecipeList(DCHeatTier.ABSOLUTE));
		list3.addAll((List<FluidCraftRecipe>) RecipeAPI.registerFluidRecipes.getRecipeList(DCHeatTier.FROSTBITE));
		list3.addAll((List<FluidCraftRecipe>) RecipeAPI.registerFluidRecipes.getRecipeList(DCHeatTier.COLD));
		list3.addAll((List<FluidCraftRecipe>) RecipeAPI.registerFluidRecipes.getRecipeList(DCHeatTier.COOL));
		list3.addAll((List<FluidCraftRecipe>) RecipeAPI.registerFluidRecipes.getRecipeList(DCHeatTier.NORMAL));
		list3.addAll((List<FluidCraftRecipe>) RecipeAPI.registerFluidRecipes.getRecipeList(DCHeatTier.WARM));
		list3.addAll((List<FluidCraftRecipe>) RecipeAPI.registerFluidRecipes.getRecipeList(DCHeatTier.HOT));
		list3.addAll((List<FluidCraftRecipe>) RecipeAPI.registerFluidRecipes.getRecipeList(DCHeatTier.OVEN));
		list3.addAll((List<FluidCraftRecipe>) RecipeAPI.registerFluidRecipes.getRecipeList(DCHeatTier.KILN));
		list3.addAll((List<FluidCraftRecipe>) RecipeAPI.registerFluidRecipes.getRecipeList(DCHeatTier.SMELTING));
		list3.addAll((List<FluidCraftRecipe>) RecipeAPI.registerFluidRecipes.getRecipeList(DCHeatTier.UHT));
		list3.addAll((List<FluidCraftRecipe>) RecipeAPI.registerFluidRecipes.getRecipeList(DCHeatTier.FROSTBITE));

		List<IClimateCrop> list4 = new ArrayList<IClimateCrop>();
		list4.addAll(CropAPI.register.getList().values());

		List<ReactorRecipe> list5 = new ArrayList<ReactorRecipe>();
		list5.addAll((List<ReactorRecipe>) RecipeAPI.registerReactorRecipes.getRecipeList(DCHeatTier.ABSOLUTE));
		list5.addAll((List<ReactorRecipe>) RecipeAPI.registerReactorRecipes.getRecipeList(DCHeatTier.FROSTBITE));
		list5.addAll((List<ReactorRecipe>) RecipeAPI.registerReactorRecipes.getRecipeList(DCHeatTier.COLD));
		list5.addAll((List<ReactorRecipe>) RecipeAPI.registerReactorRecipes.getRecipeList(DCHeatTier.COOL));
		list5.addAll((List<ReactorRecipe>) RecipeAPI.registerReactorRecipes.getRecipeList(DCHeatTier.NORMAL));
		list5.addAll((List<ReactorRecipe>) RecipeAPI.registerReactorRecipes.getRecipeList(DCHeatTier.WARM));
		list5.addAll((List<ReactorRecipe>) RecipeAPI.registerReactorRecipes.getRecipeList(DCHeatTier.HOT));
		list5.addAll((List<ReactorRecipe>) RecipeAPI.registerReactorRecipes.getRecipeList(DCHeatTier.OVEN));
		list5.addAll((List<ReactorRecipe>) RecipeAPI.registerReactorRecipes.getRecipeList(DCHeatTier.KILN));
		list5.addAll((List<ReactorRecipe>) RecipeAPI.registerReactorRecipes.getRecipeList(DCHeatTier.SMELTING));
		list5.addAll((List<ReactorRecipe>) RecipeAPI.registerReactorRecipes.getRecipeList(DCHeatTier.UHT));
		list5.addAll((List<ReactorRecipe>) RecipeAPI.registerReactorRecipes.getRecipeList(DCHeatTier.FROSTBITE));

		registry.addRecipes(list);
		// registry.addRecipes(list2);
		registry.addRecipes(RecipeAPI.registerMills.getRecipeList());
		registry.addRecipes(list3);
		registry.addRecipes(list4);
		registry.addRecipes(list5);

		if (!DCsJEIPluginLists.climateIcons.isEmpty()) {
			for (ItemStack item : DCsJEIPluginLists.climateIcons) {
				registry.addRecipeCategoryCraftingItem(item, new String[] {
						"dcs_climate.effective"
				});
			}
		}

		registry.addRecipeCategoryCraftingItem(new ItemStack(DCInit.climate_checker), new String[] {
				"dcs_climate.smelting"
		});

		if (!DCsJEIPluginLists.fluidcrafters.isEmpty()) {
			for (ItemStack item : DCsJEIPluginLists.fluidcrafters) {
				registry.addRecipeCategoryCraftingItem(item, new String[] {
						"dcs_climate.fluidcraft"
				});
			}
		}

		if (!DCsJEIPluginLists.reactors.isEmpty()) {
			for (ItemStack item : DCsJEIPluginLists.reactors) {
				registry.addRecipeCategoryCraftingItem(item, new String[] {
						"dcs_climate.reactor"
				});
			}
		}

		if (!DCsJEIPluginLists.millstones.isEmpty()) {
			for (ItemStack item : DCsJEIPluginLists.millstones) {
				registry.addRecipeCategoryCraftingItem(item, new String[] {
						"dcs_climate.mill"
				});
			}
		}

		if (!DCsJEIPluginLists.crops.isEmpty()) {
			for (ItemStack item : DCsJEIPluginLists.crops) {
				registry.addRecipeCategoryCraftingItem(item, new String[] {
						"dcs_climate.crop"
				});
			}
		}

		if (!DCsJEIPluginLists.excluder.isEmpty()) {
			for (ItemStack item : DCsJEIPluginLists.excluder) {
				helper.getIngredientBlacklist().addIngredientToBlacklist(item);
			}
		}

	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {}

	@Override
	public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {}

	@Override
	public void registerIngredients(IModIngredientRegistration registry) {
		registry.register(DCHeatTier.class, DCHeatTier.createList(), new HeatTierHelper(), new HeatTierRenderer());
		registry.register(DCHumidity.class, DCHumidity.createList(), new HumidityHelper(), new HumidityRenderer());
		registry.register(DCAirflow.class, DCAirflow.createList(), new AirflowHelper(), new AirflowRenderer());
	}

}