package com.hbm.interfaces;

import com.hbm.handler.ClimbableRegistry;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Implement this on any TE (or other object) that exposes a climbable AABB.
 * <p>
 * Lifecycle:
 *  - Call {@link #registerClimbable()} in onLoad(). Do not call on validate()!
 *  - Call {@link #unregisterClimbable()} in invalidate()/onChunkUnload().
 *  - If the climb AABB or anchor changes at runtime, call {@link ClimbableRegistry#refresh(IClimbable)}.
 */
public interface IClimbable {

    // implemented by TileEntity
    @NotNull
    World getWorld();

    // implemented by TileEntity
    @NotNull
    BlockPos getPos();

    boolean isEntityInClimbAABB(@NotNull EntityLivingBase entity);

    /**
     * AABB used for *indexing* across chunks. If null, the registry will index in the anchor chunk only.
     * Return your real climb box (world-space) for best coverage.
     */
    @Nullable
    AxisAlignedBB getClimbAABBForIndexing();

    /** Convenience wrappers for registration. */
    default void registerClimbable()   { ClimbableRegistry.register(this); }
    default void unregisterClimbable() { ClimbableRegistry.unregister(this); }
}
