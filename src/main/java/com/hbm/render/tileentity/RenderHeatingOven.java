package com.hbm.render.tileentity;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ModBlocks;
import com.hbm.interfaces.AutoRegister;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.TileEntityHeaterOven;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11; import net.minecraft.client.renderer.GlStateManager;
@AutoRegister
public class RenderHeatingOven extends TileEntitySpecialRenderer<TileEntityHeaterOven>
    implements IItemRendererProvider {
  @Override
  public boolean isGlobalRenderer(TileEntityHeaterOven te) {
    return true;
  }

  @Override
  public void render(
      TileEntityHeaterOven tile,
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

    switch (tile.getBlockMetadata() - BlockDummyable.offset) {
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
    GlStateManager.rotate(-90, 0F, 1F, 0F);

    TileEntityHeaterOven oven = (TileEntityHeaterOven) tile;

    bindTexture(ResourceManager.heater_oven_tex);
    ResourceManager.heater_oven.renderPart("Main");

    GlStateManager.pushMatrix();
    float door = oven.prevDoorAngle + (oven.doorAngle - oven.prevDoorAngle) * partialTicks;
    GlStateManager.translate(0, 0, door * 0.75D / 135D);
    ResourceManager.heater_oven.renderPart("Door");
    GlStateManager.popMatrix();

    if (oven.wasOn) {
      GlStateManager.pushMatrix();
      GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);

      GlStateManager.disableLighting();
      GlStateManager.disableCull();
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
      ResourceManager.heater_oven.renderPart("InnerBurning");
      GlStateManager.enableLighting();

      GL11.glPopAttrib();
      GlStateManager.popMatrix();
    } else {
      ResourceManager.heater_oven.renderPart("Inner");
    }

    GlStateManager.popMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.heater_oven);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.translate(0, -1, 0);
        GlStateManager.scale(1.9, 1.9, 1.9);
      }

      public void renderCommon() {
        GlStateManager.rotate(180, 0, 1, 0);
        GlStateManager.scale(1.9, 1.9, 1.9);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        bindTexture(ResourceManager.heater_oven_tex);
        ResourceManager.heater_oven.renderAll();
        GlStateManager.shadeModel(GL11.GL_FLAT);
      }
    };
  }
}
