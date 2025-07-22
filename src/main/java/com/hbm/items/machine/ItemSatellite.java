package com.hbm.items.machine;

import com.hbm.dim.CelestialBody;
import com.hbm.items.ISatChip;
import com.hbm.items.ModItems;
import com.hbm.items.weapon.ItemMissile;
import com.hbm.saveddata.satellites.Satellite;
import com.hbm.util.I18nUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import java.util.List;

public class ItemSatellite extends ItemMissile implements ISatChip {

	public ItemSatellite(String s) {
		super(s);
		makeWarhead(WarheadType.SATELLITE, 15F, 16_000, PartSize.SIZE_20);
	}

	public ItemSatellite(String s, int mass) {
		super(s);
		makeWarhead(WarheadType.SATELLITE, 15F, mass, PartSize.SIZE_20);
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		tooltip.add(I18nUtil.resolveKey("desc.satellitefr", getFreq(stack)));

		if(this == ModItems.sat_foeq)
			tooltip.add("Gives you an achievement. That's it.");

		if(this == ModItems.sat_gerald) {
			tooltip.add("Single use.");
			tooltip.add("Requires orbital module.");
			tooltip.add("Melter of CPUs, bane of every server owner.");
		}

		if(this == ModItems.sat_laser)
			tooltip.add("Allows to summon lasers with a 15 second cooldown.");

		if(this == ModItems.sat_mapper)
			tooltip.add("Displays currently loaded chunks.");

		if(this == ModItems.sat_miner)
			tooltip.add("Will deliver ore powders to a cargo landing pad.");

		if(this == ModItems.sat_lunar_miner)
			tooltip.add("Mines moon turf to deliver it to a cargo landing pad.");

		if(this == ModItems.sat_radar)
			tooltip.add("Shows a map of active entities.");

		if(this == ModItems.sat_resonator)
			tooltip.add("Allows for teleportation with no cooldown.");

		if(this == ModItems.sat_scanner)
			tooltip.add("Creates a topdown map of underground ores.");

		if(this == ModItems.sat_dyson_relay)
			tooltip.add("Allows a Dyson Receiver to function at night");

		if(worldIn != null && CelestialBody.inOrbit(worldIn))
			tooltip.add(ChatFormatting.BOLD + "Interact to deploy into orbit");
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if(!CelestialBody.inOrbit(world)) return ActionResult.newResult(EnumActionResult.PASS, player.getHeldItem(hand));

		if(!world.isRemote) {
			int targetDimensionId = CelestialBody.getTarget(world, (int)player.posX, (int)player.posZ).body.dimensionId;
			WorldServer targetWorld = DimensionManager.getWorld(targetDimensionId);
			if(targetWorld == null) {
				DimensionManager.initDimension(targetDimensionId);
				targetWorld = DimensionManager.getWorld(targetDimensionId);

				if(targetWorld == null) ActionResult.newResult(EnumActionResult.PASS, player.getHeldItem(hand));
			}

			Satellite.orbit(targetWorld, Satellite.getIDFromItem(stack.getItem()), getFreq(stack), player.posX, player.posY, player.posZ);

			player.sendMessage(new TextComponentString("Satellite launched successfully!"));
		}

		stack.shrink(1);

		return ActionResult.newResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}
}
