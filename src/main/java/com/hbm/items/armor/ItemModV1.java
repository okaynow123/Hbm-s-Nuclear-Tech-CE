package com.hbm.items.armor;

import com.google.common.collect.Multimap;
import com.hbm.handler.ArmorModHandler;
import com.hbm.interfaces.IArmorModDash;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.List;

public class ItemModV1 extends ItemArmorMod implements IArmorModDash {

	public ItemModV1(String s){
		super(ArmorModHandler.extra, false, true, false, false, s);
	}

	@Override
	public Multimap<String, AttributeModifier> getModifiers(EntityEquipmentSlot slot, ItemStack armor) {
		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, armor);

		multimap.put(SharedMonsterAttributes.MOVEMENT_SPEED.getName(), new AttributeModifier(ArmorModHandler.UUIDs[((ItemArmor)armor.getItem()).armorType.getIndex()], "V1 SPEED", 0.5, 2));
		return multimap;
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> list, ITooltipFlag flagIn){
		list.add(TextFormatting.RED + "BLOOD IS FUEL");
		list.add("");
		super.addInformation(stack, worldIn, list, flagIn);
	}
	
	@Override
	public void addDesc(List<String> list, ItemStack stack, ItemStack armor){
		list.add(TextFormatting.RED + "  " + stack.getDisplayName() + " (BLOOD IS FUEL)");
	}

	public int getDashes() {
		return 3;
	}
}
