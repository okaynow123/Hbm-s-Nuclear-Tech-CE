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

    public static void onClientSlotChange(InventoryPlayer inventory, int slotIndex, ItemStack newStack) {
        EntityPlayer player = inventory.player;
        if (player == null || !player.world.isRemote) return;
        ItemStack oldStack = inventory.getStackInSlot(slotIndex);
        if (ItemStack.areItemStacksEqual(oldStack, newStack)) return;
        if (GeneralConfig.enableExtendedLogging) coreLogger.debug("Client slot change detected with slotIndex {}", slotIndex);
        MinecraftForge.EVENT_BUS.post(new InventoryChangedEvent(player, slotIndex, oldStack, newStack, false));
    }

    public static void onServerSlotChange(EntityPlayerMP player, Container container, int slotIndex, ItemStack newStack) {
        if (player == null || player.world.isRemote) return;
        if (slotIndex < 0 || slotIndex >= container.inventorySlots.size()) return;
        Slot slot = container.getSlot(slotIndex);
        if (slot.inventory != player.inventory) return;
        int canonicalIndex = -1;
        for (int i = 0; i < player.inventoryContainer.inventorySlots.size(); i++) {
            Slot canonicalSlot = player.inventoryContainer.getSlot(i);
            if (canonicalSlot.inventory == slot.inventory && canonicalSlot.slotNumber == slot.slotNumber) {
                canonicalIndex = i;
                break;
            }
        }
        if (canonicalIndex == -1) return;
        ItemStack oldStack = slot.getStack();
        if (GeneralConfig.enableExtendedLogging) coreLogger.debug("Server slot change detected with slotIndex {}", slotIndex);
        MinecraftForge.EVENT_BUS.post(new InventoryChangedEvent(player, canonicalIndex, oldStack, newStack, true));
    }

    public static void onServerFullSync(EntityPlayer player) {
        if (player == null || player.world.isRemote) return;
        if (GeneralConfig.enableExtendedLogging) coreLogger.debug("Server full sync detected");
        MinecraftForge.EVENT_BUS.post(new InventoryChangedEvent(player, true));
    }
}