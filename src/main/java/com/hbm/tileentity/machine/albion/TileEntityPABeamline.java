package com.hbm.tileentity.machine.albion;

import com.hbm.interfaces.AutoRegister;
import com.hbm.lib.ForgeDirection;
import com.hbm.tileentity.machine.albion.TileEntityPASource.Particle;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@AutoRegister
public class TileEntityPABeamline extends TileEntity implements IParticleUser {

    @Override
    public boolean canParticleEnter(Particle particle, ForgeDirection dir, BlockPos pos) {
        ForgeDirection beamlineDir = ForgeDirection.getOrientation(this.getBlockMetadata() - 10).getRotation(ForgeDirection.DOWN);
        BlockPos input = getPos().offset(beamlineDir.toEnumFacing(), -1);
        return input.equals(pos) && beamlineDir == dir;
    }

    @Override
    public void onEnter(Particle particle, ForgeDirection dir) {
        particle.addDistance(3);
    }

    @Override
    public BlockPos getExitPos(Particle particle) {
        ForgeDirection beamlineDir = ForgeDirection.getOrientation(this.getBlockMetadata() - 10).getRotation(ForgeDirection.DOWN);
        return getPos().offset(beamlineDir.toEnumFacing(), 2);
    }

    private AxisAlignedBB bb = null;

    @Override
    public AxisAlignedBB getRenderBoundingBox() {

        if(bb == null) {
            bb = new AxisAlignedBB(getPos().add(-1, 0, -1), getPos().add(2, 1, 2));
        }

        return bb;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return 65536.0D;
    }
}
