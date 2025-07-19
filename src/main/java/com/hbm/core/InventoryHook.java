package com.hbm.core;

import com.hbm.config.GeneralConfig;
import com.hbm.events.InventoryChangedEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import static com.hbm.core.HbmCorePlugin.coreLogger;

public class InventoryHook {

    public static void onClientSlotChange(InventoryPlayer inventory, int slot, ItemStack newStack) {
        EntityPlayer player = inventory.player;
        if (player == null || !player.world.isRemote) return;
        if (GeneralConfig.enableExtendedLogging) coreLogger.info("Client slot change detected");
        ItemStack oldStack = inventory.getStackInSlot(slot);
        if (ItemStack.areItemStacksEqual(oldStack, newStack)) return;
        MinecraftForge.EVENT_BUS.post(new InventoryChangedEvent(player, oldStack.copy(), newStack.copy()));
    }

    public static void onServerSlotChange(EntityPlayerMP player, Container container, int slotIndex, ItemStack newStack) {
        if (player == null || player.world.isRemote) return;
        if (slotIndex < 0 || slotIndex >= container.inventorySlots.size()) return;
        if (GeneralConfig.enableExtendedLogging)  coreLogger.info("Server slot change detected");
        Slot slot = container.getSlot(slotIndex);
        ItemStack oldStack = slot.getStack();
        MinecraftForge.EVENT_BUS.post(new InventoryChangedEvent(player, oldStack.copy(), newStack.copy()));
    }

    public static void onServerFullSync(EntityPlayer player) {
        if (player == null || player.world.isRemote) return;
        if (GeneralConfig.enableExtendedLogging) coreLogger.info("Server full sync detected");
        MinecraftForge.EVENT_BUS.post(new InventoryChangedEvent(player));
    }
}