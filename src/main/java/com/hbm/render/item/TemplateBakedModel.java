package com.hbm.render.item;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.vecmath.Matrix4f;
import java.util.Collections;
import java.util.List;

public class TemplateBakedModel implements IBakedModel {

    public final IBakedModel originalModel;
    private final ItemOverrideList overrideList;
    private TransformType currentTransformType;

    public TemplateBakedModel(IBakedModel originalModel) {
        this.originalModel = originalModel;
        this.overrideList = new TemplateOverrideList(originalModel.getOverrides());
    }

    @Override
    public @NotNull ItemOverrideList getOverrides() {
        return this.overrideList;
    }

    @Override
    public @NotNull Pair<? extends IBakedModel, Matrix4f> handlePerspective(@NotNull TransformType cameraTransformType) {
        this.currentTransformType = cameraTransformType;
        return Pair.of(this, originalModel.handlePerspective(cameraTransformType).getRight());
    }

    @Override
    public boolean isBuiltInRenderer() {
        return this.currentTransformType == TransformType.GUI;
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
        return originalModel.getQuads(state, side, rand);
    }

    @Override
    public boolean isAmbientOcclusion() {
        return originalModel.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return originalModel.isGui3d();
    }

    @Override
    public @NotNull TextureAtlasSprite getParticleTexture() {
        return originalModel.getParticleTexture();
    }

    private static class TemplateOverrideList extends ItemOverrideList {

        private final ItemOverrideList originalOverrides;

        TemplateOverrideList(ItemOverrideList original) {
            super(Collections.emptyList());
            this.originalOverrides = original;
        }

        @NotNull
        @Override
        public IBakedModel handleItemState(@NotNull IBakedModel originalModel, @NotNull ItemStack stack, @Nullable World world,
                                           @Nullable EntityLivingBase entity) {
            TemplateItemRenderer.stackToRender = stack;
            IBakedModel newModel = this.originalOverrides.handleItemState(originalModel, stack, world, entity);
            if (newModel != originalModel) {
                TemplateBakedModel newWrapper = new TemplateBakedModel(newModel);
                if (originalModel instanceof TemplateBakedModel) {
                    newWrapper.currentTransformType = ((TemplateBakedModel) originalModel).currentTransformType;
                }

                return newWrapper;
            }
            return originalModel;
        }
    }
}