package com.hbm.lib;

import net.minecraft.util.math.BlockPos;

import java.util.function.Supplier;

/**
 * Context provider for forge capabilities.
 * makes porting 1.7 com.hbm.tileentity.IConditionalInvAccess possible.
 *
 * @author mlbv
 */
public final class CapabilityContextProvider {
    private static final ThreadLocal<BlockPos> accessorPos = new ThreadLocal<>();

    /**
     * Gets the accessor's position from the current thread's context.
     * Returns the core tile's own position if no context is set.
     *
     * @param ownPos The BlockPos of the tile entity asking for the context.
     * @return The accessor's BlockPos, or ownPos if not in a proxy context.
     */
    public static BlockPos getAccessor(BlockPos ownPos) {
        BlockPos pos = accessorPos.get();
        return pos != null ? pos : ownPos;
    }

    /**
     * Executes a Supplier within a specific BlockPos context.
     * This should be called by the proxy tile entity.
     *
     * @param pos      The BlockPos of the proxy to set as context.
     * @param supplier The code to run (e.g., the call to tile.getCapability()).
     * @return The result from the supplier.
     */
    public static <T> T runWithContext(BlockPos pos, Supplier<T> supplier) {
        accessorPos.set(pos);
        try {
            return supplier.get();
        } finally {
            accessorPos.remove();
        }
    }
}
