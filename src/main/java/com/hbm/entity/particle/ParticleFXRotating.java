package com.hbm.entity.particle;

import com.hbm.lib.RefStrings;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleFXRotating extends Particle {
    private static final ResourceLocation texture = new ResourceLocation(RefStrings.MODID + ":textures/particle/particle_base.png");
    public float hue;
    protected double newScale;

    protected ParticleFXRotating(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public int getFXLayer() {
        return 1;
    }

    //    @Override
//        public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float sX, float sY, float sZ, float dX, float dZ){
//        //Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
//
//        float posX = (float) (this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX);
//        float posY = (float) (this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY);
//        float posZ = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ);
//
//        float rotation = this.prevParticleAngle + (this.particleAngle - this.prevParticleAngle) * partialTicks;
//
//        double scale = newScale;
//
//        double x1 = 0 - sX * scale - dX * scale;
//        double y1 = 0 - sY * scale;
//        double z1 = 0 - sZ * scale - dZ * scale;
//        double x2 = 0 - sX * scale + dX * scale;
//        double y2 = 0 + sY * scale;
//        double z2 = 0 - sZ * scale + dZ * scale;
//        double x3 = 0 + sX * scale + dX * scale;
//        double y3 = 0 + sY * scale;
//        double z3 = 0 + sZ * scale + dZ * scale;
//        double x4 = 0 + sX * scale - dX * scale;
//        double y4 = 0 - sY * scale;
//        double z4 = 0 + sZ * scale - dZ * scale;
//
//        double nX = ((y2 - y1) * (z3 - z1)) - ((z2 - z1) * (y3 - y1));
//        double nY = ((z2 - z1) * (x3 - x1)) - ((x2 - x1) * (z3 - z1));
//        double nZ = ((x2 - x1) * (y3 - y1)) - ((y2 - y1) * (x3 - x1));
//
//        Vec3d vec = new Vec3d(nX, nY, nZ).normalize();
//        nX = vec.x;
//        nY = vec.y;
//        nZ = vec.z;
//
//        double cosTh = Math.cos(rotation * Math.PI / 180D);
//        double sinTh = Math.sin(rotation * Math.PI / 180D);
//
//        double x01 = x1 * cosTh + (nY * z1 - nZ * y1) * sinTh;
//        double y01 = y1 * cosTh + (nZ * x1 - nX * z1) * sinTh;
//        double z01 = z1 * cosTh + (nX * y1 - nY * x1) * sinTh;
//        double x02 = x2 * cosTh + (nY * z2 - nZ * y2) * sinTh;
//        double y02 = y2 * cosTh + (nZ * x2 - nX * z2) * sinTh;
//        double z02 = z2 * cosTh + (nX * y2 - nY * x2) * sinTh;
//        double x03 = x3 * cosTh + (nY * z3 - nZ * y3) * sinTh;
//        double y03 = y3 * cosTh + (nZ * x3 - nX * z3) * sinTh;
//        double z03 = z3 * cosTh + (nX * y3 - nY * x3) * sinTh;
//        double x04 = x4 * cosTh + (nY * z4 - nZ * y4) * sinTh;
//        double y04 = y4 * cosTh + (nZ * x4 - nX * z4) * sinTh;
//        double z04 = z4 * cosTh + (nX * y4 - nY * x4) * sinTh;
//        int i = this.getBrightnessForRender(partialTicks);
//        int j = i >> 16 & 65535;
//        int k = i & 65535;
//
//        buffer.pos(posX + x01, posY + y01, posZ + z01).tex(particleTexture.getMaxU(), particleTexture.getMaxV()).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).normal(0, 1, 0).lightmap(j,k).endVertex();
//        buffer.pos(posX + x02, posY + y02, posZ + z02).tex(particleTexture.getMaxU(), particleTexture.getMinV()).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).normal(0, 1, 0).lightmap(j,k).endVertex();
//        buffer.pos(posX + x03, posY + y03, posZ + z03).tex(particleTexture.getMinU(), particleTexture.getMinV()).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).normal(0, 1, 0).lightmap(j,k).endVertex();
//        buffer.pos(posX + x04, posY + y04, posZ + z04).tex(particleTexture.getMinU(), particleTexture.getMaxV()).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).normal(0, 1, 0).lightmap(j,k).endVertex();
//
//    }
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        float f = (float) this.particleTextureIndexX / 16.0F;
        float f1 = f + 0.0624375F;
        float f2 = (float) this.particleTextureIndexY / 16.0F;
        float f3 = f2 + 0.0624375F;
        double f4 = newScale * 0.3d;

        if (this.particleTexture != null) {
            f = this.particleTexture.getMinU();
            f1 = this.particleTexture.getMaxU();
            f2 = this.particleTexture.getMinV();
            f3 = this.particleTexture.getMaxV();
        }

        float f5 = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
        float f6 = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
        float f7 = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);
        int i = this.getBrightnessForRender(partialTicks);
        int j = i >> 16 & 65535;
        int k = i & 65535;
        Vec3d[] avec3d = new Vec3d[]{new Vec3d((double) (-rotationX * f4 - rotationXY * f4), (double) (-rotationZ * f4), (double) (-rotationYZ * f4 - rotationXZ * f4)), new Vec3d((double) (-rotationX * f4 + rotationXY * f4), (double) (rotationZ * f4), (double) (-rotationYZ * f4 + rotationXZ * f4)), new Vec3d((double) (rotationX * f4 + rotationXY * f4), (double) (rotationZ * f4), (double) (rotationYZ * f4 + rotationXZ * f4)), new Vec3d((double) (rotationX * f4 - rotationXY * f4), (double) (-rotationZ * f4), (double) (rotationYZ * f4 - rotationXZ * f4))};

        float f8 = this.prevParticleAngle + (this.particleAngle - this.prevParticleAngle) * partialTicks;
            float f9 = MathHelper.cos(f8 * 0.5F);
            float f10 = MathHelper.sin(f8 * 0.5F) * (float) cameraViewDir.x;
            float f11 = MathHelper.sin(f8 * 0.5F) * (float) cameraViewDir.y;
            float f12 = MathHelper.sin(f8 * 0.5F) * (float) cameraViewDir.z;
            Vec3d vec3d = new Vec3d((double) f10, (double) f11, (double) f12);

            for (int l = 0; l < 4; ++l) {
                avec3d[l] = vec3d.scale(2.0D * avec3d[l].dotProduct(vec3d)).add(avec3d[l].scale((double) (f9 * f9) - vec3d.dotProduct(vec3d))).add(vec3d.crossProduct(avec3d[l]).scale((double) (2.0F * f9)));
            }

        buffer.pos((double) f5 + avec3d[0].x, (double) f6 + avec3d[0].y, (double) f7 + avec3d[0].z).tex((double) f1, (double) f3).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        buffer.pos((double) f5 + avec3d[1].x, (double) f6 + avec3d[1].y, (double) f7 + avec3d[1].z).tex((double) f1, (double) f2).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        buffer.pos((double) f5 + avec3d[2].x, (double) f6 + avec3d[2].y, (double) f7 + avec3d[2].z).tex((double) f, (double) f2).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        buffer.pos((double) f5 + avec3d[3].x, (double) f6 + avec3d[3].y, (double) f7 + avec3d[3].z).tex((double) f, (double) f3).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
    }

}
