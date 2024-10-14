package com.hbm.inventory.fluid.tank;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public abstract class FluidLoadingHandler {

	public abstract boolean fillItem(IItemHandler slots, int in, int out, FluidTank tank);
	public abstract boolean emptyItem(IItemHandler slots, int in, int out, FluidTank tank);
}
