package com.hbm.render.entity.mob;

import com.hbm.entity.mob.sodtekhnologiyah.EntityBallsOTronSegment;
import com.hbm.hfr.render.loader.HFRWavefrontObject;
import com.hbm.lib.RefStrings;
import com.hbm.main.ResourceManager;
import com.hbm.render.amlfrom1710.IModelCustom;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11; import net.minecraft.client.renderer.GlStateManager;

public class RenderBalls extends Render<EntityBallsOTronSegment> {

	public static final IRenderFactory<EntityBallsOTronSegment> FACTORY = man -> new RenderBalls(man);
	
	protected RenderBalls(RenderManager renderManager) {
		super(renderManager);
		this.shadowOpaque = 0;
	}

	public static final IModelCustom capsule = new HFRWavefrontObject(new ResourceLocation(RefStrings.MODID, "models/mobs/capsule.obj"));
	
	@Override
	public void doRender(EntityBallsOTronSegment entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks - 90, 0.0F, 0.0F, 1.0F);

		this.bindEntityTexture(entity);
		capsule.renderAll();

		GlStateManager.popMatrix();
	}
	
	@Override
	protected ResourceLocation getEntityTexture(EntityBallsOTronSegment entity) {
		return ResourceManager.turbofan_blades_tex;
	}

	
}
