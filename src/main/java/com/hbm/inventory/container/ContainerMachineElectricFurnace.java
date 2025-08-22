package com.hbm.inventory.container;

import com.hbm.inventory.SlotTakeOnly;
import com.hbm.inventory.SlotUpgrade;
import com.hbm.items.machine.ItemMachineUpgrade;
import com.hbm.lib.Library;
import com.hbm.tileentity.machine.TileEntityMachineElectricFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerMachineElectricFurnace extends Container {
	
	private TileEntityMachineElectricFurnace diFurnace;
	
	public ContainerMachineElectricFurnace(InventoryPlayer invPlayer, TileEntityMachineElectricFurnace tedf) {
		
		diFurnace = tedf;
		
		this.addSlotToContainer(new SlotItemHandler(tedf.inventory, 0, 56, 53));
		this.addSlotToContainer(new SlotItemHandler(tedf.inventory, 1, 56, 17));
		this.addSlotToContainer(new SlotTakeOnly(tedf.inventory, 2, 116, 35));
		//Upgrades
		this.addSlotToContainer(new SlotUpgrade(tedf.inventory, 3, 147, 34));
		for(int i = 0; i < 3; i++)
		{
			for(int j = 0; j < 9; j++)
			{
				this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}
		
		for(int i = 0; i < 9; i++)
		{
			this.addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 142));
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index) {
		ItemStack rStack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);

		if(slot != null && slot.getHasStack()) {
			ItemStack stack = slot.getStack();
			rStack = stack.copy();

			if(index <= 3) {
				if(!this.mergeItemStack(stack, 4, this.inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}

				slot.onSlotChange(stack, rStack);
			} else {

				if(Library.isItemDischargeableBattery(rStack)) {
					if(!this.mergeItemStack(stack, 0, 1, false))
						return ItemStack.EMPTY;

				} else if(rStack.getItem() instanceof ItemMachineUpgrade) {
					if(!this.mergeItemStack(stack, 3, 4, false))
						return ItemStack.EMPTY;

				} else if(!this.mergeItemStack(stack, 1, 2, false))
					return ItemStack.EMPTY;
			}

			if(stack.getCount() == 0) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}
		}

		return rStack;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return diFurnace.isUseableByPlayer(player);
	}
}