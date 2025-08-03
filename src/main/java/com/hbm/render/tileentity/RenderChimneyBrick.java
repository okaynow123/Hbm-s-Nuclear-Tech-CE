package com.hbm.render.tileentity;

import com.hbm.blocks.ModBlocks;
import com.hbm.interfaces.AutoRegister;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.TileEntityChimneyBrick;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11;
@AutoRegister
public class RenderChimneyBrick extends TileEntitySpecialRenderer<TileEntityChimneyBrick>
    implements IItemRendererProvider {

  @Override
  public boolean isGlobalRenderer(TileEntityChimneyBrick te) {
    return true;
  }

  @Override
  public void render(
      TileEntityChimneyBrick tileEntity,
      double x,
      double y,
      double z,
      float f,
      int destroyStage,
      float alpha) {

    GlStateManager.pushMatrix();
    GlStateManager.translate(x + 0.5D, y, z + 0.5D);
    GlStateManager.enableLighting();
    GlStateManager.rotate(180, 0F, 1F, 0F);

    GlStateManager.disableCull();
    GlStateManager.shadeModel(GL11.GL_SMOOTH);
    bindTexture(ResourceManager.chimney_brick_tex);
    ResourceManager.chimney_brick.renderAll();
    GlStateManager.shadeModel(GL11.GL_FLAT);
    GlStateManager.enableCull();

    GlStateManager.popMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.chimney_brick);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.translate(0, -5, 0);
        GlStateManager.scale(2.25, 2.25, 2.25);
      }

      public void renderCommon() {
        GlStateManager.scale(0.5, 0.5, 0.5);
        GlStateManager.disableCull();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        bindTexture(ResourceManager.chimney_brick_tex);
        ResourceManager.chimney_brick.renderAll();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.enableCull();
      }
    };
  }
}
