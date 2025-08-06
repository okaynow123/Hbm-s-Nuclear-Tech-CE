package com.hbm.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public interface IEquipReceiver {

	public default void onEquip(EntityPlayer player, EnumHand hand){};
	public default void onEquip(EntityPlayer player, ItemStack stack){};
}
