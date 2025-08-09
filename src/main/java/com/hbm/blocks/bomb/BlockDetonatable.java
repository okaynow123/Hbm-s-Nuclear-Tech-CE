package com.hbm.blocks.bomb;

import com.hbm.api.block.IExploder;
import com.hbm.blocks.generic.BlockFlammable;
import com.hbm.entity.item.EntityTNTPrimedBase;
import com.hbm.render.block.BlockBakeFrame;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public abstract class BlockDetonatable  extends BlockFlammable implements IExploder {
    protected int popFuse; // A shorter fuse for when this explosive is dinked by another
    protected boolean detonateOnCollision;
    protected boolean detonateOnShot;
    protected IBlockState stateCache; //block state right before explosion


    public BlockDetonatable(Material mat, String s, int en, int flam, int popFuse, boolean detonateOnCollision, boolean detonateOnShot) {
        super(mat, s, en, flam);
        this.popFuse = popFuse;
        this.detonateOnCollision = detonateOnCollision;
        this.detonateOnShot = detonateOnShot;
    }

    public BlockDetonatable(Material mat, String s, int en, int flam, int popFuse, boolean detonateOnCollision, boolean detonateOnShot, BlockBakeFrame... frames) {
        super(mat, s, en, flam, frames);
        this.popFuse = popFuse;
        this.detonateOnCollision = detonateOnCollision;
        this.detonateOnShot = detonateOnShot;
    }

    @Override
    public void onBlockExploded(World world, BlockPos pos, Explosion explosion)
    {
        stateCache = world.getBlockState(pos);
        super.onBlockExploded(world, pos, explosion);
    }

    @Override
    public void onExplosionDestroy(World world, BlockPos pos, Explosion explosion) {
        if (!world.isRemote) {
            EntityTNTPrimedBase tntPrimed = new EntityTNTPrimedBase(
                    world,
                    pos.getX() + 0.5D,
                    pos.getY() + 0.5D,
                    pos.getZ() + 0.5D,
                    explosion.getExplosivePlacedBy(),
                    stateCache
            );

            tntPrimed.fuse = popFuse <= 0 ? 0 : world.rand.nextInt(popFuse) + popFuse / 2;
            tntPrimed.detonateOnCollision = detonateOnCollision;

            world.spawnEntity(tntPrimed);
        }
    }

    @Override
    public boolean canDropFromExplosion(Explosion explosion) {
        return false;
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos) {
        if (!world.isRemote && shouldIgnite(world, pos)) {
            world.setBlockToAir(pos);
            onBlockExploded(world, pos, null);
        }
    }


    public void onShot(World world, BlockPos pos) {
        if (!detonateOnShot) return;

        world.setBlockToAir(pos);
        explodeEntity(world, pos, null); // insta-explode
    }

}
