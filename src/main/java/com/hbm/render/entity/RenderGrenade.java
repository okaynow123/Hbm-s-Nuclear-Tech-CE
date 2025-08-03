package com.hbm.render.entity;

import com.hbm.entity.grenade.EntityGrenadeASchrab;
import com.hbm.entity.grenade.EntityGrenadeMk2;
import com.hbm.interfaces.AutoRegister;
import com.hbm.main.ResourceManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11;
@AutoRegister(entity = EntityGrenadeMk2.class, factory = "FACTORY")
@AutoRegister(entity = EntityGrenadeASchrab.class, factory = "FACTORY")
public class RenderGrenade extends Render<Entity> {

	public static final IRenderFactory<Entity> FACTORY = man -> new RenderGrenade(man);
	
	protected RenderGrenade(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(Entity grenade, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
        GlStateManager.translate((float)x, (float)y + 0.125F, (float)z);
        GlStateManager.rotate(grenade.prevRotationYaw + (grenade.rotationYaw - grenade.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(grenade.prevRotationPitch + (grenade.rotationPitch - grenade.prevRotationPitch) * partialTicks, 0.0F, 0.0F, 1.0F);

        GlStateManager.rotate(90, 0F, 1F, 0F);
        GL11.glScaled(0.125, 0.125, 0.125);
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        bindTexture(getEntityTexture(grenade));

        if(grenade instanceof EntityGrenadeMk2) {
	        GlStateManager.rotate(90, 0F, 1F, 0F);
			ResourceManager.grenade_frag.renderAll();
        }
        if(grenade instanceof EntityGrenadeASchrab) {
	        GlStateManager.rotate(90, 0F, 1F, 0F);
			ResourceManager.grenade_aschrab.renderAll();
        }
        GlStateManager.shadeModel(GL11.GL_FLAT);
        
		GlStateManager.popMatrix();
	}
	
	@Override
	protected ResourceLocation getEntityTexture(Entity grenade) {
		if(grenade instanceof EntityGrenadeMk2) {
    		return ResourceManager.grenade_mk2;
        }
        if(grenade instanceof EntityGrenadeASchrab) {
    		return ResourceManager.grenade_aschrab_tex;
        }

		return null;
	}

}
