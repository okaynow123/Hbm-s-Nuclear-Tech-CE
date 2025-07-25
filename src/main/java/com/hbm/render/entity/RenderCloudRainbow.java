package com.hbm.render.entity;

import com.hbm.entity.effect.EntityCloudFleijaRainbow;
import com.hbm.hfr.render.loader.HFRWavefrontObject;
import com.hbm.lib.RefStrings;
import com.hbm.render.amlfrom1710.IModelCustom;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11; import net.minecraft.client.renderer.GlStateManager;

public class RenderCloudRainbow extends Render<EntityCloudFleijaRainbow> {

	private static final ResourceLocation objTesterModelRL = new ResourceLocation(/*"/assets/" + */RefStrings.MODID, "models/sphere.obj");
	private IModelCustom blastModel;
    public float scale = 0;
    public float ring = 0;
	//Drillgon200: Hey I figured out how to use a lambda!
    public static final IRenderFactory<EntityCloudFleijaRainbow> FACTORY = (RenderManager manage) -> {return new RenderCloudRainbow(manage);};
    
	protected RenderCloudRainbow(RenderManager renderManager) {
		super(renderManager);
		//Drillgon200: Yes, I know, I shouldn't be using advanced model loader anymore
		blastModel = new HFRWavefrontObject(objTesterModelRL);
    	scale = 0;
	}
	
	@Override
	public void doRender(EntityCloudFleijaRainbow cloud, double x, double y, double z, float entityYaw,
			float partialTicks) {
		GlStateManager.pushMatrix();
		GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_CURRENT_BIT);
        GlStateManager.translate(x, y, z);
        GlStateManager.disableLighting();
        GlStateManager.enableCull();
		GlStateManager.disableTexture2D();
        
        GlStateManager.scale(cloud.age+partialTicks, cloud.age+partialTicks, cloud.age+partialTicks);

		GL11.glColor3ub((byte)cloud.world.rand.nextInt(0x100), (byte)cloud.world.rand.nextInt(0x100), (byte)cloud.world.rand.nextInt(0x100));

        GlStateManager.scale(0.5F, 0.5F, 0.5F);
        blastModel.renderAll();
        GlStateManager.scale(1/0.5F, 1/0.5F, 1/0.5F);

		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		
        for(float i = 0.6F; i <= 1F; i += 0.1F) {

    		GL11.glColor3ub((byte)cloud.world.rand.nextInt(0x100), (byte)cloud.world.rand.nextInt(0x100), (byte)cloud.world.rand.nextInt(0x100));
    		
            GlStateManager.scale(i, i, i);
            blastModel.renderAll();
            GlStateManager.scale(1/i, 1/i, 1/i);
        }
        
		GlStateManager.disableBlend();
        GlStateManager.enableLighting();
		GlStateManager.enableTexture2D();
        GlStateManager.disableCull();
        
        GL11.glPopAttrib();
        GlStateManager.popMatrix();
	}

	@Override
	public void doRenderShadowAndFire(Entity entityIn, double x, double y, double z, float yaw, float partialTicks) {
	}
	
	@Override
	protected ResourceLocation getEntityTexture(EntityCloudFleijaRainbow entity) {
		return null;
	}

}
