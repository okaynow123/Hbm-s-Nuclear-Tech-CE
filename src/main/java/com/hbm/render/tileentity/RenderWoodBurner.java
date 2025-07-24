package com.hbm.render.tileentity;

import com.hbm.blocks.ModBlocks;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.TileEntityMachineWoodBurner;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11;

import com.hbm.blocks.BlockDummyable;
import com.hbm.main.ResourceManager;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

public class RenderWoodBurner extends TileEntitySpecialRenderer<TileEntityMachineWoodBurner>
    implements IItemRendererProvider {

  @Override
  public void render(
      TileEntityMachineWoodBurner tile,
      double x,
      double y,
      double z,
      float f,
      int destroyStage,
      float alpha) {
    GL11.glPushMatrix();
    GL11.glTranslated(x + 0.5, y, z + 0.5);
    GL11.glEnable(GL11.GL_LIGHTING);
    GL11.glEnable(GL11.GL_CULL_FACE);

    switch (tile.getBlockMetadata() - BlockDummyable.offset) {
      case 2:
        GL11.glRotatef(180, 0F, 1F, 0F);
        break;
      case 4:
        GL11.glRotatef(270, 0F, 1F, 0F);
        break;
      case 3:
        GL11.glRotatef(0, 0F, 1F, 0F);
        break;
      case 5:
        GL11.glRotatef(90, 0F, 1F, 0F);
        break;
    }

    GL11.glTranslated(-0.5, 0, -0.5);

    GL11.glShadeModel(GL11.GL_SMOOTH);
    bindTexture(ResourceManager.wood_burner_tex);
    ResourceManager.wood_burner.renderAll();
    GL11.glShadeModel(GL11.GL_FLAT);

    GL11.glPopMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.machine_wood_burner);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.translate(0, -4.1, 0); //Slight offset to get that 1 pixel perfect
        GlStateManager.scale(3.5, 3.5, 3.5);
      }

      public void renderCommon() {
        GlStateManager.rotate(90, 0, 1, 0);
        GlStateManager.scale(1, 1, 1);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        bindTexture(ResourceManager.wood_burner_tex);
        ResourceManager.wood_burner.renderAll();
        GlStateManager.shadeModel(GL11.GL_FLAT);
      }
    };
  }
}
