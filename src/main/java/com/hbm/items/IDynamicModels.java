package com.hbm.items;

import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Used in items that require model baking;
 */
public interface IDynamicModels {
    @SideOnly(Side.CLIENT)
    public static void bakeModels(ModelBakeEvent event) {

    }


    @SideOnly(Side.CLIENT)
    public static void registerModels() {

    }

}
