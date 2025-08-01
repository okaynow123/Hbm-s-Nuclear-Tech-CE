package com.hbm.explosion.vanillant.standard;

import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.interfaces.IBlockMutator;
import com.hbm.explosion.vanillant.interfaces.IBlockProcessor;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Iterator;

public class BlockProcessorNoDamage implements IBlockProcessor {


    protected IBlockMutator convert;

    public BlockProcessorNoDamage() { }

    public BlockProcessorNoDamage withBlockEffect(IBlockMutator convert) {
        this.convert = convert;
        return this;
    }

    @Override
    public void process(ExplosionVNT explosion, World world, double x, double y, double z, HashSet<BlockPos> affectedBlocks) {

        Iterator iterator = affectedBlocks.iterator();

        while(iterator.hasNext()) {
            BlockPos blockToMutatePre = (BlockPos) iterator.next(); //Make use of BlockPos/BlockState
            IBlockState block = world.getBlockState(blockToMutatePre);

            if(block.getMaterial() != Material.AIR) {
                if(this.convert != null) this.convert.mutatePre(explosion, block, blockToMutatePre);
            }
        }


        if(this.convert != null) {
            iterator = affectedBlocks.iterator();

            while(iterator.hasNext()) {
                BlockPos blockToConvert = (BlockPos) iterator.next();
                IBlockState block = world.getBlockState(blockToConvert);//Make use of BlockPos/BlockState

                if(block.getMaterial() == Material.AIR) {
                    this.convert.mutatePost(explosion, blockToConvert);
                }
            }
        }

        affectedBlocks.clear(); //tricks the standard SFX to not do the block damage particles
    }
}
