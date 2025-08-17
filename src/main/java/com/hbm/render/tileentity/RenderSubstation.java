package com.hbm.render.tileentity;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ModBlocks;
import com.hbm.interfaces.AutoRegister;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.network.energy.TileEntityPylonBase;
import com.hbm.tileentity.network.energy.TileEntitySubstation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11;
@AutoRegister(tileentity = TileEntitySubstation.class)
public class RenderSubstation extends RenderPylonBase implements IItemRendererProvider {

  @Override
  public void render(TileEntityPylonBase tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
    if(!(tile instanceof TileEntitySubstation sub)) return;
    GlStateManager.pushMatrix();
    GlStateManager.translate((float) x + 0.5F, (float) y, (float) z + 0.5F);
    switch (sub.getBlockMetadata() - BlockDummyable.offset) {
      case 4, 5 -> GlStateManager.rotate(0, 0F, 1F, 0F);
      case 2, 3 -> GlStateManager.rotate(90, 0F, 1F, 0F);
    }
    GlStateManager.enableCull();
    GlStateManager.shadeModel(GL11.GL_SMOOTH);
    bindTexture(ResourceManager.substation_tex);
    ResourceManager.substation.renderAll();
    GlStateManager.shadeModel(GL11.GL_FLAT);

    GlStateManager.popMatrix();

    GlStateManager.pushMatrix();
    this.renderLinesGeneric(sub, x, y, z);
    GlStateManager.popMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.substation);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.translate(0, -2.5, 0);
        GlStateManager.scale(4.5, 4.5, 4.5);
      }

      public void renderCommon() {
        GlStateManager.scale(0.5, 0.5, 0.5);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        bindTexture(ResourceManager.substation_tex);
        ResourceManager.substation.renderAll();
        GlStateManager.shadeModel(GL11.GL_FLAT);
      }
    };
  }
}
