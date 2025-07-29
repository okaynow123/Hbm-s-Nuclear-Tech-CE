package com.hbm.tileentity.machine.albion;

import com.hbm.lib.ForgeDirection;
import net.minecraft.util.math.BlockPos;

public interface IParticleUser {
    boolean canParticleEnter(TileEntityPASource.Particle particle, ForgeDirection dir, BlockPos pos);
    void onEnter(TileEntityPASource.Particle particle, ForgeDirection dir);
    BlockPos getExitPos(TileEntityPASource.Particle particle);
}
