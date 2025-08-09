package com.hbm.render.entity;

import com.hbm.entity.mob.EntityCreeperNuclear;
import com.hbm.interfaces.AutoRegister;
import com.hbm.lib.RefStrings;
import com.hbm.render.entity.layers.LayerCreeperChargeUniversal;
import net.minecraft.client.renderer.entity.RenderCreeper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerCreeperCharge;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.ResourceLocation;

@AutoRegister(entity = EntityCreeperNuclear.class)
public class RenderCreeperNuclear extends RenderCreeper {
    private static final ResourceLocation creeperTextures = new ResourceLocation(RefStrings.MODID + ":" + "textures/entity/creeper.png");
    private static final ResourceLocation creeperArmor = new ResourceLocation(RefStrings.MODID + ":" + "textures/entity/creeper_armor.png");

    public RenderCreeperNuclear(RenderManager rendermanagerIn) {
        super(rendermanagerIn);
        for (int i = 0; i < this.layerRenderers.size(); i++) {
            LayerRenderer<EntityCreeper> layer = this.layerRenderers.get(i);
            if (layer instanceof LayerCreeperCharge) {
                this.layerRenderers.set(i, new LayerCreeperChargeUniversal(this, creeperArmor));
                break;
            }
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityCreeper entity) {
        return creeperTextures;
    }
}
