package com.hbm.render.tileentity;

import com.hbm.blocks.ModBlocks;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.oil.TileEntityMachineCoker;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11;

public class RenderCoker extends TileEntitySpecialRenderer<TileEntityMachineCoker>
    implements IItemRendererProvider {

  @Override
  public void render(
      TileEntityMachineCoker tile,
      double x,
      double y,
      double z,
      float interp,
      int destroyStage,
      float alpha) {
    GL11.glPushMatrix();
    GL11.glTranslated(x + 0.5D, y, z + 0.5D);
    GL11.glEnable(GL11.GL_LIGHTING);

    GL11.glDisable(GL11.GL_CULL_FACE);
    GL11.glShadeModel(GL11.GL_SMOOTH);
    bindTexture(ResourceManager.coker_tex);
    ResourceManager.coker.renderAll();
    GL11.glShadeModel(GL11.GL_FLAT);
    GL11.glEnable(GL11.GL_CULL_FACE);

    GL11.glPopMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.machine_coker);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.translate(0, -5, 0);
        GlStateManager.scale(2.75, 2.75, 2.75);
      }

      public void renderCommon() {
        GlStateManager.scale(0.25, 0.25, 0.25);
        GlStateManager.disableCull();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        bindTexture(ResourceManager.coker_tex);
        ResourceManager.coker.renderAll();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.enableCull();
      }
    };
  }
}
