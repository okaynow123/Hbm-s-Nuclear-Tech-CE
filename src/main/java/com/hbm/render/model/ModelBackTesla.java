package com.hbm.render.model;

import com.hbm.main.ResourceManager;
import com.hbm.render.loader.ModelRendererObj;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11; import net.minecraft.client.renderer.GlStateManager;

public class ModelBackTesla extends ModelArmorBase {

	public ModelBackTesla() {
		super(1);
		body = new ModelRendererObj(ResourceManager.armor_mod_tesla);
	}

	@Override
	public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7) {
		setRotationAngles(par2, par3, par4, par5, par6, par7, par1Entity);
		this.body.rotateAngleY += (float)Math.toRadians(par5);

		GlStateManager.pushMatrix();
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		
		Minecraft.getMinecraft().renderEngine.bindTexture(ResourceManager.mod_tesla);
		body.render(par7);
		
		GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.popMatrix();
	}
}