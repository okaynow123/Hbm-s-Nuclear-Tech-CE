package com.hbm.util;

import com.hbm.lib.Library;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.BitArray;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.*;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.concurrent.Immutable;

import static com.hbm.lib.UnsafeHolder.U;

/**
 * Immutable 16x16x16 snapshot of an ExtendedBlockStorage.
 *
 * @author mlbv
 */
@Immutable
public final class SubChunkSnapshot extends ExtendedBlockStorage {
    private static final SubChunkSnapshot[] emptyCache = new SubChunkSnapshot[16];
    private static final ThreadLocal<ByteBuf> TL_PALETTE_BUF = ThreadLocal.withInitial(() -> Unpooled.buffer(256));
    private static final long ARR_BASE = U.arrayBaseOffset(ExtendedBlockStorage[].class);
    private static final long ARR_SCALE = U.arrayIndexScale(ExtendedBlockStorage[].class);

    static {
        for (int i = 0; i < 16; i++) {
            emptyCache[i] = new SubChunkSnapshot(i * 16, 0, 0);
        }
    }

    private SubChunkSnapshot(int yBase, @NotNull BlockStateContainer copiedData, int blockRefCount, int tickRefCount) {
        this(yBase, blockRefCount, tickRefCount);
        this.data = copiedData;
    }

    private SubChunkSnapshot(int yBase, int blockRefCount, int tickRefCount) {
        super(yBase, false);
        this.blockRefCount = blockRefCount;
        this.tickRefCount = tickRefCount;
        this.blockLight = null;
        this.skyLight = null;
    }

    /**
     * Make an immutable snapshot from an existing (live) EBS.
     * Always returns a snapshot (even if all-air).
     */
    @NotNull
    public static SubChunkSnapshot of(@NotNull ExtendedBlockStorage src) {
        final int y = src.getYLocation();
        if (src.isEmpty()) return emptyCache[y >> 4];
        final BlockStateContainer srcData = src.getData();
        BlockStateContainer copied = copyOf(srcData);
        return new SubChunkSnapshot(y, copied, src.blockRefCount, src.tickRefCount);
    }

