package com.hbm.render.tileentity;

import com.hbm.blocks.ModBlocks;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.TileEntityMachineGasCent;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11;

public class RenderGasCent extends TileEntitySpecialRenderer<TileEntityMachineGasCent>
    implements IItemRendererProvider {

  @Override
  public boolean isGlobalRenderer(TileEntityMachineGasCent te) {
    return true;
  }

  @Override
  public void render(
      TileEntityMachineGasCent te,
      double x,
      double y,
      double z,
      float partialTicks,
      int destroyStage,
      float alpha) {
    GL11.glPushMatrix();
    GL11.glTranslated(x + 0.5D, y, z + 0.5D);
    GlStateManager.enableLighting();
    GlStateManager.shadeModel(GL11.GL_SMOOTH);
    switch (te.getBlockMetadata()) {
      case 4:
        GL11.glRotatef(90, 0F, 1F, 0F);
        break;
      case 3:
        GL11.glRotatef(180, 0F, 1F, 0F);
        break;
      case 5:
        GL11.glRotatef(270, 0F, 1F, 0F);
        break;
      case 2:
        GL11.glRotatef(0, 0F, 1F, 0F);
        break;
    }

    bindTexture(ResourceManager.centrifuge_gas_tex);
    ResourceManager.centrifuge_gas.renderAll();
    GlStateManager.shadeModel(GL11.GL_FLAT);

    GL11.glPopMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.machine_gascent);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.translate(0, -5, 0);
        GlStateManager.scale(3.8, 3.8, 3.8);
      }

      public void renderCommon() {
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        bindTexture(ResourceManager.centrifuge_gas_tex);
        ResourceManager.centrifuge_gas.renderAll();
        GlStateManager.shadeModel(GL11.GL_FLAT);
      }
    };
  }
}
