package com.hbm.api.block;

import com.hbm.entity.item.EntityTNTPrimedBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public interface IFuckingExplode {

    //Prevents stack overflows apparently
    public void explodeEntity(World world, double x, double y, double z, @Nullable EntityTNTPrimedBase entity);
    public default void explodeEntity(World world, BlockPos pos, @Nullable EntityTNTPrimedBase entity){
        explodeEntity(world, pos.getX(), pos.getY(), pos.getZ(), entity);
    };
}
