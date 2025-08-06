package com.hbm.world.feature;

import com.hbm.blocks.PlantEnums;
import com.hbm.blocks.generic.BlockFlowerPlant;
import com.hbm.blocks.generic.BlockPlantEnumMeta;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public class NTMFlowers extends WorldGenerator {
    private BlockFlowerPlant flower;
    private IBlockState state;

    public NTMFlowers(BlockFlowerPlant flowerIn, PlantEnums.EnumFlowerPlantType type) {
        this.setGeneratedBlock(flowerIn, type);
    }

    public void setGeneratedBlock(BlockFlowerPlant flowerIn, PlantEnums.EnumFlowerPlantType typeIn) {
        this.flower = flowerIn;
        this.state = BlockPlantEnumMeta.stateFromEnum(flowerIn, typeIn);
    }

    public boolean generate(World worldIn, Random rand, BlockPos position) {
        for (int i = 0; i < 64; ++i) {
            BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));

            if (worldIn.isAirBlock(blockpos) && (!worldIn.provider.isNether() || blockpos.getY() < 255) && this.flower.canBlockStay(worldIn, blockpos, this.state)) {
                worldIn.setBlockState(blockpos, this.state, 2);
            }
        }

        return true;
    }
}
