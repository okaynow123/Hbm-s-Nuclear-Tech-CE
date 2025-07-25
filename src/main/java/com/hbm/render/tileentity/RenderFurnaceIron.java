package com.hbm.render.tileentity;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ModBlocks;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.TileEntityFurnaceIron;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11; import net.minecraft.client.renderer.GlStateManager;

public class RenderFurnaceIron extends TileEntitySpecialRenderer<TileEntityFurnaceIron>
    implements IItemRendererProvider {

  @Override
  public void render(
      TileEntityFurnaceIron tileEntity,
      double x,
      double y,
      double z,
      float partialTicks,
      int destroyStage,
      float alpha) {
    GlStateManager.pushMatrix();
    GlStateManager.translate(x + 0.5D, y, z + 0.5D);
    GlStateManager.enableLighting();
    GlStateManager.enableCull();

    switch (tileEntity.getBlockMetadata() - BlockDummyable.offset) {
      case 3:
        GlStateManager.rotate(0, 0F, 1F, 0F);
        break;
      case 5:
        GlStateManager.rotate(90, 0F, 1F, 0F);
        break;
      case 2:
        GlStateManager.rotate(180, 0F, 1F, 0F);
        break;
      case 4:
        GlStateManager.rotate(270, 0F, 1F, 0F);
        break;
    }

    GlStateManager.translate(-0.5D, 0, -0.5D);

    TileEntityFurnaceIron furnace = (TileEntityFurnaceIron) tileEntity;

    bindTexture(ResourceManager.furnace_iron_tex);
    ResourceManager.furnace_iron.renderPart("Main");

    if (furnace.wasOn) {
      GlStateManager.pushMatrix();
      GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);

      GlStateManager.disableLighting();
      GlStateManager.disableCull();
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
      ResourceManager.furnace_iron.renderPart("On");
      GlStateManager.enableLighting();

      GL11.glPopAttrib();
      GlStateManager.popMatrix();
    } else {
      ResourceManager.furnace_iron.renderPart("Off");
    }

    GlStateManager.popMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.furnace_iron);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.translate(0, -2, 0);
        GlStateManager.scale(5, 5, 5);
      }

      public void renderCommon() {
        GlStateManager.rotate(90, 0, 1, 0);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        bindTexture(ResourceManager.furnace_iron_tex);
        ResourceManager.furnace_iron.renderAll();
        GlStateManager.shadeModel(GL11.GL_FLAT);
      }
    };
  }
}
