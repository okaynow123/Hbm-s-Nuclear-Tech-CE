package com.hbm.render.entity.missile;

import com.hbm.entity.missile.EntitySoyuz;
import com.hbm.main.ResourceManager;
import com.hbm.render.misc.SoyuzPronter;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11; import net.minecraft.client.renderer.GlStateManager;

public class RenderSoyuz extends Render<EntitySoyuz> {

	public static final IRenderFactory<EntitySoyuz> FACTORY = (RenderManager man) -> {return new RenderSoyuz(man);};
	
	protected RenderSoyuz(RenderManager renderManager) {
		super(renderManager);
	}
	
	@Override
	public void doRender(EntitySoyuz entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
        GlStateManager.enableCull();
        
        int type = entity.getDataManager().get(EntitySoyuz.SKIN);
        SoyuzPronter.prontSoyuz(type);
		
		GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySoyuz entity) {
		//just so if there's a mod that is trying to pull a funny
		return ResourceManager.soyuz_payload;
	}

}
