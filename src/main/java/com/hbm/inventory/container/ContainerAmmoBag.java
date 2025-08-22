package com.hbm.inventory.container;

import com.hbm.items.tool.ItemAmmoBag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerAmmoBag extends Container {

    private final ItemAmmoBag.InventoryAmmoBag bag;

    public ContainerAmmoBag(InventoryPlayer invPlayer, ItemAmmoBag.InventoryAmmoBag box) {
        this.bag = box;

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 4; j++) {
                this.addSlotToContainer(new SlotItemHandler(box, j + i * 4, 53 + j * 18, 18 + i * 18));
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 82 + i * 18));
            }
        }

        // Hotbar
        for (int i = 0; i < 9; i++) {
            this.addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 140));
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack ret = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack stackInSlot = slot.getStack();
            ret = stackInSlot.copy();

            int bagSlots = bag.getSlots();

            if (index < bagSlots) {
                if (!this.mergeItemStack(stackInSlot, bagSlots, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.mergeItemStack(stackInSlot, 0, bagSlots, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stackInSlot.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            slot.onTake(player, stackInSlot);
        }

        return ret;
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, net.minecraft.inventory.ClickType clickTypeIn, EntityPlayer player) {
        // prevents the player from moving around the currently open box
        if (clickTypeIn == net.minecraft.inventory.ClickType.SWAP && dragType == player.inventory.currentItem) {
            return ItemStack.EMPTY;
        }
        if (slotId == player.inventory.currentItem + 27 + bag.getSlots()) {
            return ItemStack.EMPTY;
        }
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
    }
}
