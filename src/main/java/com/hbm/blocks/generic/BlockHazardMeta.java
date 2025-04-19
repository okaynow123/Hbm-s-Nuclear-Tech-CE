package com.hbm.blocks.generic;

import com.hbm.hazard.HazardSystem;
import com.hbm.render.block.BlockBakeFrame;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.stream.IntStream;

/**
 * A hazard block with metadata support.
 */
public class BlockHazardMeta extends BlockMeta {

    public BlockHazardMeta(Material mat, SoundType type, String registryName, short metaCount) {
        super(mat, type, registryName, metaCount);
        if (metaCount < 0 || metaCount > 15) {
            throw new IllegalArgumentException(String.format("metaCount must be between 0 and 15 (inclusive), in %s", registryName));
        }
        this.blockFrames = assignBlockFrames(registryName);
    }

    protected BlockBakeFrame[] assignBlockFrames(String registryName) {
        return IntStream.range(0, META_COUNT)
                .mapToObj(id -> {
                    String topTexture = registryName + "_top_" + id;
                    String bottomTexture = registryName + "_bottom_" + id;
                    String sideTexture = registryName + "_normal_" + id;
                    return new BlockBakeFrame(topTexture, bottomTexture, sideTexture);
                })
                .toArray(BlockBakeFrame[]::new);
    }

    @Override
    public void onEntityWalk(World worldIn, BlockPos pos, Entity entity) {
        if (!(entity instanceof EntityLivingBase)) return;

        HazardSystem.applyHazards(this, (EntityLivingBase)entity);
    }

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entity) {
        if (!(entity instanceof EntityLivingBase)) return;

        HazardSystem.applyHazards(this, (EntityLivingBase) entity);
    }
}