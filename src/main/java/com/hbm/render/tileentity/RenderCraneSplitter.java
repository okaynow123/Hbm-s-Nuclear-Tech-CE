package com.hbm.render.tileentity;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ModBlocks;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.network.TileEntityCraneSplitter;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11;

public class RenderCraneSplitter extends TileEntitySpecialRenderer<TileEntityCraneSplitter>
    implements IItemRendererProvider {

  @Override
  public void render(
      TileEntityCraneSplitter te,
      double x,
      double y,
      double z,
      float partialTicks,
      int destroyStage,
      float alpha) {

    GL11.glPushMatrix();
    GL11.glTranslated(x + 0.5D, y, z + 0.5D);
    GL11.glEnable(GL11.GL_LIGHTING);
    GL11.glEnable(GL11.GL_CULL_FACE);
    switch (te.getBlockMetadata() - BlockDummyable.offset) {
      case 3:
        GL11.glRotatef(180, 0F, 1F, 0F);
        break;
      case 5:
        GL11.glRotatef(270, 0F, 1F, 0F);
        break;
      case 2:
        GL11.glRotatef(0, 0F, 1F, 0F);
        break;
      case 4:
        GL11.glRotatef(90, 0F, 1F, 0F);
        break;
    }

    GL11.glTranslated(0.5D, 0, 0.5D);

    bindTexture(ResourceManager.splitter_tex);
    ResourceManager.crane_splitter.renderAll();

    GL11.glPopMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.crane_splitter);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.translate(3.25D, 1.125D, 0D);
        GlStateManager.scale(6.5, 6.5, 6.5);
      }

      public void renderCommon() {
        bindTexture(ResourceManager.splitter_tex);
        ResourceManager.crane_splitter.renderAll();
      }
    };
  }
}
