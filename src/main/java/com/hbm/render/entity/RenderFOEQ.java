package com.hbm.render.entity;

import com.hbm.entity.projectile.EntityBurningFOEQ;
import com.hbm.interfaces.AutoRegister;
import com.hbm.main.ResourceManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11;

import java.util.Random;
@AutoRegister(factory = "FACTORY")
public class RenderFOEQ extends Render<EntityBurningFOEQ> {

	public static final IRenderFactory<EntityBurningFOEQ> FACTORY = RenderFOEQ::new;

	public RenderFOEQ(RenderManager renderManager) {
		super(renderManager);
	}
	
	@Override
	public void doRender(EntityBurningFOEQ e, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		GL11.glPushAttrib(GL11.GL_CURRENT_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_LIGHTING_BIT);
        GlStateManager.translate((float)x, (float)y - 10, (float)z);
        GlStateManager.rotate(e.prevRotationYaw + (e.rotationYaw - e.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(180, 0F, 0F, 1F);
        GlStateManager.rotate(e.prevRotationPitch + (e.rotationPitch - e.prevRotationPitch) * partialTicks, 0.0F, 0.0F, 1.0F);
        
        GlStateManager.enableLighting();
		//GL11.glScaled(5, 5, 5);
        GlStateManager.enableCull();
		bindTexture(ResourceManager.sat_foeq_burning_tex);
		ResourceManager.sat_foeq_burning.renderAll();
		
		GlStateManager.disableTexture2D();
		GlStateManager.disableCull();
		GlStateManager.enableBlend();
		
		Random rand = new Random(System.currentTimeMillis() / 50);

        GL11.glScaled(1.15, 0.75, 1.15);
        GlStateManager.translate(0, -0.5, 0.3);
        GlStateManager.disableCull();
		for(int i = 0; i < 10; i++) {
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			GL11.glColor3d(1, 0.75, 0.25);
	        GlStateManager.rotate(rand.nextInt(360), 0F, 1F, 0F);
			ResourceManager.sat_foeq_fire.renderAll();
	        GlStateManager.translate(0, 2, 0);
			GL11.glColor3d(1, 0.5, 0);
	        GlStateManager.rotate(rand.nextInt(360), 0F, 1F, 0F);
			ResourceManager.sat_foeq_fire.renderAll();
	        GlStateManager.translate(0, 2, 0);
			GL11.glColor3d(1, 0.25, 0);
	        GlStateManager.rotate(rand.nextInt(360), 0F, 1F, 0F);
			ResourceManager.sat_foeq_fire.renderAll();
	        GlStateManager.translate(0, 2, 0);
			GL11.glColor3d(1, 0.15, 0);
	        GlStateManager.rotate(rand.nextInt(360), 0F, 1F, 0F);
			ResourceManager.sat_foeq_fire.renderAll();
			
	        GlStateManager.translate(0, -3.8, 0);
	        
	        GL11.glScaled(0.95, 1.2, 0.95);
		}
		
		GlStateManager.disableBlend();
		GlStateManager.enableTexture2D();
        GL11.glPopAttrib();
		GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityBurningFOEQ entity) {
		return ResourceManager.sat_foeq_tex;
	}

}
