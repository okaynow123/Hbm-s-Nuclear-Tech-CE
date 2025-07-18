package com.hbm.render.tileentity;

import com.hbm.blocks.ModBlocks;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.bomb.TileEntityNukeSolinium;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11;

public class RenderNukeSolinium extends TileEntitySpecialRenderer<TileEntityNukeSolinium>
    implements IItemRendererProvider {

  @Override
  public boolean isGlobalRenderer(TileEntityNukeSolinium te) {
    return true;
  }

  @Override
  public void render(
      TileEntityNukeSolinium te,
      double x,
      double y,
      double z,
      float partialTicks,
      int destroyStage,
      float alpha) {
    GL11.glPushMatrix();
    GL11.glTranslated(x + 0.5D, y, z + 0.5D);
    GlStateManager.enableLighting();
    GlStateManager.disableCull();

    GL11.glRotatef(90, 0F, 1F, 0F);

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
    bindTexture(ResourceManager.bomb_solinium_tex);
    ResourceManager.bomb_solinium.renderAll();
    GL11.glShadeModel(GL11.GL_FLAT);

    GlStateManager.enableCull();
    GL11.glPopMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.nuke_solinium);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.scale(4, 4, 4);
      }

      public void renderCommon() {
        GlStateManager.translate(0.5, 0, 0);
        GlStateManager.rotate(90, 0, 1, 0);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        bindTexture(ResourceManager.bomb_solinium_tex);
        ResourceManager.bomb_solinium.renderAll();
        GlStateManager.shadeModel(GL11.GL_FLAT);
      }
    };
  }
}
