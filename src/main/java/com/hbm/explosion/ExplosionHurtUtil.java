package com.hbm.explosion;

import com.hbm.util.ContaminationUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class ExplosionHurtUtil {
    /**
     * Adds radiation to entities in an AoE
     * @param world
     * @param x
     * @param y
     * @param z
     * @param outer The least amount of radiation received on the very edge of the AoE
     * @param inner The greatest amount of radiation received on the very center of the AoE
     * @param radius
     */
    public static void doRadiation(World world, double x, double y, double z, float outer, float inner, double radius) {

        List<EntityLivingBase>
                entities = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius));

        for(EntityLivingBase entity : entities) {

            Vec3d vec = new Vec3d(x - entity.posX, y - entity.posY, z - entity.posZ);

            double dist = vec.length();

            if(dist > radius)
                continue;

            double interpolation = 1 - (dist / radius);
            float rad = (float) (outer + (inner - outer) * interpolation);

            ContaminationUtil.contaminate(entity, ContaminationUtil.HazardType.RADIATION, ContaminationUtil.ContaminationType.CREATIVE, rad);
        }
    }

}
