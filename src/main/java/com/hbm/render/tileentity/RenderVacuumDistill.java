package com.hbm.render.tileentity;

import com.hbm.blocks.ModBlocks;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.oil.TileEntityMachineVacuumDistill;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11;

public class RenderVacuumDistill extends TileEntitySpecialRenderer<TileEntityMachineVacuumDistill>
    implements IItemRendererProvider {

  @Override
  public void render(
      TileEntityMachineVacuumDistill tile,
      double x,
      double y,
      double z,
      float interp,
      int destroyStage,
      float alpha) {
    GL11.glPushMatrix();
    GL11.glTranslated(x + 0.5D, y, z + 0.5D);
    GL11.glEnable(GL11.GL_LIGHTING);

    GL11.glShadeModel(GL11.GL_SMOOTH);
    bindTexture(ResourceManager.vacuum_distill_tex);
    ResourceManager.vacuum_distill.renderAll();
    GL11.glShadeModel(GL11.GL_FLAT);

    GL11.glPopMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.machine_vacuum_distill);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.translate(0, -4, 0);
        GlStateManager.scale(3, 3, 3);
      }

      public void renderCommon() {
        GlStateManager.scale(0.5, 0.5, 0.5);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        bindTexture(ResourceManager.vacuum_distill_tex);
        ResourceManager.vacuum_distill.renderAll();
        GlStateManager.shadeModel(GL11.GL_FLAT);
      }
    };
  }
}
