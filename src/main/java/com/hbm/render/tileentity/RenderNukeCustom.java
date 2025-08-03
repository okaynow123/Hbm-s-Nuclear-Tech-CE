package com.hbm.render.tileentity;

import com.hbm.blocks.ModBlocks;
import com.hbm.interfaces.AutoRegister;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.bomb.TileEntityNukeCustom;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11;
@AutoRegister
public class RenderNukeCustom extends TileEntitySpecialRenderer<TileEntityNukeCustom>
    implements IItemRendererProvider {

  @Override
  public boolean isGlobalRenderer(TileEntityNukeCustom te) {
    return true;
  }

  @Override
  public void render(
      TileEntityNukeCustom te,
      double x,
      double y,
      double z,
      float partialTicks,
      int destroyStage,
      float alpha) {
    GlStateManager.pushMatrix();
    GlStateManager.translate(x + 0.5D, y, z + 0.5D);
    GlStateManager.enableLighting();
    GlStateManager.enableLighting();
    GlStateManager.disableCull();
    switch (te.getBlockMetadata()) {
      case 2:
        GlStateManager.rotate(0, 0F, 1F, 0F);
        GlStateManager.translate(-2.0D, 0.0D, 0.0D);
        break;
      case 4:
        GlStateManager.rotate(90, 0F, 1F, 0F);
        GlStateManager.translate(-2.0D, 0.0D, 0.0D);
        break;
      case 3:
        GlStateManager.rotate(180, 0F, 1F, 0F);
        GlStateManager.translate(-2.0D, 0.0D, 0.0D);
        break;
      case 5:
        GlStateManager.rotate(-90, 0F, 1F, 0F);
        GlStateManager.translate(-2.0D, 0.0D, 0.0D);
        break;
    }

    GlStateManager.shadeModel(GL11.GL_SMOOTH);
    bindTexture(ResourceManager.bomb_custom_tex);
    ResourceManager.bomb_boy.renderAll();
    GlStateManager.shadeModel(GL11.GL_FLAT);

    GlStateManager.enableCull();
    GlStateManager.popMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.nuke_custom);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.scale(5, 5, 5);
      }

      public void renderCommon() {
        GlStateManager.translate(-1, 0, 0);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        bindTexture(ResourceManager.bomb_custom_tex);
        ResourceManager.bomb_boy.renderAll();
        GlStateManager.shadeModel(GL11.GL_FLAT);
      }
    };
  }
}
