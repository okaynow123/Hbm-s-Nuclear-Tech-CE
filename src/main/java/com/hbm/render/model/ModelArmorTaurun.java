package com.hbm.render.model;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

import com.hbm.main.ResourceManager;
import com.hbm.render.loader.ModelRendererObj;

import net.minecraft.entity.Entity;

public class ModelArmorTaurun extends ModelArmorBase {

	public ModelArmorTaurun(int type) {
		super(type);

		this.head = new ModelRendererObj(ResourceManager.armor_taurun, "Helmet");
		this.body = new ModelRendererObj(ResourceManager.armor_taurun, "Chest");
		this.leftArm = new ModelRendererObj(ResourceManager.armor_taurun, "LeftArm").setRotationPoint(5.0F, 2.0F, 0.0F);
		this.rightArm = new ModelRendererObj(ResourceManager.armor_taurun, "RightArm").setRotationPoint(-5.0F, 2.0F, 0.0F);
		this.leftLeg = new ModelRendererObj(ResourceManager.armor_taurun, "LeftLeg").setRotationPoint(1.9F, 12.0F, 0.0F);
		this.rightLeg = new ModelRendererObj(ResourceManager.armor_taurun, "RightLeg").setRotationPoint(-1.9F, 12.0F, 0.0F);
		this.leftFoot = new ModelRendererObj(ResourceManager.armor_taurun, "LeftBoot").setRotationPoint(1.9F, 12.0F, 0.0F);
		this.rightFoot = new ModelRendererObj(ResourceManager.armor_taurun, "RightBoot").setRotationPoint(-1.9F, 12.0F, 0.0F);
	}

	@Override
	public void renderArmor(Entity par1Entity, float par7) {
		switch (type) {
			case 0 -> {
				Minecraft.getMinecraft().renderEngine.bindTexture(ResourceManager.taurun_helmet);
				head.render(par7 * 1.001F);
			}
			case 1 -> {
				Minecraft.getMinecraft().renderEngine.bindTexture(ResourceManager.taurun_chest);
				body.render(par7);
				Minecraft.getMinecraft().renderEngine.bindTexture(ResourceManager.taurun_arm);
				leftArm.render(par7);
				rightArm.render(par7);
			}
			case 2 -> {
				Minecraft.getMinecraft().renderEngine.bindTexture(ResourceManager.taurun_leg);
				leftLeg.render(par7);
				rightLeg.render(par7);
			}
			case 3 -> {
				Minecraft.getMinecraft().renderEngine.bindTexture(ResourceManager.taurun_leg);
				leftFoot.render(par7);
				rightFoot.render(par7);
			}
		}
	}
}
