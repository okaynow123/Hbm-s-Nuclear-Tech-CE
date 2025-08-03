package com.hbm.render.entity;

import com.hbm.entity.projectile.EntityRainbow;
import com.hbm.interfaces.AutoRegister;
import com.hbm.lib.RefStrings;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11; import net.minecraft.client.renderer.GlStateManager;
@AutoRegister(factory = "FACTORY")
public class RenderRainbow extends Render<EntityRainbow> {

	public static final IRenderFactory<EntityRainbow> FACTORY = (RenderManager man) -> {return new RenderRainbow(man);};
	
	protected static ResourceLocation rainbow_rl = new ResourceLocation(RefStrings.MODID + ":textures/models/Rainbow.png");
	
	protected RenderRainbow(RenderManager renderManager) {
		super(renderManager);
	}
	
	@Override
	public void doRender(EntityRainbow rocket, double x, double y, double z, float entityYaw, float partialTicks) {
		float radius = 0.12F;
		int distance = 4;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buf = tessellator.getBuffer();

		GlStateManager.pushMatrix();
		GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT);
		GlStateManager.disableTexture2D();
		GlStateManager.disableCull();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GlStateManager.translate((float) x, (float) y, (float) z);

		GlStateManager.rotate(rocket.rotationYaw, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-rocket.rotationPitch, 1.0F, 0.0F, 0.0F);

		boolean red = false;
		boolean green = false;
		boolean blue = false;
		
		if(rocket.getDataManager() != null){
			red = rocket.getDataManager().get(EntityRainbow.RED);
			green = rocket.getDataManager().get(EntityRainbow.GREEN);
			blue = rocket.getDataManager().get(EntityRainbow.BLUE);
		}
		buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		for (float o = 0; o <= radius; o += radius / 8) {
			float color = 1f - (o * 8.333f);
			if (color < 0)
				color = 0;
			buf.pos(0 + o, 0 - o, 0).color(red ? 1 : color, green ? 1 : color, blue ? 1 : color, 1f).endVertex();
			buf.pos(0 + o, 0 + o, 0).color(red ? 1 : color, green ? 1 : color, blue ? 1 : color, 1f).endVertex();
			buf.pos(0 + o, 0 + o, 0 + distance).color(red ? 1 : color, green ? 1 : color, blue ? 1 : color, 1f).endVertex();
			buf.pos(0 + o, 0 - o, 0 + distance).color(red ? 1 : color, green ? 1 : color, blue ? 1 : color, 1f).endVertex();
			
			buf.pos(0 - o, 0 - o, 0).color(red ? 1 : color, green ? 1 : color, blue ? 1 : color, 1f).endVertex();
			buf.pos(0 + o, 0 - o, 0).color(red ? 1 : color, green ? 1 : color, blue ? 1 : color, 1f).endVertex();
			buf.pos(0 + o, 0 - o, 0 + distance).color(red ? 1 : color, green ? 1 : color, blue ? 1 : color, 1f).endVertex();
			buf.pos(0 - o, 0 - o, 0 + distance).color(red ? 1 : color, green ? 1 : color, blue ? 1 : color, 1f).endVertex();
			
			buf.pos(0 - o, 0 + o, 0).color(red ? 1 : color, green ? 1 : color, blue ? 1 : color, 1f).endVertex();
			buf.pos(0 - o, 0 - o, 0).color(red ? 1 : color, green ? 1 : color, blue ? 1 : color, 1f).endVertex();
			buf.pos(0 - o, 0 - o, 0 + distance).color(red ? 1 : color, green ? 1 : color, blue ? 1 : color, 1f).endVertex();
			buf.pos(0 - o, 0 + o, 0 + distance).color(red ? 1 : color, green ? 1 : color, blue ? 1 : color, 1f).endVertex();
			
			buf.pos(0 + o, 0 + o, 0).color(red ? 1 : color, green ? 1 : color, blue ? 1 : color, 1f).endVertex();
			buf.pos(0 - o, 0 + o, 0).color(red ? 1 : color, green ? 1 : color, blue ? 1 : color, 1f).endVertex();
			buf.pos(0 - o, 0 + o, 0 + distance).color(red ? 1 : color, green ? 1 : color, blue ? 1 : color, 1f).endVertex();
			buf.pos(0 + o, 0 + o, 0 + distance).color(red ? 1 : color, green ? 1 : color, blue ? 1 : color, 1f).endVertex();
		}
		tessellator.draw();
		GlStateManager.disableBlend();
		GlStateManager.enableTexture2D();
		GL11.glPopAttrib();
		GlStateManager.popMatrix();
	}
	
	@Override
	public void doRenderShadowAndFire(Entity entityIn, double x, double y, double z, float yaw, float partialTicks) {}

	@Override
	protected ResourceLocation getEntityTexture(EntityRainbow entity) {
		return rainbow_rl;
	}

}
