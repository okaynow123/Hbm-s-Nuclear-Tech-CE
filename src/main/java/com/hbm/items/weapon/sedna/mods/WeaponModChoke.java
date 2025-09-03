package com.hbm.items.weapon.sedna.mods;

import com.hbm.items.weapon.sedna.Receiver;

import net.minecraft.item.ItemStack;

import java.util.Objects;

public class WeaponModChoke extends WeaponModBase {

	public WeaponModChoke(int id) {
		super(id, "BARREL");
	}

	@Override
	public <T> T eval(T base, ItemStack gun, String key, Object parent) {
		if(Objects.equals(key, Receiver.F_SPREADAMMO)) { return cast((Float) base * 0.5F, base); }
		
		return base;
	}
}
