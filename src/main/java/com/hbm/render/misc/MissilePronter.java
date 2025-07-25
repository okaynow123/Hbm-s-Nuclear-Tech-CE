package com.hbm.render.misc;

import com.hbm.entity.missile.EntityRideableRocket;
import com.hbm.handler.RocketStruct;
import com.hbm.items.weapon.ItemMissile.PartType;
import com.hbm.main.ResourceManager;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.texture.TextureManager;
import org.lwjgl.opengl.GL11; import net.minecraft.client.renderer.GlStateManager;

import java.nio.DoubleBuffer;

public class MissilePronter {
	private static DoubleBuffer buffer;

	public static void prontMissile(MissileMultipart missile, TextureManager tex) {
		
		//if(!missile.hadFuselage())
		//	return;
		
		GlStateManager.pushMatrix();
		
		if(missile.thruster != null && missile.thruster.type.name().equals(PartType.THRUSTER.name())) {
			
			tex.bindTexture(missile.thruster.texture);
			missile.thruster.model.renderAll();
			GlStateManager.translate(0, missile.thruster.height, 0);
		}
		
		if(missile.fuselage != null && missile.fuselage.type.name().equals(PartType.FUSELAGE.name())) {

			if(missile.fins != null && missile.fins.type.name().equals(PartType.FINS.name())) {
				
				tex.bindTexture(missile.fins.texture);
				missile.fins.model.renderAll();
			}
			
			tex.bindTexture(missile.fuselage.texture);
			missile.fuselage.model.renderAll();
			GlStateManager.translate(0, missile.fuselage.height, 0);
		}
		
		if(missile.warhead != null && missile.warhead.type.name().equals(PartType.WARHEAD.name())) {
			
			tex.bindTexture(missile.warhead.texture);
			missile.warhead.model.renderAll();
		}

		GlStateManager.popMatrix();
	}

	public static void prontRocket(RocketStruct rocket, TextureManager tex) {
		prontRocket(rocket, null, tex, true, 0);
	}

	public static void prontRocket(RocketStruct rocket, TextureManager tex, boolean isDeployed) {
		prontRocket(rocket, null, tex, isDeployed, 0);
	}

	// Attaches a set of stages together
	public static void prontRocket(RocketStruct rocket, EntityRideableRocket entity, TextureManager tex, boolean isDeployed, float interp) {
		GlStateManager.pushMatrix();

		GlStateManager.shadeModel(GL11.GL_SMOOTH);

		boolean hasShroud = false;

		if(buffer == null)
			buffer = GLAllocation.createDirectByteBuffer(8 * 4).asDoubleBuffer(); // four doubles

		for(RocketStruct.RocketStage stage : rocket.stages) {
			int stack = stage.getStack();
			int cluster = stage.getCluster();

			if(isDeployed && stage.thruster != null && stage.fins != null && stage.fins.height > stage.thruster.height) {
				GlStateManager.translate(0, stage.fins.height - stage.thruster.height, 0);
			}

			for(int c = 0; c < cluster; c++) {
				GlStateManager.pushMatrix();
				{

					if(c > 0) {
						float spin = (float)c / (float)(cluster - 1);
						GlStateManager.rotate(360.0F * spin, 0, 1, 0);

						if(stage.fuselage != null) {
							GlStateManager.translate(stage.fuselage.part.bottom.radius, 0, 0);
						} else if(stage.thruster != null) {
							GlStateManager.translate(stage.thruster.part.top.radius, 0, 0);
						}
					}

					if(stage.thruster != null) {
						if(hasShroud && stage.fuselage != null) {
							tex.bindTexture(ResourceManager.universal);
							buffer.put(new double[] {0, -1, 0, stage.thruster.height});
							buffer.rewind();
							GL11.glEnable(GL11.GL_CLIP_PLANE0);
							GL11.glClipPlane(GL11.GL_CLIP_PLANE0, buffer);
							stage.fuselage.getShroud().renderAll();
							GL11.glDisable(GL11.GL_CLIP_PLANE0);
						} else {
							tex.bindTexture(stage.thruster.texture);
							stage.thruster.getModel(isDeployed).renderAll();
						}
						GlStateManager.translate(0, stage.thruster.height, 0);
					}

					if(stage.fuselage != null) {
						if(stage.fins != null) {
							tex.bindTexture(stage.fins.texture);
							stage.fins.getModel(isDeployed).renderAll();
						}

						for(int s = 0; s < stack; s++) {
							tex.bindTexture(stage.fuselage.texture);
							stage.fuselage.getModel(isDeployed).renderAll();
							GlStateManager.translate(0, stage.fuselage.height, 0);
						}
					}

				}
				GlStateManager.popMatrix();
			}


			if(stage.thruster != null) GlStateManager.translate(0, stage.thruster.height, 0);
			if(stage.fuselage != null) GlStateManager.translate(0, stage.fuselage.height * stack, 0);

			// Only the bottom-most stage can be deployed
			isDeployed = false;
			hasShroud = true;
		}

		if(rocket.capsule != null) {
			if(entity != null && rocket.capsule.renderer != null) {
				rocket.capsule.renderer.render(tex, entity, interp);
			} else {
				tex.bindTexture(rocket.capsule.texture);
				rocket.capsule.model.renderAll();
			}
		}

		GlStateManager.shadeModel(GL11.GL_FLAT);

		GlStateManager.popMatrix();
	}
}
