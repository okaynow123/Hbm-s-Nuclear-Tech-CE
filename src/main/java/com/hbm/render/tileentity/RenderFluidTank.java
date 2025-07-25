package com.hbm.render.tileentity;

import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.trait.FT_Corrosive;
import com.hbm.lib.RefStrings;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.render.misc.DiamondPronter;
import com.hbm.tileentity.machine.TileEntityMachineFluidTank;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11; import net.minecraft.client.renderer.GlStateManager;

public class RenderFluidTank extends TileEntitySpecialRenderer<TileEntityMachineFluidTank>
    implements IItemRendererProvider {

  @Override
  public void render(
      TileEntityMachineFluidTank tank,
      double x,
      double y,
      double z,
      float partialTicks,
      int destroyStage,
      float alpha) {
    GlStateManager.pushMatrix();
    GlStateManager.translate(x + 0.5D, y, z + 0.5D);
    GlStateManager.enableLighting();
    GlStateManager.disableCull();

    switch (tank.getBlockMetadata() - 10) {
      case 2 -> GlStateManager.rotate(180, 0F, 1F, 0F);
      case 4 -> GlStateManager.rotate(270, 0F, 1F, 0F);
      case 3 -> GlStateManager.rotate(0, 0F, 1F, 0F);
      case 5 -> GlStateManager.rotate(90, 0F, 1F, 0F);
    }
    FluidType type = tank.tankNew.getTankType();

    GlStateManager.shadeModel(GL11.GL_SMOOTH);
    bindTexture(ResourceManager.tank_tex);

    ResourceManager.fluidtank.renderPart("Frame");
    bindTexture(
        new ResourceLocation(RefStrings.MODID, getTextureFromType(tank.tankNew.getTankType())));
    ResourceManager.fluidtank.renderPart("Tank");

    GL11.glColor3d(1D, 1D, 1D);
    GlStateManager.shadeModel(GL11.GL_FLAT);

    if (type != null && type != Fluids.NONE) {

      RenderHelper.disableStandardItemLighting();
      GlStateManager.pushMatrix();
      GlStateManager.translate(-0.25, 0.5, -1.501);
      GL11.glRotated(90, 0, 1, 0);
      GlStateManager.scale(1.0F, 0.375F, 0.375F);
      DiamondPronter.pront(type.poison, type.flammability, type.reactivity, type.symbol);
      GlStateManager.popMatrix();
      GlStateManager.pushMatrix();
      GlStateManager.translate(0.25, 0.5, 1.501);
      GL11.glRotated(-90, 0, 1, 0);
      GlStateManager.scale(1.0F, 0.375F, 0.375F);
      DiamondPronter.pront(type.poison, type.flammability, type.reactivity, type.symbol);
      GlStateManager.popMatrix();
    }

    GlStateManager.popMatrix();
    RenderHelper.enableStandardItemLighting();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.machine_fluidtank);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.translate(0, -2, 0);
        GlStateManager.rotate(90, 0, 1, 0);
        GlStateManager.scale(3, 3, 3);
      }

      public void renderCommon() {
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        bindTexture(ResourceManager.tank_tex);
        ResourceManager.fluidtank.renderPart("Frame");
        GlStateManager.shadeModel(GL11.GL_FLAT);
        bindTexture(ResourceManager.tank_label_tex);
        ResourceManager.fluidtank.renderPart("Tank");
      }
    };
  }

  private String getTextureFromType(FluidType type) {

    if (type.customFluid) {
      int color = type.getTint();
      double r = ((color & 0xff0000) >> 16) / 255D;
      double g = ((color & 0x00ff00) >> 8) / 255D;
      double b = ((color & 0x0000ff) >> 0) / 255D;
      GL11.glColor3d(r, g, b);
      return "textures/models/tank/tank_NONE.png";
    }

    String s = type.getName();

    if (type.isAntimatter()
        || (type.hasTrait(FT_Corrosive.class)
            && type.getTrait(FT_Corrosive.class).isHighlyCorrosive())) s = "DANGER";

    return "textures/models/tank/tank_" + s + ".png";
  }
}
