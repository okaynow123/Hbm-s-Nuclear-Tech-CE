package com.hbm.render.tileentity;

import com.hbm.blocks.ModBlocks;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.bomb.TileEntityCrashedBomb;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11;

public class RenderCrashedBomb extends TileEntitySpecialRenderer<TileEntityCrashedBomb>
    implements IItemRendererProvider {

  @Override
  public boolean isGlobalRenderer(TileEntityCrashedBomb te) {
    return true;
  }

  @Override
  public void render(
      TileEntityCrashedBomb te,
      double x,
      double y,
      double z,
      float partialTicks,
      int destroyStage,
      float alpha) {
    GL11.glPushMatrix();
    GL11.glTranslated(x + 0.5D, y, z + 0.5D);
    GlStateManager.disableCull();
    GlStateManager.enableLighting();
    switch (te.getBlockMetadata()) {
      case 5:
        GL11.glRotatef(0, 0F, 1F, 0F);
        break;
      case 2:
        GL11.glRotatef(90, 0F, 1F, 0F);
        break;
      case 4:
        GL11.glRotatef(180, 0F, 1F, 0F);
        break;
      case 3:
        GL11.glRotatef(-90, 0F, 1F, 0F);
        break;
    }

    bindTexture(ResourceManager.dud_tex);
    ResourceManager.dud.renderAll();

    GlStateManager.enableCull();
    GL11.glPopMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.crashed_balefire);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.translate(0, 3, 0);
        GlStateManager.scale(2, 2, 2);
      }

      public void renderCommon() {
        GlStateManager.rotate(90, 0, 1, 0);
        GlStateManager.disableCull();
        bindTexture(ResourceManager.dud_tex);
        ResourceManager.dud.renderAll();
        GlStateManager.enableCull();
      }
    };
  }
}
