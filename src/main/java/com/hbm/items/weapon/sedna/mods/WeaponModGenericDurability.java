package com.hbm.items.weapon.sedna.mods;

import com.hbm.items.weapon.sedna.GunConfig;

import net.minecraft.item.ItemStack;

import java.util.Objects;

public class WeaponModGenericDurability extends WeaponModBase {
	
	public WeaponModGenericDurability(int id) {
		super(id, "GENERIC_DURABILITY");
		this.setPriority(PRIORITY_MULTIPLICATIVE);
	}

	@Override
	public <T> T eval(T base, ItemStack gun, String key, Object parent) {
		
		if(parent instanceof GunConfig && Objects.equals(key, GunConfig.F_DURABILITY) && base instanceof Float) {
			return cast((Float) base * 2F, base);
		}
		
		return base;
	}
}
