package com.hbm.render.tileentity;

import com.hbm.blocks.ModBlocks;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.oil.TileEntitySpacer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11; import net.minecraft.client.renderer.GlStateManager;

public class RenderSpacer extends TileEntitySpecialRenderer<TileEntitySpacer>
    implements IItemRendererProvider {

  @Override
  public boolean isGlobalRenderer(TileEntitySpacer te) {
    return true;
  }

  @Override
  public void render(
      TileEntitySpacer te,
      double x,
      double y,
      double z,
      float partialTicks,
      int destroyStage,
      float alpha) {
    GlStateManager.pushMatrix();
    GlStateManager.translate(x + 0.5D, y, z + 0.5D);
    GlStateManager.enableLighting();
    GlStateManager.enableCull();

    bindTexture(ResourceManager.fraction_spacer_tex);
    ResourceManager.fraction_spacer.renderAll();

    GlStateManager.popMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.fraction_spacer);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.scale(3.25, 3.25, 3.25);
      }

      public void renderCommon() {
        GlStateManager.scale(1, 1, 1);
        bindTexture(ResourceManager.fraction_spacer_tex);
        ResourceManager.fraction_spacer.renderAll();
      }
    };
  }
}
