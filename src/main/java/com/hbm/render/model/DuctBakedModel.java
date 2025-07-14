package com.hbm.render.model;

import com.google.common.collect.ImmutableList;
import com.hbm.blocks.network.FluidDuctBox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.util.vector.Vector3f;

import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SideOnly(Side.CLIENT)
public class DuctBakedModel implements IBakedModel {

    private final int meta;

    public DuctBakedModel(int meta) {
        this.meta = meta;
    }
    // Th3_Sl1ze: okay, so half of this clusterfuck is made by me, and half of it by llm
    // Main problem - uv-based texture rotation (straight textures one!)
    // Somehow when dealing with straight textures it doesn't give a fuck about uv rotation (prob I'm too shitty at it)
    // for example, EnumFacing.UP texture is always headed along Z axis while west/east/north/south is facing along Y axis
    // that also applies to some curve textures, when they're using the straight texture
    // And also the inventory model is completely fucked. But that's mostly the model rotation in inventory gui + fucked up uv textures as well as they are in the world
    // COLOR HANDLER WILL BE DONE LATER
    //Norwoood: redid that, idk why you made it so grandiose
    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
        if (side != null || !(state instanceof IExtendedBlockState)) return ImmutableList.of();

        IExtendedBlockState ext = (IExtendedBlockState) state;
        boolean nZ = ext.getValue(FluidDuctBox.CONN_NORTH);
        boolean pZ = ext.getValue(FluidDuctBox.CONN_SOUTH);
        boolean nX = ext.getValue(FluidDuctBox.CONN_WEST);
        boolean pX = ext.getValue(FluidDuctBox.CONN_EAST);
        boolean nY = ext.getValue(FluidDuctBox.CONN_DOWN);
        boolean pY = ext.getValue(FluidDuctBox.CONN_UP);


        int useMeta = state.getBlock().getMetaFromState(state);

        List<BakedQuad> quads = new ArrayList<>(24);
        FaceBakery faceBakery = new FaceBakery();

        float minX = nX ? 0.0F : 4.0F;
        float maxX = pX ? 16.0F : 12.0F;
        float minY = nY ? 0.0F : 4.0F;
        float maxY = pY ? 16.0F : 12.0F;
        float minZ = nZ ? 0.0F : 4.0F;
        float maxZ = pZ ? 16.0F : 12.0F;

        for (EnumFacing face : EnumFacing.VALUES) {
            int s = face.ordinal();
            TextureAtlasSprite sprite = FluidDuctBox.getPipeIcon(useMeta, s, pX, nX, pY, nY, pZ, nZ);
            if (sprite == null) continue;

            Vector3f from = new Vector3f();
            Vector3f to = new Vector3f();
            switch (face) {
                case DOWN:  from.set(minX, minY, minZ); to.set(maxX, minY, maxZ); break;
                case UP:    from.set(minX, maxY, minZ); to.set(maxX, maxY, maxZ); break;
                case NORTH: from.set(minX, minY, minZ); to.set(maxX, maxY, minZ); break;
                case SOUTH: from.set(minX, minY, maxZ); to.set(maxX, maxY, maxZ); break;
                case WEST:  from.set(minX, minY, minZ); to.set(minX, maxY, maxZ); break;
                case EAST:  from.set(maxX, minY, minZ); to.set(maxX, maxY, maxZ); break;
            }

            float[] uvs = new float[]{0, 0, 16, 16};
            int rotation = getUVRotationFor(face);
            BlockPartFace bpf = new BlockPartFace(null, 0, "", new BlockFaceUV(uvs, rotation));

            BakedQuad quad = faceBakery.makeBakedQuad(from, to, bpf, sprite, face, ModelRotation.X0_Y0, null, false, true);
            quads.add(quad);
        }

        return quads;
    }
    private int getUVRotationFor(EnumFacing face) {
        switch (face) {
            case DOWN:  return 180;
            case UP:    return 0;
            case NORTH: return 90;
            case SOUTH: return 90;
            case WEST:  return 90;
            case EAST:  return 90;
            default:    return 0;
        }
    }

    @Override
    public boolean isAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return FluidDuctBox.iconStraight[0];
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }
}
