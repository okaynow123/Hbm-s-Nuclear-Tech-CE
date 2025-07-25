package com.hbm.render.tileentity;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.items.weapon.ItemMissileStandard;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.render.item.ItemRenderMissileGeneric;
import com.hbm.tileentity.bomb.TileEntityLaunchPadLarge;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11; import net.minecraft.client.renderer.GlStateManager;

import java.util.function.Consumer;

public class RenderLaunchPadLarge extends TileEntitySpecialRenderer<TileEntityLaunchPadLarge>
    implements IItemRendererProvider {

  @Override
  public void render(
      TileEntityLaunchPadLarge pad,
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

    switch (pad.getBlockMetadata() - BlockDummyable.offset) {
      case 2:
        GlStateManager.rotate(90, 0F, 1F, 0F);
        break;
      case 4:
        GlStateManager.rotate(180, 0F, 1F, 0F);
        break;
      case 3:
        GlStateManager.rotate(270, 0F, 1F, 0F);
        break;
      case 5:
        GlStateManager.rotate(0, 0F, 1F, 0F);
        break;
    }

    bindTexture(ResourceManager.missile_erector_tex);
    ResourceManager.missile_erector.renderPart("Pad");

    if (pad.formFactor >= 0
        && pad.formFactor < ItemMissileStandard.MissileFormFactor.values().length) {

      ItemMissileStandard.MissileFormFactor formFactor =
          ItemMissileStandard.MissileFormFactor.values()[pad.formFactor];
      String[] parts = null;
      double[] offset = null;

      switch (formFactor) {
        case ABM:
          parts = new String[] {"ABM_Pad", "ABM_Erector", "ABM_Pivot", "ABM_Rope"};
          offset = new double[] {1.5D, 1.25D};
          bindTexture(ResourceManager.missile_erector_abm_tex);
          break;
        case MICRO:
          parts = new String[] {"Micro_Pad", "Micro_Erector", "Micro_Pivot", "Micro_Rope"};
          offset = new double[] {1.5D, 1.25D};
          bindTexture(ResourceManager.missile_erector_micro_tex);
          break;
        case V2:
          parts = new String[] {"V2_Pad", "V2_Erector", "V2_Pivot", "V2_Rope"};
          offset = new double[] {1.75D, 1.25D};
          bindTexture(ResourceManager.missile_erector_v2_tex);
          break;
        case STRONG:
          parts = new String[] {"Strong_Pad", "Strong_Erector", "Strong_Pivot", "Strong_Rope"};
          offset = new double[] {3D, 1.5D};
          bindTexture(ResourceManager.missile_erector_strong_tex);
          break;
        case HUGE:
          parts = new String[] {"Huge_Pad", "Huge_Erector", "Huge_Pivot", "Huge_Rope"};
          offset = new double[] {3D, 1.5D};
          bindTexture(ResourceManager.missile_erector_huge_tex);
          break;
        case ATLAS:
          parts = new String[] {"Atlas_Pad", "Atlas_Erector", "Atlas_Pivot", "Atlas_Rope"};
          offset = new double[] {4D, 1.5D};
          bindTexture(ResourceManager.missile_erector_atlas_tex);
          break;
        case OTHER:
          parts = new String[] {"ABM_Pad", "ABM_Erector", "ABM_Pivot", "ABM_Rope"};
          offset = new double[] {1.5D, 1.25D};
          bindTexture(ResourceManager.missile_erector_abm_tex);
          break;
      }

      float erectorAngle = pad.prevErector + (pad.erector - pad.prevErector) * partialTicks;
      float erectorLift = pad.prevLift + (pad.lift - pad.prevLift) * partialTicks;

      GlStateManager.pushMatrix();
      GlStateManager.shadeModel(GL11.GL_SMOOTH);
      ResourceManager.missile_erector.renderPart(parts[0]);
      if (pad.toRender != null && pad.erected) ResourceManager.missile_erector.renderPart(parts[3]);
      GlStateManager.translate(0, offset[1], -offset[0]);
      GlStateManager.rotate(-erectorAngle, 1, 0, 0);
      GlStateManager.translate(0, -offset[1], offset[0]);
      ResourceManager.missile_erector.renderPart(parts[2]);
      GlStateManager.translate(0, erectorLift, 0);
      ResourceManager.missile_erector.renderPart(parts[1]);
      GlStateManager.shadeModel(GL11.GL_FLAT);

      if (pad.erected) {
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
      }

      if (pad.toRender != null && (pad.erected || pad.readyToLoad)) {
        GlStateManager.translate(0, 2, 0);
        Consumer<TextureManager> renderer =
            ItemRenderMissileGeneric.renderers.get(
                new ComparableStack(pad.toRender).makeSingular());
        if (renderer != null) renderer.accept(this.rendererDispatcher.renderEngine);
      }
      GlStateManager.popMatrix();
    }

    GlStateManager.popMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.launch_pad_large);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.translate(0, -3.75, 0);
        GlStateManager.scale(1.625, 1.625, 1.625);
      }

      public void renderCommon() {
        GlStateManager.scale(0.5, 0.5, 0.5);
        GlStateManager.rotate(90, 0F, 1F, 0F);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        bindTexture(ResourceManager.missile_erector_tex);
        ResourceManager.missile_erector.renderPart("Pad");
        bindTexture(ResourceManager.missile_erector_atlas_tex);
        ResourceManager.missile_erector.renderPart("Atlas_Pad");
        ResourceManager.missile_erector.renderPart("Atlas_Erector");
        ResourceManager.missile_erector.renderPart("Atlas_Pivot");
        GlStateManager.shadeModel(GL11.GL_FLAT);
      }
    };
  }
}
