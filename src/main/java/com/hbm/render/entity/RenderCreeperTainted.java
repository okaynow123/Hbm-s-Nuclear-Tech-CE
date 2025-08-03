package com.hbm.render.entity;

import com.hbm.entity.mob.EntityCreeperTainted;
import com.hbm.interfaces.AutoRegister;
import com.hbm.lib.RefStrings;
import net.minecraft.client.renderer.entity.RenderCreeper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.ResourceLocation;
@AutoRegister(entity = EntityCreeperTainted.class)
public class RenderCreeperTainted extends RenderCreeper {

    private static final ResourceLocation creeperTextures = new ResourceLocation(RefStrings.MODID + ":" + "textures/entity/creeper_tainted.png");

    public RenderCreeperTainted(RenderManager rendermanagerIn) {
        super(rendermanagerIn);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityCreeper entity) {
        return creeperTextures;
    }
}
