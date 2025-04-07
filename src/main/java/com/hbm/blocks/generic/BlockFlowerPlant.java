package com.hbm.blocks.generic;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.PlantEnums;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class BlockFlowerPlant extends BlockPlantEnumMeta implements IGrowable {

    public BlockFlowerPlant(String registryName) {
        super(registryName, PlantEnums.EnumFlowerPlantType.class);

        this.PLANTABLE_BLOCKS.add(Blocks.GRASS);
        this.PLANTABLE_BLOCKS.add(Blocks.DIRT);
    }

    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
        var type = this.getEnumFromState(state);
        return type == PlantEnums.EnumFlowerPlantType.HEMP ||
//                worldIn.getBlockState(pos.down()).getBlock() == ModBlocks.dirt_oily &&
                (
                type == PlantEnums.EnumFlowerPlantType.MUSTARD_WILLOW_0 ||
                type == PlantEnums.EnumFlowerPlantType.MUSTARD_WILLOW_1
                );
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        var type = this.getEnumFromState(state);
        return type == PlantEnums.EnumFlowerPlantType.HEMP || true; // TODO: Remove the "|| true"
    }

    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        var type = (PlantEnums.EnumFlowerPlantType) this.getEnumFromState(state);
        switch (type)
        {
            case HEMP:
                //TODO
                break;
            case MUSTARD_WILLOW_0:
                worldIn.setBlockState(pos, ModBlocks.plant_flower.getDefaultState().withProperty(META, PlantEnums.EnumFlowerPlantType.MUSTARD_WILLOW_1.ordinal()), 3);
                break;
            case MUSTARD_WILLOW_1:
                //TODO
                break;
        }
    }
}
