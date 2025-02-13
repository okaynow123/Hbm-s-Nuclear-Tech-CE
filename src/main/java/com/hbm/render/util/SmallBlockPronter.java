package com.hbm.render.util;

import com.hbm.render.RenderHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.lwjgl.opengl.GL11;

public class SmallBlockPronter {

	static float pixel = 1F/16F;


	public static void startDrawing(){
		GlStateManager.enableBlend();
		GlStateManager.disableLighting();
		GlStateManager.enableCull();
		GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 0.75F);
		GlStateManager.disableAlpha();
	}

	/**
	 * Bind the required texture yourself bruh
	 * @param loc
	 * @param x
	 * @param y
	 * @param z
	 */
	public static void renderSimpleBlockAt(TextureAtlasSprite loc, float x, float y, float z) {
		RenderHelper.addVertexWithUV(x + 1 - 11 * pixel / 2, y + 1 - 11 * pixel / 2, z + 1 - 11 * pixel / 2, loc.getMaxU(), loc.getMinV());
		RenderHelper.addVertexWithUV(x + 11 * pixel / 2, y + 1 - 11 * pixel / 2, z + 1 - 11 * pixel / 2, loc.getMinU(), loc.getMinV());
		RenderHelper.addVertexWithUV(x + 11 * pixel / 2, y + 11 * pixel / 2, z + 1 - 11 * pixel / 2, loc.getMinU(), loc.getMaxV());
		RenderHelper.addVertexWithUV(x + 1 - 11 * pixel / 2, y + 11 * pixel / 2, z + 1 - 11 * pixel / 2, loc.getMaxU(), loc.getMaxV());

		RenderHelper.addVertexWithUV(x + 1 - 11 * pixel / 2, y + 1 - 11 * pixel / 2, z + 11 * pixel / 2, loc.getMaxU(), loc.getMinV());
		RenderHelper.addVertexWithUV(x + 1 - 11 * pixel / 2, y + 1 - 11 * pixel / 2, z + 1 - 11 * pixel / 2, loc.getMinU(), loc.getMinV());
		RenderHelper.addVertexWithUV(x + 1 - 11 * pixel / 2, y + 11 * pixel / 2, z + 1 - 11 * pixel / 2, loc.getMinU(), loc.getMaxV());
		RenderHelper.addVertexWithUV(x + 1 - 11 * pixel / 2, y + 11 * pixel / 2, z + 11 * pixel / 2, loc.getMaxU(), loc.getMaxV());

		RenderHelper.addVertexWithUV(x + 11 * pixel / 2, y + 1 - 11 * pixel / 2, z + 11 * pixel / 2, loc.getMaxU(), loc.getMinV());
		RenderHelper.addVertexWithUV(x + 1 - 11 * pixel / 2, y + 1 - 11 * pixel / 2, z + 11 * pixel / 2, loc.getMinU(), loc.getMinV());
		RenderHelper.addVertexWithUV(x + 1 - 11 * pixel / 2, y + 11 * pixel / 2, z + 11 * pixel / 2, loc.getMinU(), loc.getMaxV());
		RenderHelper.addVertexWithUV(x + 11 * pixel / 2, y + 11 * pixel / 2, z + 11 * pixel / 2, loc.getMaxU(), loc.getMaxV());

		RenderHelper.addVertexWithUV(x + 11 * pixel / 2, y + 1 - 11 * pixel / 2, z + 1 - 11 * pixel / 2, loc.getMaxU(), loc.getMinV());
		RenderHelper.addVertexWithUV(x + 11 * pixel / 2, y + 1 - 11 * pixel / 2, z + 11 * pixel / 2, loc.getMinU(), loc.getMinV());
		RenderHelper.addVertexWithUV(x + 11 * pixel / 2, y + 11 * pixel / 2, z + 11 * pixel / 2, loc.getMinU(), loc.getMaxV());
		RenderHelper.addVertexWithUV(x + 11 * pixel / 2, y + 11 * pixel / 2, z + 1 - 11 * pixel / 2, loc.getMaxU(), loc.getMaxV());

		RenderHelper.addVertexWithUV(x + 1 - 11 * pixel / 2, y + 1 - 11 * pixel / 2, z + 11 * pixel / 2, loc.getMaxU(), loc.getMinV());
		RenderHelper.addVertexWithUV(x + 11 * pixel / 2, y + 1 - 11 * pixel / 2, z + 11 * pixel / 2, loc.getMinU(), loc.getMinV());
		RenderHelper.addVertexWithUV(x + 11 * pixel / 2, y + 1 - 11 * pixel / 2, z + 1 - 11 * pixel / 2, loc.getMinU(), loc.getMaxV());
		RenderHelper.addVertexWithUV(x + 1 - 11 * pixel / 2, y + 1 - 11 * pixel / 2, z + 1 - 11 * pixel / 2, loc.getMaxU(), loc.getMaxV());

		RenderHelper.addVertexWithUV(x + 11 * pixel / 2, y + 11 * pixel / 2, z + 11 * pixel / 2, loc.getMaxU(), loc.getMinV());
		RenderHelper.addVertexWithUV(x + 1 - 11 * pixel / 2, y + 11 * pixel / 2, z + 11 * pixel / 2, loc.getMinU(), loc.getMinV());
		RenderHelper.addVertexWithUV(x + 1 - 11 * pixel / 2, y + 11 * pixel / 2, z + 1 - 11 * pixel / 2, loc.getMinU(), loc.getMaxV());
		RenderHelper.addVertexWithUV(x + 11 * pixel / 2, y + 11 * pixel / 2, z + 1 - 11 * pixel / 2, loc.getMaxU(), loc.getMaxV());

	}




	public static void renderPilarBlockAt(TextureAtlasSprite end, TextureAtlasSprite side, float x, float y, float z) {
		// Front face
		RenderHelper.addVertexWithUV(x + 1 - 11 * pixel / 2, y + 1 - 11 * pixel / 2, z + 1 - 11 * pixel / 2, side.getMaxU(), side.getMinV());
		RenderHelper.addVertexWithUV(x + 11 * pixel / 2, y + 1 - 11 * pixel / 2, z + 1 - 11 * pixel / 2, side.getMinU(), side.getMinV());
		RenderHelper.addVertexWithUV(x + 11 * pixel / 2, y + 11 * pixel / 2, z + 1 - 11 * pixel / 2, side.getMinU(), side.getMaxV());
		RenderHelper.addVertexWithUV(x + 1 - 11 * pixel / 2, y + 11 * pixel / 2, z + 1 - 11 * pixel / 2, side.getMaxU(), side.getMaxV());

		// Left face
		RenderHelper.addVertexWithUV(x + 1 - 11 * pixel / 2, y + 1 - 11 * pixel / 2, z + 11 * pixel / 2, side.getMaxU(), side.getMinV());
		RenderHelper.addVertexWithUV(x + 1 - 11 * pixel / 2, y + 1 - 11 * pixel / 2, z + 1 - 11 * pixel / 2, side.getMinU(), side.getMinV());
		RenderHelper.addVertexWithUV(x + 1 - 11 * pixel / 2, y + 11 * pixel / 2, z + 1 - 11 * pixel / 2, side.getMinU(), side.getMaxV());
		RenderHelper.addVertexWithUV(x + 1 - 11 * pixel / 2, y + 11 * pixel / 2, z + 11 * pixel / 2, side.getMaxU(), side.getMaxV());

		// Right face
		RenderHelper.addVertexWithUV(x + 11 * pixel / 2, y + 1 - 11 * pixel / 2, z + 11 * pixel / 2, side.getMaxU(), side.getMinV());
		RenderHelper.addVertexWithUV(x + 1 - 11 * pixel / 2, y + 1 - 11 * pixel / 2, z + 11 * pixel / 2, side.getMinU(), side.getMinV());
		RenderHelper.addVertexWithUV(x + 1 - 11 * pixel / 2, y + 11 * pixel / 2, z + 11 * pixel / 2, side.getMinU(), side.getMaxV());
		RenderHelper.addVertexWithUV(x + 11 * pixel / 2, y + 11 * pixel / 2, z + 11 * pixel / 2, side.getMaxU(), side.getMaxV());

		// Back face
		RenderHelper.addVertexWithUV(x + 11 * pixel / 2, y + 1 - 11 * pixel / 2, z + 1 - 11 * pixel / 2, side.getMaxU(), side.getMinV());
		RenderHelper.addVertexWithUV(x + 11 * pixel / 2, y + 1 - 11 * pixel / 2, z + 11 * pixel / 2, side.getMinU(), side.getMinV());
		RenderHelper.addVertexWithUV(x + 11 * pixel / 2, y + 11 * pixel / 2, z + 11 * pixel / 2, side.getMinU(), side.getMaxV());
		RenderHelper.addVertexWithUV(x + 11 * pixel / 2, y + 11 * pixel / 2, z + 1 - 11 * pixel / 2, side.getMaxU(), side.getMaxV());

		// Bottom face
		RenderHelper.addVertexWithUV(x + 1 - 11 * pixel / 2, y + 1 - 11 * pixel / 2, z + 11 * pixel / 2, end.getMaxU(), end.getMinV());
		RenderHelper.addVertexWithUV(x + 11 * pixel / 2, y + 1 - 11 * pixel / 2, z + 11 * pixel / 2, end.getMinU(), end.getMinV());
		RenderHelper.addVertexWithUV(x + 11 * pixel / 2, y + 1 - 11 * pixel / 2, z + 1 - 11 * pixel / 2, end.getMinU(), end.getMaxV());
		RenderHelper.addVertexWithUV(x + 1 - 11 * pixel / 2, y + 1 - 11 * pixel / 2, z + 1 - 11 * pixel / 2, end.getMaxU(), end.getMaxV());

		// Top face
		RenderHelper.addVertexWithUV(x + 11 * pixel / 2, y + 11 * pixel / 2, z + 11 * pixel / 2, end.getMaxU(), end.getMinV());
		RenderHelper.addVertexWithUV(x + 1 - 11 * pixel / 2, y + 11 * pixel / 2, z + 11 * pixel / 2, end.getMinU(), end.getMinV());
		RenderHelper.addVertexWithUV(x + 1 - 11 * pixel / 2, y + 11 * pixel / 2, z + 1 - 11 * pixel / 2, end.getMinU(), end.getMaxV());
		RenderHelper.addVertexWithUV(x + 11 * pixel / 2, y + 11 * pixel / 2, z + 1 - 11 * pixel / 2, end.getMaxU(), end.getMaxV());
	}

}
