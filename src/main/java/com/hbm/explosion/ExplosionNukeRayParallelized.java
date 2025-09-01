package com.hbm.explosion;

import com.hbm.config.BombConfig;
import com.hbm.config.CompatibilityConfig;
import com.hbm.interfaces.IExplosionRay;
import com.hbm.lib.Library;
import com.hbm.main.MainRegistry;
import com.hbm.util.ConcurrentBitSet;
import com.hbm.util.SubChunkKey;
import com.hbm.util.SubChunkSnapshot;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.longs.*;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLongArray;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.common.util.Constants;
import com.hbm.lib.maps.NonBlockingHashMapLong;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Threaded DDA raytracer for mk5 explosion.
 *
 * @author mlbv
 */
public class ExplosionNukeRayParallelized implements IExplosionRay {

    /**
     * If true, will read blockstates off the main thread.
     */
    private static final boolean unsafeAccess = true;
    private static final int WORLD_HEIGHT = 256;
    private static final int BITSET_SIZE = 16 * WORLD_HEIGHT * 16;
    private static final int SUBCHUNK_PER_CHUNK = WORLD_HEIGHT >> 4;
    private static final float NUKE_RESISTANCE_CUTOFF = 2_000_000F;
    private static final float INITIAL_ENERGY_FACTOR = 0.3F; // Scales crater, no impact on performance
    private static final double RESOLUTION_FACTOR = 1.0;  // Scales ray density, no impact on crater radius
    private static final int LUT_RESISTANCE_BINS = 256;
    private static final int LUT_DISTANCE_BINS = 256;
    private static final float LUT_MAX_RESISTANCE = 100.0F;
    private static final float[][] ENERGY_LOSS_LUT = new float[LUT_RESISTANCE_BINS][LUT_DISTANCE_BINS];
    private static final float DAMAGE_PER_BLOCK = 0.50F; // fallback for low resistance blocks
    private static final float DAMAGE_THRESHOLD_MULT = 1.00F;
    private static final float LOW_R_BOUND = 0.25F;
    private static final float LOW_R_PASS_LENGTH_BREAK = 0.75F;

    private static final String TAG_ALGORITHM = "algorithm";
    private static final String TAG_RAY_COUNT = "rayCount";
    private static final String TAG_IS_CONTAINED = "isContained";
    private static final String TAG_COLLECT_FINISHED = "collectDone";
    private static final String TAG_CONSOLIDATE_FINISHED = "consolidateDone";
    private static final String TAG_DESTROY_FINISHED = "destroyDone";
    private static final String TAG_DESTRUCTION_MAP = "destructionMap";
    private static final String TAG_ORDERED_CHUNKS = "orderedChunks";
    private static final String TAG_DESTROYED_LIST = "destroyedList";
    private static final String TAG_CHUNK_X = "cX";
    private static final String TAG_CHUNK_Z = "cZ";
    private static final String TAG_BITSET = "bitset";
    private static final String TAG_POS_X = "pX";
    private static final String TAG_POS_Y = "pY";
    private static final String TAG_POS_Z = "pZ";

    private static final ThreadLocal<MutableBlockPos> TL_POS = ThreadLocal.withInitial(MutableBlockPos::new);
    private static final ThreadLocal<LocalAgg> TL_LOCAL_AGG = ThreadLocal.withInitial(LocalAgg::new);

    static {
        for (int r = 0; r < LUT_RESISTANCE_BINS; r++) {
            float resistance = (r / (float) (LUT_RESISTANCE_BINS - 1)) * LUT_MAX_RESISTANCE;
            for (int d = 0; d < LUT_DISTANCE_BINS; d++) {
                float distFrac = d / (float) (LUT_DISTANCE_BINS - 1);
                ENERGY_LOSS_LUT[r][d] = (float) (Math.pow(resistance + 1.0, 3.0 * distFrac) - 1.0);
            }
        }
    }

    private final WorldServer world;
    private final double explosionX, explosionY, explosionZ;
    private final int originX, originY, originZ;
    private final int radius;
    private final NonBlockingHashMapLong<ConcurrentBitSet> destructionMap;
    private final NonBlockingHashMapLong<ChunkAgg> aggMap;
    private final NonBlockingHashMapLong<SubChunkSnapshot> snapshots;
    private final NonBlockingHashMapLong<ConcurrentLinkedQueue<RayTracerTask>> waitingRoom;
    private final LongArrayList orderedChunks;
    private LongArrayList destroyedPacked;
    private final BlockingQueue<Long> reactiveSnapQueue;
    private final LongArrayList proactiveSubChunks;
    private final AtomicInteger pendingRays;

    private final int strength;
    private final CompletableFuture<double[]> directionsFuture;
    private volatile double[] directions;

    private int algorithm;
    private int rayCount;
    private int proactiveIdx = 0;
    private ForkJoinPool pool;
    private volatile UUID detonator;
    private volatile boolean collectFinished = false;
    private volatile boolean consolidationFinished = false;
    private volatile boolean destroyFinished = false;
    private volatile boolean isContained = true;

    public ExplosionNukeRayParallelized(World world, double x, double y, double z, int strength, int radius, int algorithm) {
        this(world, x, y, z, strength, radius);
        this.algorithm = algorithm;
        initializeAndStartWorkers();
    }

