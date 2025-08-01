package com.hbm.render.entity;

import com.hbm.lib.RefStrings;
import net.minecraft.client.renderer.entity.RenderCreeper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.ResourceLocation;

public class RenderCreeperNuclear extends RenderCreeper {
    private static final ResourceLocation creeperTextures = new ResourceLocation(RefStrings.MODID + ":" + "textures/entity/creeper.png");

    public RenderCreeperNuclear(RenderManager rendermanagerIn) {
        super(rendermanagerIn);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityCreeper entity) {
        return creeperTextures;
    }
}
