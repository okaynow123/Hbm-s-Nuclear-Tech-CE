package com.hbm.api.block;

import com.hbm.lib.ForgeDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IBlowable { //sloppy toppy

    /** Called server-side when a fan blows on an IBlowable in range every tick. */
    public void applyFan(World world, BlockPos pos, ForgeDirection dir, int dist);
}
