package com.hbm.render.entity;

import com.hbm.entity.missile.EntityBobmazon;
import com.hbm.interfaces.AutoRegister;
import com.hbm.main.ResourceManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
@AutoRegister(factory = "FACTORY")
public class RenderBobmazon extends Render<EntityBobmazon> {

	public static final IRenderFactory<EntityBobmazon> FACTORY = (RenderManager man) -> {return new RenderBobmazon(man);};
	
	protected RenderBobmazon(RenderManager renderManager) {
		super(renderManager);
	}
	
	@Override
	public void doRender(EntityBobmazon entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
        //GL11.glRotated(180, 0, 0, 1);
        GlStateManager.disableCull();
        
        bindTexture(ResourceManager.bobmazon_tex);
        GlStateManager.rotate(180, 1, 0, 0);
        
        ResourceManager.minerRocket.renderAll();
        
        GlStateManager.enableCull();
		GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityBobmazon entity) {
		return ResourceManager.bobmazon_tex;
	}

	
}