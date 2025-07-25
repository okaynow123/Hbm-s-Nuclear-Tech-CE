package com.hbm.render.tileentity;

import com.hbm.blocks.ModBlocks;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.oil.TileEntityMachineLiquefactor;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11; import net.minecraft.client.renderer.GlStateManager;

public class RenderLiquefactor extends TileEntitySpecialRenderer<TileEntityMachineLiquefactor>
    implements IItemRendererProvider {

  @Override
  public void render(
      TileEntityMachineLiquefactor te,
      double x,
      double y,
      double z,
      float partialTicks,
      int destroyStage,
      float alpha) {

    GlStateManager.pushMatrix();
    GlStateManager.translate(x + 0.5D, y, z + 0.5D);
    GlStateManager.enableLighting();
    GlStateManager.disableCull();

    GlStateManager.shadeModel(GL11.GL_SMOOTH);
    bindTexture(ResourceManager.liquefactor_tex);
    ResourceManager.liquefactor.renderPart("Main");

    GlStateManager.disableLighting();
    GlStateManager.disableTexture2D();

    if (te.tank.getFill() > 0) {
      int color = te.tank.getTankType().getColor();
      GL11.glColor3ub(
          (byte) ((color & 0xFF0000) >> 16),
          (byte) ((color & 0x00FF00) >> 8),
          (byte) ((color & 0x0000FF) >> 0));

      double height = (double) te.tank.getFill() / (double) te.tank.getMaxFill();
      GlStateManager.pushMatrix();
      GlStateManager.translate(0, 1, 0);
      GL11.glScaled(1, height, 1);
      GlStateManager.translate(0, -1, 0);
      ResourceManager.liquefactor.renderPart("Fluid");
      GlStateManager.popMatrix();
    }

    GlStateManager.enableBlend();
    GlStateManager.alphaFunc(GL11.GL_GREATER, 0);
    OpenGlHelper.glBlendFunc(770, 771, 1, 0);
    GlStateManager.color(0.75F, 1.0F, 1.0F, 0.15F);
    GL11.glDepthMask(false);

    ResourceManager.liquefactor.renderPart("Glass");

    GL11.glDepthMask(true);
    GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
    GlStateManager.disableBlend();
    GlStateManager.enableTexture2D();
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

    GlStateManager.shadeModel(GL11.GL_FLAT);
    GlStateManager.enableCull();
    GlStateManager.enableLighting();
    GlStateManager.popMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.machine_liquefactor);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.translate(0, -2.5, 0);
        GlStateManager.scale(3, 3, 3);
      }

      public void renderCommon() {
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        bindTexture(ResourceManager.liquefactor_tex);
        ResourceManager.liquefactor.renderPart("Main");
        GlStateManager.shadeModel(GL11.GL_FLAT);
      }
    };
  }
}
