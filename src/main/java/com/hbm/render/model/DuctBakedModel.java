package com.hbm.render.model;

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
    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
        if (side != null) return Collections.emptyList();

        List<BakedQuad> quads = new ArrayList<>();

        boolean pX, nX, pY, nY, pZ, nZ;
        int useMeta = this.meta;

        if (state == null) {
            pX = true;
            nX = true;
            pY = false;
            nY = false;
            pZ = false;
            nZ = false;
        } else {
            IExtendedBlockState ext = (IExtendedBlockState) state;
            nZ = Boolean.TRUE.equals(ext.getValue(FluidDuctBox.CONN_NORTH));
            pZ = Boolean.TRUE.equals(ext.getValue(FluidDuctBox.CONN_SOUTH));
            nX = Boolean.TRUE.equals(ext.getValue(FluidDuctBox.CONN_WEST));
            pX = Boolean.TRUE.equals(ext.getValue(FluidDuctBox.CONN_EAST));
            nY = Boolean.TRUE.equals(ext.getValue(FluidDuctBox.CONN_DOWN));
            pY = Boolean.TRUE.equals(ext.getValue(FluidDuctBox.CONN_UP));
            useMeta = ext.getValue(FluidDuctBox.META);
        }

        int sizeLevel = Math.min(useMeta / 3, 4);

        float lower = 0.125f + sizeLevel * 0.0625f;
        float upper = 0.875f - sizeLevel * 0.0625f;
        float jLower = 0.0625f + sizeLevel * 0.0625f;
        float jUpper = 0.9375f - sizeLevel * 0.0625f;

        if (lower > upper) { float temp = lower; lower = upper; upper = temp; }
        if (jLower > jUpper) { float temp = jLower; jLower = jUpper; jUpper = temp; }

        int mask = (pX ? 32 : 0) + (nX ? 16 : 0) + (pY ? 8 : 0) + (nY ? 4 : 0) + (pZ ? 2 : 0) + (nZ ? 1 : 0);
        int count = Integer.bitCount(mask);

        int[] uvRotate = new int[6]; // All 0, no rotation - we handle orientation via UV manipulation

        List<float[]> boundsList = new ArrayList<>();


        if ((mask & 0b001111) == 0 && mask > 0) { // straight X
            boundsList.add(new float[]{0, lower, lower, 1, upper, upper});
        } else if ((mask & 0b111100) == 0 && mask > 0) { // straight Z
            boundsList.add(new float[]{lower, lower, 0, upper, upper, 1});
        } else if ((mask & 0b110011) == 0 && mask > 0) { // straight Y
            boundsList.add(new float[]{lower, 0, lower, upper, 1, upper});
        } else if (count == 2) { // curve
            boundsList.add(new float[]{lower, lower, lower, upper, upper, upper});
            if (nY) boundsList.add(new float[]{lower, 0, lower, upper, lower, upper});
            if (pY) boundsList.add(new float[]{lower, upper, lower, upper, 1, upper});
            if (nX) boundsList.add(new float[]{0, lower, lower, lower, upper, upper});
            if (pX) boundsList.add(new float[]{upper, lower, lower, 1, upper, upper});
            if (nZ) boundsList.add(new float[]{lower, lower, 0, upper, upper, lower});
            if (pZ) boundsList.add(new float[]{lower, lower, upper, upper, upper, 1});
        } else { // junction
            boundsList.add(new float[]{jLower, jLower, jLower, jUpper, jUpper, jUpper});
            if (nY) boundsList.add(new float[]{lower, 0, lower, upper, jLower, upper});
            if (pY) boundsList.add(new float[]{lower, jUpper, lower, upper, 1, upper});
            if (nX) boundsList.add(new float[]{0, lower, lower, jLower, upper, upper});
            if (pX) boundsList.add(new float[]{jUpper, lower, lower, 1, upper, upper});
            if (nZ) boundsList.add(new float[]{lower, lower, 0, upper, upper, jLower});
            if (pZ) boundsList.add(new float[]{lower, lower, jUpper, upper, upper, 1});
        }

        FaceBakery faceBakery = new FaceBakery();

        for (float[] b : boundsList) {
            float minX = b[0] * 16f;
            float minY = b[1] * 16f;
            float minZ = b[2] * 16f;
            float maxX = b[3] * 16f;
            float maxY = b[4] * 16f;
            float maxZ = b[5] * 16f;

            if (minX > maxX) { float temp = minX; minX = maxX; maxX = temp; }
            if (minY > maxY) { float temp = minY; minY = maxY; maxY = temp; }
            if (minZ > maxZ) { float temp = minZ; minZ = maxZ; maxZ = temp; }

            if (minX == maxX || minY == maxY || minZ == maxZ) continue;

            for (EnumFacing face : EnumFacing.VALUES) {
                int s = face.ordinal();
                TextureAtlasSprite sprite = FluidDuctBox.getPipeIcon(useMeta, s, pX, nX, pY, nY, pZ, nZ);
                if (sprite == null) continue;

                // Standard UV calculation
                float uMin, uMax, vMin, vMax;
                switch (face) {
                    case UP:
                        uMin = minX; uMax = maxX;
                        vMin = minZ; vMax = maxZ;
                        break;
                    case DOWN:
                        uMin = minX; uMax = maxX;
                        vMin = 16f - maxZ; vMax = 16f - minZ;
                        break;
                    case SOUTH:
                        uMin = minX; uMax = maxX;
                        vMin = minY; vMax = maxY;
                        break;
                    case NORTH:
                        uMin = 16f - maxX; uMax = 16f - minX;
                        vMin = minY; vMax = maxY;
                        break;
                    case EAST:
                        uMin = 16f - maxZ; uMax = 16f - minZ;
                        vMin = minY; vMax = maxY;
                        break;
                    case WEST:
                        uMin = minZ; uMax = maxZ;
                        vMin = minY; vMax = maxY;
                        break;
                    default:
                        continue;
                }


                // Clamp min <= max (cuz obviously just doing 0, 0, 16, 16 uv won't work here)
                if (uMin > uMax) { float temp = uMin; uMin = uMax; uMax = temp; }
                if (vMin > vMax) { float temp = vMin; vMin = vMax; vMax = temp; }

                float[] uvs = new float[]{uMin, vMin, uMax, vMax};

                BlockPartFace bpf = new BlockPartFace(null, 0, "", new BlockFaceUV(uvs, uvRotate[s]));

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

                ModelRotation rotation = ModelRotation.X0_Y0;
                BakedQuad quad = faceBakery.makeBakedQuad(from, to, bpf, sprite, face, rotation, null, false, true);
                quads.add(quad);
            }
        }

        return quads;
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
