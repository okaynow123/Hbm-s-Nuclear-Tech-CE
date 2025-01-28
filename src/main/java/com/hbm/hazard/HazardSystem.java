package com.hbm.hazard;
import com.hbm.util.ItemStackUtil;

import java.util.*;

import com.hbm.hazard.modifier.HazardModifier;
import com.hbm.hazard.transformer.HazardTransformerBase;
import com.hbm.hazard.type.HazardTypeBase;
import com.hbm.interfaces.Untested;
import com.hbm.inventory.RecipesCommon.ComparableStack;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

@Untested
public class HazardSystem {

	/*
	 * Map for OreDict entries, always evaluated first. Avoid registering HazardData with 'doesOverride', as internal order is based on the item's ore dict keys.
	 */
	public static final HashMap<String, HazardData> oreMap = new HashMap();
	/*
	 * Map for items, either with wildcard meta or stuff that's expected to have a variety of damage values, like tools.
	 */
	public static final HashMap<Item, HazardData> itemMap = new HashMap();
	/*
	 * Very specific stacks with item and meta matching. ComparableStack does not support NBT matching, to scale hazards with NBT please use HazardModifiers.
	 */
	public static final HashMap<ComparableStack, HazardData> stackMap = new HashMap();
	/*
	 * For items that should, for whichever reason, be completely exempt from the hazard system.
	 */
	public static final HashSet<ComparableStack> stackBlacklist = new HashSet();
	public static final HashSet<String> dictBlacklist = new HashSet();
	/*
	 * List of hazard transformers, called in order before and after unrolling all the HazardEntries.
	 */
	public static final List<HazardTransformerBase> trafos = new ArrayList();
	
	/**
	 * Automatically casts the first parameter and registers it to the HazSys
	 * @param o
	 * @param data
	 */
	public static void register(final Object o, final HazardData data) {

		if(o instanceof String)
			oreMap.put((String)o, data);
		if(o instanceof Item)
			itemMap.put((Item)o, data);
		if(o instanceof Block)
			itemMap.put(Item.getItemFromBlock((Block)o), data);
		if(o instanceof ItemStack)
			stackMap.put(ItemStackUtil.comparableStackFrom((ItemStack)o), data);
		if(o instanceof ComparableStack)
			stackMap.put((ComparableStack)o, data);
	}
	
	/**
	 * Prevents the stack from returning any HazardData
	 * @param o
	 */
	public static void blacklist(final Object o) {
		
		if(o instanceof ItemStack) {
			stackBlacklist.add(ItemStackUtil.comparableStackFrom((ItemStack) o).makeSingular());
		} else if(o instanceof String) {
			dictBlacklist.add((String) o);
		}
	}
	
	public static boolean isItemBlacklisted(final ItemStack stack) {

		if(stackBlacklist.contains(ItemStackUtil.comparableStackFrom(stack).makeSingular()))
			return true;

		final int[] ids = OreDictionary.getOreIDs(stack);
		for(final int id : ids) {
			final String name = OreDictionary.getOreName(id);
			
			if(dictBlacklist.contains(name))
				return true;
		}
		
		return false;
	}
	
