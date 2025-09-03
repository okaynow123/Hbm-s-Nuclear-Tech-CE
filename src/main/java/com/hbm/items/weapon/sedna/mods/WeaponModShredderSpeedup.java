package com.hbm.items.weapon.sedna.mods;

import com.hbm.items.weapon.sedna.Receiver;

import net.minecraft.item.ItemStack;

import java.util.Objects;

public class WeaponModShredderSpeedup extends WeaponModBase {

	public WeaponModShredderSpeedup(int id) {
		super(id, "SPEED");
	}

	@Override
	public <T> T eval(T base, ItemStack gun, String key, Object parent) {
		if(Objects.equals(key, Receiver.I_DELAYAFTERFIRE)) return cast((Integer) base / 2, base);
		if(Objects.equals(key, Receiver.I_DELAYAFTERDRYFIRE)) return cast((Integer) base / 2, base);
		return base;
	}
}
