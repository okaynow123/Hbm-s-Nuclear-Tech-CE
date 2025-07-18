package com.hbm.render.tileentity;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ModBlocks;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.TileEntityMachinePlasmaHeater;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11;

public class RenderPlasmaHeater extends TileEntitySpecialRenderer<TileEntityMachinePlasmaHeater>
    implements IItemRendererProvider {

  @Override
  public boolean isGlobalRenderer(TileEntityMachinePlasmaHeater te) {
    return true;
  }

  @Override
  public void render(
      TileEntityMachinePlasmaHeater te,
      double x,
      double y,
      double z,
      float partialTicks,
      int destroyStage,
      float alpha) {
    GL11.glPushMatrix();
    GL11.glTranslatef((float) x + 0.5F, (float) y, (float) z + 0.5F);

    GlStateManager.enableCull();
    GlStateManager.enableLighting();
    GlStateManager.shadeModel(GL11.GL_SMOOTH);

    switch (te.getBlockMetadata() - BlockDummyable.offset) {
      case 2:
        GL11.glRotatef(0, 0F, 1F, 0F);
        break;
      case 4:
        GL11.glRotatef(90, 0F, 1F, 0F);
        break;
      case 3:
        GL11.glRotatef(180, 0F, 1F, 0F);
        break;
      case 5:
        GL11.glRotatef(270, 0F, 1F, 0F);
        break;
    }

    GL11.glTranslatef(0, 0, 18);

    bindTexture(ResourceManager.iter_microwave);
    ResourceManager.iter.renderPart("Microwave");

    GlStateManager.shadeModel(GL11.GL_FLAT);
    GL11.glPopMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.plasma_heater);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.translate(0, -1, 0);
        GlStateManager.rotate(90, 0, 1, 0);
        GlStateManager.scale(2.5, 2.5, 2.5);
      }

      public void renderCommon() {
        GlStateManager.scale(0.5, 0.5, 0.5);
        GlStateManager.translate(0, 0, 14);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        bindTexture(ResourceManager.iter_microwave);
        ResourceManager.iter.renderPart("Microwave");
        GlStateManager.shadeModel(GL11.GL_FLAT);
      }
    };
  }
}
