package com.hbm.render.tileentity;

import com.hbm.interfaces.AutoRegister;
import com.hbm.main.ResourceManager;
import com.hbm.tileentity.machine.TileEntitySoyuzCapsule;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import org.lwjgl.opengl.GL11;
@AutoRegister
public class RenderCapsule extends TileEntitySpecialRenderer<TileEntitySoyuzCapsule> {

	@Override
	public boolean isGlobalRenderer(TileEntitySoyuzCapsule te) {
		return true;
	}
	
	@Override
	public void render(TileEntitySoyuzCapsule te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y, z + 0.5);
        GlStateManager.enableLighting();

        GlStateManager.translate(0.0F, -0.25F, 0.0F);
        GlStateManager.rotate(-25, 0, 1, 0);
        GlStateManager.rotate(15, 0, 0, 1);
        
        GlStateManager.enableCull();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        if(te.getBlockMetadata() > 0)
        	bindTexture(ResourceManager.soyuz_lander_rust_tex);
        else
        	bindTexture(ResourceManager.soyuz_lander_tex);
        ResourceManager.soyuz_lander.renderPart("Capsule");
        GlStateManager.shadeModel(GL11.GL_FLAT);
        
        GlStateManager.popMatrix();
	}
}
