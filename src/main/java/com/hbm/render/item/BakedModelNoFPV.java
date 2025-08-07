package com.hbm.render.item;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

import java.util.Collections;
import java.util.List;
// Th3_Sl1ze: the funny fucking thing. for now it translates only null transformtype meaning it will render fpv model for every transform type
// and if you try somehow translating the type it will render absolute nonsense dogshit without animations at all
// how? do I fucking know?
// TODO help me
public class BakedModelNoFPV implements IBakedModel {

    private TEISRBase renderer;

    public BakedModelNoFPV(TEISRBase renderer) {
        this.renderer = renderer;
    }

    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
        return Collections.emptyList();
    }
    @Override
    public boolean isAmbientOcclusion() {
        return (renderer.type != ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND && renderer.type != ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND) ? false : renderer.itemModel.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return (renderer.type != ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND && renderer.type != ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND) ? false : renderer.itemModel.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return (renderer.type != ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND && renderer.type != ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND) ? true : renderer.itemModel.isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return renderer.itemModel.getParticleTexture();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }
}
