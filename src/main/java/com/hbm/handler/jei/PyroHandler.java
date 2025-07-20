package com.hbm.handler.jei;

import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.recipes.PyroOvenRecipes;
import mezz.jei.api.IGuiHelper;
import net.minecraft.item.ItemStack;


public class PyroHandler extends JEIUniversalHandler {

    public PyroHandler(IGuiHelper help) {
        super(help, JEIConfig.PYROLYSIS, ModBlocks.machine_pyrooven.getTranslationKey(), new ItemStack[]{new ItemStack(ModBlocks.machine_pyrooven)}, wrapRecipes1(PyroOvenRecipes.getRecipes()));
    }
}
