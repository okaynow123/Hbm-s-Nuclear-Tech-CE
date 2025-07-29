package com.hbm.handler;

import com.hbm.capability.HbmLivingProps;
import com.hbm.config.GeneralConfig;
import com.hbm.config.RadiationConfig;
import com.hbm.entity.mob.EntityDuck;
import com.hbm.entity.mob.EntityNuclearCreeper;
import com.hbm.entity.mob.EntityQuackos;
import com.hbm.entity.mob.EntityRADBeast;
import com.hbm.handler.threading.PacketThreading;
import com.hbm.interfaces.IRadResistantBlock;
import com.hbm.interfaces.Untested;
import com.hbm.lib.ModDamageSource;
import com.hbm.lib.RefStrings;
import com.hbm.main.AdvancementManager;
import com.hbm.main.MainRegistry;
import com.hbm.packet.toclient.AuxParticlePacketNT;
import com.hbm.saveddata.AuxSavedData;
import com.hbm.saveddata.RadiationSavedData;
import com.hbm.util.SubChunkKey;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Refactored to be fully threaded. Do not aim for upstream parity.
 * This is actually way better than upstream.
 *
 * @author Drillgon, Zach2039, mlbv
 */
@Mod.EventBusSubscriber(modid = RefStrings.MODID)
public class RadiationSystemNT {

    /**
     * Per world radiation storage data
     */
    private static final Map<World, WorldRadiationData> worldMap = new ConcurrentHashMap<>();
    private static final ForkJoinPool pool = ForkJoinPool.commonPool();
    private static final ByteBuffer buf = ByteBuffer.allocate(524288);
    private static final float PRUNE_THRESHOLD = 0.1F;
    private static final float EXTRA_DECAY = 0.05F;
    /**
     * A tick counter so radiation only updates once every second.
     */
    private static int ticks;
    private static CompletableFuture<RadiationUpdates> radiationFuture;

    /**
     * Increments the radiation at the specified block position. Only increments if the current radiation stored is less than max
     *
     * @param world  - the world to increment radiation in
     * @param pos    - the block position to increment radiation at
     * @param amount - the amount to increment by
     * @param max    - the maximum amount of radiation allowed before it doesn't increment
     */
    public static void incrementRad(World world, BlockPos pos, float amount, float max) {
        if (pos.getY() < 0 || pos.getY() > 255 || !world.isBlockLoaded(pos)) return;
        if (!isSubChunkLoaded(world, pos)) {
            rebuildChunkPockets(world.getChunk(pos), pos.getY() >> 4);
        }
        RadPocket p = getPocket(world, pos);
        if (p != null && p.radiation.get() < max) {
            p.radiation.updateAndGet(current -> current + amount);
            //Mark this pocket as active so it gets updated
            if (amount > 0) {
                WorldRadiationData data = getWorldRadData(world);
                data.addActivePocket(p);
            }
        }
    }

    /**
     * Subtracts amount from the current radiation at pos.
     *
     * @param world  - the world to edit radiation in
     * @param pos    - the position to edit radiation at
     * @param amount - the amount to subtract from current rads
     */
    public static void decrementRad(World world, BlockPos pos, float amount) {
        //If there's nothing to decrement, return
        if (pos.getY() < 0 || pos.getY() > 255 || !isSubChunkLoaded(world, pos)) return;
        RadPocket p = getPocket(world, pos);
        if (p == null) return;
        p.radiation.updateAndGet(current -> current - Math.max(amount, 0));
        if (p.radiation.get() < 0) p.radiation.set(0.0f);
    }

    /**
     * Sets the radiation at pos to the specified amount
     *
     * @param world  - the world to set radiation in
     * @param pos    - the position to set radiation at
     * @param amount - the amount to set the radiation to
     */
    public static void setRadForCoord(World world, BlockPos pos, float amount) {
        if (pos.getY() < 0 || pos.getY() > 255 || !world.isBlockLoaded(pos)) return;
        if (!isSubChunkLoaded(world, pos)) {
            rebuildChunkPockets(world.getChunk(pos), pos.getY() >> 4);
        }
        RadPocket p = getPocket(world, pos);
        if (p == null) return;
        p.radiation.set(Math.max(amount, 0));
        //If the amount is greater than 0, make sure to mark it as dirty so it gets updated
        if (amount > 0) {
            WorldRadiationData data = getWorldRadData(world);
            data.addActivePocket(p);
        }
    }

    /**
     * Gets the radiation at the pos
     *
     * @param world - the world to get raadiation in
     * @param pos   - the position to get radiation at
     * @return - the radiation value at the specified position
     */
    public static float getRadForCoord(World world, BlockPos pos) {
        //If it's not loaded, assume there's no radiation. Makes sure to not keep a lot of chunks loaded
        if (!isSubChunkLoaded(world, pos)) return 0;

        // If no pockets, assume no radiation
        RadPocket pocket = getPocket(world, pos);
        if (pocket == null) return 0;

        return pocket.radiation.get();
    }

    /**
     * Removes all loaded radiation from a world
     *
     * @param world - the world from which to remove radiation
     */
    public static void jettisonData(World world) {
        WorldRadiationData data = getWorldRadData(world);
        data.data.clear();
        data.clearActivePockets();
    }

    /**
     * Gets the pocket at the position (pockets explained below)
     *
     * @param world - the world to get the pocket from
     * @param pos   - the position the pocket should contain
     * @return - the RadPocket at the specified position, or null if it's a radiation-resistant block
     */
    @Nullable
    public static RadPocket getPocket(World world, BlockPos pos) {
        SubChunkRadiationStorage storage = getSubChunkStorage(world, pos);
        return storage != null ? storage.getPocket(pos) : null;
    }

    /**
     * Gets the collection of RadiationPockets that have active radiation data
     *
     * @param world - the world to get radiation pockets from
     * @return - collection of active rad pockets
     */
    public static Collection<RadPocket> getActiveCollection(World world) {
        return getWorldRadData(world).getActivePockets();
    }

    /**
     * Gets whether the sub chunk at spefified position is loaded
     *
     * @param world - the world to check in
     * @param pos   - ths position to check at
     * @return whether the specified position currently has an active sub chunk
     */
    public static boolean isSubChunkLoaded(World world, BlockPos pos) {
        //If the position is out of bounds, it isn't loaded
        if (pos.getY() > 255 || pos.getY() < 0) return false;
        //If the world radiation data doesn't exist, nothing is loaded
        WorldRadiationData worldRadData = worldMap.get(world);
        if (worldRadData == null) {
            return false;
        }
        SubChunkKey key = new SubChunkKey(pos.getX() >> 4, pos.getZ() >> 4, pos.getY() >> 4);
        return worldRadData.data.containsKey(key);
    }

