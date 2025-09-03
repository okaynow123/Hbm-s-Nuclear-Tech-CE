package com.hbm.items.weapon.sedna.mods;

import com.hbm.items.weapon.sedna.Receiver;

import net.minecraft.item.ItemStack;

import java.util.Objects;

public class WeaponModTestFirerate extends WeaponModBase {
	
	public WeaponModTestFirerate(int id, String... slots) {
		super(id, slots);
		this.setPriority(PRIORITY_MULT_FINAL);
	}

	@Override
	public <T> T eval(T base, ItemStack gun, String key, Object parent) {

		if(parent instanceof Receiver && Objects.equals(key, Receiver.I_DELAYAFTERFIRE) && base instanceof Integer) {
			return cast(Math.max((Integer) base / 2, 1), base);
		}
		
		return base;
	}
}
