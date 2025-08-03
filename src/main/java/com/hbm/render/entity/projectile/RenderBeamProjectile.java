package com.hbm.render.entity.projectile;

import com.hbm.entity.projectile.EntityBulletBeamBase;
import com.hbm.interfaces.AutoRegister;
import com.hbm.main.ResourceManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;
@AutoRegister(factory = "FACTORY")
public class RenderBeamProjectile extends Render<EntityBulletBeamBase> {

    public static final IRenderFactory<EntityBulletBeamBase> FACTORY = RenderBeamProjectile::new;

    protected RenderBeamProjectile(RenderManager renderManager) {
        super(renderManager);
        this.shadowSize = -1; //Disable shadow
    }

    @Override
    public void doRender(EntityBulletBeamBase bullet, double x, double y, double z, float f0, float interp) {
        if (bullet.config == null) bullet.config = bullet.getBulletConfig();
        if (bullet.config == null) return;

        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y, (float) z);

        boolean fog = GL11.glIsEnabled(GL11.GL_FOG);
        GlStateManager.disableFog();

        if (bullet.config.renderRotations) {
            GlStateManager.rotate(bullet.prevRotationYaw + (bullet.rotationYaw - bullet.prevRotationYaw) * interp - 90.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(bullet.prevRotationPitch + (bullet.rotationPitch - bullet.prevRotationPitch) * interp + 180, 0.0F, 0.0F, 1.0F);
        }

        if (bullet.config.rendererBeam != null) {
            bullet.config.rendererBeam.accept(bullet, interp);
        }

        if (fog) GlStateManager.enableFog();

        GlStateManager.popMatrix();
    }

    @Override
    protected @Nullable ResourceLocation getEntityTexture(EntityBulletBeamBase entity) {
        return ResourceManager.universal;
    }
}
