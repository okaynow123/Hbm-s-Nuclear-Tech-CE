package com.hbm.handler.jei;

import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.recipes.CrystallizerRecipes;
import com.hbm.inventory.recipes.SolderingRecipes;
import com.hbm.items.machine.ItemFluidIcon;
import mezz.jei.api.IGuiHelper;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SolderingStationRecipeHandler extends JEIUniversalHandler {

  @Override
  protected void buildRecipes(HashMap<Object, Object> recipeMap, ItemStack[] machines) {
    for (Map.Entry<Object, Object> entry : recipeMap.entrySet()) {
      Object key = entry.getKey();
      Object value = entry.getValue();

      SolderingRecipes.SolderingRecipe orig = findSolderingRecipe(value);

      List<List<ItemStack>> inputs = extractInputLists(key);
      ItemStack[] outputs = extractOutput(value);

      if (!inputs.isEmpty() && outputs.length > 0) {
        recipes.add(new JeiRecipes.SolderingRecipe(
                inputs,
                outputs,
                machines,
                orig.duration,
                (int) orig.consumption
        ));
      }
    }
  }

  private SolderingRecipes.SolderingRecipe findSolderingRecipe(Object value) {
    if (value instanceof ItemStack) {
      for (SolderingRecipes.SolderingRecipe recipe : SolderingRecipes.recipes) {
        if (ItemStack.areItemStacksEqual(recipe.output, (ItemStack) value)) {
          return recipe;
        }
      }
    }
    return null;
  }

  public SolderingStationRecipeHandler(IGuiHelper helper) {
    super(helper, JEIConfig.SOLDERING_STATION, ModBlocks.machine_soldering_station.getTranslationKey(), new ItemStack[] {new ItemStack(ModBlocks.machine_soldering_station)}, SolderingRecipes.getRecipes());
  }
}
