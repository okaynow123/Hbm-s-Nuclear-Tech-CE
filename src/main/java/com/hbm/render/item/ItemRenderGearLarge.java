package com.hbm.render.item;

import static com.hbm.render.NTMRenderHelper.bindTexture;

import com.hbm.main.ResourceManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;

public class ItemRenderGearLarge extends ItemRenderBase {
  @Override
  public void renderInventory() {
    GlStateManager.translate(3, -5, 0);
    GlStateManager.scale(6, 6, 6);
    GlStateManager.rotate(-45, 0, 1, 0);
    GlStateManager.rotate(30, 1, 0, 0);
    GlStateManager.rotate(90, 1, 0, 0);
    GlStateManager.translate(0, 1.375, 0);
    GlStateManager.rotate(System.currentTimeMillis() % 3600 * 0.1F, 0, 0, 1);
    GlStateManager.translate(0, -1.375, 0);
  }

  @Override
  public void renderCommon(ItemStack item) {
    GlStateManager.translate(0, 0, -0.875);

    if (item.getMetadata() == 1) {
      bindTexture(ResourceManager.stirling_steel_tex);
    } else {
      bindTexture(ResourceManager.stirling_tex);
    }
    ResourceManager.stirling.renderPart("Cog");
  }
}
