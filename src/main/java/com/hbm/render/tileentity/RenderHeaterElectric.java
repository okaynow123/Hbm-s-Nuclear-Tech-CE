package com.hbm.render.tileentity;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ModBlocks;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.TileEntityHeaterElectric;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11;

public class RenderHeaterElectric extends TileEntitySpecialRenderer<TileEntityHeaterElectric>
    implements IItemRendererProvider {

  @Override
  public boolean isGlobalRenderer(TileEntityHeaterElectric te) {
    return true;
  }

  @Override
  public void render(
      TileEntityHeaterElectric tile,
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

    switch (tile.getBlockMetadata() - BlockDummyable.offset) {
      case 3:
        GL11.glRotatef(270, 0F, 1F, 0F);
        break;
      case 5:
        GL11.glRotatef(0, 0F, 1F, 0F);
        break;
      case 2:
        GL11.glRotatef(90, 0F, 1F, 0F);
        break;
      case 4:
        GL11.glRotatef(180, 0F, 1F, 0F);
        break;
    }

    bindTexture(ResourceManager.heater_electric_tex);
    ResourceManager.heater_electric.renderAll();

    GL11.glPopMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.heater_electric);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.translate(0, -1, 0);
        GlStateManager.scale(1.9, 1.9, 1.9);
      }

      public void renderCommon() {
        GlStateManager.rotate(180, 0, 1, 0);
        GlStateManager.scale(1.9, 1.9, 1.9);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        bindTexture(ResourceManager.heater_electric_tex);
        ResourceManager.heater_electric.renderAll();
        GlStateManager.shadeModel(GL11.GL_FLAT);
      }
    };
  }
}
