package defeatedcrow.hac.core.climate.recipe;

import java.util.ArrayList;
import java.util.List;

import defeatedcrow.hac.api.recipe.IMillRecipeRegister;
import defeatedcrow.hac.api.recipe.ISpinningRecipe;
import defeatedcrow.hac.api.recipe.ISpinningRecipeRegister;
import defeatedcrow.hac.api.recipe.RecipeAPI;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.item.ItemStack;

public class SpinningRecipeRegister implements ISpinningRecipeRegister {

	public SpinningRecipeRegister() {
		this.list = new ArrayList<SpinningRecipe>();
	}

	public IMillRecipeRegister instance() {
		return RecipeAPI.registerMills;
	}

	private static List<SpinningRecipe> list;

	@Override
	public List<? extends ISpinningRecipe> getRecipeList() {
		return this.list;
	}

	@Override
	public void addRecipe(ItemStack output, int count, Object input) {
		if (input != null && !DCUtil.isEmpty(output)) {
			list.add(new SpinningRecipe(output, count, input));
		}
	}

	@Override
	public void addRecipe(ItemStack output, Object input) {
		addRecipe(output, 1, input);
	}

	@Override
	public void addRecipe(ISpinningRecipe recipe) {
		if (recipe instanceof SpinningRecipe) {
			list.add((SpinningRecipe) recipe);
		}
	}

	@Override
	public ISpinningRecipe getRecipe(ItemStack item) {
		ISpinningRecipe ret = null;
		if (!list.isEmpty()) {
			for (ISpinningRecipe recipe : list) {
				if (recipe.matchInput(item)) {
					ret = recipe;
				}
			}
		}
		return ret;
	}

}
