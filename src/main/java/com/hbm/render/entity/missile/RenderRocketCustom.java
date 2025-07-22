package com.hbm.render.entity.missile;

import com.hbm.entity.missile.EntityRideableRocket;
import com.hbm.handler.RocketStruct;
import com.hbm.main.ResourceManager;
import com.hbm.render.misc.MissilePronter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11;

public class RenderRocketCustom extends Render<EntityRideableRocket> {

    public static final IRenderFactory<EntityRideableRocket> FACTORY = (RenderManager man) -> {return new RenderRocketCustom(man);};

    protected RenderRocketCustom(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(EntityRideableRocket entity, double x, double y, double z, float f, float interp) {
        RocketStruct rocket = entity.getRocket();

        GL11.glPushMatrix();
        {

            GL11.glTranslated(x, y, z);
            GL11.glRotatef(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * interp - 90.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * interp, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * interp - 90.0F, 0.0F, -1.0F, 0.0F);

            MissilePronter.prontRocket(rocket, entity, Minecraft.getMinecraft().getTextureManager(), true, interp);

        }
        GL11.glPopMatrix();
    }
    @Override
    protected ResourceLocation getEntityTexture(EntityRideableRocket entity) {
        return ResourceManager.universal;
    }
}
