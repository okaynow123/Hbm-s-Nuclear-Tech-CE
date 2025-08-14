package com.hbm.handler.jei;

import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.recipes.ElectrolyserFluidRecipes;
import mezz.jei.api.IGuiHelper;
import net.minecraft.item.ItemStack;

public class ElectrolyserFluidHandler extends JEIUniversalHandler {

    public ElectrolyserFluidHandler(IGuiHelper helper) {
        super(helper, JEIConfig.ELECTROLYSIS_FLUID, ModBlocks.machine_electrolyser.getTranslationKey(), new ItemStack[]{new ItemStack(ModBlocks.machine_electrolyser)}, wrapRecipes2(ElectrolyserFluidRecipes.getRecipes()));
    }
}