    @NotNull
    @Contract("_ -> new")
    public static BlockStateContainer copyOf(@NotNull BlockStateContainer srcData) {
        final int bits = srcData.bits;
        final BitArray srcStorage = srcData.storage;
        final IBlockStatePalette srcPalette = srcData.palette;
        final BlockStateContainer copied = new BlockStateContainer();
        copied.bits = bits;
        if (bits <= 4) copied.palette = new BlockStatePaletteLinear(bits, copied);
        else if (bits <= 8) copied.palette = new BlockStatePaletteHashMap(bits, copied);
        else copied.palette = BlockStateContainer.REGISTRY_BASED_PALETTE;
        if (bits <= 8) {
            final ByteBuf raw = TL_PALETTE_BUF.get();
            raw.clear();
            raw.ensureWritable(srcPalette.getSerializedSize());
            final PacketBuffer buf = new PacketBuffer(raw);
            srcPalette.write(buf);
            buf.readerIndex(0);
            copied.palette.read(buf);
            raw.clear();
        }
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
    public static ExtendedBlockStorage copyAndCarve(@NotNull WorldServer world, int chunkX, int chunkZ, int subY, ExtendedBlockStorage src,
                                                    ConcurrentBitSet bs, LongArrayList teRemovals, LongOpenHashSet edgeOut) {
        if (src == Chunk.NULL_BLOCK_STORAGE || src.isEmpty()) return Chunk.NULL_BLOCK_STORAGE;
        final boolean hasSky = world.provider.hasSkyLight();
        final int height = world.getHeight();
        final ExtendedBlockStorage dst = new ExtendedBlockStorage(src.getYLocation(), hasSky);
        dst.data = SubChunkSnapshot.copyOf(src.getData());
        if (!(src instanceof SubChunkSnapshot)) {
            dst.blockLight = new NibbleArray(src.getBlockLight().getData().clone());
            dst.skyLight = hasSky ? new NibbleArray(src.getSkyLight().getData().clone()) : null;
        }
        dst.blockRefCount = src.blockRefCount;
        dst.tickRefCount = src.tickRefCount;
        ExtendedBlockStorage[] selfStorages = null;
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
                    if (selfStorages == null) selfStorages = world.getChunk(chunkX, chunkZ).getBlockStorageArray();
                    ExtendedBlockStorage below = selfStorages[subY - 1];
                    if (below != Chunk.NULL_BLOCK_STORAGE && !below.isEmpty()) {
                        IBlockState nb = below.get(xLocal, 15, zLocal);
                        if (nb.getBlock() != Blocks.AIR) touchesOutsideNonAir = true;
                    }
                }
                if (!touchesOutsideNonAir && yLocal == 15 && subY < (height >> 4) - 1) {
                    if (selfStorages == null) selfStorages = world.getChunk(chunkX, chunkZ).getBlockStorageArray();
                    ExtendedBlockStorage above = selfStorages[subY + 1];
                    if (above != Chunk.NULL_BLOCK_STORAGE && !above.isEmpty()) {
                        IBlockState nb = above.get(xLocal, 0, zLocal);
                        if (nb.getBlock() != Blocks.AIR) touchesOutsideNonAir = true;
                    }
                }
                if (!touchesOutsideNonAir && xLocal == 0) {
                    int nCX = chunkX - 1;
                    if (world.getChunkProvider().chunkExists(nCX, chunkZ)) {
                        if (storagesNegX == null) storagesNegX = world.getChunk(nCX, chunkZ).getBlockStorageArray();
                        ExtendedBlockStorage n = storagesNegX[subY];
                        if (n != Chunk.NULL_BLOCK_STORAGE && !n.isEmpty()) {
                            IBlockState nb = n.get(15, yLocal, zLocal);
                            if (nb.getBlock() != Blocks.AIR) touchesOutsideNonAir = true;
                        }
                    }
                }
                if (!touchesOutsideNonAir && xLocal == 15) {
                    int nCX = chunkX + 1;
                    if (world.getChunkProvider().chunkExists(nCX, chunkZ)) {
                        if (storagesPosX == null) storagesPosX = world.getChunk(nCX, chunkZ).getBlockStorageArray();
                        ExtendedBlockStorage n = storagesPosX[subY];
                        if (n != Chunk.NULL_BLOCK_STORAGE && !n.isEmpty()) {
                            IBlockState nb = n.get(0, yLocal, zLocal);
                            if (nb.getBlock() != Blocks.AIR) touchesOutsideNonAir = true;
                        }
                    }
                }
                if (!touchesOutsideNonAir && zLocal == 0) {
                    int nCZ = chunkZ - 1;
                    if (world.getChunkProvider().chunkExists(chunkX, nCZ)) {
                        if (storagesNegZ == null) storagesNegZ = world.getChunk(chunkX, nCZ).getBlockStorageArray();
                        ExtendedBlockStorage n = storagesNegZ[subY];
                        if (n != Chunk.NULL_BLOCK_STORAGE && !n.isEmpty()) {
                            IBlockState nb = n.get(xLocal, yLocal, 15);
                            if (nb.getBlock() != Blocks.AIR) touchesOutsideNonAir = true;
                        }
                    }
                }
                if (!touchesOutsideNonAir && zLocal == 15) {
                    int nCZ = chunkZ + 1;
                    if (world.getChunkProvider().chunkExists(chunkX, nCZ)) {
                        if (storagesPosZ == null) storagesPosZ = world.getChunk(chunkX, nCZ).getBlockStorageArray();
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
                dst.set(xLocal, yLocal, zLocal, Blocks.AIR.getDefaultState()); // updates ref counts
            }
            bit = bs.nextSetBit(bit + 1);
        }
        return dst;
    }

