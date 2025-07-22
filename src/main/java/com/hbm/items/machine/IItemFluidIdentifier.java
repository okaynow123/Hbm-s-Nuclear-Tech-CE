package com.hbm.items.machine;

import com.hbm.inventory.fluid.FluidType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IItemFluidIdentifier {

    FluidType getType(World world, int x, int y, int z, ItemStack stack);
}
