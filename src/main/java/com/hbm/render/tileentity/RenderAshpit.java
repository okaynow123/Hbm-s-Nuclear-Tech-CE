package com.hbm.render.tileentity;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ModBlocks;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.TileEntityAshpit;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11;

public class RenderAshpit extends TileEntitySpecialRenderer<TileEntityAshpit>
    implements IItemRendererProvider {
  @Override
  public boolean isGlobalRenderer(TileEntityAshpit te) {
    return true;
  }

  @Override
  public void render(
      TileEntityAshpit oven,
      double x,
      double y,
      double z,
      float interp,
      int destroyStage,
      float alpha) {
    GL11.glPushMatrix();
    GL11.glTranslated(x + 0.5D, y, z + 0.5D);
    GL11.glEnable(GL11.GL_LIGHTING);
    GL11.glEnable(GL11.GL_CULL_FACE);

    switch (oven.getBlockMetadata() - BlockDummyable.offset) {
      case 3:
        GL11.glRotatef(0, 0F, 1F, 0F);
        break;
      case 5:
        GL11.glRotatef(90, 0F, 1F, 0F);
        break;
      case 2:
        GL11.glRotatef(180, 0F, 1F, 0F);
        break;
      case 4:
        GL11.glRotatef(270, 0F, 1F, 0F);
        break;
    }
    GL11.glRotatef(-90, 0F, 1F, 0F);

    bindTexture(ResourceManager.ashpit_tex);
    ResourceManager.heater_oven.renderPart("Main");

    GL11.glPushMatrix();
    float door = oven.prevDoorAngle + (oven.doorAngle - oven.prevDoorAngle) * interp;
    GL11.glTranslated(0, 0, door * 0.75D / 135D);
    ResourceManager.heater_oven.renderPart("Door");
    GL11.glPopMatrix();

    if (oven.isFull) {
      ResourceManager.heater_oven.renderPart("InnerBurning");
    } else {
      ResourceManager.heater_oven.renderPart("Inner");
    }

    GL11.glPopMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.machine_ashpit);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.translate(0, -1, 0);
        GlStateManager.scale(3.25, 3.25, 3.25);
      }

      public void renderCommon() {
        bindTexture(ResourceManager.ashpit_tex);
        ResourceManager.heater_oven.renderPart("Main");
        ResourceManager.heater_oven.renderPart("Door");
      }
    };
  }
}
