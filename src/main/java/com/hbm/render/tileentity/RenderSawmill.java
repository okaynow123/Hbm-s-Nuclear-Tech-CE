package com.hbm.render.tileentity;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ModBlocks;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.TileEntitySawmill;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11; import net.minecraft.client.renderer.GlStateManager;

public class RenderSawmill extends TileEntitySpecialRenderer<TileEntitySawmill>
    implements IItemRendererProvider {
  @Override
  public void render(
      TileEntitySawmill sawmill,
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

    switch (sawmill.getBlockMetadata() - BlockDummyable.offset) {
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

    float rot = sawmill.lastSpin + (sawmill.spin - sawmill.lastSpin) * partialTicks;
    bindTexture(ResourceManager.sawmill_tex);
    renderCommon(rot, sawmill.hasBlade);

    GlStateManager.popMatrix();
  }

  public static void renderCommon(float rot, boolean hasBlade) {
    ResourceManager.sawmill.renderPart("Main");

    if (hasBlade) {
      GlStateManager.pushMatrix();
      GlStateManager.translate(0, 1.375, 0);
      GlStateManager.rotate(-rot * 2, 0, 0, 1);
      GlStateManager.translate(0, -1.375, 0);
      ResourceManager.sawmill.renderPart("Blade");
      GlStateManager.popMatrix();
    }

    GlStateManager.pushMatrix();
    GlStateManager.translate(0.5625, 1.375, 0);
    GlStateManager.rotate(rot, 0, 0, 1);
    GlStateManager.translate(-0.5625, -1.375, 0);
    ResourceManager.sawmill.renderPart("GearLeft");
    GlStateManager.popMatrix();

    GlStateManager.pushMatrix();
    GlStateManager.translate(-0.5625, 1.375, 0);
    GlStateManager.rotate(-rot, 0, 0, 1);
    GlStateManager.translate(0.5625, -1.375, 0);
    ResourceManager.sawmill.renderPart("GearRight");
    GlStateManager.popMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.machine_sawmill);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.translate(0, -1.5, 0);
        GlStateManager.scale(3.25, 3.25, 3.25);
      }

      public void renderCommon(ItemStack stack) {
        GlStateManager.rotate(90, 0F, 1F, 0F);
        boolean cog = stack.getItemDamage() != 1;
        bindTexture(ResourceManager.sawmill_tex);
        RenderSawmill.renderCommon(cog ? System.currentTimeMillis() % 3600 * 0.1F : 0, cog);
      }
    };
  }
}
