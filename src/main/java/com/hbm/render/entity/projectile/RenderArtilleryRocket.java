package com.hbm.render.entity.projectile;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11;

import com.hbm.entity.projectile.EntityArtilleryRocket;
import com.hbm.items.weapon.ItemAmmoHIMARS.HIMARSRocket;
import com.hbm.main.ResourceManager;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.util.ResourceLocation;

public class RenderArtilleryRocket extends Render<EntityArtilleryRocket> {

  public static final IRenderFactory<EntityArtilleryRocket> FACTORY = RenderArtilleryRocket::new;

  protected RenderArtilleryRocket(RenderManager renderManager) {
    super(renderManager);
  }

  @Override
  public void doRender(
      EntityArtilleryRocket rocket, double x, double y, double z, float f0, float f1) {

    GL11.glPushMatrix();
    GL11.glTranslated(x, y, z);
    GL11.glRotatef(
        rocket.prevRotationYaw + (rocket.rotationYaw - rocket.prevRotationYaw) * f1 - 90.0F,
        0.0F,
        1.0F,
        0.0F);
    GL11.glRotatef(
        rocket.prevRotationPitch + (rocket.rotationPitch - rocket.prevRotationPitch) * f1 - 90,
        0.0F,
        0.0F,
        1.0F);
    GL11.glRotated(90, 0, 1, 0);
    GL11.glRotated(90, 1, 0, 0);

    this.bindEntityTexture(rocket);

    boolean fog = GL11.glIsEnabled(GL11.GL_FOG);

    if (fog) GL11.glDisable(GL11.GL_FOG);
    GL11.glShadeModel(GL11.GL_SMOOTH);
    HIMARSRocket rocketType = rocket.getRocket();
    if (rocketType.modelType == HIMARSRocket.Type.Standard)
      ResourceManager.turret_himars.renderPart("RocketStandard");
    if (rocketType.modelType == HIMARSRocket.Type.Single)
      ResourceManager.turret_himars.renderPart("RocketSingle");
    GL11.glShadeModel(GL11.GL_FLAT);
    if (fog) GL11.glEnable(GL11.GL_FOG);

    GL11.glPopMatrix();
  }

  @Override
  protected ResourceLocation getEntityTexture(EntityArtilleryRocket rocket) {
    return rocket.getRocket().texture;
  }
}
