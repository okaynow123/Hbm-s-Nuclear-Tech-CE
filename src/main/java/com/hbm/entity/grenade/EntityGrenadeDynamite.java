package com.hbm.entity.grenade;

import com.hbm.interfaces.AutoRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

@AutoRegister(name = "entity_grenade_dynamite")
public class EntityGrenadeDynamite extends EntityGrenadeBouncyBase {

    public EntityGrenadeDynamite(World world) {
        super(world);
    }

    public EntityGrenadeDynamite(World world, EntityLivingBase living, EnumHand hand) {
        super(world, living, hand);
    }

    public EntityGrenadeDynamite(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public void explode() {
        world.newExplosion(this, posX, posY + 0.25D, posZ, 3F, false, false);
        this.setDead();
    }

    @Override
    protected int getMaxTimer() {
        return 60;
    }

    @Override
    protected double getBounceMod() {
        return 0.5D;
    }
}
