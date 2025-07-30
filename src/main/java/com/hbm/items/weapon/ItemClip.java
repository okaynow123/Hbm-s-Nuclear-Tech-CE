package com.hbm.items.weapon;

import com.hbm.items.ModItems;
import com.hbm.items.special.ItemSimpleConsumable;
import com.hbm.lib.Library;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import java.util.List;

public class ItemClip extends Item {

	public ItemClip(String s) {
		this.setTranslationKey(s);
		this.setRegistryName(s);
		this.setMaxDamage(1);
		this.setMaxStackSize(32);
		ModItems.ALL_ITEMS.add(this);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand handIn) {
		ItemStack stack = player.getHeldItem(handIn);
		stack.shrink(1);;
		if(stack.getCount() <= 0)
			stack.damageItem(5, player);
		
		//REVOLVERS
		if(this == ModItems.clip_revolver_iron)
		{
			ItemSimpleConsumable.tryAddItem(player, new ItemStack(ModItems.gun_revolver_iron_ammo, 24));
		}
		
		if(this == ModItems.clip_revolver)
		{
			ItemSimpleConsumable.tryAddItem(player, new ItemStack(ModItems.gun_revolver_ammo, 12));
		}
		
		if(this == ModItems.clip_revolver_gold)
		{
			ItemSimpleConsumable.tryAddItem(player, new ItemStack(ModItems.gun_revolver_gold_ammo, 12));
		}

		if(this == ModItems.clip_revolver_lead)
		{
			ItemSimpleConsumable.tryAddItem(player, new ItemStack(ModItems.gun_revolver_lead_ammo, 12));
		}
		
		if(this == ModItems.clip_revolver_schrabidium)
		{
			ItemSimpleConsumable.tryAddItem(player, new ItemStack(ModItems.gun_revolver_schrabidium_ammo, 12));
		}

		if(this == ModItems.clip_revolver_cursed)
		{
			ItemSimpleConsumable.tryAddItem(player, new ItemStack(ModItems.gun_revolver_cursed_ammo, 17));
		}

		if(this == ModItems.clip_revolver_nightmare)
		{
			ItemSimpleConsumable.tryAddItem(player, new ItemStack(ModItems.gun_revolver_nightmare_ammo, 6));
		}
		
		if(this == ModItems.clip_revolver_nightmare2)
		{
			ItemSimpleConsumable.tryAddItem(player, new ItemStack(ModItems.gun_revolver_nightmare2_ammo, 6));
		}

		if(this == ModItems.clip_revolver_pip)
		{
			ItemSimpleConsumable.tryAddItem(player, new ItemStack(ModItems.ammo_44_pip, 6));
		}
		
		if(this == ModItems.clip_revolver_nopip)
		{
			ItemSimpleConsumable.tryAddItem(player, new ItemStack(ModItems.ammo_44, 12));
		}


		//EXPLOSIVES
		if(this == ModItems.clip_rpg)
		{
			ItemSimpleConsumable.tryAddItem(player, new ItemStack(ModItems.ammo_rocket, 6));
		}

		if(this == ModItems.clip_stinger)
		{
			ItemSimpleConsumable.tryAddItem(player, new ItemStack(ModItems.gun_stinger_ammo, 6));
		}

		if(this == ModItems.clip_fatman)
		{
			ItemSimpleConsumable.tryAddItem(player, new ItemStack(ModItems.ammo_nuke, 6));
		}

		if(this == ModItems.clip_mirv)
		{
			ItemSimpleConsumable.tryAddItem(player, new ItemStack(ModItems.ammo_mirv, 3));
		}

		if(this == ModItems.clip_bf)
		{
			ItemSimpleConsumable.tryAddItem(player, new ItemStack(ModItems.gun_bf_ammo, 2));
		}


		//MAGAZINES
		if(this == ModItems.clip_mp40)
		{
			ItemSimpleConsumable.tryAddItem(player, new ItemStack(ModItems.ammo_9mm, 32));
		}

		if(this == ModItems.clip_uzi)
		{
			ItemSimpleConsumable.tryAddItem(player, new ItemStack(ModItems.ammo_22lr, 32));
		}

		if(this == ModItems.clip_uboinik)
		{
			ItemSimpleConsumable.tryAddItem(player, new ItemStack(ModItems.ammo_12gauge, 24));
		}
		
		if(this == ModItems.clip_lever_action)
		{
			ItemSimpleConsumable.tryAddItem(player, new ItemStack(ModItems.ammo_20gauge, 24));
		}
		
		if(this == ModItems.clip_bolt_action)
		{
			ItemSimpleConsumable.tryAddItem(player, new ItemStack(ModItems.ammo_20gauge_slug, 24));
		}


		
		if(this == ModItems.clip_osipr)
		{
			ItemSimpleConsumable.tryAddItem(player, new ItemStack(ModItems.gun_osipr_ammo, 30));
		}

		if(this == ModItems.clip_immolator)
		{
			ItemSimpleConsumable.tryAddItem(player, new ItemStack(ModItems.gun_immolator_ammo, 64));
		}
		
		if(this == ModItems.clip_cryolator)
		{
			ItemSimpleConsumable.tryAddItem(player, new ItemStack(ModItems.gun_cryolator_ammo, 64));
		}

		if(this == ModItems.clip_mp)
		{
			ItemSimpleConsumable.tryAddItem(player, new ItemStack(ModItems.ammo_566_gold, 40));
		}
		
		if(this == ModItems.clip_xvl1456)
		{
			ItemSimpleConsumable.tryAddItem(player, new ItemStack(ModItems.gun_xvl1456_ammo, 64));
		}
		
		if(this == ModItems.clip_emp)
		{
			ItemSimpleConsumable.tryAddItem(player, new ItemStack(ModItems.gun_emp_ammo, 6));
		}
		
		if(this == ModItems.clip_jack)
		{
			ItemSimpleConsumable.tryAddItem(player, new ItemStack(ModItems.gun_jack_ammo, 12));
		}
		
		if(this == ModItems.clip_spark)
		{
			ItemSimpleConsumable.tryAddItem(player, new ItemStack(ModItems.gun_spark_ammo, 4));
		}
		
		if(this == ModItems.clip_hp)
		{
			ItemSimpleConsumable.tryAddItem(player, new ItemStack(ModItems.gun_hp_ammo, 12));
		}
		
		if(this == ModItems.clip_euthanasia)
		{
			ItemSimpleConsumable.tryAddItem(player, new ItemStack(ModItems.gun_euthanasia_ammo, 16));
		}
		
		if(this == ModItems.clip_defabricator)
		{
			ItemSimpleConsumable.tryAddItem(player, new ItemStack(ModItems.gun_defabricator_ammo, 16));
		}
		return super.onItemRightClick(worldIn, player, handIn);
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if(this == ModItems.ammo_container)
		{
			tooltip.add("Gives ammo for all held weapons.");
		}
	}
}
