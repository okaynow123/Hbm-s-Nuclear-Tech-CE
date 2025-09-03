package com.hbm.items.weapon.sedna.mods;

import com.hbm.items.weapon.sedna.Receiver;

import net.minecraft.item.ItemStack;

import java.util.Objects;

public class WeaponModGenericDamage extends WeaponModBase {
	
	public WeaponModGenericDamage(int id) {
		super(id, "GENERIC_DAMAGE");
		this.setPriority(PRIORITY_MULTIPLICATIVE);
	}

	@Override
	public <T> T eval(T base, ItemStack gun, String key, Object parent) {
		
		if(parent instanceof Receiver && Objects.equals(key, Receiver.F_BASEDAMAGE) && base instanceof Float) {
			return cast((Float) base * 1.15F, base);
		}
		
		return base;
	}
}
