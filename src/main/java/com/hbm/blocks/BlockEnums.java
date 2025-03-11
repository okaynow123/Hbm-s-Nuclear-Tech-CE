package com.hbm.blocks;

import com.hbm.items.ModItems;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.function.Function;

public class BlockEnums {

	public static enum EnumStoneType {
		SULFUR,
		ASBESTOS,
		HEMATITE,
		MALACHITE,
		LIMESTONE,
		BAUXITE
	}

	public static enum EnumStalagmiteType {
		SULFUR,
		ASBESTOS
	}
	
	/** DECO / STRUCTURE ENUMS */
	//i apologize in advance
	
	public static enum TileType {
		LARGE,
		SMALL
	}
	
	public static enum DecoComputerEnum {
		IBM_300PL
	}
	
	public static enum DecoCabinetEnum {
		GREEN,
		STEEL
	}

	public static enum OreType {
		EMERALD ("emerald", new ItemStack(Items.EMERALD), (x -> 1 + x )),
		DIAMOND ("diamond", new ItemStack(Items.DIAMOND), (x -> 1 + x)),
		RADGEM ("radgem", new ItemStack(ModItems.gem_rad), (x -> 1 + x));

		public final String overlayTexture;
		public final ItemStack drop;
		private final Function<Integer, Integer> fortuneFunction;

		public String getName(){
			return overlayTexture;
		}
		public ItemStack getDrop(){
			return drop;
		}
		public int getDropCount(int rand){
			return fortuneFunction.apply(rand);
		}

		OreType(String overlayTexture, @Nullable ItemStack drop, Function<Integer, Integer> fortuneFunction) {
			this.overlayTexture = overlayTexture;
			this.drop = drop;
			this.fortuneFunction = fortuneFunction;
		}
	}

}