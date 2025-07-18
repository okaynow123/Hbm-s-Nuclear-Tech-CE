package com.hbm.render.tileentity;

import com.hbm.blocks.ModBlocks;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.TileEntityWatz;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11;

public class RenderWatz extends TileEntitySpecialRenderer<TileEntityWatz>
    implements IItemRendererProvider {

  @Override
  public void render(
      TileEntityWatz te,
      double x,
      double y,
      double z,
      float partialTicks,
      int destroyStage,
      float alpha) {

    GL11.glPushMatrix();

    GL11.glTranslatef((float) x + 0.5F, (float) y, (float) z + 0.5F);

    GL11.glDisable(GL11.GL_CULL_FACE);
    GL11.glEnable(GL11.GL_LIGHTING);

    GL11.glShadeModel(GL11.GL_SMOOTH);
    bindTexture(ResourceManager.watz_tex);
    ResourceManager.watz.renderAll();
    GL11.glShadeModel(GL11.GL_FLAT);

    GL11.glPopMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.watz);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.translate(0, -1, 0);
        GlStateManager.scale(2, 2, 2);
      }

      public void renderCommon() {
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        bindTexture(ResourceManager.watz_tex);
        ResourceManager.watz.renderAll();
        GlStateManager.shadeModel(GL11.GL_FLAT);
      }
    };
  }
}
