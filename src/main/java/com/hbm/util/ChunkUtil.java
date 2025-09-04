package com.hbm.util;

import com.hbm.lib.Library;
import com.hbm.lib.UnsafeHolder;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongCollection;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BitArray;
import net.minecraft.util.IntIdentityHashBiMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.*;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.hbm.lib.UnsafeHolder.U;

public final class ChunkUtil {

    private static final long ARR_BASE = U.arrayBaseOffset(ExtendedBlockStorage[].class);
    private static final long ARR_SCALE = U.arrayIndexScale(ExtendedBlockStorage[].class);
    private static final long UNLOAD_QUEUED_OFFSET = UnsafeHolder.fieldOffset(Chunk.class, "unloadQueued", "field_189550_d");
    private static final long BSL_STATES_OFFSET = UnsafeHolder.fieldOffset(BlockStatePaletteLinear.class, "states", "field_186042_a");
    private static final long BSL_ARRAY_SIZE_OFFSET = UnsafeHolder.fieldOffset(BlockStatePaletteLinear.class, "arraySize", "field_186045_d");
    private static final long BSHM_MAP_OFFSET = UnsafeHolder.fieldOffset(BlockStatePaletteHashMap.class, "statePaletteMap", "field_186046_a");
    private static final long IIHBM_VALUES_OFFSET = UnsafeHolder.fieldOffset(IntIdentityHashBiMap.class, "values", "field_186818_b");
    private static final long IIHBM_INTKEYS_OFFSET = UnsafeHolder.fieldOffset(IntIdentityHashBiMap.class, "intKeys", "field_186819_c");
    private static final long IIHBM_BYID_OFFSET = UnsafeHolder.fieldOffset(IntIdentityHashBiMap.class, "byId", "field_186820_d");
    private static final long IIHBM_NEXTFREE_OFFSET = UnsafeHolder.fieldOffset(IntIdentityHashBiMap.class, "nextFreeIndex", "field_186821_e");
    private static final long IIHBM_MAPSIZE_OFFSET = UnsafeHolder.fieldOffset(IntIdentityHashBiMap.class, "mapSize", "field_186822_f");

    private static final ThreadLocal<BlockPos.MutableBlockPos> TL_POS = ThreadLocal.withInitial(BlockPos.MutableBlockPos::new);
    private static final IBlockState AIR_DEFAULT_STATE = Blocks.AIR.getDefaultState();

    private ChunkUtil() {
    }

    /**
     * @return a deep copy of the given {@link BlockStateContainer}
     */
    @NotNull
    @Contract("_ -> new")
    public static BlockStateContainer copyOf(@NotNull BlockStateContainer srcData) {
        final int bits = srcData.bits;
        final IBlockStatePalette srcPalette = srcData.palette;
        final BlockStateContainer copied = new BlockStateContainer();
        copied.bits = bits;

        if (bits <= 4) {
            copied.palette = new BlockStatePaletteLinear(bits, copied);
            final int arraySize = U.getInt(srcPalette, BSL_ARRAY_SIZE_OFFSET);
            U.putInt(copied.palette, BSL_ARRAY_SIZE_OFFSET, arraySize);
            final IBlockState[] srcStates = (IBlockState[]) U.getObject(srcPalette, BSL_STATES_OFFSET);
            final IBlockState[] dstStates = (IBlockState[]) U.getObject(copied.palette, BSL_STATES_OFFSET);
            System.arraycopy(srcStates, 0, dstStates, 0, arraySize);
        } else if (bits <= 8) {
            copied.palette = new BlockStatePaletteHashMap(bits, copied);
            final Object srcMap = U.getObject(srcPalette, BSHM_MAP_OFFSET);
            final Object dstMap = U.getObject(copied.palette, BSHM_MAP_OFFSET);

            final int nextFree = U.getInt(srcMap, IIHBM_NEXTFREE_OFFSET);
            final int mapSize = U.getInt(srcMap, IIHBM_MAPSIZE_OFFSET);
            U.putInt(dstMap, IIHBM_NEXTFREE_OFFSET, nextFree);
            U.putInt(dstMap, IIHBM_MAPSIZE_OFFSET, mapSize);

            final Object[] srcValues = (Object[]) U.getObject(srcMap, IIHBM_VALUES_OFFSET);
            final int[] srcIntKeys = (int[]) U.getObject(srcMap, IIHBM_INTKEYS_OFFSET);
            final Object[] srcById = (Object[]) U.getObject(srcMap, IIHBM_BYID_OFFSET);

            U.putObject(dstMap, IIHBM_VALUES_OFFSET, srcValues.clone());
            U.putObject(dstMap, IIHBM_INTKEYS_OFFSET, srcIntKeys.clone());
            U.putObject(dstMap, IIHBM_BYID_OFFSET, srcById.clone());
        } else {
            copied.palette = BlockStateContainer.REGISTRY_BASED_PALETTE;
        }

        final BitArray srcStorage = srcData.storage;
        copied.storage = new BitArray(bits, 4096);
        final long[] srcLongs = srcStorage.getBackingLongArray();
        final long[] dstLongs = copied.storage.getBackingLongArray();
        System.arraycopy(srcLongs, 0, dstLongs, 0, srcLongs.length);
        return copied;
    }

