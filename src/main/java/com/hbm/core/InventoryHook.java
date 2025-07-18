package com.hbm.core;

import com.hbm.events.InventoryChangedEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

public class InventoryHook {
    public static void onFullInventoryChange(EntityPlayer player) {
        if (player != null) {
            MinecraftForge.EVENT_BUS.post(new InventoryChangedEvent(player));
        }
    }
    public static void onSlotChange(InventoryPlayer inventory, int slot, ItemStack newStack) {
        EntityPlayer player = inventory.player;
        if (player != null) {
            ItemStack oldStack = inventory.getStackInSlot(slot);
            if (ItemStack.areItemStacksEqual(oldStack, newStack)) return;
            MinecraftForge.EVENT_BUS.post(new InventoryChangedEvent(player, oldStack.copy(), newStack.copy()));
        }
    }
}