package com.hbm.render.model;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

import com.hbm.main.ResourceManager;
import com.hbm.render.loader.ModelRendererObj;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;

public class ModelArmorEnvsuit extends ModelArmorBase {

	ModelRendererObj lamps;

	public ModelArmorEnvsuit(int type) {
		super(type);

		this.head = new ModelRendererObj(ResourceManager.armor_envsuit, "Helmet");
		this.lamps = new ModelRendererObj(ResourceManager.armor_envsuit, "Lamps");
		this.body = new ModelRendererObj(ResourceManager.armor_envsuit, "Chest");
		this.leftArm = new ModelRendererObj(ResourceManager.armor_envsuit, "LeftArm").setRotationPoint(5.0F, 2.0F, 0.0F);
		this.rightArm = new ModelRendererObj(ResourceManager.armor_envsuit, "RightArm").setRotationPoint(-5.0F, 2.0F, 0.0F);
		this.leftLeg = new ModelRendererObj(ResourceManager.armor_envsuit, "LeftLeg").setRotationPoint(1.9F, 12.0F, 0.0F);
		this.rightLeg = new ModelRendererObj(ResourceManager.armor_envsuit, "RightLeg").setRotationPoint(-1.9F, 12.0F, 0.0F);
		this.leftFoot = new ModelRendererObj(ResourceManager.armor_envsuit, "LeftFoot").setRotationPoint(1.9F, 12.0F, 0.0F);
		this.rightFoot = new ModelRendererObj(ResourceManager.armor_envsuit, "RightFoot").setRotationPoint(-1.9F, 12.0F, 0.0F);
	}

	@Override
	public void renderArmor(Entity par1Entity, float par7) {
		switch (type) {
			case 0 -> {
				this.head.copyTo(this.lamps);
				Minecraft.getMinecraft().renderEngine.bindTexture(ResourceManager.envsuit_helmet);
				GL11.glEnable(GL11.GL_BLEND);
				OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
				this.head.render(par7);
				GL11.glDisable(GL11.GL_BLEND);

				/// START GLOW ///
				float lastX = OpenGlHelper.lastBrightnessX;
				float lastY = OpenGlHelper.lastBrightnessY;
				GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glColor3f(1F, 1F, 0.8F);
				this.lamps.render(par7);
				GL11.glColor3f(1F, 1F, 1F);
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glPopAttrib();
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastX, lastY);
				/// END GLOW ///

			}
			case 1 -> {
				Minecraft.getMinecraft().renderEngine.bindTexture(ResourceManager.envsuit_chest);
				body.render(par7);
				Minecraft.getMinecraft().renderEngine.bindTexture(ResourceManager.envsuit_arm);
				leftArm.render(par7);
				rightArm.render(par7);
			}
			case 2 -> {
				Minecraft.getMinecraft().renderEngine.bindTexture(ResourceManager.envsuit_leg);
				leftLeg.render(par7);
				rightLeg.render(par7);
			}
			case 3 -> {
				Minecraft.getMinecraft().renderEngine.bindTexture(ResourceManager.envsuit_leg);
				leftFoot.render(par7);
				rightFoot.render(par7);
			}
		}
	}
}
