package com.hbm.render.entity;

import com.hbm.entity.projectile.EntityRocketHoming;
import com.hbm.interfaces.AutoRegister;
import com.hbm.lib.RefStrings;
import com.hbm.render.model.ModelSRocket;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
@AutoRegister(factory = "FACTORY")
public class RenderSRocket extends Render<EntityRocketHoming> {

	public static final IRenderFactory<EntityRocketHoming> FACTORY = (RenderManager man) -> {return new RenderSRocket(man);};
	
	private static final ResourceLocation texture = new ResourceLocation(RefStrings.MODID + ":textures/models/projectiles/ModelSRocket.png");
	private ModelSRocket missile;
	
	protected RenderSRocket(RenderManager renderManager) {
		super(renderManager);
		missile = new ModelSRocket();
	}
	
	@Override
	public void doRender(EntityRocketHoming rocket, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x, (float) y, (float) z);
		GlStateManager.rotate(rocket.prevRotationYaw + (rocket.rotationYaw - rocket.prevRotationYaw) * partialTicks - 90.0F,
				0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(rocket.prevRotationPitch + (rocket.rotationPitch - rocket.prevRotationPitch) * partialTicks + 180,
				0.0F, 0.0F, 1.0F);
		GlStateManager.scale(1.5F, 1.5F, 1.5F);

		if(rocket.getIsCritical())
			bindTexture(new ResourceLocation(RefStrings.MODID + ":textures/models/projectiles/ModelSVTRocket.png"));
		else
			bindTexture(new ResourceLocation(RefStrings.MODID + ":textures/models/projectiles/ModelSRocket.png"));
		missile.renderAll(0.0625F);
		GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityRocketHoming entity) {
		return texture;
	}

}