    public ExplosionNukeRayParallelized(World world, double x, double y, double z, int strength, int radius) {
        this.world = (WorldServer) world; // Casted here
        this.explosionX = x;
        this.explosionY = y;
        this.explosionZ = z;
        this.originX = (int) Math.floor(x);
        this.originY = (int) Math.floor(y);
        this.originZ = (int) Math.floor(z);
        this.strength = strength;
        this.radius = radius;

        if (!CompatibilityConfig.isWarDim(world)) {
            this.collectFinished = true;
            this.consolidationFinished = true;
            this.destroyFinished = true;
            this.isContained = true;

            this.proactiveSubChunks = new LongArrayList(0);
            this.reactiveSnapQueue = new LinkedBlockingQueue<>();
            this.destructionMap = new NonBlockingHashMapLong<>(16);
            this.aggMap = new NonBlockingHashMapLong<>(16);
            this.snapshots = new NonBlockingHashMapLong<>(16);
            this.waitingRoom = new NonBlockingHashMapLong<>(16);
            this.orderedChunks = new LongArrayList(0);
            this.destroyedPacked = new LongArrayList(0);
            this.directionsFuture = CompletableFuture.completedFuture(new double[0]);
            this.pool = null;
            this.pendingRays = new AtomicInteger(0);
            return;
        }

        this.rayCount = Math.max(0, (int) (2.5 * Math.PI * strength * strength * RESOLUTION_FACTOR));
        this.pendingRays = new AtomicInteger(this.rayCount);
        this.proactiveSubChunks = getAllSubChunksPacked();
        this.reactiveSnapQueue = new LinkedBlockingQueue<>();

        int estimatedChunkCount = Math.max(16, (int) distinctChunkCount(proactiveSubChunks));
        int cap = capFor(estimatedChunkCount * 2);

        this.destructionMap = new NonBlockingHashMapLong<>(cap);
        this.aggMap = new NonBlockingHashMapLong<>(cap);

        int subChunkCount = Math.max(16, proactiveSubChunks.size());
        this.snapshots = new NonBlockingHashMapLong<>(capFor(subChunkCount));
        this.waitingRoom = new NonBlockingHashMapLong<>(capFor(subChunkCount));
        this.orderedChunks = new LongArrayList(estimatedChunkCount);
        this.destroyedPacked = new LongArrayList(1024);

        this.directionsFuture = CompletableFuture.supplyAsync(() -> generateSphereRays(rayCount));
    }

    private static int capFor(int n) {
        int c = 1;
        while (c < n) c <<= 1;
        return Math.max(16, c);
    }

    private static long distinctChunkCount(LongArrayList subkeys) {
        LongOpenHashSet set = new LongOpenHashSet();
        LongIterator it = subkeys.iterator();
        while (it.hasNext()) {
            long sk = it.nextLong();
            set.add(SubChunkKey.getPosLong(sk));
        }
        return set.size();
    }

    @SuppressWarnings({"DataFlowIssue", "deprecation"})
    private static float getNukeResistance(Block b) {
        if (b.getDefaultState().getMaterial().isLiquid()) return 0.1F;
        if (b == Blocks.SANDSTONE) return 4.0F;
        if (b == Blocks.OBSIDIAN) return 18.0F;
        return b.getExplosionResistance(null);
    }

    private static double[] generateSphereRays(int count) {
        final double[] arr = new double[count * 3];
        final double phi = Math.PI * (3.0 - Math.sqrt(5.0));
        if (count < 16384) {
            if (count <= 0) return new double[0];
            if (count == 1) {
                arr[0] = 1.0;
                return arr;
            }
            for (int i = 0, baseIndex = 0; i < count; i++, baseIndex += 3) {
                double y = 1.0 - (i / (double) (count - 1)) * 2.0;
                double r = Math.sqrt(1.0 - y * y);
                double t = phi * i;
                arr[baseIndex] = Math.cos(t) * r;
                arr[baseIndex + 1] = y;
                arr[baseIndex + 2] = Math.sin(t) * r;
            }
            return arr;
        }
        final double inv = 1.0 / (count - 1);
        new RayGenTask(arr, 0, count, phi, inv).invoke();
        return arr;
    }

    private void initializeAndStartWorkers() {
        if (rayCount <= 0) {
            this.collectFinished = true;
            this.consolidationFinished = true;
            return;
        }
        int processors = Runtime.getRuntime().availableProcessors();
        int workers = BombConfig.maxThreads <= 0 ? Math.max(1, processors + BombConfig.maxThreads) : Math.min(BombConfig.maxThreads, processors);
        this.pool = new ForkJoinPool(workers);

        final RayTracerTask mainTask = new RayTracerTask(0, rayCount);

        CompletableFuture.runAsync(() -> pool.invoke(mainTask), pool).exceptionally(ex -> {
            MainRegistry.logger.error("Nuke ray-tracing failed catastrophically.", ex);
            onAllRaysFinished();
            return null;
        });
    }

    private void onAllRaysFinished() {
        if (collectFinished) return;
        collectFinished = true;
        if (algorithm == 2) {
            pool.submit(this::runConsolidation);
        } else {
            consolidationFinished = true;
        }
    }

