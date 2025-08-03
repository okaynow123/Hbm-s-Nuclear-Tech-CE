package com.hbm.items.armor;

import com.hbm.items.gear.ArmorFSB;
import com.hbm.render.model.ModelArmorBismuth;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class ArmorBismuth extends ArmorFSB {
	
	public ArmorBismuth(ArmorMaterial material, int layer, EntityEquipmentSlot slot, String texture, String s) {
		super(material, layer, slot, texture, s);
		this.setMaxDamage(0);
	}

	@SideOnly(Side.CLIENT)
	ModelArmorBismuth[] models;

	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {
		
		if(models == null) {
			models = new ModelArmorBismuth[4];
			
			for(int i = 0; i < 4; i++)
				models[i] = new ModelArmorBismuth(i);
		}
		
		return models[3 - armorSlot.getIndex()];
	}

}
