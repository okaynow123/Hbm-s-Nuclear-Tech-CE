package com.hbm.render.entity;

import com.hbm.entity.effect.EntityEMPBlast;
import com.hbm.hfr.render.loader.HFRWavefrontObject;
import com.hbm.lib.RefStrings;
import com.hbm.render.amlfrom1710.IModelCustom;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11; import net.minecraft.client.renderer.GlStateManager;

public class RenderEMPBlast extends Render<EntityEMPBlast> {

	public static final IRenderFactory<EntityEMPBlast> FACTORY = (RenderManager man) -> {return new RenderEMPBlast(man);};
	
	private static final ResourceLocation ringModelRL = new ResourceLocation(/*"/assets/" + */RefStrings.MODID, "models/Ring.obj");
	private IModelCustom ringModel;
    private ResourceLocation ringTexture;
	
	protected RenderEMPBlast(RenderManager renderManager) {
		super(renderManager);
		ringModel = new HFRWavefrontObject(ringModelRL);
    	ringTexture = new ResourceLocation(RefStrings.MODID, "textures/models/explosion/EMPBlast.png");
	}
	
	@Override
	public void doRender(EntityEMPBlast entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
		GlStateManager.translate(x, y, z);
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.scale(entity.scale+partialTicks, 1F, entity.scale+partialTicks);
        
        bindTexture(ringTexture);
        ringModel.renderAll();
        GL11.glPopAttrib();
        GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityEMPBlast entity) {
		return ringTexture;
	}

}
