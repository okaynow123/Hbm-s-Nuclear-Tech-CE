package com.hbm.render.entity;

import com.hbm.entity.projectile.EntityBuilding;
import com.hbm.interfaces.AutoRegister;
import com.hbm.main.ResourceManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
@AutoRegister(factory = "FACTORY")
public class RenderBuilding extends Render<EntityBuilding> {

	public static final IRenderFactory<EntityBuilding> FACTORY = (RenderManager man) -> {return new RenderBuilding(man);};
	
	protected RenderBuilding(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntityBuilding entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.disableCull();
        
        bindTexture(ResourceManager.building_tex);
        ResourceManager.building.renderAll();
        
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
	}
	
	@Override
	protected ResourceLocation getEntityTexture(EntityBuilding entity) {
		return ResourceManager.building_tex;
	}

}
