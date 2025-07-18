package com.hbm.render.tileentity;

import com.hbm.blocks.ModBlocks;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.bomb.TileEntityNukeTsar;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11;

public class RenderNukeTsar extends TileEntitySpecialRenderer<TileEntityNukeTsar>
    implements IItemRendererProvider {

  @Override
  public boolean isGlobalRenderer(TileEntityNukeTsar te) {
    return true;
  }

  @Override
  public void render(
      TileEntityNukeTsar te,
      double x,
      double y,
      double z,
      float partialTicks,
      int destroyStage,
      float alpha) {
    GL11.glPushMatrix();
    GL11.glTranslated(x + 0.5D, y, z + 0.5D);
    GL11.glEnable(GL11.GL_LIGHTING);
    GlStateManager.enableLighting();
    GlStateManager.disableCull();
    switch (te.getBlockMetadata()) {
      case 2:
        GL11.glRotatef(0, 0F, 1F, 0F);
        break;
      case 4:
        GL11.glRotatef(90, 0F, 1F, 0F);
        break;
      case 3:
        GL11.glRotatef(180, 0F, 1F, 0F);
        break;
      case 5:
        GL11.glRotatef(-90, 0F, 1F, 0F);
        break;
    }

    GL11.glShadeModel(GL11.GL_SMOOTH);
    bindTexture(ResourceManager.bomb_tsar_tex);
    ResourceManager.bomb_tsar.renderAll();
    GL11.glShadeModel(GL11.GL_FLAT);

    GlStateManager.enableCull();
    GL11.glPopMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.nuke_tsar);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.scale(2.25, 2.25, 2.25);
      }

      public void renderCommon() {
        GlStateManager.translate(1.5, 0, 0);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        bindTexture(ResourceManager.bomb_tsar_tex);
        ResourceManager.bomb_tsar.renderAll();
        GlStateManager.shadeModel(GL11.GL_FLAT);
      }
    };
  }
}
