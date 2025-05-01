package com.hbm.render.tileentity;

import com.hbm.items.weapon.ItemAmmoHIMARS;
import com.hbm.main.ResourceManager;
import com.hbm.tileentity.turret.TileEntityTurretHIMARS;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class RenderTurretHIMARS extends TileEntitySpecialRenderer<TileEntityTurretHIMARS> {
  @Override
  public void render(
      TileEntityTurretHIMARS turret,
      double x,
      double y,
      double z,
      float interp,
      int destroyStage,
      float alpha) {
    Vec3d pos = turret.byHorizontalIndexOffset();

    GL11.glPushMatrix();
    GL11.glTranslated(x + pos.x, y, z + pos.z);
    GlStateManager.enableLighting();
    GlStateManager.enableCull();
    GlStateManager.shadeModel(GL11.GL_SMOOTH);

    bindTexture(ResourceManager.turret_arty_tex);
    ResourceManager.turret_himars.renderPart("Base");
    double yaw =
        -Math.toDegrees(
                turret.lastRotationYaw + (turret.rotationYaw - turret.lastRotationYaw) * interp)
            - 90D;
    double pitch =
        Math.toDegrees(
            turret.lastRotationPitch + (turret.rotationPitch - turret.lastRotationPitch) * interp);

    bindTexture(ResourceManager.turret_himars_tex);
    GL11.glRotated(yaw - 90, 0, 1, 0);
    ResourceManager.turret_himars.renderPart("Carriage");

    GL11.glTranslated(0, 2.25, 2);
    GL11.glRotated(pitch, 1, 0, 0);
    GL11.glTranslated(0, -2.25, -2);
    ResourceManager.turret_himars.renderPart("Launcher");

    double barrel = turret.lastCrane + (turret.crane - turret.lastCrane) * interp;
    double length = -5D;
    GL11.glTranslated(0, 0, barrel * length);
    ResourceManager.turret_himars.renderPart("Crane");

    if (turret.typeLoaded >= 0) {
      ItemAmmoHIMARS.HIMARSRocket rocket = ItemAmmoHIMARS.itemTypes[turret.typeLoaded];
      bindTexture(rocket.texture);

      if (rocket.modelType == ItemAmmoHIMARS.HIMARSRocket.Type.Standard) {
        ResourceManager.turret_himars.renderPart("TubeStandard");

        for (int i = 0; i < turret.ammo; i++) {
          ResourceManager.turret_himars.renderPart("CapStandard" + (5 - i + 1));
        }
      } else if (rocket.modelType == ItemAmmoHIMARS.HIMARSRocket.Type.Single) {
        ResourceManager.turret_himars.renderPart("TubeSingle");

        if (turret.hasAmmo()) {
          ResourceManager.turret_himars.renderPart("CapSingle");
        }
      }
    }

    GL11.glShadeModel(GL11.GL_FLAT);
    GL11.glPopMatrix();
  }
}
