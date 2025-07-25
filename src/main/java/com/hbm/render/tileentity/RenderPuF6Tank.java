package com.hbm.render.tileentity;

import com.hbm.blocks.ModBlocks;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.TileEntityMachinePuF6Tank;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11; import net.minecraft.client.renderer.GlStateManager;

public class RenderPuF6Tank extends TileEntitySpecialRenderer<TileEntityMachinePuF6Tank>
    implements IItemRendererProvider {

  @Override
  public boolean isGlobalRenderer(TileEntityMachinePuF6Tank te) {
    return true;
  }

  @Override
  public void render(
      TileEntityMachinePuF6Tank te,
      double x,
      double y,
      double z,
      float partialTicks,
      int destroyStage,
      float alpha) {
    GlStateManager.pushMatrix();
    GlStateManager.translate(x + 0.5D, y, z + 0.5D);
    GlStateManager.enableLighting();
    switch (te.getBlockMetadata()) {
      case 4:
        GlStateManager.rotate(90, 0F, 1F, 0F);
        break;
      case 3:
        GlStateManager.rotate(180, 0F, 1F, 0F);
        break;
      case 5:
        GlStateManager.rotate(270, 0F, 1F, 0F);
        break;
      case 2:
        GlStateManager.rotate(0, 0F, 1F, 0F);
        break;
    }

    bindTexture(ResourceManager.puf6_tex);
    ResourceManager.tank.renderAll();

    GlStateManager.popMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.machine_puf6_tank);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.translate(0, -4, 0);
        GlStateManager.scale(6, 6, 6);
      }

      public void renderCommon() {
        GlStateManager.rotate(90, 0, -1, 0);
        bindTexture(ResourceManager.puf6_tex);
        ResourceManager.tank.renderAll();
      }
    };
  }
}