    /**
     * Creates a SubChunkSnapshot.
     *
     * @param world           The World instance from which to retrieve the chunk.
     * @param packed          The packed SubChunkKey.
     * @param allowGeneration Whether to generate chunks. If false, attempting to retrieve a snapshot of a chunk that doesn't exist would return an
     *                        empty snapshot.
     * @return A SubChunkSnapshot containing the palette and block data for the sub-chunk,
     * or an empty snapshot if the region contains only air.
     */
    @NotNull
    public static SubChunkSnapshot snapshot(@NotNull World world, long packed, boolean allowGeneration) {
        int cx = SubChunkKey.getSubX(packed);
        int cz = SubChunkKey.getSubZ(packed);
        int sy = SubChunkKey.getSubY(packed);
        return snapshot(world, cx, cz, sy, allowGeneration);
    }

    /**
     * Snapshot by world + key.
     */
    @NotNull
    public static SubChunkSnapshot snapshot(@NotNull World world, @NotNull SubChunkKey key, boolean allowGeneration) {
        return snapshot(world, key.getChunkXPos(), key.getChunkZPos(), key.getSubY(), allowGeneration);
    }

    /**
     * Snapshot by explicit coords.
     *
     * @return immutable EBS snapshot; or {@code null} if chunk wasn't generated and generation disallowed.
     * @throws ArrayIndexOutOfBoundsException if subY is out of range for the chunk's EBS array.
     */
    @NotNull
    public static SubChunkSnapshot snapshot(@NotNull World world, int chunkXPos, int chunkZPos, int subY, boolean allowGeneration) {
        if (!world.getChunkProvider().isChunkGeneratedAt(chunkXPos, chunkZPos) && !allowGeneration) return emptyCache[subY];
        Chunk chunk = world.getChunkProvider().provideChunk(chunkXPos, chunkZPos);
        ExtendedBlockStorage[] arr = chunk.getBlockStorageArray();
        ExtendedBlockStorage ebs = arr[subY];
        if (ebs == Chunk.NULL_BLOCK_STORAGE) return emptyCache[subY];
        return of(ebs);
    }

    public static boolean compareAndSwap(ExtendedBlockStorage expect, ExtendedBlockStorage update, ExtendedBlockStorage[] arr, int subY) {
        final long off = ARR_BASE + ((long) subY) * ARR_SCALE;
        return U.compareAndSwapObject(arr, off, expect, update);
    }

    @Override
    public void set(int x, int y, int z, IBlockState state) {
        throw new UnsupportedOperationException("SubChunkSnapshot is immutable");
    }

    @Override
    public void setSkyLight(int x, int y, int z, int value) {
        throw new UnsupportedOperationException("Lighting not stored in SubChunkSnapshot");
    }

    @Override
    public int getSkyLight(int x, int y, int z) {
        throw new UnsupportedOperationException("Lighting not stored in SubChunkSnapshot");
    }

    @Override
    public void setBlockLight(int x, int y, int z, int value) {
        throw new UnsupportedOperationException("Lighting not stored in SubChunkSnapshot");
    }

    @Override
    public int getBlockLight(int x, int y, int z) {
        throw new UnsupportedOperationException("Lighting not stored in SubChunkSnapshot");
    }

    @Override
    public void recalculateRefCounts() {
        throw new UnsupportedOperationException("Snapshot refcounts are fixed");
    }

    @Override
    public NibbleArray getBlockLight() {
        throw new UnsupportedOperationException("Lighting not stored in SubChunkSnapshot");
    }

    @Override
    public void setBlockLight(NibbleArray newBlocklightArray) {
        throw new UnsupportedOperationException("Lighting not stored in SubChunkSnapshot");
    }

    @Override
    public NibbleArray getSkyLight() {
        throw new UnsupportedOperationException("Lighting not stored in SubChunkSnapshot");
    }

    @Override
    public void setSkyLight(NibbleArray newSkylightArray) {
        throw new UnsupportedOperationException("Lighting not stored in SubChunkSnapshot");
    }
}
