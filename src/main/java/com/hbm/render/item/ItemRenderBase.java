package com.hbm.render.item;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class ItemRenderBase extends TEISRBase {

	@Override
	public void renderByItem(ItemStack itemStackIn) {
		GlStateManager.pushMatrix();
		GlStateManager.enableCull();

		switch (type) {
			case FIRST_PERSON_LEFT_HAND:
			case FIRST_PERSON_RIGHT_HAND:
			case THIRD_PERSON_LEFT_HAND:
			case THIRD_PERSON_RIGHT_HAND:
				GlStateManager.rotate(-90F, 0F, 1F, 0F);
				GlStateManager.scale(0.4F, 0.4F, 0.4F);
				break;

			case HEAD:
			case FIXED:
			case GROUND:
				GlStateManager.scale(0.4F, 0.4F, 0.4F);
				GlStateManager.rotate(-90F, 0F, 1F, 0F);
				GlStateManager.translate(2.5F, 0F, 0F);
				renderNonInv(itemStackIn);
				break;

			case GUI:
				GlStateManager.enableLighting();
				GlStateManager.rotate(30F, 1F, 0F, 0F);
				GlStateManager.rotate(225F, 0F, 1F, 0F); // 45 + 180
				GlStateManager.scale(0.062F, 0.062F, 0.062F);
				GlStateManager.translate(0F, 12F, -11.3F);
				renderInventory(itemStackIn);
				break;

			case NONE:
				break;
		}

		renderCommon(itemStackIn);
		GlStateManager.popMatrix();
	}



	public void renderNonInv(ItemStack stack) { renderNonInv(); }
	public void renderInventory(ItemStack stack) { renderInventory(); }
	public void renderCommon(ItemStack stack) { renderCommon(); }
	public void renderNonInv() { }
	public void renderInventory() { }
	public void renderCommon() { }
}
