package com.hbm.render.tileentity;

import com.hbm.blocks.ModBlocks;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.TileEntityReactorZirnox;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11;

public class RenderZirnox extends TileEntitySpecialRenderer<TileEntityReactorZirnox>
    implements IItemRendererProvider {

  @Override
  public void render(
      TileEntityReactorZirnox tileEntity,
      double x,
      double y,
      double z,
      float interp,
      int destroyStage,
      float alpha) {
    GL11.glPushMatrix();
    GL11.glTranslated(x + 0.5D, y, z + 0.5D);
    GL11.glEnable(GL11.GL_LIGHTING);
    GL11.glDisable(GL11.GL_CULL_FACE);
    switch (tileEntity.getBlockMetadata() - 10) {
      case 2:
        GL11.glRotatef(90, 0F, 1F, 0F);
        break;
      case 3:
        GL11.glRotatef(270, 0F, 1F, 0F);
        break;
      case 4:
        GL11.glRotatef(180, 0F, 1F, 0F);
        break;
      case 5:
        GL11.glRotatef(0, 0F, 1F, 0F);
        break;
    }

    GL11.glShadeModel(GL11.GL_SMOOTH);
    bindTexture(ResourceManager.zirnox_tex);
    ResourceManager.zirnox.renderAll();

    GL11.glEnable(GL11.GL_CULL_FACE);
    GL11.glShadeModel(GL11.GL_FLAT);

    GL11.glPopMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.reactor_zirnox);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.scale(2.7, 2.7, 2.7);
        GlStateManager.translate(0.5, -1.55, 0);
      }

      public void renderCommon() {
        GlStateManager.scale(0.75, 0.75, 0.75);
        GlStateManager.rotate(90, 0, 1, 0);
        GlStateManager.translate(0.5, 1.0, -0.3);
        bindTexture(ResourceManager.zirnox_tex);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        ResourceManager.zirnox.renderAll();
        GlStateManager.shadeModel(GL11.GL_FLAT);
      }
    };
  }
}
