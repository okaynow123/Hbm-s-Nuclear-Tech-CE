package com.hbm.handler.jei;

import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.recipes.CrackRecipes;
import mezz.jei.api.IGuiHelper;
import net.minecraft.item.ItemStack;

public class CrackingHandler extends JEIUniversalHandler{
    public CrackingHandler(IGuiHelper helper) {
        super(helper, JEIConfig.CRACKING, "tile.machine_catalytic_cracker.name", new ItemStack(ModBlocks.machine_catalytic_cracker), CrackRecipes.getCrackingRecipesForJEI());
    }
}