    /**
     * @return null when src is null or empty
     */
    @Nullable
    @Contract(mutates = "param7, param8") // teRemovals and edgeOut
    public static ExtendedBlockStorage copyAndCarve(@NotNull WorldServer world, int chunkX, int chunkZ, int subY, ExtendedBlockStorage[] srcs,
                                                    ConcurrentBitSet bs, LongCollection teRemovals, LongCollection edgeOut) {
        ExtendedBlockStorage src = srcs[subY];
        if (src == Chunk.NULL_BLOCK_STORAGE || src.isEmpty()) return Chunk.NULL_BLOCK_STORAGE;
        final boolean hasSky = world.provider.hasSkyLight();
        final int height = world.getHeight();
        final ExtendedBlockStorage dst = new ExtendedBlockStorage(src.getYLocation(), hasSky);
        copyEBS(hasSky, src, dst);
        ExtendedBlockStorage[] storagesNegX = null, storagesPosX = null, storagesNegZ = null, storagesPosZ = null;
        final int startBit = (height - 1 - ((subY << 4) + 15)) << 8;
        final int endBit = ((height - 1 - (subY << 4)) << 8) | 0xFF;
        int bit = bs.nextSetBit(startBit);
        while (bit >= 0 && bit <= endBit) {
            final int yGlobal = height - 1 - (bit >>> 8);
            final int xGlobal = (chunkX << 4) | ((bit >>> 4) & 0xF);
            final int zGlobal = (chunkZ << 4) | (bit & 0xF);

            final int xLocal = xGlobal & 0xF;
            final int yLocal = yGlobal & 0xF;
            final int zLocal = zGlobal & 0xF;

            final IBlockState old = dst.get(xLocal, yLocal, zLocal);
            final Block oldBlock = old.getBlock();
            if (oldBlock != Blocks.AIR) {
                final long packed = Library.blockPosToLong(xGlobal, yGlobal, zGlobal);
                if (oldBlock.hasTileEntity(old)) teRemovals.add(packed);
                boolean touchesOutsideNonAir = false;
                if (yLocal == 0 && subY > 0) {
                    ExtendedBlockStorage below = srcs[subY - 1];
                    if (below != Chunk.NULL_BLOCK_STORAGE && !below.isEmpty()) {
                        IBlockState nb = below.get(xLocal, 15, zLocal);
                        if (nb.getBlock() != Blocks.AIR) touchesOutsideNonAir = true;
                    }
                }
                if (!touchesOutsideNonAir && yLocal == 15 && subY < (height >> 4) - 1) {
                    ExtendedBlockStorage above = srcs[subY + 1];
                    if (above != Chunk.NULL_BLOCK_STORAGE && !above.isEmpty()) {
                        IBlockState nb = above.get(xLocal, 0, zLocal);
                        if (nb.getBlock() != Blocks.AIR) touchesOutsideNonAir = true;
                    }
                }
                if (!touchesOutsideNonAir && xLocal == 0) {
                    int nCX = chunkX - 1;
                    if (storagesNegX == null) storagesNegX = getLoadedEBS(world, ChunkPos.asLong(nCX, chunkZ));
                    if (storagesNegX != null) {
                        ExtendedBlockStorage n = storagesNegX[subY];
                        if (n != Chunk.NULL_BLOCK_STORAGE && !n.isEmpty()) {
                            IBlockState nb = n.get(15, yLocal, zLocal);
                            if (nb.getBlock() != Blocks.AIR) touchesOutsideNonAir = true;
                        }
                    }
                }
                if (!touchesOutsideNonAir && xLocal == 15) {
                    int nCX = chunkX + 1;
                    if (storagesPosX == null) storagesPosX = getLoadedEBS(world, ChunkPos.asLong(nCX, chunkZ));
                    if (storagesPosX != null) {
                        ExtendedBlockStorage n = storagesPosX[subY];
                        if (n != Chunk.NULL_BLOCK_STORAGE && !n.isEmpty()) {
                            IBlockState nb = n.get(0, yLocal, zLocal);
                            if (nb.getBlock() != Blocks.AIR) touchesOutsideNonAir = true;
                        }
                    }
                }
                if (!touchesOutsideNonAir && zLocal == 0) {
                    int nCZ = chunkZ - 1;
                    if (storagesNegZ == null) storagesNegZ = getLoadedEBS(world, ChunkPos.asLong(chunkX, nCZ));
                    if (storagesNegZ != null) {
                        ExtendedBlockStorage n = storagesNegZ[subY];
                        if (n != Chunk.NULL_BLOCK_STORAGE && !n.isEmpty()) {
                            IBlockState nb = n.get(xLocal, yLocal, 15);
                            if (nb.getBlock() != Blocks.AIR) touchesOutsideNonAir = true;
                        }
                    }
                }
                if (!touchesOutsideNonAir && zLocal == 15) {
                    int nCZ = chunkZ + 1;
                    if (storagesPosZ == null) storagesPosZ = getLoadedEBS(world, ChunkPos.asLong(chunkX, nCZ));
                    if (storagesPosZ != null) {
                        ExtendedBlockStorage n = storagesPosZ[subY];
                        if (n != Chunk.NULL_BLOCK_STORAGE && !n.isEmpty()) {
                            IBlockState nb = n.get(xLocal, yLocal, 0);
                            if (nb.getBlock() != Blocks.AIR) touchesOutsideNonAir = true;
                        }
                    }
                }
                if (touchesOutsideNonAir) {
                    edgeOut.add(packed);
                }
                dst.set(xLocal, yLocal, zLocal, AIR_DEFAULT_STATE); // updates ref counts
            }
            bit = bs.nextSetBit(bit + 1);
        }
        return dst;
    }

