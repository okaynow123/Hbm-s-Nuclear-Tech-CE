package com.hbm.render.entity;

import com.hbm.blocks.ModBlocks;
import com.hbm.entity.projectile.EntityBoxcar;
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

public class RenderBoxcar extends Render<EntityBoxcar> implements IItemRendererProvider {

  public static final IRenderFactory<EntityBoxcar> FACTORY =
      (RenderManager man) -> {
        return new RenderBoxcar(man);
      };

  protected RenderBoxcar(RenderManager renderManager) {
    super(renderManager);
  }

  @Override
  public void doRender(
      EntityBoxcar entity, double x, double y, double z, float entityYaw, float partialTicks) {
    GL11.glPushMatrix();
    GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
    GL11.glTranslatef((float) x, (float) y, (float) z);
    GL11.glEnable(GL11.GL_CULL_FACE);
    GlStateManager.enableLighting();

    GL11.glTranslatef(0, 0, -1.5F);
    GL11.glRotated(180, 0, 0, 1);
    GL11.glRotated(90, 1, 0, 0);

    bindTexture(ResourceManager.boxcar_tex);
    ResourceManager.boxcar.renderAll();

    GL11.glPopAttrib();
    GL11.glPopMatrix();
  }

  @Override
  protected ResourceLocation getEntityTexture(EntityBoxcar entity) {
    return ResourceManager.boxcar_tex;
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.boxcar);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.rotate(90, 0, 1, 0);
        GlStateManager.translate(0, -1, 0);
        GlStateManager.scale(4, 4, 4);
      }

      public void renderCommon() {
        GlStateManager.scale(0.5, 0.5, 0.5);
        bindTexture(ResourceManager.boxcar_tex);
        ResourceManager.boxcar.renderAll();
      }
    };
  }
}
