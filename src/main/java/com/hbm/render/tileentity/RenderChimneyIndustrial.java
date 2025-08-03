package com.hbm.render.tileentity;

import com.hbm.blocks.ModBlocks;
import com.hbm.interfaces.AutoRegister;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.TileEntityChimneyIndustrial;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11;
@AutoRegister
public class RenderChimneyIndustrial extends TileEntitySpecialRenderer<TileEntityChimneyIndustrial>
    implements IItemRendererProvider {

  @Override
  public boolean isGlobalRenderer(TileEntityChimneyIndustrial te) {
    return true;
  }

  @Override
  public void render(
      TileEntityChimneyIndustrial tileEntity,
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
    bindTexture(ResourceManager.chimney_industrial_tex);
    ResourceManager.chimney_industrial.renderAll();
    GlStateManager.shadeModel(GL11.GL_FLAT);
    GlStateManager.enableCull();

    GlStateManager.popMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.chimney_industrial);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.translate(0, -5, 0);
        GlStateManager.scale(2.75, 2.75, 2.75);
      }

      public void renderCommon() {
        GlStateManager.scale(0.25, 0.25, 0.25);
        GlStateManager.disableCull();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        bindTexture(ResourceManager.chimney_industrial_tex);
        ResourceManager.chimney_industrial.renderAll();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.enableCull();
      }
    };
  }
}
