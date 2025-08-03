package com.hbm.render.tileentity;

import com.hbm.blocks.ModBlocks;
import com.hbm.interfaces.AutoRegister;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.bomb.TileEntityNukeFleija;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11;
@AutoRegister
public class RenderNukeFleija extends TileEntitySpecialRenderer<TileEntityNukeFleija>
    implements IItemRendererProvider {

  @Override
  public void render(
      TileEntityNukeFleija te,
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
      case 5:
        GlStateManager.rotate(90, 0F, 1F, 0F);
        break;
      case 2:
        GlStateManager.rotate(180, 0F, 1F, 0F);
        break;
      case 4:
        GlStateManager.rotate(270, 0F, 1F, 0F);
        break;
      case 3:
        GlStateManager.rotate(0, 0F, 1F, 0F);
        break;
    }

    bindTexture(ResourceManager.bomb_fleija_tex);
    ResourceManager.bomb_fleija.renderAll();
    GlStateManager.popMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.nuke_fleija);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.translate(0, -2, 0);
        GlStateManager.scale(4.5, 4.5, 4.5);
      }

      public void renderCommon() {
        GlStateManager.scale(2, 2, 2);
        GlStateManager.rotate(90, 0, 1, 0);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        bindTexture(ResourceManager.bomb_fleija_tex);
        ResourceManager.bomb_fleija.renderAll();
        GlStateManager.shadeModel(GL11.GL_FLAT);
      }
    };
  }
}
