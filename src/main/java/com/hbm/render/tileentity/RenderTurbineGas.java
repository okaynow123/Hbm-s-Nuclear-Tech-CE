package com.hbm.render.tileentity;

import com.hbm.blocks.ModBlocks;
import com.hbm.render.item.ItemRenderBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11;

import com.hbm.blocks.BlockDummyable;
import com.hbm.main.ResourceManager;
import com.hbm.tileentity.machine.TileEntityMachineTurbineGas;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

public class RenderTurbineGas extends TileEntitySpecialRenderer<TileEntityMachineTurbineGas>
    implements IItemRendererProvider {

  @Override
  public void render(
      TileEntityMachineTurbineGas tile,
      double x,
      double y,
      double z,
      float f,
      int destroyStage,
      float alpha) {
    GlStateManager.pushMatrix();
    GlStateManager.translate(x + 0.5D, y, z + 0.5D);

    TileEntityMachineTurbineGas turbinegas = (TileEntityMachineTurbineGas) tile;

    switch (turbinegas.getBlockMetadata() - BlockDummyable.offset) {
      case 2:
        GL11.glRotatef(90, 0F, 1F, 0F);
        break;
      case 4:
        GL11.glRotatef(180, 0F, 1F, 0F);
        break;
      case 3:
        GL11.glRotatef(270, 0F, 1F, 0F);
        break;
      case 5:
        GL11.glRotatef(0, 0F, 1F, 0F);
        break;
    }

    GL11.glEnable(GL11.GL_LIGHTING);
    GL11.glDisable(GL11.GL_CULL_FACE);
    GL11.glShadeModel(GL11.GL_SMOOTH);

    bindTexture(ResourceManager.turbine_gas_tex);
    ResourceManager.turbine_gas.renderAll();

    GL11.glShadeModel(GL11.GL_FLAT);
    GL11.glPopMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.machine_turbine_gas);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.translate(-0.7, -1, 1.5);//small X change to fit in slot`
        GlStateManager.scale(2.5, 2.5, 2.5);
      }

      public void renderCommon() {
        GlStateManager.disableCull();
        GlStateManager.scale(0.75, 0.75, 0.75);
        GlStateManager.rotate(90, 0, 1, 0);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        bindTexture(ResourceManager.turbine_gas_tex);
        ResourceManager.turbine_gas.renderAll();
        GL11.glShadeModel(GL11.GL_FLAT);
      }
    };
  }
}
