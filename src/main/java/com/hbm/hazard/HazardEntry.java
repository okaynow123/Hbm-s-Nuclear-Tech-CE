package com.hbm.hazard;

import com.hbm.hazard.modifier.HazardModifier;
import com.hbm.hazard.type.HazardTypeBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class HazardEntry {

	HazardTypeBase type;
	float baseLevel;
	
	/*
	 * Modifiers are evaluated in the order they're being applied to the entry.
	 */
	List<HazardModifier> mods = new ArrayList();
	
	public HazardEntry(final HazardTypeBase type) {
		this(type, 1F);
	}
	
	public HazardEntry(final HazardTypeBase type, final float level) {
		this.type = type;
		this.baseLevel = level;
	}
	
	public HazardEntry addMod(final HazardModifier mod) {
		this.mods.add(mod);
		return this;
	}
	
	public void applyHazard(final ItemStack stack, final EntityLivingBase entity) {
		// mlbv: I hate this, but I have to do it here
		// apparently I introduced a double-counting bug in HazardSystem earlier(c8721b7e), but when I fix it, all the hazards halved,
		// so I must have introduced the halving bug simultaneously with the double-counting bug, but I couldn't find it
		// perhaps it's introduced along with the async hazard system, idk, but currently I just don't know if there's any other way to fix it
		type.onUpdate(entity, HazardModifier.evalAllModifiers(stack, entity, baseLevel * 2, mods), stack);
	}
	
	public HazardTypeBase getType() {
		return this.type;
	}
	
	public HazardEntry clone() {
		return clone(1F);
	}
	
	public HazardEntry clone(final float mult) {
		final HazardEntry clone = new HazardEntry(type, baseLevel * mult);
		clone.mods = this.mods;
		return clone;
	}
}
