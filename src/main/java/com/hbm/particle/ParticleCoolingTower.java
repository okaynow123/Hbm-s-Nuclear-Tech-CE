package com.hbm.particle;

import com.hbm.main.ModEventHandlerClient;
import com.hbm.particle_instanced.ParticleInstanced;
import net.minecraft.client.renderer.texture.TextureManager;
import org.lwjgl.opengl.GL11;

import com.hbm.lib.RefStrings;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.nio.ByteBuffer;

public class ParticleCoolingTower extends ParticleInstanced {

	private float baseScale = 1.0F;
	private float maxScale = 1.0F;
	private float lift = 0.3F;
	private float strafe = 0.075F;
	private boolean windDir = true;
	private float alphaMod = 0.25F;

	public ParticleCoolingTower( World world, double x, double y, double z) {
		super(world, x, y, z);
		this.particleTexture = ModEventHandlerClient.particle_base;
		this.particleRed = this.particleGreen = this.particleBlue = 0.9F + world.rand.nextFloat() * 0.05F;
		this.canCollide = false;
	}

	public void setBaseScale(float f) { this.baseScale = f; }
	public void setMaxScale(float f) { this.maxScale = f; }
	public void setLift(float f) { this.lift = f; }
	public void setLife(int i) { this.particleMaxAge = i; }
	public void setStrafe(float f) { this.strafe = f; }
	public void noWind() { this.windDir = false; }
	public void alphaMod(float mod) { this.alphaMod = mod; }

	@Override
	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		float ageScale = (float) this.particleAge / (float) this.particleMaxAge;
		this.particleAlpha = alphaMod - ageScale * alphaMod;
		this.particleScale = baseScale + (float) Math.pow((maxScale * ageScale - baseScale), 2);

		this.particleAge++;
		if (lift > 0 && this.motionY < this.lift) this.motionY += 0.01F;
		if (lift < 0 && this.motionY > this.lift) this.motionY -= 0.01F;

		this.motionX += rand.nextGaussian() * strafe * ageScale;
		this.motionZ += rand.nextGaussian() * strafe * ageScale;

		if (windDir) {
			this.motionX += 0.02 * ageScale;
			this.motionZ -= 0.01 * ageScale;
		}

		if (this.particleAge >= this.particleMaxAge) this.setExpired();
		this.move(this.motionX, this.motionY, this.motionZ);
		motionX *= 0.925;
		motionY *= 0.925;
		motionZ *= 0.925;
	}

	@Override
	public void addDataToBuffer(ByteBuffer buf, float partialTicks) {
		float x = (float) ((this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX));
		float y = (float) ((this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY));
		float z = (float) ((this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ));
		buf.putFloat(x);
		buf.putFloat(y);
		buf.putFloat(z);
		buf.putFloat(this.particleScale);
		buf.putFloat(this.particleTexture.getMinU());
		buf.putFloat(this.particleTexture.getMinV());
		buf.putFloat(this.particleTexture.getMaxU() - this.particleTexture.getMinU());
		buf.putFloat(this.particleTexture.getMaxV() - this.particleTexture.getMinV());
		buf.put((byte) (this.particleRed * 255));
		buf.put((byte) (this.particleGreen * 255));
		buf.put((byte) (this.particleBlue * 255));
		buf.put((byte) (this.particleAlpha * 255));
		buf.put((byte) 240);
		buf.put((byte) 240);
	}

	@Override
	public int getFaceCount() {
		return 1;
	}

}
