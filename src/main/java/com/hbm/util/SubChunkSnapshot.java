package com.hbm.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockStateContainer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.Immutable;

/**
 * Immutable 16x16x16 snapshot of an ExtendedBlockStorage.
 *
 * @author mlbv
 */
@Immutable
public final class SubChunkSnapshot extends ExtendedBlockStorage {
    private static final SubChunkSnapshot[] emptyCache = new SubChunkSnapshot[16];

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
    public static SubChunkSnapshot snapshot(@NotNull ExtendedBlockStorage src) {
        final int y = src.getYLocation();
        if (src.isEmpty()) return emptyCache[y >> 4];
        final BlockStateContainer srcData = src.getData();
        BlockStateContainer copied = ChunkUtil.copyOf(srcData);
        return new SubChunkSnapshot(y, copied, src.blockRefCount, src.tickRefCount);
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
        return snapshot(ebs);
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