    private LongArrayList getAllSubChunksPacked() {
        LongArrayList keys = new LongArrayList();
        int cr = (radius + 15) >> 4;
        int minCX = (originX >> 4) - cr;
        int maxCX = (originX >> 4) + cr;
        int minCZ = (originZ >> 4) - cr;
        int maxCZ = (originZ >> 4) + cr;
        int minSubY = Math.max(0, (originY - radius) >> 4);
        int maxSubY = Math.min(SUBCHUNK_PER_CHUNK - 1, (originY + radius) >> 4);

        for (int cx = minCX; cx <= maxCX; cx++) {
            for (int cz = minCZ; cz <= maxCZ; cz++) {
                int chunkCenterX = (cx << 4) + 8;
                int chunkCenterZ = (cz << 4) + 8;
                for (int subY = minSubY; subY <= maxSubY; subY++) {
                    int chunkCenterY = (subY << 4) + 8;
                    double dx = chunkCenterX - explosionX;
                    double dy = chunkCenterY - explosionY;
                    double dz = chunkCenterZ - explosionZ;
                    if (dx * dx + dy * dy + dz * dz <= (radius + 14) * (radius + 14)) {
                        keys.add(SubChunkKey.asLong(cx, cz, subY));
                    }
                }
            }
        }
        keys.sort((Long a, Long b) -> {
            int ax = SubChunkKey.getSubX(a) - (originX >> 4);
            int az = SubChunkKey.getSubZ(a) - (originZ >> 4);
            int ay = SubChunkKey.getSubY(a) - (originY >> 4);
            int da = ax * ax + az * az + ay * ay;
            int bx = SubChunkKey.getSubX(b) - (originX >> 4);
            int bz = SubChunkKey.getSubZ(b) - (originZ >> 4);
            int by = SubChunkKey.getSubY(b) - (originY >> 4);
            int db = bx * bx + bz * bz + by * by;
            return Integer.compare(da, db);
        });
        return keys;
    }

    @Override
    public void cacheChunksTick(int timeBudgetMs) {
        if (collectFinished) return;
        final long deadline = System.nanoTime() + (timeBudgetMs * 1_000_000L);
        while (System.nanoTime() < deadline) {
            Long ck = reactiveSnapQueue.poll();
            if (ck == null) break;
            processCacheKey(ck);
        }
        while (System.nanoTime() < deadline && proactiveIdx < proactiveSubChunks.size()) {
            long ck = proactiveSubChunks.getLong(proactiveIdx++);
            processCacheKey(ck);
        }
    }

    private void processCacheKey(long packedSub) {
        if (snapshots.containsKey(packedSub)) return;
        snapshots.put(packedSub, SubChunkSnapshot.getSnapshot(world, packedSub, BombConfig.chunkloading));
        ConcurrentLinkedQueue<RayTracerTask> waiters = waitingRoom.remove(packedSub);
        if (waiters != null && pool != null && !pool.isShutdown()) {
            for (RayTracerTask task : waiters) pool.submit(task);
        }
    }

    @Override
    public void destructionTick(int timeBudgetMs) {
        if (!collectFinished || !consolidationFinished || destroyFinished) return;

        final long deadline = System.nanoTime() + timeBudgetMs * 1_000_000L;

        if (orderedChunks.isEmpty() && !destructionMap.isEmpty()) {
            destructionMap.forEachLong((ck, bs) -> orderedChunks.add(ck));
            final int ox = originX >> 4, oz = originZ >> 4;
            orderedChunks.sort((Long a, Long b) -> {
                int ax = SubChunkKey.getChunkX(a);
                int az = SubChunkKey.getChunkZ(a);
                int bx = SubChunkKey.getChunkX(b);
                int bz = SubChunkKey.getChunkZ(b);
                int da = Math.abs(ox - ax) + Math.abs(oz - az);
                int db = Math.abs(ox - bx) + Math.abs(oz - bz);
                return Integer.compare(da, db);
            });
        }

        int i = 0;
        final MutableBlockPos pos = new MutableBlockPos();
        while (i < orderedChunks.size() && System.nanoTime() < deadline) {
            final long cpLong = orderedChunks.getLong(i);
            final int cx = SubChunkKey.getChunkX(cpLong);
            final int cz = SubChunkKey.getChunkZ(cpLong);
            ConcurrentBitSet bs = destructionMap.get(cpLong);
            if (bs == null) {
                orderedChunks.removeLong(i);
                continue;
            }

            Chunk chunk = world.getChunk(cx, cz);
            ExtendedBlockStorage[] storages = chunk.getBlockStorageArray();
            boolean chunkModified = false;

            for (int subY = 0; subY < SUBCHUNK_PER_CHUNK; subY++) {
                final int startBit = (WORLD_HEIGHT - 1 - ((subY << 4) + 15)) << 8;
                final int endBit   = ((WORLD_HEIGHT - 1 - (subY << 4)) << 8) | 0xFF;
                ExtendedBlockStorage storage = storages[subY];
                if (storage == null || storage.isEmpty()) {
                    bs.clear(startBit, endBit + 1);
                    continue;
                }

                int bit = bs.nextSetBit(startBit);

                while (bit >= 0 && bit <= endBit) {
                    if (System.nanoTime() >= deadline) {
                        if (chunkModified) chunk.markDirty();
                        return;
                    }
                    int yGlobal = WORLD_HEIGHT - 1 - (bit >>> 8);
                    int xGlobal = (cx << 4) | ((bit >>> 4) & 0xF);
                    int zGlobal = (cz << 4) | (bit & 0xF);
                    int xLocal = xGlobal & 0xF;
                    int yLocal = yGlobal & 0xF;
                    int zLocal = zGlobal & 0xF;

                    IBlockState oldState = storage.get(xLocal, yLocal, zLocal);
                    if (oldState.getBlock() != Blocks.AIR) {
                        pos.setPos(xGlobal, yGlobal, zGlobal);
                        world.removeTileEntity(pos);
                        storage.set(xLocal, yLocal, zLocal, Blocks.AIR.getDefaultState());
                        chunkModified = true;
                        destroyedPacked.add(Library.blockPosToLong(xGlobal, yGlobal, zGlobal));
                    }
                    bs.clear(bit);
                    bit = bs.nextSetBit(bit + 1);
                }
            }
            if (chunkModified) chunk.markDirty();
            if (bs.isEmpty()) {
                destructionMap.remove(cpLong);
                for (int subY = 0; subY < SUBCHUNK_PER_CHUNK; subY++) snapshots.remove(SubChunkKey.asLong(cx, cz, subY));
                orderedChunks.removeLong(i);
            } else {
                i++;
            }
        }
        if (orderedChunks.isEmpty() && destructionMap.isEmpty()) {
            secondPass(world, destroyedPacked);
            destroyedPacked = null;
            destroyFinished = true;
            if (pool != null) pool.shutdown();
        }
    }

