package com.hbm.items.weapon.sedna.mods;

import com.hbm.items.weapon.sedna.ItemGunBaseNT;
import com.hbm.items.weapon.sedna.ItemGunBaseNT.LambdaContext;
import com.hbm.items.weapon.sedna.Receiver;
import net.minecraft.item.ItemStack;

import java.util.Objects;
import java.util.function.BiConsumer;

public class WeaponModPolymerFurniture extends WeaponModBase {

	public WeaponModPolymerFurniture(int id) {
		super(id, "FURNITURE");
	}

	@Override
	public <T> T eval(T base, ItemStack gun, String key, Object parent) {
		if(Objects.equals(key, Receiver.CON_ONRECOIL)) return (T) LAMBDA_RECOIL_G3;
		return base;
	}
	
	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_RECOIL_G3 = (stack, ctx) -> ItemGunBaseNT.setupRecoil((float) (ctx.getPlayer().getRNG().nextGaussian() * 0.125), (float) (ctx.getPlayer().getRNG().nextGaussian() * 0.125));

}