    /**
     * Gets the sub chunk from the specified pos. Does not load it if it doesn't exist
     *
     * @param world - the world to get from
     * @param pos   - the position to get the sub chunk at
     * @return the sub chunk at the specified position or null if not loaded
     */
    @Nullable
    public static SubChunkRadiationStorage getSubChunkStorage(World world, BlockPos pos) {
        WorldRadiationData worldRadData = getWorldRadData(world);
        SubChunkKey key = new SubChunkKey(pos.getX() >> 4, pos.getZ() >> 4, pos.getY() >> 4);
        return worldRadData.data.get(key);
    }

    /**
     * Gets the world radiation data for the world
     *
     * @param world - the world to get the radiation data from
     * @return the radiation data for the world
     */
    @NotNull
    private static WorldRadiationData getWorldRadData(World world) {
        return worldMap.computeIfAbsent(world, WorldRadiationData::new);
    }

    private static void updateRadSaveData(World world) {
        RadiationSavedData data = RadiationSavedData.getData(world);

        if (data.world == null) {
            data.world = world;
        }

        if (GeneralConfig.enableDebugMode) {
            MainRegistry.logger.info("[Debug] Updated system for entity contamination processing at worldtime {}", world.getTotalWorldTime());
        }

        data.updateSystem();
    }

    /**
     * Updates world-specific radiation data, such as thunder and the radiation save data.
     */
    private static void updateWorldRadiationData(World world, boolean updateData) {
        if (world != null && !world.isRemote && GeneralConfig.enableRads) {
            int thunder = AuxSavedData.getThunder(world);
            if (thunder > 0) AuxSavedData.setThunder(world, thunder - 1);

            RadiationSavedData data = RadiationSavedData.getData(world);
            if (data.world == null) {
                data.world = world;
            }

            if (world.getTotalWorldTime() % 20 == 15 && updateData) {
                // unless a chunk requires update
                updateRadSaveData(world);
            }
        }
    }

