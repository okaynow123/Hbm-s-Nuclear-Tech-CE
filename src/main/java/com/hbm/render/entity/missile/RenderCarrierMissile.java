package com.hbm.render.entity.missile;

import com.hbm.entity.missile.EntityCarrier;
import com.hbm.main.ResourceManager;
import com.hbm.render.NTMRenderHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11; import net.minecraft.client.renderer.GlStateManager;

public class RenderCarrierMissile extends Render<EntityCarrier> {

	public static final IRenderFactory<EntityCarrier> FACTORY = (RenderManager man) -> {return new RenderCarrierMissile(man);};
	
	protected RenderCarrierMissile(RenderManager renderManager) {
		super(renderManager);
	}
	
	@Override
	public void doRender(EntityCarrier rocket, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
		double[] renderPos = NTMRenderHelper.getRenderPosFromMissile(rocket, partialTicks);
		x = renderPos[0];
		y = renderPos[1];
		z = renderPos[2];
		GlStateManager.translate(x, y, z);
		GlStateManager.scale(1.0F, 1.0F, 1.0F);
		
        GlStateManager.enableLighting();
        GlStateManager.disableCull();
        GlStateManager.scale(2F, 2F, 2F);
		bindTexture(ResourceManager.missileCarrier_tex);
		ResourceManager.missileCarrier.renderAll();
		
		if(rocket.getDataManager().get(EntityCarrier.HASBOOSTERS)) {
	        GlStateManager.translate(0.0D, 0.5D, 0.0D);
	        GlStateManager.translate(1.25D, 0.0D, 0.0D);
			bindTexture(ResourceManager.missileBooster_tex);
			ResourceManager.missileBooster.renderAll();
	        GlStateManager.translate(-2.5D, 0.0D, 0.0D);
			ResourceManager.missileBooster.renderAll();
	        GlStateManager.translate(1.25D, 0.0D, 0.0D);
	        GlStateManager.translate(0.0D, 0.0D, 1.25D);
			ResourceManager.missileBooster.renderAll();
	        GlStateManager.translate(0.0D, 0.0D, -2.5D);
			ResourceManager.missileBooster.renderAll();
	        GlStateManager.translate(0.0D, 0.0D, 1.25D);
		}

        GlStateManager.enableCull();
        GL11.glPopAttrib();
		GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityCarrier entity) {
		return ResourceManager.missileCarrier_tex;
	}

}
