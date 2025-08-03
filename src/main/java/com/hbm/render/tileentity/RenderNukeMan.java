package com.hbm.render.tileentity;

import com.hbm.blocks.ModBlocks;
import com.hbm.interfaces.AutoRegister;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.bomb.TileEntityNukeMan;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11;
@AutoRegister
public class RenderNukeMan extends TileEntitySpecialRenderer<TileEntityNukeMan>
    implements IItemRendererProvider {

  @Override
  public void render(
      TileEntityNukeMan te,
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
    switch (te.getBlockMetadata()) {
      case 3:
        GlStateManager.rotate(90, 0F, 1F, 0F);
        break;
      case 5:
        GlStateManager.rotate(180, 0F, 1F, 0F);
        break;
      case 2:
        GlStateManager.rotate(270, 0F, 1F, 0F);
        break;
      case 4:
        GlStateManager.rotate(0, 0F, 1F, 0F);
        break;
    }

    GlStateManager.shadeModel(GL11.GL_SMOOTH);
    bindTexture(ResourceManager.bomb_man_tex);
    ResourceManager.bomb_man.renderAll();
    GlStateManager.shadeModel(GL11.GL_FLAT);

    GlStateManager.enableCull();

    GlStateManager.popMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.nuke_man);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.translate(0, -2, 0);
        GlStateManager.scale(5.5, 5.5, 5.5);
      }

      public void renderCommon() {
        GlStateManager.rotate(180, 0, 1, 0);
        GlStateManager.translate(-0.75, 0, 0);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        bindTexture(ResourceManager.bomb_man_tex);
        ResourceManager.bomb_man.renderAll();
        GlStateManager.shadeModel(GL11.GL_FLAT);
      }
    };
  }
}
