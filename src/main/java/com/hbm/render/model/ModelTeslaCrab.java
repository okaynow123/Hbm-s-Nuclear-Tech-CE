package com.hbm.render.model;

import com.hbm.main.ResourceManager;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11; import net.minecraft.client.renderer.GlStateManager;

public class ModelTeslaCrab extends ModelBase {

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		
		GlStateManager.pushMatrix();

		GlStateManager.rotate(180, 0, 0, 1);
		GlStateManager.translate(0, -1.5F, 0);

		float rot = -(MathHelper.cos(f * 0.6662F * 2.0F + 0.0F) * 0.4F) * f1 * 57.3F;
		
		ResourceManager.teslacrab.renderPart("Body");

		GlStateManager.pushMatrix();
		GlStateManager.rotate(rot, 0, 1, 0);
		ResourceManager.teslacrab.renderPart("Front");
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.rotate(rot, 0, -1, 0);
		ResourceManager.teslacrab.renderPart("Back");
		GlStateManager.popMatrix();
		
		GlStateManager.popMatrix();
	}

}
