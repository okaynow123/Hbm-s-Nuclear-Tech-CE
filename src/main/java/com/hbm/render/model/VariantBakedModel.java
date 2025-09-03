package com.hbm.render.model;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import java.util.List;

public class VariantBakedModel implements IBakedModel {
    private final IBakedModel[] variants;
    private final IBakedModel fallback;
    private final IUnlistedProperty<Integer> VARIANT;

    public VariantBakedModel(IBakedModel[] variants, IBakedModel fallback, IUnlistedProperty<Integer> VARIANT) {
        this.variants = variants;
        this.fallback = fallback;
        this.VARIANT =  VARIANT;
    }

    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
        if (state instanceof IExtendedBlockState) {
            int variant = ((IExtendedBlockState) state).getValue(VARIANT);
                return variants[variant].getQuads(state, side, rand);
        }
        return fallback.getQuads(state, side, rand);
    }

    @Override public boolean isAmbientOcclusion() { return fallback.isAmbientOcclusion(); }
    @Override public boolean isGui3d() { return fallback.isGui3d(); }
    @Override public boolean isBuiltInRenderer() { return fallback.isBuiltInRenderer(); }
    @Override public TextureAtlasSprite getParticleTexture() { return fallback.getParticleTexture(); }
    @Override public ItemCameraTransforms getItemCameraTransforms() { return fallback.getItemCameraTransforms(); }

    @Override public ItemOverrideList getOverrides() { return fallback.getOverrides(); }
}


