package com.hbm.render.tileentity;

import com.hbm.blocks.ModBlocks;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.oil.TileEntityMachineFractionTower;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11;

public class RenderFractionTower extends TileEntitySpecialRenderer<TileEntityMachineFractionTower>
    implements IItemRendererProvider {

  @Override
  public boolean isGlobalRenderer(TileEntityMachineFractionTower te) {
    return true;
  }

  @Override
  public void render(
      TileEntityMachineFractionTower te,
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

    bindTexture(ResourceManager.fraction_tower_tex);
    ResourceManager.fraction_tower.renderAll();

    GL11.glPopMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.machine_fraction_tower);
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
        bindTexture(ResourceManager.fraction_tower_tex);
        ResourceManager.fraction_tower.renderAll();
      }
    };
  }
}
