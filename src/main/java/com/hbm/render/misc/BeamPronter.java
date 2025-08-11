package com.hbm.render.misc;

import com.hbm.config.GeneralConfig;
import com.hbm.handler.HbmShaderManager2;
import com.hbm.main.ResourceManager;
import com.hbm.util.BobMathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import java.util.Random;

public class BeamPronter {
	
	public static enum EnumWaveType {
		RANDOM,
		SPIRAL,
		STRAIGHT
	}
	
	public static enum EnumBeamType {
		SOLID,
		LINE
	}

	public static void prontBeam(Vec3d skeleton, EnumWaveType wave, EnumBeamType beam, int outerColor, int innerColor, int start, int segments, float spinRadius, int layers, float thickness) {

		GlStateManager.pushMatrix();

		float sYaw = (float)(Math.atan2(skeleton.x, skeleton.z) * 180F / Math.PI);
		float sqrt = MathHelper.sqrt(skeleton.x * skeleton.x + skeleton.z * skeleton.z);
		float sPitch = (float)(Math.atan2(skeleton.y, (double)sqrt) * 180F / Math.PI);


		GlStateManager.rotate(180, 0, 1F, 0);
		GlStateManager.rotate(sYaw, 0, 1F, 0);
		GlStateManager.rotate(sPitch - 90, 1F, 0, 0);

		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();
		GlStateManager.depthMask(true);

		if (beam == EnumBeamType.SOLID) {
			GlStateManager.depthMask(false);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
			GlStateManager.disableCull();
		}

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		if (beam == EnumBeamType.LINE) {
			buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
		} else if (beam == EnumBeamType.SOLID) {
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		}

		Vec3d unit = new Vec3d(0, 1, 0);
		Random rand = new Random(start);
		double length = skeleton.length();
		double segLength = length / segments;
		double lastX = 0, lastY = 0, lastZ = 0;

		for (int i = 0; i <= segments; i++) {
			double pX = unit.x * segLength * i;
			double pY = unit.y * segLength * i;
			double pZ = unit.z * segLength * i;

			if (wave != EnumWaveType.STRAIGHT) {
				Vec3d spinner = new Vec3d(spinRadius, 0, 0);
				if (wave == EnumWaveType.SPIRAL) {
					float angle1 = (float) Math.PI * start / 180F;
					float angle2 = (float) Math.PI * 45F / 180F * i;
					spinner = spinner.rotateYaw(angle1).rotateYaw(angle2);
				} else if (wave == EnumWaveType.RANDOM) {
					spinner = spinner.rotateYaw((float) (Math.PI * 2 * rand.nextFloat()));
				}
				pX += spinner.x;
				pY += spinner.y;
				pZ += spinner.z;
			}

			if (beam == EnumBeamType.LINE && i > 0) {
				int color = outerColor;
				buffer.pos(pX, pY, pZ).color((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, 255).endVertex();
				buffer.pos(lastX, lastY, lastZ).color((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, 255).endVertex();
			}

			if (beam == EnumBeamType.SOLID && i > 0) {
				float radius = thickness / layers;

				for (int j = 1; j <= layers; j++) {
					int color;
					if (layers == 1) {
						color = outerColor;
					} else {
						float inter = (float)(j - 1) / (layers - 1);
						color = BobMathUtil.interpolateColor(innerColor, outerColor, inter);
					}

					int r = (color >> 16) & 0xFF;
					int g = (color >> 8) & 0xFF;
					int b = color & 0xFF;

					float radJ = radius * j;

					buffer.pos(lastX + radJ, lastY, lastZ + radJ).color(r, g, b, 255).endVertex();
					buffer.pos(lastX + radJ, lastY, lastZ - radJ).color(r, g, b, 255).endVertex();
					buffer.pos(pX + radJ, pY, pZ - radJ).color(r, g, b, 255).endVertex();
					buffer.pos(pX + radJ, pY, pZ + radJ).color(r, g, b, 255).endVertex();

					buffer.pos(lastX - radJ, lastY, lastZ + radJ).color(r, g, b, 255).endVertex();
					buffer.pos(lastX - radJ, lastY, lastZ - radJ).color(r, g, b, 255).endVertex();
					buffer.pos(pX - radJ, pY, pZ - radJ).color(r, g, b, 255).endVertex();
					buffer.pos(pX - radJ, pY, pZ + radJ).color(r, g, b, 255).endVertex();

					buffer.pos(lastX + radJ, lastY, lastZ + radJ).color(r, g, b, 255).endVertex();
					buffer.pos(lastX - radJ, lastY, lastZ + radJ).color(r, g, b, 255).endVertex();
					buffer.pos(pX - radJ, pY, pZ + radJ).color(r, g, b, 255).endVertex();
					buffer.pos(pX + radJ, pY, pZ + radJ).color(r, g, b, 255).endVertex();

					buffer.pos(lastX + radJ, lastY, lastZ - radJ).color(r, g, b, 255).endVertex();
					buffer.pos(lastX - radJ, lastY, lastZ - radJ).color(r, g, b, 255).endVertex();
					buffer.pos(pX - radJ, pY, pZ - radJ).color(r, g, b, 255).endVertex();
					buffer.pos(pX + radJ, pY, pZ - radJ).color(r, g, b, 255).endVertex();
				}
			}

			lastX = pX;
			lastY = pY;
			lastZ = pZ;
		}

		if (beam == EnumBeamType.LINE) {
			int color = innerColor;
			buffer.pos(0, 0, 0).color((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, 255).endVertex();
			buffer.pos(0, skeleton.length(), 0).color((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, 255).endVertex();
		}

		tessellator.draw();

		if (beam == EnumBeamType.SOLID) {
			GlStateManager.disableBlend();
			GlStateManager.enableCull();
			GlStateManager.depthMask(true);
		}

		GlStateManager.enableLighting();
		GlStateManager.enableTexture2D();
		GlStateManager.popMatrix();
	}


	//Drillgon200: Yeah, I don't know what to do about fluid colors so I'm just going butcher it and try my best to use the middle pixel of the icon
	//Alcater: I figured out a way to extract the fluid colors from the texture and save them in a HashMap at loadup. This function wont be needed anymore.
	//public static void prontBeamWithIcon(Vec3 skeleton, EnumWaveType wave, EnumBeamType beam, TextureAtlasSprite icon, int innerColor, int start, int segments, float spinRadius, int layers, float thickness) {

	
	public static void gluonBeam(Vec3d pos1, Vec3d pos2, float size){
		GlStateManager.pushMatrix();
		GlStateManager.depthMask(false);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
		GlStateManager.disableCull();
		if(!GeneralConfig.useShaders2){
			GlStateManager.color(0.4F, 0.7F, 1, 1);
		}
		
		Vec3d diff = pos1.subtract(pos2);
		float len = (float) diff.length();
		Vec3d angles = BobMathUtil.getEulerAngles(diff);
		GlStateManager.translate(pos1.x, pos1.y, pos1.z);
		
		GL11.glRotated(angles.x+90, 0, 1, 0);
		GL11.glRotated(-angles.y, 0, 0, 1);
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(ResourceManager.noise_1);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		Minecraft.getMinecraft().getTextureManager().bindTexture(ResourceManager.noise_2);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		Minecraft.getMinecraft().getTextureManager().bindTexture(ResourceManager.bfg_core_lightning);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		
		net.minecraft.client.renderer.Tessellator tes = net.minecraft.client.renderer.Tessellator.getInstance();
		BufferBuilder buf = tes.getBuffer();
		
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, HbmShaderManager2.AUX_GL_BUFFER);
		HbmShaderManager2.AUX_GL_BUFFER.rewind();
		Matrix4f mvMatrix = new Matrix4f();
		mvMatrix.load(HbmShaderManager2.AUX_GL_BUFFER);
		HbmShaderManager2.AUX_GL_BUFFER.rewind();
		Matrix4f.invert(mvMatrix, mvMatrix);
		Vector4f billboardPos = Matrix4f.transform(mvMatrix, new Vector4f(0, 0, 0, 1), null);
		//System.out.println(billboardPos);
		//GL20.glUniform3f(GL20.glGetUniformLocation(ResourceManager.gluon_beam.getShaderId(), "playerPos"), billboardPos.x, billboardPos.y, billboardPos.z);
		
		//GL20.glUniform3f(GL20.glGetUniformLocation(ResourceManager.gluon_beam.getShaderId(), "playerPos"), 0.0F, 0.1F, 0F);
		int SUBDIVISIONS_PER_BLOCK = 16;
		int subdivisions = (int)Math.ceil(len*SUBDIVISIONS_PER_BLOCK);
		
		//System.out.println(billboardPos);
		ResourceManager.gluon_spiral.use();
		ResourceManager.gluon_spiral.uniform3f("playerPos", billboardPos.x, billboardPos.y, billboardPos.z);
		ResourceManager.gluon_spiral.uniform1f("subdivXAmount", 1/(float)SUBDIVISIONS_PER_BLOCK);
		ResourceManager.gluon_spiral.uniform1f("subdivUAmount", 1/(float)(subdivisions+1));
		ResourceManager.gluon_spiral.uniform1f("len", len);
		
		buf.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_TEX);
		for(int i = 0; i <= subdivisions; i ++){
			float iN = ((float)i/(float)subdivisions);
			float pos = iN*len;
			buf.pos(pos, 0, -size*0.025).tex(iN, 0.45).endVertex();
			buf.pos(pos, 0, size*0.025).tex(iN, 0.55).endVertex();
		}
		tes.draw();
		
		SUBDIVISIONS_PER_BLOCK *= 0.5;
		subdivisions = (int)Math.ceil(len*SUBDIVISIONS_PER_BLOCK);
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(ResourceManager.gluon_beam_tex);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		//GlStateManager.depthMask(true);
		ResourceManager.gluon_beam.use();
		ResourceManager.gluon_beam.uniform1f("beam_length", len);
		
		buf.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_TEX);

		Vec3d vec = new Vec3d(billboardPos.x, billboardPos.y, billboardPos.z).crossProduct(new Vec3d(1, 0, 0)).normalize();
		for(int i = 0; i <= subdivisions; i ++){
			float iN = ((float)i/(float)subdivisions);
			float pos = iN*len;
			buf.pos(pos, -vec.y, -vec.z).tex(iN, 0).endVertex();
			buf.pos(pos, vec.y, vec.z).tex(iN, 1).endVertex();
		}
		tes.draw();
		
		HbmShaderManager2.releaseShader();
		
		//System.out.println(System.nanoTime() - l);
		if(!GeneralConfig.useShaders2){
			GlStateManager.color(1, 1, 1, 1);
		}
		GlStateManager.disableBlend();
		GlStateManager.enableCull();
		GlStateManager.popMatrix();
	}
}