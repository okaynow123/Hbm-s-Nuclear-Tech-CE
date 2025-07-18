package com.hbm.render.tileentity;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ModBlocks;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.network.energy.TileEntitySubstation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11;

public class RenderSubstation extends TileEntitySpecialRenderer<TileEntitySubstation>
    implements IItemRendererProvider {

  @Override
  public void render(
      TileEntitySubstation sub,
      double x,
      double y,
      double z,
      float partialTicks,
      int destroyStage,
      float alpha) {
    GL11.glPushMatrix();
    GL11.glTranslatef((float) x + 0.5F, (float) y, (float) z + 0.5F);
    switch (sub.getBlockMetadata() - BlockDummyable.offset) {
      case 4:
      case 5:
        GL11.glRotatef(0, 0F, 1F, 0F);
        break;
      case 2:
      case 3:
        GL11.glRotatef(90, 0F, 1F, 0F);
        break;
    }
    GL11.glEnable(GL11.GL_CULL_FACE);
    GlStateManager.shadeModel(GL11.GL_SMOOTH);
    bindTexture(ResourceManager.substation_tex);
    ResourceManager.substation.renderAll();
    GlStateManager.shadeModel(GL11.GL_FLAT);

    GL11.glPopMatrix();

    RenderPylon.renderPowerLines(sub, x, y, z);
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.substation);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.translate(0, -2.5, 0);
        GlStateManager.scale(4.5, 4.5, 4.5);
      }

      public void renderCommon() {
        GlStateManager.scale(0.5, 0.5, 0.5);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        bindTexture(ResourceManager.substation_tex);
        ResourceManager.substation.renderAll();
        GlStateManager.shadeModel(GL11.GL_FLAT);
      }
    };
  }
}
