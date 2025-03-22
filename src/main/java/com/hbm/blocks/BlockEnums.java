package com.hbm.blocks;

import com.hbm.items.ModItems;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.function.Function;

import static com.hbm.blocks.OreEnumUtil.*;

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
		EMERALD ("emerald",OreEnum.EMERALD),
		DIAMOND ("diamond", OreEnum.DIAMOND),
		RADGEM ("radgem",OreEnum.RAD_GEM),
		//URANIUM_SCORCEHD ("uranium_scorched", null),
		URANIUM ("uranium", null),
		SCHRABIDIUM ("schrabidium", null);

		public final String overlayTexture;
		public final OreEnum oreEnum;

		public String getName(){
			return overlayTexture;
		}

		OreType(String overlayTexture, @Nullable OreEnum oreEnum) {
			this.overlayTexture = overlayTexture;
			this.oreEnum = oreEnum;

		}
	}


	public static enum EnumBasaltOreType {
		SULFUR (new ItemStack(ModItems.sulfur)),
		FLUORITE(new ItemStack(ModItems.fluorite)),
		ASBESTOS(new ItemStack(ModItems.ingot_asbestos)),
		GEM(new ItemStack(ModItems.gem_volcanic)),
		MOLYSITE(new ItemStack(ModItems.powder_molysite));

		public final ItemStack drop;

		public ItemStack getDrop(){
			return drop;
		}
		public int getDropCount(int rand){
			return rand + 1;
		}

        EnumBasaltOreType(ItemStack drop) {
            this.drop = drop;
        }
    }


}