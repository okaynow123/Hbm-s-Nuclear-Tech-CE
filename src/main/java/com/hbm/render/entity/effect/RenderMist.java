package com.hbm.render.entity.effect;

import com.hbm.entity.effect.EntityFireLingering;
import com.hbm.entity.effect.EntityMist;
import com.hbm.interfaces.AutoRegister;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.jetbrains.annotations.Nullable;

@AutoRegister(entity = EntityMist.class, factory = "FACTORY")
@AutoRegister(entity = EntityFireLingering.class, factory = "FACTORY")
public class RenderMist extends Render<Entity> {

    public static final IRenderFactory<Entity> FACTORY = manager -> {return new RenderMist(manager);};
    protected RenderMist(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float f0, float f1) {
    }

    @Override
    protected @Nullable ResourceLocation getEntityTexture(Entity entity) {
        return null;
    }

}

