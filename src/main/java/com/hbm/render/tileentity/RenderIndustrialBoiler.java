package com.hbm.render.tileentity;

import com.hbm.blocks.ModBlocks;
import com.hbm.interfaces.AutoRegister;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.TileEntityHeatBoilerIndustrial;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;
@AutoRegister
public class RenderIndustrialBoiler
    extends TileEntitySpecialRenderer<TileEntityHeatBoilerIndustrial>
    implements IItemRendererProvider {

  @Override
  public void render(
      @NotNull TileEntityHeatBoilerIndustrial boiler,
      double x,
      double y,
      double z,
      float partialTicks,
      int destroyStage,
      float alpha) {
    GlStateManager.pushMatrix();
    GlStateManager.translate(x + 0.5D, y, z + 0.5D);
    GlStateManager.enableLighting();

    GlStateManager.shadeModel(GL11.GL_SMOOTH);
    bindTexture(ResourceManager.boiler_industrial_tex);
    ResourceManager.boiler_industrial.renderAll();
    GlStateManager.shadeModel(GL11.GL_FLAT);

    GlStateManager.popMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.machine_industrial_boiler);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.translate(0, -3, 0);
        GlStateManager.scale(2.5, 2.5, 2.5);
      }

      public void renderCommon() {
        GlStateManager.rotate(90, 0F, 1F, 0F);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        bindTexture(ResourceManager.boiler_industrial_tex);
        ResourceManager.boiler_industrial.renderAll();
        GlStateManager.shadeModel(GL11.GL_FLAT);
      }
    };
  }
}
