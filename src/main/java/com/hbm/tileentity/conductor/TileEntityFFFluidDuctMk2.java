package com.hbm.tileentity.conductor;

import com.hbm.interfaces.AutoRegisterTE;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@AutoRegisterTE
public class TileEntityFFFluidDuctMk2 extends TileEntityFFDuctBaseMk2 {

    @Override
    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return 65536.0D;
    }
}
