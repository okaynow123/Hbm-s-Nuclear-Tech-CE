package com.hbm.api.block;

import com.hbm.entity.item.EntityTNTPrimedBase;
import net.minecraft.world.World;

public interface IFuckingExplode {

    public void explodeEntity(World world, double x, double y, double z, EntityTNTPrimedBase entity);
}
