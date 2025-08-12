package com.hbm.blocks.bomb;

import com.hbm.explosion.ExplosionNT;
import com.hbm.particle.helper.ExplosionSmallCreator;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockChargeDynamite extends BlockChargeBase {

    public BlockChargeDynamite(String registryName) {
        super(registryName);
    }

    @Override
    public BombReturnCode explode(World world, BlockPos pos, Entity detonator) {
        if(!world.isRemote) {
            safe = true;
            world.setBlockToAir(pos);
            safe = false;
            int x = pos.getX(), y = pos.getY(), z = pos.getZ();
            ExplosionNT exp = new ExplosionNT(world, null, x + 0.5, y + 0.5, z + 0.5, 4F);
            exp.explode();
            ExplosionSmallCreator.composeEffect(world, x + 0.5, y + 0.5, z + 0.5, 15, 3F, 1.25F);

            return BombReturnCode.DETONATED;
        }
        return BombReturnCode.UNDEFINED;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }
}
