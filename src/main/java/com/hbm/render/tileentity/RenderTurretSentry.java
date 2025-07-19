package com.hbm.render.tileentity;

import com.hbm.blocks.ModBlocks;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.turret.TileEntityTurretSentry;
import com.hbm.tileentity.turret.TileEntityTurretSentryDamaged;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

public class RenderTurretSentry extends TileEntitySpecialRenderer<TileEntityTurretSentry>
    implements IItemRendererProvider {

  @Override
  public void render(
      @NotNull TileEntityTurretSentry turret,
      double x,
      double y,
      double z,
      float partialTicks,
      int destroyStage,
      float alpha) {

    Vec3d pos = new Vec3d(0.5, 0, 0.5);

    GlStateManager.pushMatrix();
    GlStateManager.translate(x + pos.x, y, z + pos.z);
    GlStateManager.enableLighting();
    GlStateManager.enableCull();
    GlStateManager.shadeModel(GL11.GL_SMOOTH);

    boolean damaged = turret instanceof TileEntityTurretSentryDamaged;

    if (damaged) bindTexture(ResourceManager.turret_sentry_damaged_tex);
    else bindTexture(ResourceManager.turret_sentry_tex);

    ResourceManager.turret_sentry.renderPart("Base");

    double yaw =
        -Math.toDegrees(
            turret.lastRotationYaw + (turret.rotationYaw - turret.lastRotationYaw) * partialTicks);
    double pitch =
        Math.toDegrees(
            turret.lastRotationPitch
                + (turret.rotationPitch - turret.lastRotationPitch) * partialTicks);

    GlStateManager.rotate((float) yaw, 0, 1, 0);
    ResourceManager.turret_sentry.renderPart("Pivot");

    GlStateManager.translate(0, 1.25, 0);
    GlStateManager.rotate((float) -pitch, 1, 0, 0);
    GlStateManager.translate(0, -1.25, 0);
    ResourceManager.turret_sentry.renderPart("Body");
    ResourceManager.turret_sentry.renderPart("Drum");

    GlStateManager.pushMatrix();
    GlStateManager.translate(
        0,
        0,
        (turret.lastBarrelLeftPos
                + (turret.barrelLeftPos - turret.lastBarrelLeftPos) * partialTicks)
            * -0.5);
    ResourceManager.turret_sentry.renderPart("BarrelL");
    GlStateManager.popMatrix();

    GlStateManager.pushMatrix();
    if (damaged) {
      GlStateManager.translate(0, 1.5, 0.5);
      GlStateManager.rotate(25, 1, 0, 0);
      GlStateManager.translate(0, -1.5, -0.5);
    } else {
      GlStateManager.translate(
          0,
          0,
          (turret.lastBarrelRightPos
                  + (turret.barrelRightPos - turret.lastBarrelRightPos) * partialTicks)
              * -0.5);
    }
    ResourceManager.turret_sentry.renderPart("BarrelR");
    GlStateManager.popMatrix();

    GlStateManager.shadeModel(GL11.GL_FLAT);
    GlStateManager.popMatrix();
  }

  @Override
  public Item[] getItemsForRenderer() {
    return new Item[] {
      Item.getItemFromBlock(ModBlocks.turret_sentry),
      Item.getItemFromBlock(ModBlocks.turret_sentry_damaged)
    };
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.turret_sentry);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.translate(0, -4, 0);
        GlStateManager.scale(7, 7, 7);
      }

      public void renderCommon() {
        GlStateManager.rotate(90, 0, 1, 0);
        boolean damaged = item == Item.getItemFromBlock(ModBlocks.turret_sentry_damaged);

        if (damaged) {
          bindTexture(ResourceManager.turret_sentry_damaged_tex);
        } else {
          bindTexture(ResourceManager.turret_sentry_tex);
        }

        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        ResourceManager.turret_sentry.renderPart("Base");
        ResourceManager.turret_sentry.renderPart("Pivot");
        ResourceManager.turret_sentry.renderPart("Body");
        ResourceManager.turret_sentry.renderPart("Drum");
        ResourceManager.turret_sentry.renderPart("BarrelL");
        ResourceManager.turret_sentry.renderPart("BarrelR");
        GlStateManager.shadeModel(GL11.GL_FLAT);
      }
    };
  }
}
