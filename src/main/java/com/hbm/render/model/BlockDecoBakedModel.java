package com.hbm.render.model;

import com.hbm.blocks.BlockEnumMeta;
import com.hbm.render.amlfrom1710.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;

import javax.vecmath.Vector3f;
import java.util.*;
public class BlockDecoBakedModel implements IBakedModel {

    private final WavefrontObject model;
    private final TextureAtlasSprite sprite;
    private final boolean forBlock; // true: world block model, false: item/inventory model
    private final float baseScale;
    private final float tx, ty, tz; // base translation
    private final VertexFormat format = DefaultVertexFormats.ITEM;

    private final Map<EnumFacing, List<BakedQuad>> cacheByFacing = new EnumMap<>(EnumFacing.class);
    private List<BakedQuad> itemQuads;

    public BlockDecoBakedModel(WavefrontObject model, TextureAtlasSprite sprite, boolean forBlock, float baseScale, float tx, float ty, float tz) {
        this.model = model;
        this.sprite = sprite;
        this.forBlock = forBlock;
        this.baseScale = baseScale;
        this.tx = tx;
        this.ty = ty;
        this.tz = tz;
    }

    public static BlockDecoBakedModel forBlock(WavefrontObject model, TextureAtlasSprite sprite) {
        return new BlockDecoBakedModel(model, sprite, true, 1.0F, 0.0F, 0.0F, 0.0F);
    }

    public static BlockDecoBakedModel forBlock(WavefrontObject model, TextureAtlasSprite sprite, float ty) {
        return new BlockDecoBakedModel(model, sprite, true, 1.0F, 0.0F, ty, 0.0F);
    }

    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
        if (side != null) return Collections.emptyList();

