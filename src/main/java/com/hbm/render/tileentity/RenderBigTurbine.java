package com.hbm.render.tileentity;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ModBlocks;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.TileEntityMachineLargeTurbine;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11;

public class RenderBigTurbine extends TileEntitySpecialRenderer<TileEntityMachineLargeTurbine>
    implements IItemRendererProvider {

  @Override
  public boolean isGlobalRenderer(TileEntityMachineLargeTurbine te) {
    return true;
  }

  @Override
  public void render(
      TileEntityMachineLargeTurbine turbine,
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

    switch (turbine.getBlockMetadata() - BlockDummyable.offset) {
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

    GL11.glTranslated(0, 0, -1);

    bindTexture(ResourceManager.turbine_tex);

    GlStateManager.shadeModel(GL11.GL_SMOOTH);
    ResourceManager.turbine.renderPart("Body");
    GlStateManager.shadeModel(GL11.GL_FLAT);

    GL11.glTranslated(0, 1, 0);
    GL11.glRotatef(turbine.lastRotor + (turbine.rotor - turbine.lastRotor) * partialTicks, 0, 0, 1);
    GL11.glTranslated(0, -1, 0);

    bindTexture(ResourceManager.turbofan_blades_tex);
    ResourceManager.turbine.renderPart("Blades");

    GlStateManager.enableCull();

    GL11.glPopMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.machine_large_turbine);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.translate(0, -1, 0);
        GlStateManager.scale(2.5, 2.5, 2.5);
      }

      public void renderCommon() {
        GlStateManager.rotate(90, 0, 1, 0);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.disableCull();
        bindTexture(ResourceManager.turbine_tex);
        ResourceManager.turbine.renderPart("Body");
        bindTexture(ResourceManager.turbofan_blades_tex);
        ResourceManager.turbine.renderPart("Blades");
        GlStateManager.enableCull();
        GlStateManager.shadeModel(GL11.GL_FLAT);
      }
    };
  }
}
