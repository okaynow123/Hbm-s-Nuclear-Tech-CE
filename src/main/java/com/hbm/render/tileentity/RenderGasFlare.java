package com.hbm.render.tileentity;

import com.hbm.blocks.ModBlocks;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.oil.TileEntityMachineGasFlare;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11;

public class RenderGasFlare extends TileEntitySpecialRenderer<TileEntityMachineGasFlare>
    implements IItemRendererProvider {

  @Override
  public boolean isGlobalRenderer(TileEntityMachineGasFlare te) {
    return true;
  }

  @Override
  public void render(
      TileEntityMachineGasFlare te,
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
    GL11.glRotatef(180, 0F, 1F, 0F);

    bindTexture(ResourceManager.oilflare_tex);
    ResourceManager.oilflare.renderAll();

    GlStateManager.enableCull();
    GL11.glPopMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.machine_flare);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.translate(0, -4, 0);
        GlStateManager.scale(2.5, 2.5, 2.5);
      }

      public void renderCommon() {
        GlStateManager.scale(0.5, 0.5, 0.5);
        GlStateManager.disableCull();
        bindTexture(ResourceManager.oilflare_tex);
        ResourceManager.oilflare.renderAll();
        GlStateManager.enableCull();
      }
    };
  }
}