    public static boolean compareAndSwap(ExtendedBlockStorage expect, ExtendedBlockStorage update, ExtendedBlockStorage[] arr, int subY) {
        final long off = ARR_BASE + ((long) subY) * ARR_SCALE;
        return U.compareAndSwapObject(arr, off, expect, update);
    }

    @Nullable
    public static Chunk getLoadedChunk(@NotNull WorldServer world, long chunkPos) {
        try {
            Chunk chunk = world.getChunkProvider().loadedChunks.get(chunkPos);
            if (chunk != null) U.putBooleanVolatile(chunk, UNLOAD_QUEUED_OFFSET, false);
            return chunk;
        } catch (Exception ignored) {
            // ChunkProviderServer#loadedChunks is a Long2ObjectOpenHashMap, which isn't thread-safe
            // This is extremely unlikely to happen for small to medium-sized blasts
            // with radius = 1000, strength = 2000 for ExplosionNukeRayParallelized I can only observe two ArrayIndexOutOfBoundsException thrown
            // that's only 0.0636 ppm
            return null;
        }
    }

    @Nullable
    public static ExtendedBlockStorage[] getLoadedEBS(WorldServer world, long chunkPos) {
        try {
            Chunk chunk = world.getChunkProvider().loadedChunks.get(chunkPos);
            if (chunk == null) return null;
            U.putBooleanVolatile(chunk, UNLOAD_QUEUED_OFFSET, false);
            return chunk.getBlockStorageArray();
        } catch (Exception ignored) {
            return null;
        }
    }

    public static int getChunkPosX(long chunkKey) {
        return (int) (chunkKey & 0xFFFFFFFFL);
    }

    public static int getChunkPosZ(long chunkKey) {
        return (int) ((chunkKey >>> 32) & 0xFFFFFFFFL);
    }

