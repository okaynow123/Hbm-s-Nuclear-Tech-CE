package com.hbm.render.entity;

import com.hbm.blocks.ModBlocks;
import com.hbm.entity.projectile.EntityDuchessGambit;
import com.hbm.interfaces.AutoRegister;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.render.tileentity.IItemRendererProvider;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11;
@AutoRegister(factory = "FACTORY")
public class RenderBoat extends Render<EntityDuchessGambit> implements IItemRendererProvider {

  public static final IRenderFactory<EntityDuchessGambit> FACTORY =
      (RenderManager man) -> {
        return new RenderBoat(man);
      };

  protected RenderBoat(RenderManager renderManager) {
    super(renderManager);
  }

  @Override
  public void doRender(
      EntityDuchessGambit entity,
      double x,
      double y,
      double z,
      float entityYaw,
      float partialTicks) {
    GlStateManager.pushMatrix();
    GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
    GlStateManager.translate((float) x, (float) y, (float) z);
    GlStateManager.enableCull();
    GlStateManager.enableLighting();

    GlStateManager.translate(0, 0, -1.0F);

    bindTexture(ResourceManager.duchessgambit_tex);
    ResourceManager.duchessgambit.renderAll();

    GL11.glPopAttrib();
    GlStateManager.popMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.boat);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.rotate(-90, 0, 1, 0);
        GlStateManager.translate(0, 1, 0);
        GlStateManager.scale(1.75, 1.75, 1.75);
      }

      public void renderCommon() {
        GlStateManager.scale(0.5, 0.5, 0.5);
        GlStateManager.translate(0, 0, -3);
        bindTexture(ResourceManager.duchessgambit_tex);
        ResourceManager.duchessgambit.renderAll();
      }
    };
  }

  @Override
  protected ResourceLocation getEntityTexture(EntityDuchessGambit entity) {
    return ResourceManager.boxcar_tex;
  }
}
