package com.hbm.items.armor;

import com.hbm.api.entity.IRadarDetectable;
import com.hbm.api.entity.IRadarDetectableNT;
import com.hbm.capability.HbmLivingProps;
import com.hbm.handler.ArmorModHandler;
import com.hbm.packet.PacketDispatcher;
import com.hbm.packet.toclient.PlayerSoundPacket;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

public class ItemModRadar extends ItemArmorMod {
	public int range;
	private static final WeakHashMap<EntityLivingBase, Set<Integer>> lastDetectedMissiles = new WeakHashMap<>();

	public ItemModRadar(String s, int range){
		super(ArmorModHandler.extra, true, false, false, false, s);
		this.range = range;
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> list, ITooltipFlag flagIn){
		list.add("§eAlerts when incoming missiles are detected");
		list.add("§eRange: "+range+"m");
		super.addInformation(stack, worldIn, list, flagIn);
	}

	@Override
	public void addDesc(List<String> list, ItemStack stack, ItemStack armor){
		list.add("§5  " + stack.getDisplayName() + " (Range: "+range+"m)");
	}

	@Override
	public void modUpdate(EntityLivingBase entity, ItemStack armor) {
		if (entity.world.isRemote || !(entity instanceof EntityPlayerMP player)) {
			return;
		}

		if (entity.ticksExisted % 10 == 0) {
			List<Entity> currentMissiles = getApproachingMissiles(entity, this.range);
			Set<Integer> currentMissileIds = currentMissiles.stream().map(Entity::getEntityId).collect(Collectors.toSet());
			Set<Integer> lastMissileIds = lastDetectedMissiles.getOrDefault(entity, Collections.emptySet());

			if (!currentMissileIds.isEmpty()) {
				boolean newMissileDetected = currentMissileIds.stream().anyMatch(id -> !lastMissileIds.contains(id));

				if (newMissileDetected) {
//					Set<Integer> newIds = currentMissileIds.stream()
//							.filter(id -> !lastMissileIds.contains(id))
//							.collect(Collectors.toSet());
//					MainRegistry.logger.info("New missile(s) detected for player {}: {}", player.getName(), newIds);
					PlayerSoundPacket packet = new PlayerSoundPacket(PlayerSoundPacket.SoundType.NULLRADAR, true);
					PacketDispatcher.wrapper.sendTo(packet, player);
				} else if (entity.ticksExisted % 20 == 0) {
					PlayerSoundPacket packet = new PlayerSoundPacket(PlayerSoundPacket.SoundType.NULLRADAR, false);
					PacketDispatcher.wrapper.sendTo(packet, player);
				}
				lastDetectedMissiles.put(entity, currentMissileIds);
			} else {
				lastDetectedMissiles.remove(entity);
			}
		}
	}

	private static boolean isEntityApproaching(EntityLivingBase entity, Entity e){
		boolean xAxisApproaching = (entity.posX < e.posX  && e.motionX < 0) || (entity.posX > e.posX  && e.motionX > 0);
		boolean zAxisApproaching = (entity.posZ < e.posZ && e.motionZ < 0) || (entity.posZ > e.posZ && e.motionZ > 0);
		return xAxisApproaching && zAxisApproaching;
	}

	private static List<Entity> getApproachingMissiles(EntityLivingBase entity, int r) {
		List<Entity> list = entity.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(entity.posX - r, 0D, entity.posZ - r, entity.posX + r, 10_000D, entity.posZ + r));
		for(Entity e : list) {
			if(e instanceof EntityLivingBase entittLiving && HbmLivingProps.getDigamma(entittLiving) > 0.001) {
				return Collections.emptyList();
			}
		}

		List<Entity> detected = new ArrayList<>();
		for (Entity e : list) {
			if((e instanceof IRadarDetectable || e instanceof IRadarDetectableNT) && e.motionY < 0 && isEntityApproaching(entity, e)){
				detected.add(e);
			}
		}
		return detected;
	}
}