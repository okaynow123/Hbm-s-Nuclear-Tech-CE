package com.hbm.render.tileentity;

import com.hbm.blocks.ModBlocks;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.bomb.TileEntityBombMulti;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11;

public class RenderBombMulti extends TileEntitySpecialRenderer<TileEntityBombMulti>
    implements IItemRendererProvider {

  @Override
  public boolean isGlobalRenderer(TileEntityBombMulti te) {
    return true;
  }

  @Override
  public void render(
      TileEntityBombMulti te,
      double x,
      double y,
      double z,
      float partialTicks,
      int destroyStage,
      float alpha) {
    GL11.glPushMatrix();
    GL11.glTranslated(x + 0.5D, y + 0.5D, z + 0.5D);
    GlStateManager.enableLighting();
    GlStateManager.disableCull();
    GL11.glRotatef(180, 1F, 0F, 0F);

    switch (te.getBlockMetadata()) {
      case 5:
        GL11.glRotatef(180, 0F, 1F, 0F);
        break;
      case 2:
        GL11.glRotatef(90, 0F, 1F, 0F);
        break;
      case 4:
        GL11.glRotatef(0, 0F, 1F, 0F);
        break;
      case 3:
        GL11.glRotatef(270, 0F, 1F, 0F);
        break;
    }

    bindTexture(ResourceManager.bomb_multi_tex);
    ResourceManager.bomb_multi.renderAll();

    GlStateManager.enableCull();
    GL11.glPopMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.bomb_multi);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.translate(0, -1, 0);
        GlStateManager.scale(4, 4, 4);
      }

      public void renderCommon() {
        GlStateManager.translate(0.75, 0, 0);
        GlStateManager.scale(3, 3, 3);
        GlStateManager.translate(0, 0.5, 0);
        GlStateManager.rotate(180, 1F, 0F, 0F);
        GlStateManager.rotate(90, 0F, 1F, 0F);
        GlStateManager.disableCull();
        bindTexture(ResourceManager.bomb_multi_tex);
        ResourceManager.bomb_multi.renderAll();
        GlStateManager.enableCull();
      }
    };
  }
}