    /**
     * Updates entity contamination and applies effects based on current rad levels
     */
    @SubscribeEvent
    public static void onEntityUpdate(LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        World world = entity.world;

        if (world.isRemote || !GeneralConfig.enableRads || entity.isEntityInvulnerable(ModDamageSource.radiation)) return;

        float eRad = HbmLivingProps.getRadiation(entity);

        if (eRad < 100) return;
        if (eRad >= 200 && entity.getHealth() > 0 && entity instanceof EntityCreeper) {
            if (world.rand.nextInt(3) == 0) {
                EntityNuclearCreeper creep = new EntityNuclearCreeper(world);
                creep.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
                if (!entity.isDead) world.spawnEntity(creep);
                entity.setDead();
            } else {
                entity.attackEntityFrom(ModDamageSource.radiation, 100F);
            }
            return;
        } else if (eRad >= 500 && entity instanceof EntityCow && !(entity instanceof EntityMooshroom)) {
            EntityMooshroom creep = new EntityMooshroom(world);
            creep.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
            if (!entity.isDead) world.spawnEntity(creep);
            entity.setDead();
            return;
        } else if (eRad >= 600 && entity instanceof EntityVillager vil) {
            EntityZombieVillager creep = new EntityZombieVillager(world);
            creep.setProfession(vil.getProfession());
            creep.setForgeProfession(vil.getProfessionForge());
            creep.setChild(vil.isChild());
            creep.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
            if (!entity.isDead) world.spawnEntity(creep);
            entity.setDead();
            return;
        } else if (eRad >= 700 && entity instanceof EntityBlaze) {
            EntityRADBeast creep = new EntityRADBeast(world);
            creep.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
            if (!entity.isDead) world.spawnEntity(creep);
            entity.setDead();
            return;
        } else if (eRad >= 800 && entity instanceof EntityHorse horsie) {
            EntityZombieHorse zomhorsie = new EntityZombieHorse(world);
            zomhorsie.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
            zomhorsie.setGrowingAge(horsie.getGrowingAge());
            zomhorsie.setTemper(horsie.getTemper());
            zomhorsie.setHorseSaddled(horsie.isHorseSaddled());
            zomhorsie.setHorseTamed(horsie.isTame());
            zomhorsie.setOwnerUniqueId(horsie.getOwnerUniqueId());
            zomhorsie.makeMad();
            if (!entity.isDead) world.spawnEntity(zomhorsie);
            entity.setDead();
            return;
        } else if (eRad >= 900 && entity instanceof EntityDuck) { // This is now safe since EntityQuackos is invulnerable
            EntityQuackos quacc = new EntityQuackos(world);
            quacc.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
            if (!entity.isDead) world.spawnEntity(quacc);
            entity.setDead();
            return;
        }

        if (eRad > 2500000) HbmLivingProps.setRadiation(entity, 2500000);

        if (eRad >= 1000) {
            entity.attackEntityFrom(ModDamageSource.radiation, 1000F);
            HbmLivingProps.setRadiation(entity, 0);

            if (entity.getHealth() > 0) {
                entity.setHealth(0);
                entity.onDeath(ModDamageSource.radiation);
            }

            if (entity instanceof EntityPlayerMP)
                AdvancementManager.grantAchievement((EntityPlayerMP) entity, AdvancementManager.achRadDeath);
        } else if (eRad >= 800) {
            if (world.rand.nextInt(300) == 0) entity.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 5 * 30, 0));
            if (world.rand.nextInt(300) == 0) entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 10 * 20, 2));
            if (world.rand.nextInt(300) == 0) entity.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 10 * 20, 2));
            if (world.rand.nextInt(500) == 0) entity.addPotionEffect(new PotionEffect(MobEffects.POISON, 3 * 20, 2));
            if (world.rand.nextInt(700) == 0) entity.addPotionEffect(new PotionEffect(MobEffects.WITHER, 3 * 20, 1));
            if (world.rand.nextInt(300) == 0) entity.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 5 * 20, 3));
            if (world.rand.nextInt(300) == 0) entity.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 5 * 20, 3));
        } else if (eRad >= 600) {
            if (world.rand.nextInt(300) == 0) entity.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 5 * 30, 0));
            if (world.rand.nextInt(300) == 0) entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 10 * 20, 2));
            if (world.rand.nextInt(300) == 0) entity.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 10 * 20, 2));
            if (world.rand.nextInt(500) == 0) entity.addPotionEffect(new PotionEffect(MobEffects.POISON, 3 * 20, 1));
            if (world.rand.nextInt(300) == 0) entity.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 3 * 20, 3));
            if (world.rand.nextInt(400) == 0) entity.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 6 * 20, 2));
        } else if (eRad >= 400) {
            if (world.rand.nextInt(300) == 0) entity.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 5 * 30, 0));
            if (world.rand.nextInt(500) == 0) entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 5 * 20, 0));
            if (world.rand.nextInt(300) == 0) entity.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 5 * 20, 1));
            if (world.rand.nextInt(500) == 0) entity.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 3 * 20, 2));
            if (world.rand.nextInt(600) == 0) entity.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 4 * 20, 1));
        } else if (eRad >= 200) {
            if (world.rand.nextInt(300) == 0) entity.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 5 * 20, 0));
            if (world.rand.nextInt(500) == 0) entity.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 5 * 20, 0));
            if (world.rand.nextInt(700) == 0) entity.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 3 * 20, 2));
            if (world.rand.nextInt(800) == 0) entity.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 4 * 20, 0));
        } else if (eRad >= 100) {
            if (world.rand.nextInt(800) == 0) entity.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 2 * 20, 0));
            if (world.rand.nextInt(1000) == 0) entity.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 20, 0));

            if (entity instanceof EntityPlayerMP)
                AdvancementManager.grantAchievement((EntityPlayerMP) entity, AdvancementManager.achRadPoison);
        }
    }

    /**
     * Marks a chunk to be rebuilt. This is used when a radiation resistant block is added or removed
     *
     * @param world - the world to mark in
     * @param pos   - the position to mark at
     */
    public static void markChunkForRebuild(World world, BlockPos pos) {
        if (!GeneralConfig.advancedRadiation) return;

        SubChunkKey key = new SubChunkKey(pos.getX() >> 4, pos.getZ() >> 4, pos.getY() >> 4);
        WorldRadiationData r = getWorldRadData(world);

        if (GeneralConfig.enableDebugMode) {
            MainRegistry.logger.info("[Debug] Marking chunk dirty at {}", key);
        }

        //Ensures we don't run into any problems with concurrent modification
        if (r.iteratingDirty) {
            r.dirtyChunks2.add(key);
        } else {
            r.dirtyChunks.add(key);
        }
    }

    /**
     * Rebuilds stored dirty chunks
     */
    private static void rebuildDirty() {
        worldMap.values().parallelStream().forEach(r -> {
            //Set the iteration flag to avoid concurrent modification
            r.iteratingDirty = true;

            //For each dirty sub chunk, rebuild it
            Iterator<SubChunkKey> itr = r.dirtyChunks.iterator();
            while (itr.hasNext()) {
                SubChunkKey dirtyKey = itr.next();
                if (GeneralConfig.enableDebugMode) {
                    MainRegistry.logger.info("[Debug] Rebuilding chunk pockets for dirty chunk at {}", dirtyKey);
                }
                int cx = dirtyKey.getChunkXPos();
                int cz = dirtyKey.getChunkZPos();
                if (r.world instanceof WorldServer server && server.getChunkProvider().chunkExists(cx, cz)) {
                    rebuildChunkPockets(r.world.getChunk(cx, cz), dirtyKey.getSubY());
                    itr.remove();
                } else {
                    r.deferredRebuild.add(dirtyKey);
                    itr.remove();
                }
            }
            r.iteratingDirty = false;
            //Clear the dirty chunks lists, and add any chunks that might have been marked while iterating to be dealt with next tick.
            r.dirtyChunks.addAll(r.dirtyChunks2);
            r.dirtyChunks2.clear();
        });
    }

    @SubscribeEvent
    public static void onWorldUpdate(TickEvent.WorldTickEvent e) {
        if (GeneralConfig.enableDebugMode) {
            MainRegistry.logger.info("[Debug] onWorldUpdate called for RadSys tick {}", ticks);
        }

        boolean allowUpdate = (e.phase == Phase.START);

        if (allowUpdate) {
            // Make the world stinky
            RadiationWorldHandler.handleWorldDestruction(e.world);
        }

        // Update world-level radiation data.
        updateWorldRadiationData(e.world, allowUpdate);
    }

    @SubscribeEvent
    public static void onUpdate(TickEvent.ServerTickEvent e) {
        //If we don't do advanced radiation, don't update
        if (!GeneralConfig.enableRads || !GeneralConfig.advancedRadiation) return;

        if (GeneralConfig.enableDebugMode) {
            MainRegistry.logger.info("[Debug] onUpdate called for RadSys tick {}", ticks);
        }

        if (e.phase == Phase.END) {
            ticks++;
            if (ticks % 20 == 17 && radiationFuture == null) {
                //Every second, do a full system update, which will spread around radiation and all that
                radiationFuture = CompletableFuture.supplyAsync(RadiationSystemNT::computeRadiationUpdates, pool);
            }
        }

        if (radiationFuture != null && radiationFuture.isDone()) {
            try {
                RadiationUpdates updates = radiationFuture.get();
                applyRadiationUpdates(updates);
            } catch (InterruptedException | ExecutionException ex) {
                MainRegistry.logger.error("Error applying radiation updates", ex);
            }
            radiationFuture = null;
        }

        //Make sure any chunks marked as dirty by radiation resistant blocks are rebuilt instantly
        rebuildDirty();
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload e) {
        if (!GeneralConfig.enableRads || !GeneralConfig.advancedRadiation) return;
        if (!e.getWorld().isRemote) {
            //When a chunk is unloaded, also unload it from our radiation data if it exists
            WorldRadiationData data = getWorldRadData(e.getWorld());
            for (int i = 0; i < 16; i++) {
                SubChunkKey key = new SubChunkKey(e.getChunk().getPos(), i);
                if (data.data.containsKey(key)) {
                    data.data.get(key).unload();
                    data.data.remove(key);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkDataEvent.Load e) {
        if (!GeneralConfig.enableRads || !GeneralConfig.advancedRadiation) return;
        if (!e.getWorld().isRemote) {
            WorldRadiationData data;
            if (e.getData().hasKey("hbmRadDataNT")) {
                //If this chunk had saved radiation in it, read it and add the persistent chunk data at this chunk position
                data = getWorldRadData(e.getWorld());
                readFromNBT(data, e.getChunk().getPos(), e.getData().getCompoundTag("hbmRadDataNT"));
            } else return;
            ChunkPos cpos = e.getChunk().getPos();
            if (data.deferredDirty.contains(cpos)) {
                data.deferredDirty.remove(cpos);
                e.getChunk().markDirty();
            }
            for (int i = 0; i < 16; i++) {
                SubChunkKey key = new SubChunkKey(cpos.x, cpos.z, i);
                if (data.deferredRebuild.contains(key)) {
                    data.deferredRebuild.remove(key);
                    data.dirtyChunks.add(key);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onChunkSave(ChunkDataEvent.Save e) {
        if (!GeneralConfig.enableRads || !GeneralConfig.advancedRadiation) return;
        if (!e.getWorld().isRemote) {
            WorldRadiationData data = getWorldRadData(e.getWorld());
            NBTTagCompound tag = writeToNBT(data, e.getChunk().getPos());
            if (tag != null) {
                e.getData().setTag("hbmRadDataNT", tag);
            }
        }
    }

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load e) {
        if (!GeneralConfig.enableRads || !GeneralConfig.advancedRadiation) return;
        if (!e.getWorld().isRemote) {
            //Always make sure worlds have radiation data
            worldMap.put(e.getWorld(), new WorldRadiationData(e.getWorld()));
        }
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload e) {
        if (!GeneralConfig.enableRads || !GeneralConfig.advancedRadiation) return;
        if (!e.getWorld().isRemote) {
            //Remove the world data on unload
            worldMap.remove(e.getWorld());
        }
    }

    private static RadiationUpdates computeRadiationUpdates() {
        RadiationUpdates res = new RadiationUpdates();
        worldMap.values().parallelStream().forEach(w -> {
            RadiationUpdates.WorldUpdate wu = res.updates.computeIfAbsent(w, k -> new RadiationUpdates.WorldUpdate());
            List<RadPocket> itrActive = new ArrayList<>(w.getActivePockets());
            final ThreadLocalRandom rand = ThreadLocalRandom.current();
            itrActive.parallelStream().forEach(p -> {
                BlockPos pos = p.parent.subChunkPos;
                p.radiation.updateAndGet(current -> Math.max(0.0f, current * 0.999F - 0.05F));
                wu.dirtyChunkPositions.add(new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4));
                float currentRadiation = p.radiation.get();
                if (currentRadiation <= 0) {
                    // Pocket is depleted before spreading, add to its own accumulatedRads and it will be removed later.
                    p.radiation.set(0.0f);
                    p.accumulatedRads.set(0.0f);
                    return;
                }

                // Apply pruning for near-zero radiation in non-sealed pockets
                if (!p.isSealed() && currentRadiation < PRUNE_THRESHOLD) {
                    p.radiation.updateAndGet(cur -> Math.max(0.0f, cur - EXTRA_DECAY));
                    currentRadiation = p.radiation.get();
                    if (currentRadiation <= 0) {
                        p.radiation.set(0.0f);
                        p.accumulatedRads.set(0.0f);
                        return;
                    }
                }

                if (currentRadiation > RadiationConfig.fogRad && rand.nextInt(RadiationConfig.fogCh) == 0) {
                    //Fog calculation works slightly differently here to account for the 3d nature of the system
                    //We just try 10 random coordinates of the sub chunk
                    //If the coordinate is inside this pocket and the block at the coordinate is air,
                    //use it to spawn a rad particle at that block and break
                    //Also only spawn it if it's close to the ground, otherwise you get a giant fart when nukes go off.
                    for (int i = 0; i < 10; i++) {
                        BlockPos randPos = new BlockPos(rand.nextInt(16), rand.nextInt(16), rand.nextInt(16));
                        if (p.parent.getPocket(randPos) == p) {
                            randPos = randPos.add(pos);
                            @Untested("this is bad practice, but no crash observed so far")
                            IBlockState state = w.world.getBlockState(randPos);
                            Vec3d rPos = new Vec3d(randPos.getX() + 0.5, randPos.getY() + 0.5, randPos.getZ() + 0.5);
                            RayTraceResult trace = w.world.rayTraceBlocks(rPos, rPos.add(0, -6, 0));
                            if (state.getBlock().isAir(state, w.world, randPos) && trace != null && trace.typeOfHit == Type.BLOCK) {
                                wu.fogPositions.add(randPos);
                                break;
                            }
                        }
                    }
                }

                float count = 0;
                for (EnumFacing e : EnumFacing.VALUES) {
                    count += p.connectionIndices[e.ordinal()].size();
                }
                float amountPer = count > 0 ? 0.7F / count : 0;
                if (count == 0 || currentRadiation < 1) {
                    //Don't update if we have no connections or our own radiation is less than 1. Prevents micro radiation bleeding.
                    amountPer = 0;
                }

                final float radForThisTick = p.radiation.get();
                // All pockets, even those not spreading, retain their own radiation value.
                p.accumulatedRads.updateAndGet(acc -> acc + radForThisTick);

                if (radForThisTick > 0 && amountPer > 0) {
                    // If spreading, subtract the spread amount from its own accumulated value.
                    p.accumulatedRads.updateAndGet(acc -> acc - radForThisTick * 0.7F);
                    for (EnumFacing e : EnumFacing.VALUES) {
                        BlockPos nPos = pos.offset(e, 16);
                        if (!w.world.isBlockLoaded(nPos) || nPos.getY() < 0 || nPos.getY() > 255) continue;
                        List<Integer> cons = p.connectionIndices[e.ordinal()];
                        if (cons.contains(-1)) {
                            wu.dirtyRebuild.add(nPos);
                            continue;
                        }
                        SubChunkRadiationStorage sc2 = getSubChunkStorage(w.world, nPos);
                        if (sc2 == null) {
                            wu.dirtyRebuild.add(nPos);
                            continue;
                        }
                        for (int idx : cons) {
                            if (idx < 0 || idx >= sc2.pockets.length) continue;
                            RadPocket target = sc2.pockets[idx];
                            if (!target.isSealed()) {
                                final float radToAdd = radForThisTick * amountPer;
                                target.accumulatedRads.updateAndGet(acc -> acc + radToAdd);
                                // Collect the newly irradiated pocket.
                                wu.toAdd.add(target);
                            }
                        }
                    }
                }
            });
            Set<RadPocket> allPocketsToUpdate = new HashSet<>(itrActive);
            allPocketsToUpdate.addAll(wu.toAdd);
            for (RadPocket act : allPocketsToUpdate) {
                float newRadiation = act.accumulatedRads.getAndSet(0.0f);
                act.radiation.set(newRadiation);

                if (newRadiation <= 0) {
                    wu.toRemove.add(act);
                }
            }
        });
        return res;
    }

    private static void applyRadiationUpdates(RadiationUpdates updates) {
        for (Map.Entry<WorldRadiationData, RadiationUpdates.WorldUpdate> entry : updates.updates.entrySet()) {
            WorldRadiationData w = entry.getKey();
            RadiationUpdates.WorldUpdate wu = entry.getValue();
            for (RadPocket p : wu.toAdd) {
                w.addActivePocket(p);
            }
            for (RadPocket p : wu.toRemove) {
                w.removeActivePocket(p);
            }
            for (ChunkPos cp : wu.dirtyChunkPositions) {
                int cx = cp.x;
                int cz = cp.z;
                if (w.world instanceof WorldServer server && server.getChunkProvider().chunkExists(cx, cz)) {
                    w.world.getChunk(cx, cz).markDirty();
                } else {
                    w.deferredDirty.add(cp);
                }
            }
            for (BlockPos d : wu.dirtyRebuild) {
                markChunkForRebuild(w.world, d);
            }
            for (BlockPos f : wu.fogPositions) {
                NBTTagCompound data = new NBTTagCompound();
                data.setString("type", "radiationfog");
                PacketThreading.createAllAroundThreadedPacket(new AuxParticlePacketNT(data, f.getX() + 0.5F, f.getY() + 0.5F, f.getZ() + 0.5F),
                        new TargetPoint(w.world.provider.getDimension(), f.getX(), f.getY(), f.getZ(), 100));
            }
        }
    }

    /**
     * Divides a 16x16x16 sub chunk into pockets that are separated by radiation resistant blocks.
     * These pockets are also linked to other pockets in neighboring chunks
     *
     * @param chunk  - the chunk to rebuild
     * @param yIndex - the Y index of the sub chunk to rebuild
     */
    private static void rebuildChunkPockets(Chunk chunk, int yIndex) {
        BlockPos subChunkPos = new BlockPos(chunk.getPos().x << 4, yIndex << 4, chunk.getPos().z << 4);

        if (GeneralConfig.enableDebugMode) {
            MainRegistry.logger.info("[Debug] Starting rebuild of chunk at {}", new BlockPos(chunk.getPos().x, yIndex, chunk.getPos().z));
        }

        //Initialize all the necessary variables. A list of pockets for the sub chunk, the block storage for this sub chunk,
        //an array of rad pockets for fast pocket lookup by blockpos, chunk radiation storage for this position
        //And finally a new sub chunk that will be added to the chunk radiation storage when it's filled with data
        List<RadPocket> pockets = new ArrayList<>();
        ExtendedBlockStorage blocks = chunk.getBlockStorageArray()[yIndex];
        WorldRadiationData data = getWorldRadData(chunk.getWorld());
        SubChunkRadiationStorage subChunk = new SubChunkRadiationStorage(data, subChunkPos);

        if (blocks != null) {
            byte[] pocketData = new byte[2048]; // 4096 blocks * 4 bits/block / 8 bits/byte
            Arrays.fill(pocketData, (byte) 0xFF);

            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 16; y++) {
                    for (int z = 0; z < 16; z++) {
                        // Check if this block has already been assigned to a pocket.
                        int blockIndex = (x << 8) | (y << 4) | z;
                        int byteIndex = blockIndex / 2;
                        int paletteIndex = (blockIndex % 2 == 0) ? (pocketData[byteIndex] >> 4) & 0x0F : pocketData[byteIndex] & 0x0F;

                        if (paletteIndex != SubChunkRadiationStorage.NO_POCKET_INDEX) {
                            continue; // Already processed
                        }

                        Block block = blocks.get(x, y, z).getBlock();
                        BlockPos localPos = new BlockPos(x, y, z);

                        if (!(block instanceof IRadResistantBlock && ((IRadResistantBlock) block).isRadResistant(chunk.getWorld(), localPos.add(subChunkPos)))) {
                            // This block is not resistant and has no pocket yet. Start a flood fill.
                            int currentPaletteIndex;
                            // If we have more than 14 pockets, merge new pockets into pocket 0 to prevent overflow.
                            // This is intended! having > 14 pockets in one single subchunk is extremely rare
                            if (pockets.size() >= 15) {
                                currentPaletteIndex = 0; // Merge with pocket 0
                            } else {
                                currentPaletteIndex = pockets.size();
                                pockets.add(new RadPocket(subChunk, currentPaletteIndex));
                            }
                            // Run the flood fill for this new pocket.
                            buildPocket(subChunk, chunk.getWorld(), localPos, subChunkPos, blocks, pocketData, pockets.get(currentPaletteIndex));
                        }
                    }
                }
            }
            // If there's only one pocket, we can save 2KB by using null, as the entire subchunk is uniform.
            subChunk.pocketData = pockets.size() <= 1 ? null : pocketData;
        } else {
            RadPocket pocket = new RadPocket(subChunk, 0);
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    doEmptyChunk(chunk, subChunkPos, new BlockPos(x, 15, z), pocket, EnumFacing.UP);
                    doEmptyChunk(chunk, subChunkPos, new BlockPos(x, 0, z), pocket, EnumFacing.DOWN);
                }
            }
            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 16; y++) {
                    doEmptyChunk(chunk, subChunkPos, new BlockPos(x, y, 0), pocket, EnumFacing.NORTH);
                    doEmptyChunk(chunk, subChunkPos, new BlockPos(x, y, 15), pocket, EnumFacing.SOUTH);
                }
            }
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    doEmptyChunk(chunk, subChunkPos, new BlockPos(15, y, z), pocket, EnumFacing.EAST);
                    doEmptyChunk(chunk, subChunkPos, new BlockPos(0, y, z), pocket, EnumFacing.WEST);
                }
            }
            pockets.add(pocket);
            subChunk.pocketData = null;
        }

        subChunk.pockets = pockets.toArray(new RadPocket[0]);

        //Finally, put the newly built sub chunk into the data
        SubChunkKey key = new SubChunkKey(chunk.getPos(), yIndex);
        SubChunkRadiationStorage old = data.data.put(key, subChunk);
        if (old != null) {
            subChunk.setRad(old);
        }
        subChunk.add(chunk.getWorld(), subChunkPos);
        if (GeneralConfig.enableDebugMode) {
            MainRegistry.logger.info("[Debug] Finished rebuild of chunk at {} with {} pockets.", new BlockPos(chunk.getPos().x, yIndex, chunk.getPos().z), pockets.size());
        }
    }

    private static void doEmptyChunk(Chunk chunk, BlockPos subChunkPos, BlockPos pos, RadPocket pocket, EnumFacing facing) {
        BlockPos newPos = pos.offset(facing);
        BlockPos outPos = newPos.add(subChunkPos);
        Block block = chunk.getWorld().getBlockState(outPos).getBlock();
        //If the block isn't radiation resistant...
        if (!(block instanceof IRadResistantBlock && ((IRadResistantBlock) block).isRadResistant(chunk.getWorld(), outPos))) {
            if (!isSubChunkLoaded(chunk.getWorld(), outPos)) {
                //if it's not loaded, mark it with a single -1 value. This will tell the update method that the
                //Chunk still needs to be loaded to propagate radiation into it
                if (!pocket.connectionIndices[facing.ordinal()].contains(-1)) {
                    pocket.connectionIndices[facing.ordinal()].add(-1);
                }
            } else {
                //If it is loaded, see if the pocket at that position is already connected to us. If not, add it as a connection.
                //Setting outPocket's connection will be handled in setForYLevel

                RadPocket outPocket = getPocket(chunk.getWorld(), outPos);
                if (outPocket != null && !pocket.connectionIndices[facing.ordinal()].contains(outPocket.index))
                    pocket.connectionIndices[facing.ordinal()].add(outPocket.index);
            }
        }
    }

    /**
     * Builds a pocket using a flood fill.
     *
     * @param subChunk         - sub chunk to build a pocket in
     * @param world            - world we're building in
     * @param start            - the block pos to flood fill from
     * @param subChunkWorldPos - the world position of the sub chunk
     * @param chunk            - the block storage to pull blocks from
     * @param pocketData       - the byte array to populate with palette indices
     * @param pocket           - the pocket object (containing the index) to assign
     */
    private static void buildPocket(SubChunkRadiationStorage subChunk, World world, BlockPos start, BlockPos subChunkWorldPos,
                                    ExtendedBlockStorage chunk, byte[] pocketData, RadPocket pocket) {
        int paletteIndex = pocket.index;
        Queue<BlockPos> queue = new ArrayDeque<>(1024);
        queue.add(start);

        // Set the starting block's palette index
        setPaletteIndex(pocketData, start, paletteIndex);

        while (!queue.isEmpty()) {
            BlockPos pos = queue.poll();

            //For each direction...
            for (EnumFacing facing : EnumFacing.VALUES) {
                BlockPos newPos = pos.offset(facing);

                if (newPos.getX() < 0 || newPos.getX() > 15 || newPos.getY() < 0 || newPos.getY() > 15 || newPos.getZ() < 0 || newPos.getZ() > 15) {
                    //If we're outside the sub chunk bounds, try to connect to neighbor chunk pockets
                    BlockPos outPos = newPos.add(subChunkWorldPos);
                    //If this position is out of bounds, do nothing
                    if (outPos.getY() < 0 || outPos.getY() > 255) continue;
                    //Will also attempt to load the chunk, which will cause neighbor data to be updated correctly if it's unloaded.
                    Block block = world.getBlockState(outPos).getBlock();
                    //If the block isn't radiation resistant...
                    if (!(block instanceof IRadResistantBlock && ((IRadResistantBlock) block).isRadResistant(world, outPos))) {
                        if (!isSubChunkLoaded(world, outPos)) {
                            //if it's not loaded, mark it with a single -1 value. This will tell the update method that the
                            //Chunk still needs to be loaded to propagate radiation into it
                            if (!pocket.connectionIndices[facing.ordinal()].contains(-1)) {
                                pocket.connectionIndices[facing.ordinal()].add(-1);
                            }
                        } else {
                            //If it is loaded, see if the pocket at that position is already connected to us. If not, add it as a connection.
                            //Setting outPocket's connection will be handled in setForYLevel
                            RadPocket outPocket = getPocket(world, outPos);
                            if (outPocket != null && !pocket.connectionIndices[facing.ordinal()].contains(outPocket.index)) {
                                pocket.connectionIndices[facing.ordinal()].add(outPocket.index);
                            }
                        }
                    }
                } else {
                    // Inside the sub-chunk, check if we should continue the flood fill
                    int blockIndex = (newPos.getX() << 8) | (newPos.getY() << 4) | newPos.getZ();
                    int byteIndex = blockIndex / 2;
                    int existingPaletteIndex = (blockIndex % 2 == 0) ? (pocketData[byteIndex] >> 4) & 0x0F : pocketData[byteIndex] & 0x0F;

                    // Continue if the block is not yet processed
                    if (existingPaletteIndex == SubChunkRadiationStorage.NO_POCKET_INDEX) {
                        Block block = chunk.get(newPos.getX(), newPos.getY(), newPos.getZ()).getBlock();
                        if (!(block instanceof IRadResistantBlock && ((IRadResistantBlock) block).isRadResistant(world, newPos.add(subChunkWorldPos)))) {
                            setPaletteIndex(pocketData, newPos, paletteIndex);
                            queue.add(newPos);
                        }
                    }
                }
            }
        }
    }

    private static void setPaletteIndex(byte[] pocketData, BlockPos pos, int paletteIndex) {
        int blockIndex = (pos.getX() << 8) | (pos.getY() << 4) | pos.getZ();
        int byteIndex = blockIndex / 2;
        byte existingByte = pocketData[byteIndex];
        if (blockIndex % 2 == 0) { // Even index, use high nibble
            pocketData[byteIndex] = (byte) ((existingByte & 0x0F) | (paletteIndex << 4));
        } else { // Odd index, use low nibble
            pocketData[byteIndex] = (byte) ((existingByte & 0xF0) | paletteIndex);
        }
    }

    private static NBTTagCompound writeToNBT(WorldRadiationData data, ChunkPos chunkPos) {
        boolean hasData = false;
        buf.clear();
        for (int i = 0; i < 16; i++) {
            SubChunkKey key = new SubChunkKey(chunkPos, i);
            SubChunkRadiationStorage sc = data.data.get(key);
            if (sc == null) {
                buf.put((byte) 0);
            } else {
                hasData = true;
                buf.put((byte) 1);
                buf.putShort((short) sc.yLevel);
                buf.putShort((short) sc.pockets.length);
                for (RadPocket p : sc.pockets) {
                    writePocket(buf, p);
                }
                if (sc.pocketData == null) {
                    buf.put((byte) 0);
                } else {
                    buf.put((byte) 1);
                    buf.put(sc.pocketData);
                }
            }
        }
        if (!hasData) return null;
        buf.flip();
        byte[] arr = new byte[buf.limit()];
        buf.get(arr);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setByteArray("chunkRadData", arr);
        return tag;
    }

    private static void readFromNBT(WorldRadiationData data, ChunkPos chunkPos, NBTTagCompound tag) {
        ByteBuffer bdata = ByteBuffer.wrap(tag.getByteArray("chunkRadData"));
        for (int i = 0; i < 16; i++) {
            try {
                if (bdata.remaining() == 0) break;
                byte has = bdata.get();
                if (has == 1) {
                    int yLevel = bdata.getShort();
                    SubChunkRadiationStorage sc = new SubChunkRadiationStorage(data, new BlockPos(chunkPos.x << 4, yLevel, chunkPos.z << 4));
                    int len = bdata.getShort();
                    sc.pockets = new RadPocket[len];
                    for (int j = 0; j < len; j++) {
                        sc.pockets[j] = readPocket(bdata, sc);
                        if (sc.pockets[j] != null && sc.pockets[j].radiation.get() > 0) {
                            data.addActivePocket(sc.pockets[j]);
                        }
                    }
                    byte hasPalette = bdata.get();
                    if (hasPalette == 1) {
                        sc.pocketData = new byte[2048];
                        bdata.get(sc.pocketData);
                    } else {
                        sc.pocketData = null;
                    }
                    SubChunkKey key = new SubChunkKey(chunkPos, yLevel >> 4);
                    data.data.put(key, sc);
                }
            } catch (BufferUnderflowException ex) {
                MainRegistry.logger.error("BufferUnderflowException while reading sub-chunk {}. Defaulting to 0s.", i);
            }
        }
    }

    private static void writePocket(@SuppressWarnings("SameParameterValue") ByteBuffer buf, RadPocket p) {
        if (p == null) {
            buf.putInt(-1);
            buf.putFloat(0.0f);
            for (EnumFacing ignored : EnumFacing.VALUES) {
                buf.putShort((short) 0);
            }
            return;
        }
        buf.putInt(p.index);
        buf.putFloat(p.radiation.get());
        for (EnumFacing e : EnumFacing.VALUES) {
            List<Integer> indc = p.connectionIndices[e.ordinal()];
            buf.putShort((short) indc.size());
            for (int idx : indc) {
                buf.putShort((short) idx);
            }
        }
    }

    private static RadPocket readPocket(ByteBuffer buf, SubChunkRadiationStorage parent) {
        int index = buf.getInt();
        if (index == -1) return null;
        RadPocket p = new RadPocket(parent, index);
        p.radiation.set(buf.getFloat());
        for (EnumFacing e : EnumFacing.VALUES) {
            short size = buf.getShort();
            List<Integer> indc = p.connectionIndices[e.ordinal()];
            for (short k = 0; k < size; k++) {
                indc.add((int) buf.getShort());
            }
        }
        return p;
    }

    private static class RadiationUpdates {
        Map<WorldRadiationData, WorldUpdate> updates = new ConcurrentHashMap<>();

        static class WorldUpdate {
            final Set<RadPocket> toAdd = ConcurrentHashMap.newKeySet();
            final Collection<RadPocket> toRemove = new ConcurrentLinkedQueue<>();
            final Set<ChunkPos> dirtyChunkPositions = ConcurrentHashMap.newKeySet();
            final Collection<BlockPos> dirtyRebuild = new ConcurrentLinkedQueue<>();
            final Set<BlockPos> fogPositions = ConcurrentHashMap.newKeySet();
        }
    }

    /*
     * And finally, the data structure part.
     * The hierarchy goes like this:
     * WorldRadiationData - Stores ChunkRadiationStorages, one per chunk. Also keeps dirty chunks that need to be rebuilt and a set of active rad
     * pockets
     * 		ChunkRadiationStorage - Stores an array of SubChunkRadiationStorage, one for each 16 tall section.
     * 			SubChunkRadiationStorage - Stores and array of RadPockets as well as a larger array representing the RadPocket in each position in
     * the sub chunk
     * 				RadPocket - Stores the actual radiation value as well as connections to neighboring RadPockets by indices
     */

    //A list of pockets completely closed off by radiation resistant blocks
    public static class RadPocket {
        public final AtomicReference<Float> radiation = new AtomicReference<>(0.0f);
        @SuppressWarnings("unchecked")
        public final List<Integer>[] connectionIndices = new CopyOnWriteArrayList[EnumFacing.VALUES.length];
        private final AtomicReference<Float> accumulatedRads = new AtomicReference<>(0.0f);
        public SubChunkRadiationStorage parent;
        public int index;

        public RadPocket(SubChunkRadiationStorage parent, int index) {
            this.parent = parent;
            this.index = index;
            for (int i = 0; i < EnumFacing.VALUES.length; i++) {
                connectionIndices[i] = new CopyOnWriteArrayList<>();
            }
        }

        /**
         * Mainly just removes itself from the active pockets list
         *
         * @param world - the world to remove from (unused)
         * @param pos   - the pos to remove from (also unused)
         */
        protected void remove(World world, BlockPos pos) {
            for (EnumFacing e : EnumFacing.VALUES) {
                connectionIndices[e.ordinal()].clear();
            }
            parent.parent.removeActivePocket(this);
        }

        /**
         * @return the world position of the sub chunk this pocket is in
         */
        public BlockPos getSubChunkPos() {
            return parent.subChunkPos;
        }

        /**
         * Checks if a pocket is radiation shielded against other pockets or chunks
         *
         * @return if pocket is sealed
         */
        public boolean isSealed() {
            // Sealed pockets should have no connects to other chunks (-1) or other pockets
            float count = 0;
            for (EnumFacing e : EnumFacing.VALUES) {
                count += this.connectionIndices[e.ordinal()].size();
            }
            return (count == 0);
        }
    }

    //the smaller 16*16*16 chunk
    public static class SubChunkRadiationStorage {
        public static final int NO_POCKET_INDEX = 15; // The sentinel value for a resistant block (binary 1111)

        public WorldRadiationData parent;
        public BlockPos subChunkPos;
        public int yLevel;
        /**
         * A bit-packed array storing the palette index for each of the 4096 blocks.
         * Each byte holds two 4-bit indices.
         * If this is null, it signifies the entire sub-chunk is one single pocket (optimization).
         */
        public byte[] pocketData;
        /** The palette of unique pockets in this sub-chunk. */
        public RadPocket[] pockets;

        public SubChunkRadiationStorage(WorldRadiationData parent, BlockPos subChunkPos) {
            this.parent = parent;
            this.subChunkPos = subChunkPos;
            this.yLevel = subChunkPos.getY();
        }

        /**
         * Gets the pocket at the position using the optimized palette encoding.
         *
         * @param pos - the position to get the pocket at
         * @return the pocket at the specified position, or null if the block is radiation-resistant.
         */
        @Nullable
        public RadPocket getPocket(BlockPos pos) {
            if (this.pocketData == null) {
                return (pockets != null && pockets.length > 0) ? pockets[0] : null;
            }
            int x = pos.getX() & 15;
            int y = pos.getY() & 15;
            int z = pos.getZ() & 15;

            int blockIndex = (x << 8) | (y << 4) | z;
            int byteIndex = blockIndex / 2;
            byte b = pocketData[byteIndex];

            // Extract the 4-bit palette index from the correct half of the byte (nibble).
            int paletteIndex = (blockIndex % 2 == 0) ? (b >> 4) & 0x0F : b & 0x0F;

            if (paletteIndex == NO_POCKET_INDEX || paletteIndex >= pockets.length) {
                return null; // This is a resistant block or invalid data.
            }
            return pockets[paletteIndex];
        }

        /**
         * Attempts to distribute radiation from another sub chunk into this one's pockets.
         *
         * @param other - the sub chunk to set from
         */
        public void setRad(SubChunkRadiationStorage other) {
            //Accumulate a total, and divide that evenly among our pockets
            float total = 0;
            for (RadPocket p : other.pockets) {
                // Sealed pockets should not attribute to total rad count
                if (!p.isSealed()) {
                    total += p.radiation.get();
                }
            }

            if (pockets.length > 0) {
                float radPer = total / pockets.length;
                for (RadPocket p : pockets) {
                    p.radiation.set(radPer);
                    if (radPer > 0) {
                        //If the pocket now has radiation or is sealed, mark it as active
                        parent.addActivePocket(p);
                    }
                }
            }
        }

        /**
         * Remove from the world
         *
         * @param world - the world to remove from
         * @param pos   - the pos to remove from
         */
        public void remove(World world, BlockPos pos) {
            for (RadPocket p : pockets) {
                //Call remove for each pocket
                p.remove(world, pos);
            }
            for (EnumFacing e : EnumFacing.VALUES) {
                //Tries to load the chunk so it updates right.
                world.getBlockState(pos.offset(e, 16));
                if (isSubChunkLoaded(world, pos.offset(e, 16))) {
                    SubChunkRadiationStorage sc = getSubChunkStorage(world, pos.offset(e, 16));
                    //Clears any connections the neighboring chunk has to this sub chunk
                    if (sc != null) {
                        for (RadPocket p : sc.pockets) {
                            p.connectionIndices[e.getOpposite().ordinal()].clear();
                        }
                    }
                }
            }
        }

        /**
         * Adds to the world
         *
         * @param world - the world to add to
         * @param pos   - the position to add to
         */
        public void add(World world, BlockPos pos) {
            for (EnumFacing e : EnumFacing.VALUES) {
                // Force chunk loading by accessing block state
                world.getBlockState(pos.offset(e, 16));
                if (isSubChunkLoaded(world, pos.offset(e, 16))) {
                    SubChunkRadiationStorage sc = getSubChunkStorage(world, pos.offset(e, 16));
                    if (sc != null && sc.pockets != null) {
                        // Clear all the neighbor's references to this sub-chunk
                        for (RadPocket p : sc.pockets) {
                            p.connectionIndices[e.getOpposite().ordinal()].clear();
                        }
                        // Sync connections to the neighbor to make it two-way
                        for (RadPocket p : pockets) {
                            List<Integer> indc = p.connectionIndices[e.ordinal()];
                            for (int idx : indc) {
                                if (idx >= 0 && idx < sc.pockets.length) {
                                    List<Integer> oppList = sc.pockets[idx].connectionIndices[e.getOpposite().ordinal()];
                                    if (oppList.contains(-1)) {
                                        oppList.remove(Integer.valueOf(-1));
                                    }
                                    if (!oppList.contains(p.index)) {
                                        oppList.add(p.index);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        public void unload() {
            for (RadPocket p : pockets) {
                parent.removeActivePocket(p);
            }
        }
    }

    public static class WorldRadiationData {
        private final Set<SubChunkKey> dirtyChunks = ConcurrentHashMap.newKeySet();
        private final Set<SubChunkKey> dirtyChunks2 = ConcurrentHashMap.newKeySet();
        private final Set<RadPocket> activePockets = ConcurrentHashMap.newKeySet();
        private final Set<SubChunkKey> deferredRebuild = ConcurrentHashMap.newKeySet();
        private final Set<ChunkPos> deferredDirty = ConcurrentHashMap.newKeySet();
        public World world;
        public Map<SubChunkKey, SubChunkRadiationStorage> data = new ConcurrentHashMap<>();
        private volatile boolean iteratingDirty = false;

        public WorldRadiationData(World world) {
            this.world = world;
        }

        public Set<RadPocket> getActivePockets() {
            if (GeneralConfig.enableDebugMode) {
                MainRegistry.logger.info("[Debug] Queried active pockets for world {}", world);
            }
            return this.activePockets;
        }

        public void addActivePocket(RadPocket radPocket) {
            this.activePockets.add(radPocket);
            if (GeneralConfig.enableDebugMode) {
                SubChunkKey chunkKey = new SubChunkKey(radPocket.getSubChunkPos().getX() >> 4, radPocket.getSubChunkPos().getZ() >> 4,
                        radPocket.getSubChunkPos().getY() >> 4);
                MainRegistry.logger.info("[Debug] Added active pocket {} (radiation: {}, accumulatedRads: {}, sealed: {}) at {} (Chunk:{}) for " +
                                "world {}", radPocket.index, radPocket.radiation.get(), radPocket.accumulatedRads.get(), radPocket.isSealed(),
                        radPocket.getSubChunkPos(), chunkKey, world);
            }
        }

        public void removeActivePocket(RadPocket radPocket) {
            this.activePockets.remove(radPocket);
            if (GeneralConfig.enableDebugMode) {
                SubChunkKey chunkKey = new SubChunkKey(radPocket.getSubChunkPos().getX() >> 4, radPocket.getSubChunkPos().getZ() >> 4,
                        radPocket.getSubChunkPos().getY() >> 4);
                MainRegistry.logger.info("[Debug] Removed active pocket {} (radiation: {}, accumulatedRads: {}, sealed: {}) at {} (Chunk:{}) for " + "world {}", radPocket.index, radPocket.radiation.get(), radPocket.accumulatedRads.get(), radPocket.isSealed(), radPocket.getSubChunkPos(), chunkKey, world);

            }
        }

        public void clearActivePockets() {
            this.activePockets.clear();
            if (GeneralConfig.enableDebugMode) {
                MainRegistry.logger.info("[Debug] Cleared active pockets for world {}", world);
            }
        }
    }
}