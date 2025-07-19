package com.hbm.particle;

import com.hbm.main.ModEventHandlerClient;
import com.hbm.particle.helper.FlameCreator;
import java.awt.*;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

@SideOnly(Side.CLIENT)
public class ParticleFlamethrower extends ParticleRotating {

  public int type;

  public ParticleFlamethrower(World world, double x, double y, double z, int type) {
    super(world, x, y, z);
    this.particleTexture = ModEventHandlerClient.particle_base;
    this.particleMaxAge = 20 + rand.nextInt(10);
    this.particleScale = 0.5F;
    this.type = type;

    this.motionX = world.rand.nextGaussian() * 0.02;
    this.motionZ = world.rand.nextGaussian() * 0.02;

    float initialColor = 15F + rand.nextFloat() * 25F;

    if (type == FlameCreator.META_BALEFIRE) initialColor = 65F + rand.nextFloat() * 35F;
    if (type == FlameCreator.META_DIGAMMA) initialColor = 0F - rand.nextFloat() * 15F;

    Color color = Color.getHSBColor(initialColor / 255F, 1F, 1F);
    this.particleRed = color.getRed() / 255F;
    this.particleGreen = color.getGreen() / 255F;
    this.particleBlue = color.getBlue() / 255F;

    if (type == FlameCreator.META_OXY)
      this.particleRed = this.particleGreen = this.particleBlue = 1F;
    if (type == FlameCreator.META_BLACK)
      this.particleRed = this.particleGreen = this.particleBlue = 1F;
  }

  @Override
  public void onUpdate() {
    this.prevPosX = this.posX;
    this.prevPosY = this.posY;
    this.prevPosZ = this.posZ;

    this.particleAge++;

    if (this.particleAge >= this.particleMaxAge) {
      this.setExpired();
    }

    this.motionX *= 0.91D;
    this.motionY *= 0.91D;
    this.motionZ *= 0.91D;

    this.motionY += 0.01D;
    this.prevParticleAngle = this.particleAngle;
    this.particleAngle += 0.1F;

    this.move(this.motionX, this.motionY, this.motionZ);
  }

  @Override
  public void renderParticle(
      @NotNull BufferBuilder buffer,
      @NotNull Entity entity,
      float partialTicks,
      float sX,
      float sY,
      float sZ,
      float dX,
      float dZ) {

    double ageScaled = (double) this.particleAge / (double) this.particleMaxAge;

    if (type == FlameCreator.META_OXY) {
      this.particleAlpha = (float) (1 - ageScaled);
      float add = (float) ageScaled * 1.25F - 0.25F;
      buffer.color(
          this.particleRed - add,
          this.particleGreen - add * 0.75F,
          this.particleBlue,
          this.particleAlpha);
    } else if (type == FlameCreator.META_BLACK) {
      this.particleAlpha = (float) (1 - ageScaled);
      float add = (float) ageScaled * 2F - 0.25F;
      buffer.color(
          this.particleRed - add * 0.75F,
          this.particleGreen - add,
          this.particleBlue - add * 0.5F,
          this.particleAlpha);
    } else {
      this.particleAlpha = (float) Math.pow(1 - Math.min(ageScaled, 1), 0.5);
      float add = 0.75F - (float) ageScaled;
      buffer.color(
          this.particleRed + add,
          this.particleGreen + add,
          this.particleBlue + add,
          this.particleAlpha * 0.5F);
    }

    buffer.normal(0.0F, 1.0F, 0.0F);

    double scale = (ageScaled * 1.25 + 0.25) * particleScale;
    renderParticleRotated(buffer, partialTicks, sX, sY, sZ, dX, dZ, scale);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public int getBrightnessForRender(float partialTicks) {
    return 15728880;
  }
}
