package com.hbm.render.tileentity;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ModBlocks;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.TileEntitySolarBoiler;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11;

public class RenderSolarBoiler extends TileEntitySpecialRenderer<TileEntitySolarBoiler>
    implements IItemRendererProvider {

  @Override
  public void render(
      TileEntitySolarBoiler te,
      double x,
      double y,
      double z,
      float partialTicks,
      int destroyStage,
      float alpha) {
    GL11.glPushMatrix();
    GL11.glTranslated(x + 0.5D, y, z + 0.5D);
    GlStateManager.enableLighting();
    GlStateManager.disableCull();
    GL11.glRotatef(90, 0F, 1F, 0F);

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

    bindTexture(ResourceManager.solar_tex);

    GlStateManager.shadeModel(GL11.GL_SMOOTH);
    ResourceManager.solar_boiler.renderPart("Base");
    GlStateManager.shadeModel(GL11.GL_FLAT);

    GL11.glPopMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.machine_solar_boiler);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.translate(0, -2.5, 0);
        GlStateManager.scale(3.25, 3.25, 3.25);
      }

      public void renderCommon() {
        GlStateManager.scale(1, 1, 1);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        bindTexture(ResourceManager.solar_tex);
        ResourceManager.solar_boiler.renderPart("Base");
        GlStateManager.shadeModel(GL11.GL_FLAT);
      }
    };
  }
}
