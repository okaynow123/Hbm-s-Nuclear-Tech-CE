package com.hbm.handler.jei;

import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.recipes.ExposureChamberRecipes;
import mezz.jei.api.IGuiHelper;
import net.minecraft.item.ItemStack;

public class ExposureChamberHandler extends JEIUniversalHandler {
    public ExposureChamberHandler(IGuiHelper helper) {
        super(helper, JEIConfig.EXPOSURE, ModBlocks.machine_exposure_chamber.getTranslationKey(),
                new ItemStack[]{new ItemStack(ModBlocks.machine_exposure_chamber)}, ExposureChamberRecipes.getRecipes());
    }
}
