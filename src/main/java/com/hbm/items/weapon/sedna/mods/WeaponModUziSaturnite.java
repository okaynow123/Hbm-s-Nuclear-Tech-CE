package com.hbm.items.weapon.sedna.mods;

import com.hbm.items.weapon.sedna.GunConfig;
import com.hbm.items.weapon.sedna.Receiver;

import net.minecraft.item.ItemStack;

import java.util.Objects;

public class WeaponModUziSaturnite extends WeaponModBase {

	public WeaponModUziSaturnite(int id) {
		super(id, "FURNITURE");
		this.setPriority(PRIORITY_ADDITIVE);
	}

	@Override
	public <T> T eval(T base, ItemStack gun, String key, Object parent) {
		if(Objects.equals(key, GunConfig.F_DURABILITY)) return cast((Float) base * 5F, base);
		if(Objects.equals(key, Receiver.F_BASEDAMAGE)) return cast((Float) base + 3F, base);
		return base;
	}
}
