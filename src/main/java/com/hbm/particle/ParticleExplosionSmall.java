package com.hbm.particle;

import com.hbm.entity.particle.ParticleFXRotating;
import com.hbm.main.ModEventHandlerClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;

@SideOnly(Side.CLIENT)
public class ParticleExplosionSmall extends ParticleFXRotating {


    public ParticleExplosionSmall(World world, double x, double y, double z, float scale, float speedMult) {
        super(world, x, y, z);

        this.particleMaxAge = 25 + this.rand.nextInt(10);
        this.particleScale = scale * 0.9F + rand.nextFloat() * 0.2F;

        this.motionX = world.rand.nextGaussian() * speedMult;
        this.motionZ = world.rand.nextGaussian() * speedMult;
        this.particleGravity = rand.nextFloat() * -0.01F;

        this.hue = 20F + rand.nextFloat() * 20F;
        Color color = Color.getHSBColor(hue / 255F, 1F, 1F);
        this.setRBGColorF(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F);

        this.canCollide = true;
        this.setParticleTexture(ModEventHandlerClient.particle_base);
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        this.particleAge++;

        if(this.particleAge >= this.particleMaxAge) {
            this.setExpired();
        }

        this.motionY -= particleGravity;
        this.prevParticleAngle = this.particleAngle;

        float ageScaled = (float) this.particleAge / (float) this.particleMaxAge;
        this.particleAngle += (1 - ageScaled) * 5 * ((rand.nextInt(100) % 2) - 0.5);

        this.motionX *= 0.65D;
        this.motionZ *= 0.65D;

        this.move(this.motionX, this.motionY, this.motionZ);    }

  @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        double ageScaled = (double) (this.particleAge + partialTicks) / (double) this.particleMaxAge;

        Color color = Color.getHSBColor(
                hue / 255F,
                Math.max(1F - (float) ageScaled * 2F, 0F),
                MathHelper.clamp(1.25F - (float) ageScaled * 2F, hue * 0.01F - 0.1F, 1F)
        );

        this.setRBGColorF(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F);
        this.particleAlpha = (float) Math.pow(1 - Math.min(ageScaled, 1), 0.25);

        this.particleScale = (float) ((0.25 + 1 - Math.pow(1 - ageScaled, 4) + (this.particleAge + partialTicks) * 0.02) * this.particleScale);

        GlStateManager.color(particleRed, particleGreen, particleBlue, particleAlpha * 0.5F);
        GlStateManager.depthMask(false);

        //Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        newScale = (0.25 + 1 - Math.pow(1 - ageScaled, 4) + (this.particleAge + partialTicks) * 0.02) * this.particleScale;
        super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
    }


}
