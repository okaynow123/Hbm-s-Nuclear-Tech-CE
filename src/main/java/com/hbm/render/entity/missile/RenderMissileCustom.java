package com.hbm.render.entity.missile;

import com.hbm.entity.missile.EntityMissileCustom;
import com.hbm.interfaces.AutoRegister;
import com.hbm.main.ResourceManager;
import com.hbm.render.NTMRenderHelper;
import com.hbm.render.misc.MissileMultipart;
import com.hbm.render.misc.MissilePart;
import com.hbm.render.misc.MissilePronter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
@AutoRegister(factory = "FACTORY")
public class RenderMissileCustom extends Render<EntityMissileCustom> {

	public static final IRenderFactory<EntityMissileCustom> FACTORY = (RenderManager man) -> {return new RenderMissileCustom(man);};
	
	protected RenderMissileCustom(RenderManager renderManager) {
		super(renderManager);
	}
	
	@Override
	public void doRender(EntityMissileCustom entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		double[] pos = NTMRenderHelper.getRenderPosFromMissile(entity, partialTicks);
		x = pos[0];
		y = pos[1];
		z = pos[2];
		GlStateManager.translate(x, y, z);
        GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, 0.0F, 0.0F, 1.0F);
		int w = entity.getDataManager().get(entity.WARHEAD);
		int f = entity.getDataManager().get(entity.FUSELAGE);
		int s = entity.getDataManager().get(entity.FINS);
		int t = entity.getDataManager().get(entity.THRUSTER);
		MissileMultipart missile = new MissileMultipart();
		missile.warhead = MissilePart.getPart(Item.getItemById(w));
		missile.fuselage = MissilePart.getPart(Item.getItemById(f));
		missile.fins = MissilePart.getPart(Item.getItemById(s));
		missile.thruster = MissilePart.getPart(Item.getItemById(t));
        MissilePronter.prontMissile(missile, Minecraft.getMinecraft().getTextureManager());
        
		GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityMissileCustom entity) {
		return ResourceManager.universal;
	}

}
