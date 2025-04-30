package com.hbm.render.item;

import com.hbm.main.MainRegistry;
import net.minecraft.item.ItemStack;

public class ItemRenderTurretHIMARSAmmo extends TEISRBase {
    @Override
    public void renderByItem(ItemStack itemStack) {
        MainRegistry.logger.info("test");
    }
}