    private static void secondPass(WorldServer world, LongArrayList destroyedPacked) {
        if (destroyedPacked == null || destroyedPacked.isEmpty()) return;
        MutableBlockPos pos = new MutableBlockPos();
        Long2IntOpenHashMap sectionMaskByChunk = new Long2IntOpenHashMap();
        LongIterator it = destroyedPacked.iterator();
        while (it.hasNext()) {
            long lp = it.nextLong();
            Library.fromLong(pos, lp);
            world.notifyNeighborsOfStateChange(pos, Blocks.AIR, true);
            long key = ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4);
            int mask = sectionMaskByChunk.getOrDefault(key, 0);
            mask |= 1 << (pos.getY() >>> 4);
            sectionMaskByChunk.put(key, mask);
        }
        for (Long2IntMap.Entry e : sectionMaskByChunk.long2IntEntrySet()) {
            long longKey = e.getLongKey();
            int cx = (int) (longKey & 0xFFFFFFFFL);
            int cz = (int) (longKey >>> 32);
            int mask = e.getIntValue();
            Chunk chunk = world.getChunk(cx, cz);
            chunk.generateSkylightMap();
            chunk.resetRelightChecks();
            PlayerChunkMapEntry entry = world.getPlayerChunkMap().getEntry(cx, cz);
            if (entry != null) entry.sendPacket(new SPacketChunkData(chunk, mask));
        }
        destroyedPacked.clear();
    }

    @Override
    public boolean isComplete() {
        return collectFinished && consolidationFinished && destroyFinished;
    }

    @Override
    public boolean isContained() {
        return isContained;
    }

    @Override
    public void setDetonator(UUID detonator) {
        this.detonator = detonator;
    }

    @Override
    public void cancel() {
        this.collectFinished = true;
        this.consolidationFinished = true;
        this.destroyFinished = true;

        if (this.waitingRoom != null) this.waitingRoom.clear();
        if (this.destroyedPacked != null) this.destroyedPacked.clear();

        if (this.pool != null && !this.pool.isShutdown()) {
            this.pool.shutdownNow();
            try {
                if (!this.pool.awaitTermination(100, TimeUnit.MILLISECONDS))
                    MainRegistry.logger.error("ExplosionNukeRayParallelized ForkJoinPool did not terminate promptly on cancel.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                if (!this.pool.isShutdown()) this.pool.shutdownNow();
            }
        }
        if (this.destructionMap != null) this.destructionMap.clear();
        if (this.aggMap != null) this.aggMap.clear();
        if (this.snapshots != null) this.snapshots.clear();
        if (this.orderedChunks != null) this.orderedChunks.clear();
    }

    private void runConsolidation() {
        Arrays.stream(this.aggMap.keySetLong()).parallel().forEach(cpLong -> {
            final ChunkAgg agg = this.aggMap.get(cpLong);
            if (agg == null) return;
            agg.drainUnlocked();
            processConsolidationForChunk(cpLong, agg);
            agg.clear();
            aggMap.remove(cpLong);
        });
        aggMap.clear();
        consolidationFinished = true;
    }

    private void processConsolidationForChunk(long cpLong, ChunkAgg agg) {
        ConcurrentBitSet bitset = destructionMap.get(cpLong);
        if (bitset == null) {
            ConcurrentBitSet nb = new ConcurrentBitSet(BITSET_SIZE);
            ConcurrentBitSet prev = destructionMap.putIfAbsent(cpLong, nb);
            bitset = (prev != null) ? prev : nb;
        }
        if (agg == null) return;

        Int2DoubleOpenHashMap dmg = agg.damage;
        Int2DoubleOpenHashMap len = agg.passLen;

        if (!dmg.isEmpty()) {
            for (Int2DoubleOpenHashMap.Entry e : dmg.int2DoubleEntrySet()) {
                int bitIndex = e.getIntKey();
                double accumulatedDamage = e.getDoubleValue();
                double passLen = len.get(bitIndex);
                evaluateAndMaybeSet(cpLong, bitIndex, accumulatedDamage, passLen, bitset);
            }
        }
        if (!len.isEmpty()) {
            for (Int2DoubleOpenHashMap.Entry e : len.int2DoubleEntrySet()) {
                int bitIndex = e.getIntKey();
                if (dmg.containsKey(bitIndex)) continue;
                double passLen = e.getDoubleValue();
                evaluateAndMaybeSet(cpLong, bitIndex, 0.0, passLen, bitset);
            }
        }
    }

    private void evaluateAndMaybeSet(long cpLong, int bitIndex, double accumulatedDamage, double passLen, ConcurrentBitSet outBits) {
        int yGlobal = WORLD_HEIGHT - 1 - (bitIndex >>> 8);

        Block originalBlock;
        if (unsafeAccess) {
            int xGlobal = (SubChunkKey.getChunkX(cpLong) << 4) | ((bitIndex >>> 4) & 0xF);
            int zGlobal = (SubChunkKey.getChunkZ(cpLong) << 4) | (bitIndex & 0xF);
            MutableBlockPos pos = TL_POS.get();
            pos.setPos(xGlobal, yGlobal, zGlobal);
            originalBlock = world.getBlockState(pos).getBlock();
        } else {
            int subY = yGlobal >> 4;
            if (subY < 0) return;
            long snapshotKey = SubChunkKey.asLong(SubChunkKey.getChunkX(cpLong), SubChunkKey.getChunkZ(cpLong), subY);
            SubChunkSnapshot snap = snapshots.get(snapshotKey);
            if (snap == null || snap == SubChunkSnapshot.EMPTY) return;
            int xLocal = (bitIndex >>> 4) & 0xF;
            int zLocal = bitIndex & 0xF;
            originalBlock = snap.getBlock(xLocal, yGlobal & 0xF, zLocal);
        }

        if (originalBlock == Blocks.AIR) return;
        float resistance = getNukeResistance(originalBlock);

        boolean destroy = false;
        if (accumulatedDamage >= (resistance * DAMAGE_THRESHOLD_MULT)) {
            destroy = true;
        } else if (resistance <= LOW_R_BOUND && passLen >= LOW_R_PASS_LENGTH_BREAK) {
            destroy = true;
        }
        if (destroy) outBits.set(bitIndex);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        this.algorithm = nbt.getInteger(TAG_ALGORITHM);
        this.rayCount = nbt.getInteger(TAG_RAY_COUNT);
        this.isContained = nbt.getBoolean(TAG_IS_CONTAINED);
        this.collectFinished = nbt.getBoolean(TAG_COLLECT_FINISHED);
        this.consolidationFinished = nbt.getBoolean(TAG_CONSOLIDATE_FINISHED);
        this.destroyFinished = nbt.getBoolean(TAG_DESTROY_FINISHED);

        if (collectFinished && consolidationFinished && !destroyFinished) {
            if (nbt.hasKey(TAG_DESTRUCTION_MAP, Constants.NBT.TAG_LIST)) {
                NBTTagList list = nbt.getTagList(TAG_DESTRUCTION_MAP, Constants.NBT.TAG_COMPOUND);
                for (int i = 0; i < list.tagCount(); i++) {
                    NBTTagCompound tag = list.getCompoundTagAt(i);
                    int cx = tag.getInteger(TAG_CHUNK_X);
                    int cz = tag.getInteger(TAG_CHUNK_Z);
                    long ck = ChunkPos.asLong(cx, cz);
                    long[] bitsetData = ((NBTTagLongArray) tag.getTag(TAG_BITSET)).data;
                    this.destructionMap.put(ck, ConcurrentBitSet.fromLongArray(bitsetData, BITSET_SIZE));
                }
            }
            if (nbt.hasKey(TAG_ORDERED_CHUNKS, Constants.NBT.TAG_LIST)) {
                NBTTagList list = nbt.getTagList(TAG_ORDERED_CHUNKS, Constants.NBT.TAG_COMPOUND);
                for (int i = 0; i < list.tagCount(); i++) {
                    NBTTagCompound nbt1 = list.getCompoundTagAt(i);
                    long ck = ChunkPos.asLong(nbt1.getInteger(TAG_CHUNK_X), nbt1.getInteger(TAG_CHUNK_Z));
                    this.orderedChunks.add(ck);
                }
            }
            if (nbt.hasKey(TAG_DESTROYED_LIST, Constants.NBT.TAG_LIST)) {
                NBTTagList list = nbt.getTagList(TAG_DESTROYED_LIST, Constants.NBT.TAG_COMPOUND);
                for (int i = 0; i < list.tagCount(); i++) {
                    NBTTagCompound nbt1 = list.getCompoundTagAt(i);
                    long lp = Library.blockPosToLong(nbt1.getInteger(TAG_POS_X), nbt1.getInteger(TAG_POS_Y), nbt1.getInteger(TAG_POS_Z));
                    this.destroyedPacked.add(lp);
                }
            }
        } else if (!collectFinished || !consolidationFinished) {
            if (!CompatibilityConfig.isWarDim(world)) {
                this.collectFinished = this.consolidationFinished = this.destroyFinished = true;
                return;
            }
            this.pendingRays.set(this.rayCount);
            initializeAndStartWorkers();
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        if (!BombConfig.enableNukeNBTSaving) return;
        nbt.setInteger(TAG_ALGORITHM, this.algorithm);
        nbt.setInteger(TAG_RAY_COUNT, this.rayCount);
        nbt.setBoolean(TAG_IS_CONTAINED, this.isContained);
        nbt.setBoolean(TAG_COLLECT_FINISHED, this.collectFinished);
        nbt.setBoolean(TAG_CONSOLIDATE_FINISHED, this.consolidationFinished);
        nbt.setBoolean(TAG_DESTROY_FINISHED, this.destroyFinished);

        if (!this.collectFinished || !this.consolidationFinished || this.destroyFinished) return;
        if (!this.destructionMap.isEmpty()) {
            NBTTagList list = new NBTTagList();
            this.destructionMap.forEachLong((ck, bitset) -> {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setInteger(TAG_CHUNK_X, SubChunkKey.getChunkX(ck));
                tag.setInteger(TAG_CHUNK_Z, SubChunkKey.getChunkZ(ck));
                tag.setTag(TAG_BITSET, new NBTTagLongArray(bitset.toLongArray()));
                list.appendTag(tag);
            });
            nbt.setTag(TAG_DESTRUCTION_MAP, list);
        }
        if (!this.orderedChunks.isEmpty()) {
            NBTTagList list = new NBTTagList();
            for (int i = 0; i < orderedChunks.size(); i++) {
                long ck = orderedChunks.getLong(i);
                NBTTagCompound nbt1 = new NBTTagCompound();
                nbt1.setInteger(TAG_CHUNK_X, SubChunkKey.getChunkX(ck));
                nbt1.setInteger(TAG_CHUNK_Z, SubChunkKey.getChunkZ(ck));
                list.appendTag(nbt1);
            }
            nbt.setTag(TAG_ORDERED_CHUNKS, list);
        }
        if (this.destroyedPacked != null && !this.destroyedPacked.isEmpty()) {
            MutableBlockPos p = new MutableBlockPos();
            NBTTagList list = new NBTTagList();
            for (int i = 0; i < destroyedPacked.size(); i++) {
                long lp = destroyedPacked.getLong(i);
                Library.fromLong(p, lp);
                NBTTagCompound nbt1 = new NBTTagCompound();
                nbt1.setInteger(TAG_POS_X, p.getX());
                nbt1.setInteger(TAG_POS_Y, p.getY());
                nbt1.setInteger(TAG_POS_Z, p.getZ());
                list.appendTag(nbt1);
            }
            nbt.setTag(TAG_DESTROYED_LIST, list);
        }
    }

    private static final class IntDoubleAccumulator {
        private static final int EMPTY = Integer.MIN_VALUE;
        private int[] keys;
        private double[] vals;
        private int mask;
        private int size;
        private int resizeThreshold;

        IntDoubleAccumulator() {
            this(64);
        }

        IntDoubleAccumulator(int expected) {
            init(capFor(expected));
        }

        private static int capFor(int n) {
            int c = 1;
            while (c < n) c <<= 1;
            return Math.max(16, c);
        }

        private void init(int capacity) {
            keys = new int[capacity];
            Arrays.fill(keys, EMPTY);
            vals = new double[capacity];
            mask = capacity - 1;
            size = 0;
            resizeThreshold = (int) (capacity * 0.67);
        }

        void clear() {
            Arrays.fill(keys, EMPTY);
            Arrays.fill(vals, 0.0);
            size = 0;
        }

        void add(int key, double delta) {
            int idx = key & mask;
            while (true) {
                int k = keys[idx];
                if (k == EMPTY) {
                    keys[idx] = key;
                    vals[idx] = delta;
                    if (++size >= resizeThreshold) rehash();
                    return;
                } else if (k == key) {
                    vals[idx] += delta;
                    return;
                }
                idx = (idx + 1) & mask;
            }
        }

        int size() {
            return size;
        }

        void accumulateTo(Int2DoubleOpenHashMap map) {
            for (int i = 0; i < keys.length; i++) {
                int k = keys[i];
                if (k != EMPTY) map.addTo(k, vals[i]);
            }
        }

        private void rehash() {
            int[] oldK = keys;
            double[] oldV = vals;
            init(oldK.length << 1);
            for (int i = 0; i < oldK.length; i++) {
                int k = oldK[i];
                if (k != EMPTY) add(k, oldV[i]);
            }
        }
    }

    private static final class LocalAgg {
        final Long2ObjectOpenHashMap<IntDoubleAccumulator> localDamage = new Long2ObjectOpenHashMap<>(16);
        final Long2ObjectOpenHashMap<IntDoubleAccumulator> localLen    = new Long2ObjectOpenHashMap<>(16);

        void clear() {
            if (!localDamage.isEmpty()) {
                for (IntDoubleAccumulator acc : localDamage.values()) acc.clear();
                localDamage.clear();
            }
            if (!localLen.isEmpty()) {
                for (IntDoubleAccumulator acc : localLen.values()) acc.clear();
                localLen.clear();
            }
        }

        IntDoubleAccumulator dmg(long cp) {
            IntDoubleAccumulator acc = localDamage.get(cp);
            if (acc == null) { acc = new IntDoubleAccumulator(); localDamage.put(cp, acc); }
            return acc;
        }
        IntDoubleAccumulator len(long cp) {
            IntDoubleAccumulator acc = localLen.get(cp);
            if (acc == null) { acc = new IntDoubleAccumulator(); localLen.put(cp, acc); }
            return acc;
        }
    }

    private static final class ChunkAgg {
        final ReentrantLock lock = new ReentrantLock(false);
        final Int2DoubleOpenHashMap damage = new Int2DoubleOpenHashMap();
        final Int2DoubleOpenHashMap passLen = new Int2DoubleOpenHashMap();
        final ConcurrentLinkedQueue<IntDoubleAccumulator> qDmg = new ConcurrentLinkedQueue<>();
        final ConcurrentLinkedQueue<IntDoubleAccumulator> qLen = new ConcurrentLinkedQueue<>();

        void merge(@Nullable IntDoubleAccumulator d, @Nullable IntDoubleAccumulator l) {
            if (lock.tryLock()) {
                try {
                    drainUnlocked();
                    if (d != null && d.size() > 0) d.accumulateTo(damage);
                    if (l != null && l.size() > 0) l.accumulateTo(passLen);
                } finally {
                    lock.unlock();
                }
            } else {
                if (d != null && d.size() > 0) qDmg.add(d);
                if (l != null && l.size() > 0) qLen.add(l);
            }
        }

        void drainUnlocked() {
            IntDoubleAccumulator a;
            while ((a = qDmg.poll()) != null) a.accumulateTo(damage);
            while ((a = qLen.poll()) != null) a.accumulateTo(passLen);
        }

        void clear() {
            lock.lock();
            try {
                damage.clear();
                passLen.clear();
                qDmg.clear();
                qLen.clear();
            } finally {
                lock.unlock();
            }
        }
    }

    private static final class RayGenTask extends RecursiveAction {
        private static final int THRESHOLD = 16384;
        private final double[] arr;
        private final int start, end;
        private final double phi, inv;

        RayGenTask(double[] arr, int start, int end, double phi, double inv) {
            this.arr = arr;
            this.start = start;
            this.end = end;
            this.phi = phi;
            this.inv = inv;
        }

        @Override
        protected void compute() {
            int len = end - start;
            if (len <= THRESHOLD) {
                int baseIndex = start * 3;
                for (int i = start; i < end; i++, baseIndex += 3) {
                    double y = 1.0 - (i * inv) * 2.0;
                    double r = Math.sqrt(Math.max(0.0, 1.0 - y * y));
                    double t = phi * i;
                    arr[baseIndex    ] = Math.cos(t) * r;
                    arr[baseIndex + 1] = y;
                    arr[baseIndex + 2] = Math.sin(t) * r;
                }
            } else {
                int mid = start + (len >>> 1);
                invokeAll(new RayGenTask(arr, start, mid, phi, inv), new RayGenTask(arr, mid, end, phi, inv));
            }
        }
    }

    private class RayTracerTask extends RecursiveAction {
        private static final int THRESHOLD = 2048; // rays per task
        private static final double RAY_DIRECTION_EPSILON = 1e-6;
        private static final double PROCESSING_EPSILON = 1e-9;
        private static final float MIN_EFFECTIVE_DIST_FOR_ENERGY_CALC = 0.01f;
        private final int start, end;

        RayTracerTask(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        protected void compute() {
            if (directions == null) directions = directionsFuture.join();

            if (end - start <= THRESHOLD) {
                LocalAgg agg = TL_LOCAL_AGG.get();
                agg.clear();

                for (int i = start; i < end; i++) {
                    if (Thread.currentThread().isInterrupted()) return;
                    trace(i, agg);
                }
                if (algorithm == 2) {
                    for(Long2ObjectMap.Entry<IntDoubleAccumulator> entry : agg.localDamage.long2ObjectEntrySet()) {
                        long cp = entry.getLongKey();
                        IntDoubleAccumulator acc = entry.getValue();

                        ChunkAgg g = aggMap.get(cp);
                        if (g == null) {
                            ChunkAgg created = new ChunkAgg();
                            ChunkAgg prev = aggMap.putIfAbsent(cp, created);
                            g = (prev != null) ? prev : created;
                        }
                        g.merge(acc, null);
                    }
                    for(Long2ObjectMap.Entry<IntDoubleAccumulator> entry : agg.localLen.long2ObjectEntrySet()) {
                        long cp = entry.getLongKey();
                        IntDoubleAccumulator acc = entry.getValue();

                        ChunkAgg g = aggMap.get(cp);
                        if (g == null) {
                            ChunkAgg created = new ChunkAgg();
                            ChunkAgg prev = aggMap.putIfAbsent(cp, created);
                            g = (prev != null) ? prev : created;
                        }
                        g.merge(null, acc);
                    }
                }
            } else {
                int mid = start + (end - start) / 2;
                invokeAll(new RayTracerTask(start, mid), new RayTracerTask(mid, end));
            }
        }

        void trace(int dirIndex, LocalAgg agg) {
            boolean paused = false;
            float energy = strength * INITIAL_ENERGY_FACTOR;
            double px = explosionX;
            double py = explosionY;
            double pz = explosionZ;
            int x = originX;
            int y = originY;
            int z = originZ;
            double currentRayPosition = 0.0;

            final int baseIndex = dirIndex * 3;
            final double dirX = directions[baseIndex];
            final double dirY = directions[baseIndex + 1];
            final double dirZ = directions[baseIndex + 2];

            double absDirX = Math.abs(dirX);
            int stepX = (absDirX < RAY_DIRECTION_EPSILON) ? 0 : (dirX > 0 ? 1 : -1);
            double tDeltaX = (stepX == 0) ? Double.POSITIVE_INFINITY : 1.0 / absDirX;
            double tMaxX = (stepX == 0) ? Double.POSITIVE_INFINITY : ((stepX > 0 ? (x + 1 - px) : (px - x)) * tDeltaX);

            double absDirY = Math.abs(dirY);
            int stepY = (absDirY < RAY_DIRECTION_EPSILON) ? 0 : (dirY > 0 ? 1 : -1);
            double tDeltaY = (stepY == 0) ? Double.POSITIVE_INFINITY : 1.0 / absDirY;
            double tMaxY = (stepY == 0) ? Double.POSITIVE_INFINITY : ((stepY > 0 ? (y + 1 - py) : (py - y)) * tDeltaY);

            double absDirZ = Math.abs(dirZ);
            int stepZ = (absDirZ < RAY_DIRECTION_EPSILON) ? 0 : (dirZ > 0 ? 1 : -1);
            double tDeltaZ = (stepZ == 0) ? Double.POSITIVE_INFINITY : 1.0 / absDirZ;
            double tMaxZ = (stepZ == 0) ? Double.POSITIVE_INFINITY : ((stepZ > 0 ? (z + 1 - pz) : (pz - z)) * tDeltaZ);

            int lastCX = Integer.MIN_VALUE, lastCZ = Integer.MIN_VALUE, lastSubY = Integer.MIN_VALUE;
            long currentSubChunkKey = 0L;
            int cachedCX = Integer.MIN_VALUE, cachedCZ = Integer.MIN_VALUE;
            long cachedCPLong = 0L;

            try {
                if (energy <= 0) return;

                while (energy > 0) {
                    if (y < 0 || y >= WORLD_HEIGHT || Thread.currentThread().isInterrupted()) break;
                    if (currentRayPosition >= radius - PROCESSING_EPSILON) break;
                    Block block;

                    int cx = x >> 4;
                    int cz = z >> 4;

                    if (cx != cachedCX || cz != cachedCZ) {
                        cachedCX = cx;
                        cachedCZ = cz;
                        cachedCPLong = ChunkPos.asLong(cx, cz);
                    }

                    if (unsafeAccess && world.getChunkProvider().chunkExists(cx, cz)) {
                        MutableBlockPos pos = TL_POS.get();
                        pos.setPos(x, y, z);
                        block = world.getBlockState(pos).getBlock();
                    } else {
                        int subY = y >> 4;
                        if (cx != lastCX || cz != lastCZ || subY != lastSubY) {
                            currentSubChunkKey = SubChunkKey.asLong(cx, cz, subY);
                            lastCX = cx;
                            lastCZ = cz;
                            lastSubY = subY;
                        }
                        SubChunkSnapshot snap = snapshots.get(currentSubChunkKey);
                        if (snap == null) {
                            final boolean[] amFirst = new boolean[]{false};
                            ConcurrentLinkedQueue<RayTracerTask> waiters = waitingRoom.get(currentSubChunkKey);
                            if (waiters == null) {
                                ConcurrentLinkedQueue<RayTracerTask> created = new ConcurrentLinkedQueue<>();
                                ConcurrentLinkedQueue<RayTracerTask> prev = waitingRoom.putIfAbsent(currentSubChunkKey, created);
                                waiters = (prev != null) ? prev : created;
                                amFirst[0] = (prev == null);
                            }
                            if (amFirst[0]) reactiveSnapQueue.add(currentSubChunkKey);

                            waiters.add(new RayTracerTask(dirIndex, dirIndex + 1));
                            paused = true;
                            return;
                        }
                        if (snap == SubChunkSnapshot.EMPTY) {
                            block = Blocks.AIR;
                        } else {
                            block = snap.getBlock(x & 0xF, y & 0xF, z & 0xF);
                        }
                    }

                    double t_exit_voxel = Math.min(tMaxX, Math.min(tMaxY, tMaxZ));
                    double segmentLenInVoxel = t_exit_voxel - currentRayPosition;
                    double segmentLenForProcessing;
                    boolean stopAfterThisSegment = false;

                    if (currentRayPosition + segmentLenInVoxel > radius - PROCESSING_EPSILON) {
                        segmentLenForProcessing = Math.max(0.0, radius - currentRayPosition);
                        stopAfterThisSegment = true;
                    } else {
                        segmentLenForProcessing = segmentLenInVoxel;
                    }

                    if (block != Blocks.AIR && segmentLenForProcessing > PROCESSING_EPSILON) {
                        float resistance = getNukeResistance(block);
                        if (resistance >= NUKE_RESISTANCE_CUTOFF) {
                            energy = 0;
                        } else {
                            double distFrac = Math.max(currentRayPosition, MIN_EFFECTIVE_DIST_FOR_ENERGY_CALC) / radius;
                            double energyLoss = getEnergyLossFactor(resistance, distFrac) * segmentLenForProcessing;
                            if (energyLoss > 0) energy -= (float) energyLoss;

                            int bitIndex = ((WORLD_HEIGHT - 1 - y) << 8) | ((x & 0xF) << 4) | (z & 0xF);
                            if (algorithm == 2) {
                                double damageInc = Math.max(DAMAGE_PER_BLOCK * segmentLenForProcessing, energyLoss) * INITIAL_ENERGY_FACTOR;
                                if (damageInc > 0) agg.dmg(cachedCPLong).add(bitIndex, damageInc);
                                agg.len(cachedCPLong).add(bitIndex, segmentLenForProcessing);
                            } else if (energy > 0) {
                                if (energyLoss > 0) {
                                    ConcurrentBitSet bs = destructionMap.get(cachedCPLong);
                                    if (bs == null) {
                                        ConcurrentBitSet nbs = new ConcurrentBitSet(BITSET_SIZE);
                                        ConcurrentBitSet prev = destructionMap.putIfAbsent(cachedCPLong, nbs);
                                        bs = (prev != null) ? prev : nbs;
                                    }
                                    bs.set(bitIndex);
                                }
                            }
                        }
                    }

                    currentRayPosition = t_exit_voxel;

                    if (energy <= 0 || stopAfterThisSegment) break;
                    if (tMaxX < tMaxY) {
                        if (tMaxX < tMaxZ) {
                            x += stepX;
                            tMaxX += tDeltaX;
                        } else {
                            z += stepZ;
                            tMaxZ += tDeltaZ;
                        }
                    } else {
                        if (tMaxY < tMaxZ) {
                            y += stepY;
                            tMaxY += tDeltaY;
                        } else {
                            z += stepZ;
                            tMaxZ += tDeltaZ;
                        }
                    }
                }
                if (energy > 0) ExplosionNukeRayParallelized.this.isContained = false;

            } catch (Exception e) {
                MainRegistry.logger.error("A ray in batch {}-{} finished exceptionally: ", start, end, e);
            } finally {
                if (!paused) {
                    if (ExplosionNukeRayParallelized.this.pendingRays.decrementAndGet() == 0) {
                        onAllRaysFinished();
                    }
                }
            }
        }

        private static double getEnergyLossFactor(float resistance, double distFrac) {
            if (resistance >= NUKE_RESISTANCE_CUTOFF) return resistance;
            if (resistance <= 0) return 0.0;
            if (resistance > LUT_MAX_RESISTANCE) return Math.pow(resistance + 1.0, 3.0 * distFrac) - 1.0;
            int rBin = (int) (resistance * (LUT_RESISTANCE_BINS - 1) / LUT_MAX_RESISTANCE);
            if (rBin == 0 && resistance > 0) return Math.pow(resistance + 1.0, 3.0 * distFrac) - 1.0;
            int dBin = (int) (distFrac * (LUT_DISTANCE_BINS - 1));
            return ENERGY_LOSS_LUT[rBin][dBin];
        }
    }
}
