package com.hbm.tileentity.machine;

import com.hbm.config.MobConfig;
import com.hbm.main.MainRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityChimneyIndustrial extends TileEntityChimneyBase {

    @Override
    public void spawnParticles() {

        if(world.getTotalWorldTime() % 2 == 0) {
            NBTTagCompound fx = new NBTTagCompound();
            fx.setString("type", "tower");
            fx.setFloat("lift", 10F);
            fx.setFloat("base", 0.75F);
            fx.setFloat("max", 3F);
            fx.setInteger("life", 250 + world.rand.nextInt(50));
            fx.setInteger("color",0x404040);
            fx.setDouble("posX", pos.getX() + 0.5);
            fx.setDouble("posY", pos.getY() + 22);
            fx.setDouble("posZ", pos.getZ() + 0.5);
            MainRegistry.proxy.effectNT(fx);
        }
    }

    @Override
    public double getPollutionMod() {
        return 0.1D;
        //return MobConfig.rampantMode ? MobConfig.rampantSmokeStackOverride / 2 : 0.1D;
    }

    @Override
    public boolean cpaturesSoot() {
        return true;
    }

    AxisAlignedBB bb = null;

    @Override
    public AxisAlignedBB getRenderBoundingBox() {

        if(bb == null) {
            bb = new AxisAlignedBB(
                    pos.getX() - 1,
                    pos.getY(),
                    pos.getZ() - 1,
                    pos.getX() + 2,
                    pos.getY() + 23,
                    pos.getZ() + 2
            );
        }

        return bb;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return 65536.0D;
    }
}
