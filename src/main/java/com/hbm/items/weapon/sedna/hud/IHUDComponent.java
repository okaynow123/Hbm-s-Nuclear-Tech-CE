package com.hbm.items.weapon.sedna.hud;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public interface IHUDComponent {

    public int getComponentHeight(EntityPlayer player, ItemStack stack);
    public void renderHUDComponent(RenderGameOverlayEvent.Pre event, RenderGameOverlayEvent.ElementType type, EntityPlayer player, ItemStack stack, int bottomOffset, int gunIndex);
}
