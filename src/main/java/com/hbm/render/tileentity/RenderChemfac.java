package com.hbm.render.tileentity;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ModBlocks;
import com.hbm.interfaces.AutoRegister;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.TileEntityMachineChemfac;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11;
@AutoRegister
public class RenderChemfac extends TileEntitySpecialRenderer<TileEntityMachineChemfac>
    implements IItemRendererProvider {
  @Override
  public boolean isGlobalRenderer(TileEntityMachineChemfac te) {
    return true;
  }

  @Override
  public void render(
      TileEntityMachineChemfac chemfac,
      double x,
      double y,
      double z,
      float partialTicks,
      int destroyStage,
      float alpha) {
    GlStateManager.enableAlpha();
    GlStateManager.pushMatrix();
    GlStateManager.translate(x + 0.5D, y, z + 0.5D);
    GlStateManager.enableLighting();
    GlStateManager.disableCull();

    switch (chemfac.getBlockMetadata() - BlockDummyable.offset) {
      case 5:
        GlStateManager.rotate(180, 0F, 1F, 0F);
        break;
      case 2:
        GlStateManager.rotate(270, 0F, 1F, 0F);
        break;
      case 4:
        GlStateManager.rotate(0, 0F, 1F, 0F);
        break;
      case 3:
        GlStateManager.rotate(90, 0F, 1F, 0F);
        break;
    }

    GlStateManager.translate(0.5D, 0.0D, -0.5D);

    GlStateManager.shadeModel(GL11.GL_SMOOTH);
    bindTexture(ResourceManager.chemfac_tex);
    ResourceManager.chemfac.renderPart("Main");

    float rot = chemfac.prevRot + (chemfac.rot - chemfac.prevRot) * partialTicks;

    GlStateManager.pushMatrix();
    GlStateManager.translate(1, 0, 0);
    GL11.glRotated(rot, 0, -1, 0);
    GlStateManager.translate(-1, 0, 0);
    ResourceManager.chemfac.renderPart("Fan1");
    GlStateManager.popMatrix();

    GlStateManager.pushMatrix();
    GlStateManager.translate(-1, 0, 0);
    GL11.glRotated(rot, 0, -1, 0);
    GlStateManager.translate(1, 0, 0);
    ResourceManager.chemfac.renderPart("Fan2");
    GlStateManager.popMatrix();

    GlStateManager.shadeModel(GL11.GL_FLAT);

    GlStateManager.popMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.machine_chemfac);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.scale(2.5, 2.5, 2.5);
      }

      public void renderCommon() {
        GlStateManager.scale(0.5, 0.5, 0.5);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        bindTexture(ResourceManager.chemfac_tex);
        ResourceManager.chemfac.renderPart("Main");
        GlStateManager.shadeModel(GL11.GL_FLAT);
      }
    };
  }
}
