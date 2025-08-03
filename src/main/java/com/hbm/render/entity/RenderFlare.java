package com.hbm.render.entity;

import com.hbm.entity.grenade.EntityGrenadeFlare;
import com.hbm.entity.projectile.EntitySchrab;
import com.hbm.interfaces.AutoRegister;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11; import net.minecraft.client.renderer.GlStateManager;

import java.util.Random;
@AutoRegister(entity = EntityGrenadeFlare.class, factory = "FACTORY")
@AutoRegister(entity = EntitySchrab.class, factory = "FACTORY_SCHRAB")
public  class  RenderFlare <E extends Entity> extends Render<E> {

	public static final IRenderFactory<EntityGrenadeFlare> FACTORY = (RenderManager man) -> {return new RenderFlare<EntityGrenadeFlare>(man);};
	public static final IRenderFactory<EntitySchrab> FACTORY_SCHRAB = (RenderManager man) -> {return new RenderFlare<EntitySchrab>(man);};
	
	protected RenderFlare(RenderManager renderManager) {
		super(renderManager);
	}
	
	@Override
	public void doRender(E entity, double x, double y, double z, float entityYaw, float partialTicks) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buf = tessellator.getBuffer();
		RenderHelper.disableStandardItemLighting();
        float f1 = (entity.ticksExisted + 2.0F) / 250.0F;
        
        if(f1 > 1){
        	f1 = 1.0F;
        }
        
        float f2 = 0.0F;
        int count = 250;
        if(entity.ticksExisted < 250)
        {
        	count = entity.ticksExisted * 3;
        }

        if (f1 > 0.8F)
        {
            f2 = (f1 - 0.8F) / 0.2F;
        }

        Random random = new Random(432L);
        GlStateManager.disableTexture2D();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GlStateManager.disableAlpha();
        GlStateManager.enableCull();
        GL11.glDepthMask(false);
        GlStateManager.pushMatrix();
        GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT);
        //GlStateManager.translate(0.0F, -1.0F, -2.0F);

        GlStateManager.translate(x, y, z);
        
        //for (int i = 0; (float)i < (f1 + f1 * f1) / 2.0F * 60.0F; ++i)
        for(int i = 0; i < count; i++)
        {
            GlStateManager.rotate(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(random.nextFloat() * 360.0F + f1 * 90.0F, 0.0F, 0.0F, 1.0F);
            buf.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
            float f3 = random.nextFloat() * 20.0F + 5.0F + f2 * 10.0F;
            float f4 = random.nextFloat() * 2.0F + 1.0F + f2 * 2.0F;
            //tessellator.setColorRGBA_I(16777215, (int)(255.0F * (1.0F - f2)));
            buf.pos(0.0D, 0.0D, 0.0D).color(0.53725490196F, 0.54509803921F, 0.2F, 1.0F - f2).endVertex();
            //tessellator.setColorRGBA_I(16711935, 0);
            buf.pos(-0.866D * f4, f3, -0.5F * f4).color(0.53725490196F, 0.54509803921F, 0.27843137254F, 0.0F).endVertex();
            buf.pos(0.866D * f4, f3, -0.5F * f4).color(0.53725490196F, 0.54509803921F, 0.27843137254F, 0.0F).endVertex();
            buf.pos(0.0D, f3, 1.0F * f4).color(0.53725490196F, 0.54509803921F, 0.27843137254F, 0.0F).endVertex();
            buf.pos(-0.866D * f4, f3, -0.5F * f4).color(0.53725490196F, 0.54509803921F, 0.27843137254F, 0.0F).endVertex();
    		GlStateManager.scale(0.99F, 0.99F, 0.99F);
    		tessellator.draw();
        }
        
        
        GL11.glPopAttrib();
        GlStateManager.popMatrix();
        GL11.glDepthMask(true);
        GlStateManager.disableCull();
        GlStateManager.disableBlend();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
        RenderHelper.enableStandardItemLighting();
	}
	
	@Override
	public void doRenderShadowAndFire(Entity entityIn, double x, double y, double z, float yaw, float partialTicks) {}

	@Override
	protected ResourceLocation getEntityTexture(E entity) {
		return null;
	}

}