        if (forBlock) {
            EnumFacing facing = EnumFacing.SOUTH;
            if (state != null) {
                if (state.getPropertyKeys().contains(BlockEnumMeta.META)) {
                    int meta = state.getValue(BlockEnumMeta.META);
                    int orient = (meta >> 2) & 3;
                    switch (orient) {
                        case 0 -> facing = EnumFacing.NORTH;
                        case 1 -> {
                        }
                        case 2 -> facing = EnumFacing.WEST;
                        case 3 -> facing = EnumFacing.EAST;
                    }
                }
            }
            return cacheByFacing.computeIfAbsent(facing, this::buildQuadsForFacing);
        } else {
            if (itemQuads == null) {
                itemQuads = buildItemQuads();
            }
            return itemQuads;
        }
    }

    private List<BakedQuad> buildQuadsForFacing(EnumFacing facing) {
        float yaw = switch (facing) {
            case NORTH -> (float) Math.PI;
            case WEST -> 1.5F * (float) Math.PI;
            case EAST -> 0.5F * (float) Math.PI;
            default -> 0.0F;
        };
        // World: shadow enabled, center model to block (+0.5)
        return bakeQuads(0.0F, 0.0F, yaw, true, true);
    }

    private List<BakedQuad> buildItemQuads() {
        // Item: no shadow, no centering (+0.5), but apply base scale and translation
        return bakeQuads(0.0F, 0.0F, 0.0F, false, false);
    }

    private List<BakedQuad> bakeQuads(float roll, float pitch, float yaw, boolean shadow, boolean centerToBlock) {
        List<BakedQuad> quads = new ArrayList<>();

        for (GroupObject go : model.groupObjects) {
            for (Face f : go.faces) {
                Vertex n = f.faceNormal;

                double[] n1 = rotateX(n.x, n.y, n.z, roll);
                double[] n2 = rotateZ(n1[0], n1[1], n1[2], pitch);
                double[] n3 = rotateY(n2[0], n2[1], n2[2], yaw);

                float fnx = (float) n3[0];
                float fny = (float) n3[1];
                float fnz = (float) n3[2];

                float brightness = 1.0F;
                if (shadow) {
                    brightness = (fny + 0.7F) * 0.9F - Math.abs(fnx) * 0.1F + Math.abs(fnz) * 0.1F;
                    if (brightness < 0.45F) brightness = 0.45F;
                    if (brightness > 1.0F) brightness = 1.0F;
                }
                int cr = clampColor((int) (brightness * 255.0F));

                int vCount = f.vertices.length;
                if (vCount < 3) continue;

                // Prepare 4 vertices (duplicate the last one if triangle)
                int[] idxs = vCount >= 4 ? new int[]{0, 1, 2, 3} : new int[]{0, 1, 2, 2};

                // Build vertex data
                float[] px = new float[4];
                float[] py = new float[4];
                float[] pz = new float[4];
                float[] uu = new float[4];
                float[] vv = new float[4];

                for (int j = 0; j < 4; j++) {
                    int i = idxs[j];
                    Vertex v = f.vertices[i];

                    double[] p1 = rotateX(v.x, v.y, v.z, roll);
                    double[] p2 = rotateZ(p1[0], p1[1], p1[2], pitch);
                    double[] p3 = rotateY(p2[0], p2[1], p2[2], yaw);

                    float x = (float) p3[0];
                    float y = (float) p3[1];
                    float z = (float) p3[2];

                    if (centerToBlock) {
                        x += 0.5F;
                        y += 0.5F;
                        z += 0.5F;
                    }

                    // Apply base scale and translation
                    x = x * baseScale + tx;
                    y = y * baseScale + ty;
                    z = z * baseScale + tz;

                    TextureCoordinate t = f.textureCoordinates[i];
                    float u = sprite.getInterpolatedU(t.u * 16.0D);
                    float w = sprite.getInterpolatedV(t.v * 16.0D);

                    px[j] = x; py[j] = y; pz[j] = z;
                    uu[j] = u; vv[j] = w;
                }

                EnumFacing face = facingFromNormal(fnx, fny, fnz);
                UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
                builder.setQuadOrientation(face);
                builder.setTexture(sprite);
                builder.setApplyDiffuseLighting(true);

                Vector3f normal = new Vector3f(fnx, fny, fnz);
                normal.normalize();
                for (int j = 0; j < 4; j++) {
                    putVertex(builder, px[j], py[j], pz[j], uu[j], vv[j], cr, cr, cr, normal);
                }

                quads.add(builder.build());
            }
        }

        return quads;
    }

    private void putVertex(UnpackedBakedQuad.Builder builder, float x, float y, float z, float u, float v, int cr, int cg, int cb, Vector3f normal) {
        for (int e = 0; e < format.getElementCount(); e++) {
            VertexFormatElement element = format.getElement(e);
            switch (element.getUsage()) {
                case POSITION -> builder.put(e, x, y, z);
                case COLOR -> builder.put(e, cr / 255.0F, cg / 255.0F, cb / 255.0F, 1.0F);
                case UV -> {
                    if (element.getIndex() == 0) {
                        builder.put(e, u, v);
                    } else {
                        builder.put(e, 0.0F, 0.0F);
                    }
                }
                case NORMAL -> builder.put(e, normal.x, normal.y, normal.z);
                case PADDING -> builder.put(e, 0.0F);
                default -> builder.put(e);
            }
        }
    }

    private static int clampColor(int c) {
        if (c < 0) return 0;
        return Math.min(c, 255);
    }

    private static EnumFacing facingFromNormal(float nx, float ny, float nz) {
        return EnumFacing.getFacingFromVector(nx, ny, nz);
    }

    private static double[] rotateX(double x, double y, double z, float angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double ny = y * cos - z * sin;
        double nz = y * sin + z * cos;
        return new double[]{x, ny, nz};
    }

    private static double[] rotateY(double x, double y, double z, float angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double nx = x * cos + z * sin;
        double nz = -x * sin + z * cos;
        return new double[]{nx, y, nz};
    }

    private static double[] rotateZ(double x, double y, double z, float angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double nx = x * cos - y * sin;
        double ny = x * sin + y * cos;
        return new double[]{nx, ny, z};
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
        return sprite;
    }
    // TODO: probably implement debug transformer here (why? look at HEV battery block, for example)
    // I don't have Norwood's baked model wrapper yet, so..
    private static final ItemCameraTransforms CUSTOM_TRANSFORMS = createCustomTransforms();
    private static ItemCameraTransforms createCustomTransforms() {
        ItemTransformVec3f gui = new ItemTransformVec3f(
                new org.lwjgl.util.vector.Vector3f(30, -45, 0),
                new org.lwjgl.util.vector.Vector3f(0, 0, 0),
                new org.lwjgl.util.vector.Vector3f(0.625f, 0.625f, 0.625f)
        );

        ItemTransformVec3f thirdPerson = new ItemTransformVec3f(
                new org.lwjgl.util.vector.Vector3f(75, 45, 0),
                new org.lwjgl.util.vector.Vector3f(0, 1.5f / 16, -2.5f / 16),
                new org.lwjgl.util.vector.Vector3f(0.5f, 0.5f, 0.5f)
        );

        ItemTransformVec3f firstPerson = new ItemTransformVec3f(
                new org.lwjgl.util.vector.Vector3f(0, 45, 0),
                new org.lwjgl.util.vector.Vector3f(0, 0, 0),
                new org.lwjgl.util.vector.Vector3f(0.5f, 0.5f, 0.5f)
        );

        ItemTransformVec3f ground = new ItemTransformVec3f(
                new org.lwjgl.util.vector.Vector3f(0, 0, 0),
                new org.lwjgl.util.vector.Vector3f(0, 2f / 16, 0),
                new org.lwjgl.util.vector.Vector3f(0.5f, 0.5f, 0.5f)
        );

        ItemTransformVec3f head = new ItemTransformVec3f(
                new org.lwjgl.util.vector.Vector3f(0, 0, 0),
                new org.lwjgl.util.vector.Vector3f(0, 13f / 16, 7f / 16),
                new org.lwjgl.util.vector.Vector3f(1, 1, 1)
        );

        ItemTransformVec3f fixed = new ItemTransformVec3f(
                new org.lwjgl.util.vector.Vector3f(0, 180, 0),
                new org.lwjgl.util.vector.Vector3f(0, 0, 0),
                new org.lwjgl.util.vector.Vector3f(0.75f, 0.75f, 0.75f)
        );

        return new ItemCameraTransforms(thirdPerson, thirdPerson, firstPerson, firstPerson, head, gui, ground, fixed);
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return CUSTOM_TRANSFORMS;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }
}