	/**
	 * Will return a full list of applicable HazardEntries for this stack.
	 * <br><br>ORDER:
	 * <ol>
	 * <li>ore dict (if multiple keys, in order of the ore dict keys for this stack)
	 * <li>item
	 * <li>item stack
	 * </ol>
	 * 
	 * "Applicable" means that entries that are overridden or excluded via mutex are not in this list.
	 * Entries that are marked as "overriding" will delete all fetched entries that came before it.
	 * Entries that use mutex will prevent subsequent entries from being considered, shall they collide. The mutex system already assumes that
	 * two keys are the same in priority, so the flipped order doesn't matter.
	 * @param stack
	 * @return
	 */
	public static List<HazardEntry> getHazardsFromStack(final ItemStack stack) {

		if (stack.isEmpty() || isItemBlacklisted(stack)) {
			return Collections.emptyList();
		}

		final List<HazardData> chronological = new ArrayList<>();

		// ORE DICT
		final int[] ids = OreDictionary.getOreIDs(stack);
        for (final int id : ids) {
            final String name = OreDictionary.getOreName(id);
            final HazardData hazardData = oreMap.get(name);
            if (hazardData != null) {
                chronological.add(hazardData);
            }
        }

        // ITEM
		final HazardData itemHazardData = itemMap.get(stack.getItem());
		if (itemHazardData != null) {
			chronological.add(itemHazardData);
		}

		// STACK
		final ComparableStack comp = ItemStackUtil.comparableStackFrom(stack).makeSingular();
		final HazardData stackHazardData = stackMap.get(comp);
		if (stackHazardData != null) {
			chronological.add(stackHazardData);
		}

		final List<HazardEntry> entries = new ArrayList<>();

		// Pre-transformations
		for (final HazardTransformerBase trafo : trafos) {
			trafo.transformPre(stack, entries);
		}

		int mutex = 0;

		for (final HazardData data : chronological) {
			if (data.doesOverride) {
				entries.clear();
			}
			if ((data.getMutex() & mutex) == 0) {
				entries.addAll(data.entries);
				mutex |= data.getMutex();
			}
		}

		// Post-transformations
		for (final HazardTransformerBase trafo : trafos) {
			trafo.transformPost(stack, entries);
		}

		return entries;
	}

	public static float getHazardLevelFromStack(final ItemStack stack, final HazardTypeBase hazard) {
		final List<HazardEntry> entries = getHazardsFromStack(stack);

		for (final HazardEntry entry : entries) {
			if (entry.type == hazard) {
				return HazardModifier.evalAllModifiers(stack, null, entry.baseLevel, entry.mods);
			}
		}

		return 0F;
	}

	/**
	 * Will grab and iterate through all assigned hazards of the given stack and apply their effects to the holder.
	 * @param stack
	 * @param entity
	 */
	public static void applyHazards(final ItemStack stack, final EntityLivingBase entity) {
		final List<HazardEntry> hazards = getHazardsFromStack(stack);
		
		for(final HazardEntry hazard : hazards) {
			hazard.applyHazard(stack, entity);
		}
	}
	
	/**
	 * Will apply the effects of all carried items, including the armor inventory.
	 * @param player
	 */
	public static void updatePlayerInventory(final EntityPlayer player) {
		final NonNullList<ItemStack> mainInventory = player.inventory.mainInventory;
		final NonNullList<ItemStack> armorInventory = player.inventory.armorInventory;

		// Iterate over main inventory
		for (int i = 0; i < mainInventory.size(); i++) {
			final ItemStack stack = mainInventory.get(i);

			// Check if stack is not empty
			if (!stack.isEmpty()) {
				applyHazards(stack, player);

				// Check if stack is now empty after applying hazards
				if (stack.getCount() == 0) {
					mainInventory.set(i, ItemStack.EMPTY);
				}
			}
		}

		// Iterate over armor inventory
		for (final ItemStack stack : armorInventory) {
			if (!stack.isEmpty()) {
				applyHazards(stack, player);
			}
		}
	}

	public static void updateLivingInventory(final EntityLivingBase entity) {
		
		for(int i = 0; i < 5; i++) {
			final ItemStack stack = entity.getItemStackFromSlot(EntityEquipmentSlot.values()[i]);

			if(stack != ItemStack.EMPTY) {
				applyHazards(stack, entity);
			}
		}
	}

	public static void updateDroppedItem(final EntityItem entity) {
		
		final ItemStack stack = entity.getItem();
		
		if(entity.isDead || stack == ItemStack.EMPTY || stack.getCount() <= 0) return;
		
		final List<HazardEntry> hazards = getHazardsFromStack(stack);
		for(final HazardEntry entry : hazards) {
			entry.type.updateEntity(entity, HazardModifier.evalAllModifiers(stack, null, entry.baseLevel, entry.mods));
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static void addFullTooltip(final ItemStack stack, final EntityPlayer player, final List list) {
		
		final List<HazardEntry> hazards = getHazardsFromStack(stack);
		
		for(final HazardEntry hazard : hazards) {
			hazard.type.addHazardInformation(player, list, hazard.baseLevel, stack, hazard.mods);
		}
	}
}
