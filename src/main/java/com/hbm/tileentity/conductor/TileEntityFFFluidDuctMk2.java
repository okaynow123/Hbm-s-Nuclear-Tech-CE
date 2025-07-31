package com.hbm.tileentity.conductor;

import com.hbm.interfaces.AutoRegister;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@AutoRegister
public class TileEntityFFFluidDuctMk2 extends TileEntityFFDuctBaseMk2 {

    @Override
    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return 65536.0D;
    }
}
