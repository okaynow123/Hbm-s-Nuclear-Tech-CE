package com.hbm.render.tileentity;

import com.hbm.blocks.ModBlocks;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.TileEntityReactorZirnox;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11; import net.minecraft.client.renderer.GlStateManager;

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
    GlStateManager.pushMatrix();
    GlStateManager.translate(x + 0.5D, y, z + 0.5D);
    GlStateManager.enableLighting();
    GlStateManager.disableCull();
    switch (tileEntity.getBlockMetadata() - 10) {
      case 2:
        GlStateManager.rotate(90, 0F, 1F, 0F);
        break;
      case 3:
        GlStateManager.rotate(270, 0F, 1F, 0F);
        break;
      case 4:
        GlStateManager.rotate(180, 0F, 1F, 0F);
        break;
      case 5:
        GlStateManager.rotate(0, 0F, 1F, 0F);
        break;
    }

    GlStateManager.shadeModel(GL11.GL_SMOOTH);
    bindTexture(ResourceManager.zirnox_tex);
    ResourceManager.zirnox.renderAll();

    GlStateManager.enableCull();
    GlStateManager.shadeModel(GL11.GL_FLAT);

    GlStateManager.popMatrix();
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
