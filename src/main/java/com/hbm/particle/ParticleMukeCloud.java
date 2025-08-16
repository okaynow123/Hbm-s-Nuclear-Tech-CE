package com.hbm.particle;

import com.hbm.lib.RefStrings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class ParticleMukeCloud extends Particle {

    private static final ResourceLocation texture = new ResourceLocation(RefStrings.MODID + ":textures/particle/explosion.png");

    private float friction;

    public ParticleMukeCloud(World world, double x, double y, double z, double mx, double my, double mz) {
        super(world, x, y, z, mx, my, mz);
        this.motionX = mx;
        this.motionY = my;
        this.motionZ = mz;

        if (motionY > 0) {
            this.friction = 0.9F;

            if (motionY > 0.1)
                this.particleMaxAge = 92 + this.rand.nextInt(11) + (int) (motionY * 20);
            else
                this.particleMaxAge = 72 + this.rand.nextInt(11);

        } else if (motionY == 0) {

            this.friction = 0.95F;
            this.particleMaxAge = 52 + this.rand.nextInt(11);

        } else {

            this.friction = 0.85F;
            this.particleMaxAge = 122 + this.rand.nextInt(31);
            this.particleAge = 80;
        }
    }

    @Override
    public int getFXLayer() {
        return 3;
    }

    @Override
    public void onUpdate() {

        this.canCollide = this.particleAge > 2;

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge - 2) {
            this.setExpired();
        }

        this.motionY -= 0.04D * (double) this.particleGravity;
        this.move(this.motionX, this.motionY, this.motionZ);
        this.motionX *= friction;
        this.motionY *= friction;
        this.motionZ *= friction;

        if (this.onGround) {
            this.motionX *= 0.7D;
            this.motionZ *= 0.7D;
        }
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float interp, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {

        Minecraft.getMinecraft().renderEngine.bindTexture(getTexture());

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0F);
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderHelper.disableStandardItemLighting();

        if (this.particleAge > this.particleMaxAge)
            this.particleAge = this.particleMaxAge;

        int texIndex = this.particleAge * 25 / this.particleMaxAge;
        float f0 = 1F / 5F;

        float uMin = (texIndex % 5) * f0;
        float uMax = uMin + f0;
        float vMin = (texIndex / 5) * f0;
        float vMax = vMin + f0;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buf = tessellator.getBuffer();
        buf.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);

        this.particleAlpha = 1F;
        this.particleScale = 3;

        float pX = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) interp - interpPosX);
        float pY = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) interp - interpPosY);
        float pZ = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) interp - interpPosZ);

        int brightness = this.getBrightnessForRender(interp);
        int j = (brightness >> 16) & 65535;
        int k = brightness & 65535;

        buf.pos((double) (pX - rotationX * this.particleScale - rotationXY * this.particleScale), (double) (pY - 1 * this.particleScale), (double) (pZ - rotationZ * this.particleScale - rotationXZ * this.particleScale)).tex(uMax, vMax).color(1.0F, 1.0F, 1.0F, this.particleAlpha).lightmap(j, k).endVertex();
        buf.pos((double) (pX - rotationX * this.particleScale + rotationXY * this.particleScale), (double) (pY + 1 * this.particleScale), (double) (pZ - rotationZ * this.particleScale + rotationXZ * this.particleScale)).tex(uMax, vMin).color(1.0F, 1.0F, 1.0F, this.particleAlpha).lightmap(j, k).endVertex();
        buf.pos((double) (pX + rotationX * this.particleScale + rotationXY * this.particleScale), (double) (pY + 1 * this.particleScale), (double) (pZ + rotationZ * this.particleScale + rotationXZ * this.particleScale)).tex(uMin, vMin).color(1.0F, 1.0F, 1.0F, this.particleAlpha).lightmap(j, k).endVertex();
        buf.pos((double) (pX + rotationX * this.particleScale - rotationXY * this.particleScale), (double) (pY - 1 * this.particleScale), (double) (pZ + rotationZ * this.particleScale - rotationXZ * this.particleScale)).tex(uMin, vMax).color(1.0F, 1.0F, 1.0F, this.particleAlpha).lightmap(j, k).endVertex();

        tessellator.draw();

        GlStateManager.doPolygonOffset(0.0F, 0.0F);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.enableLighting();
    }

    protected ResourceLocation getTexture() {
        return texture;
    }
}
