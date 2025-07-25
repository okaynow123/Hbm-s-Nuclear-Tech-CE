package com.hbm.render.tileentity;

import com.hbm.blocks.ModBlocks;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.TileEntityDeuteriumTower;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11; import net.minecraft.client.renderer.GlStateManager;

public class RenderDeuteriumTower extends TileEntitySpecialRenderer<TileEntityDeuteriumTower>
    implements IItemRendererProvider {

  @Override
  public boolean isGlobalRenderer(TileEntityDeuteriumTower te) {
    return true;
  }

  @Override
  public void render(
      TileEntityDeuteriumTower te,
      double x,
      double y,
      double z,
      float partialTicks,
      int destroyStage,
      float alpha) {

    GlStateManager.pushMatrix();
    GlStateManager.translate(x, y, z);
    GlStateManager.enableLighting();
    GlStateManager.disableCull();
    GlStateManager.rotate(180F, 0F, 1F, 0F);
    switch (te.getBlockMetadata() - 10) {
      case 2:
        GlStateManager.rotate(0F, 0F, 1F, 0F);
        GlStateManager.translate(0F, 0F, -1F);
        break;
      case 3:
        GlStateManager.rotate(180F, 0F, 1F, 0F);
        GlStateManager.translate(1F, 0F, 0F);
        break;
      case 4:
        GlStateManager.rotate(90F, 0F, 1F, 0F);
        GlStateManager.translate(1F, 0F, -1F);
        break;
      case 5:
        GlStateManager.rotate(270F, 0F, 1F, 0F);
        break;
    }
    GlStateManager.shadeModel(GL11.GL_SMOOTH);
    bindTexture(ResourceManager.deuterium_tower_tex);
    ResourceManager.deuterium_tower.renderAll();
    GlStateManager.shadeModel(GL11.GL_FLAT);

    GlStateManager.enableCull();
    GlStateManager.popMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.machine_deuterium_tower);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.translate(0, -5, 0);
        GlStateManager.scale(3, 3, 3);
      }

      public void renderCommon() {
        GlStateManager.rotate(180, 0, 1, 0);
        GlStateManager.scale(0.5, 0.5, 0.5);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        bindTexture(ResourceManager.deuterium_tower_tex);
        ResourceManager.deuterium_tower.renderAll();
        GlStateManager.shadeModel(GL11.GL_FLAT);
      }
    };
  }
}