    /**
     * Return a modified copy of a chunk.
     *
     * @param chunk            The chunk to copy.
     * @param newStates        A map of packed BlockPos to new block states.
     * @param teRemovals       A collection of tile entity positions to remove.
     * @param teAdditions      A collection of tile entity positions to add.
     * @param blockPosToNotify A collection of All the blocks that need to notify all its neighbours.
     * @return A modified copy of the chunk's storageArrays. Can have null elements if the section wasn't modified, or null if none was modified.
     */
    @Nullable
    public static ExtendedBlockStorage @Nullable [] copyAndModify(@NotNull Chunk chunk, @NotNull Long2ObjectMap<IBlockState> newStates,
                                                                  @Nullable LongCollection teRemovals, @Nullable LongCollection teAdditions,
                                                                  @Nullable LongCollection blockPosToNotify) {

        if (newStates.isEmpty()) return null;
        final ExtendedBlockStorage[] srcs = chunk.getBlockStorageArray();
        final int sections = srcs.length;
        final ExtendedBlockStorage[] out = new ExtendedBlockStorage[sections];
        final WorldServer world = (WorldServer) chunk.getWorld();
        final boolean hasSky = world.provider.hasSkyLight();
        final int height = world.getHeight();
        final ChunkPos cpos = chunk.getPos();
        final int chunkX = cpos.x;
        final int chunkZ = cpos.z;

        boolean anyChange = false;
        for (Long2ObjectMap.Entry<IBlockState> e : newStates.long2ObjectEntrySet()) {
            final long packed = e.getLongKey();
            final int x = Library.getBlockPosX(packed);
            final int y = Library.getBlockPosY(packed);
            final int z = Library.getBlockPosZ(packed);
            if ((x >> 4) != chunkX || (z >> 4) != chunkZ) continue;
            if (y < 0 || y >= height) continue;
            final IBlockState newState = (e.getValue() == null) ? AIR_DEFAULT_STATE : e.getValue();
            final int subY = y >> 4;
            final int lx = x & 15, ly = y & 15, lz = z & 15;
            final ExtendedBlockStorage src = srcs[subY];
            final IBlockState oldState = (src != Chunk.NULL_BLOCK_STORAGE && !src.isEmpty()) ? src.get(lx, ly, lz) : AIR_DEFAULT_STATE;
            if (oldState == newState) continue;

            ExtendedBlockStorage dst = out[subY];
            if (dst == null) {
                if (src != Chunk.NULL_BLOCK_STORAGE && !src.isEmpty()) {
                    dst = new ExtendedBlockStorage(src.getYLocation(), hasSky);
                    copyEBS(hasSky, src, dst);
                } else {
                    if (newState.getBlock() == Blocks.AIR) continue;
                    dst = new ExtendedBlockStorage(subY << 4, hasSky);
                }
                out[subY] = dst;
            }
            dst.set(lx, ly, lz, newState);
            anyChange = true;
            if (blockPosToNotify != null) blockPosToNotify.add(packed);
            final BlockPos.MutableBlockPos pos = TL_POS.get().setPos(x, y, z);
            final boolean newHasTE = newState.getBlock().hasTileEntity(newState);
            final boolean oldHasTE = oldState.getBlock().hasTileEntity(oldState);
            final TileEntity te = chunk.tileEntities.get(pos); // bad practice, hope this won't break
            if (te != null && !te.isInvalid()) {
                final boolean replace = te.shouldRefresh(world, pos, oldState, newState);
                if (replace) {
                    if (teRemovals != null) teRemovals.add(packed);
                    if (teAdditions != null && newHasTE) teAdditions.add(packed);
                } else {
                    if (teRemovals != null && !newHasTE) teRemovals.add(packed);
                }
            } else {
                if (teRemovals != null && oldHasTE) teRemovals.add(packed);
                if (teAdditions != null && newHasTE) teAdditions.add(packed);
            }
        }
        return anyChange ? out : null;
    }

    private static void copyEBS(boolean hasSky, ExtendedBlockStorage src, ExtendedBlockStorage dst) {
        dst.data = copyOf(src.getData());
        if (!(src instanceof SubChunkSnapshot)) {
            dst.blockLight = new NibbleArray(src.getBlockLight().getData().clone());
            dst.skyLight = hasSky ? new NibbleArray(src.getSkyLight().getData().clone()) : null;
        }
        dst.blockRefCount = src.blockRefCount;
        dst.tickRefCount = src.tickRefCount;
    }
}
