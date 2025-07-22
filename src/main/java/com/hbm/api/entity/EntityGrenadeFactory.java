package com.hbm.api.entity;

import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.world.World;

@FunctionalInterface
public interface EntityGrenadeFactory {
    IProjectile create(World world, IPosition position);
}