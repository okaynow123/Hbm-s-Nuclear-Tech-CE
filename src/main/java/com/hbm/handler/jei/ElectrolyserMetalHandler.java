package com.hbm.handler.jei;

import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.recipes.ElectrolyserMetalRecipes;
import mezz.jei.api.IGuiHelper;
import net.minecraft.item.ItemStack;

import java.util.HashMap;

public class ElectrolyserMetalHandler extends JEIUniversalHandler {

    public ElectrolyserMetalHandler(IGuiHelper guiHelper) {
        super(guiHelper, JEIConfig.ELECTROLYSIS_METAL, ModBlocks.machine_electrolyser.getTranslationKey(), new ItemStack[]{new ItemStack(ModBlocks.machine_electrolyser)}, wrap(ElectrolyserMetalRecipes.getRecipes()));
    }

    private static HashMap<Object, Object> wrap(HashMap<Object[], Object[]> map) {
        return new HashMap<>(map);
    }
}
