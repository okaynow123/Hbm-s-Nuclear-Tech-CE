package com.hbm.handler.jei;

import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.recipes.CompressorRecipes;
import mezz.jei.api.IGuiHelper;
import net.minecraft.item.ItemStack;

public class CompressingRecipeHandler extends JEIUniversalHandler {

    public CompressingRecipeHandler(IGuiHelper helper) {
        super(helper, JEIConfig.COMPRESSING, ModBlocks.machine_compressor.getTranslationKey(), new ItemStack[]{new ItemStack(ModBlocks.machine_compressor)}, CompressorRecipes.getRecipes());
    }
}
