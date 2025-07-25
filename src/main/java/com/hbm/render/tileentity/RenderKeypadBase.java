package com.hbm.render.tileentity;

import com.hbm.tileentity.TileEntityKeypadBase;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import org.lwjgl.opengl.GL11; import net.minecraft.client.renderer.GlStateManager;

public class RenderKeypadBase extends TileEntitySpecialRenderer<TileEntityKeypadBase> {

	@Override
	public void render(TileEntityKeypadBase te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x+0.5, y, z+0.5);
		te.keypad.client().render();
		GlStateManager.popMatrix();
	}
}
