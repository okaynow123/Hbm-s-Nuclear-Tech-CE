package com.hbm.handler.jei;

import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.recipes.CentrifugeRecipes;
import mezz.jei.api.IGuiHelper;
import net.minecraft.item.ItemStack;

public class CentrifugeRecipeHandler extends JEIUniversalHandler {

	public CentrifugeRecipeHandler(IGuiHelper helper) {
		super(helper, JEIConfig.CENTRIFUGE, ModBlocks.machine_centrifuge.getTranslationKey(), new ItemStack[]{new ItemStack(ModBlocks.machine_centrifuge)}, wrapRecipes2(CentrifugeRecipes.getRecipes()));
	}

}
