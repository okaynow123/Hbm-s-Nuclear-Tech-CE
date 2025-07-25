package com.hbm.render.tileentity;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.TileEntityMachineCombustionEngine;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11; import net.minecraft.client.renderer.GlStateManager;

public class RenderCombustionEngine
    extends TileEntitySpecialRenderer<TileEntityMachineCombustionEngine>
    implements IItemRendererProvider {

  @Override
  public void render(
      TileEntityMachineCombustionEngine engine,
      double x,
      double y,
      double z,
      float partialTicks,
      int destroyStage,
      float alpha) {
    GlStateManager.pushMatrix();
    GlStateManager.translate(x + 0.5, y, z + 0.5);
    GlStateManager.enableLighting();
    GlStateManager.enableCull();

    switch (engine.getBlockMetadata() - BlockDummyable.offset) {
      case 3:
        GlStateManager.rotate(270, 0F, 1F, 0F);
        break;
      case 5:
        GlStateManager.rotate(0, 0F, 1F, 0F);
        break;
      case 2:
        GlStateManager.rotate(90, 0F, 1F, 0F);
        break;
      case 4:
        GlStateManager.rotate(180, 0F, 1F, 0F);
        break;
    }

    GlStateManager.translate(-0.5, 0, 3);

    GlStateManager.shadeModel(GL11.GL_SMOOTH);
    bindTexture(ResourceManager.combustion_engine_tex);
    ResourceManager.combustion_engine.renderPart("Engine");

    Fluids.CD_Canister canister = engine.tank.getTankType().getContainer(Fluids.CD_Canister.class);

    if (canister != null) {
      int color = canister.color;
      float r = ((color & 0xff0000) >> 16) / 256F;
      float g = ((color & 0x00ff00) >> 8) / 256F;
      float b = ((color & 0x0000ff)) / 256F;
      GlStateManager.color(r, g, b, 1F);
    }
    ResourceManager.combustion_engine.renderPart("Canister");
    GlStateManager.color(1F, 1F, 1F, 1F);

    GlStateManager.translate(1, 0, -2.6875);
    GlStateManager.rotate(
        engine.prevDoorAngle + (engine.doorAngle - engine.prevDoorAngle) * partialTicks, 0, -1, 0);
    GlStateManager.translate(-1, 0, 2.6875);
    ResourceManager.combustion_engine.renderPart("Hatch");

    GlStateManager.shadeModel(GL11.GL_FLAT);

    GlStateManager.popMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.machine_combustion_engine);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.translate(0, -1, 0);
        GlStateManager.scale(2.75, 2.75, 2.75);
      }

      public void renderCommon() {
        GlStateManager.rotate(90, 0F, 1F, 0F);
        GlStateManager.translate(0, 0, 2.75);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        bindTexture(ResourceManager.combustion_engine_tex);
        ResourceManager.combustion_engine.renderAll();
        GlStateManager.shadeModel(GL11.GL_FLAT);
      }
    };
  }
}
