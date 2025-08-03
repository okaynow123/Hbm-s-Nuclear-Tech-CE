package com.hbm.render.tileentity;

import com.hbm.interfaces.AutoRegister;
import com.hbm.lib.RefStrings;
import com.hbm.render.model.ModelGeiger;
import com.hbm.tileentity.machine.TileEntityGeiger;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
@AutoRegister
public class RenderGeiger extends TileEntitySpecialRenderer<TileEntityGeiger> {

	private static final ResourceLocation texture9 = new ResourceLocation(RefStrings.MODID + ":" + "textures/models/tools/ModelGeiger.png");
	private ModelGeiger model8;
	
	public RenderGeiger() {
		this.model8 = new ModelGeiger();
	}
	
	@Override
	public void render(TileEntityGeiger te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GlStateManager.rotate(180, 0F, 0F, 1F);
		GL11.glRotated(180, 0, 1, 0);
		GlStateManager.enableLighting();
		GlStateManager.enableLighting();
		this.bindTexture(texture9);
		switch(te.getBlockMetadata())
		{
		case 4:
			GlStateManager.rotate(90, 0F, 1F, 0F); break;
		case 2:
			GlStateManager.rotate(180, 0F, 1F, 0F); break;
		case 5:
			GlStateManager.rotate(270, 0F, 1F, 0F); break;
		case 3:
			GlStateManager.rotate(0, 0F, 1F, 0F); break;
		}
		this.model8.renderModel(0.0625F);
		GlStateManager.popMatrix();
	}
}
