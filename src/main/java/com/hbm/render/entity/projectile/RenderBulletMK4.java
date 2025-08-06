package com.hbm.render.entity.projectile;

import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.entity.projectile.EntityBulletBaseMK4CL;
import com.hbm.interfaces.AutoRegister;
import com.hbm.main.ResourceManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.jetbrains.annotations.Nullable;
@AutoRegister(entity = EntityBulletBaseMK4.class, factory = "FACTORY")
@AutoRegister(entity = EntityBulletBaseMK4CL.class, factory = "FACTORY")
public class RenderBulletMK4 extends Render<EntityBulletBaseMK4> {

    public static final IRenderFactory<EntityBulletBaseMK4> FACTORY = RenderBulletMK4::new;

    protected RenderBulletMK4(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(EntityBulletBaseMK4 bullet, double x, double y, double z, float f0, float interp) {
        if (bullet.config == null) bullet.config = bullet.getBulletConfig();
        if (bullet.config == null) return;

        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y, (float) z);

        if (bullet.config.renderRotations) {
            GlStateManager.rotate(bullet.prevRotationYaw + (bullet.rotationYaw - bullet.prevRotationYaw) * interp - 90.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(bullet.prevRotationPitch + (bullet.rotationPitch - bullet.prevRotationPitch) * interp + 180, 0.0F, 0.0F, 1.0F);
        }

        if (bullet.config.renderer != null) {
            bullet.config.renderer.accept(bullet, interp);
        }

        GlStateManager.popMatrix();
    }


    @Override
    protected @Nullable ResourceLocation getEntityTexture(EntityBulletBaseMK4 entity) {
        return ResourceManager.universal;
    }
}
