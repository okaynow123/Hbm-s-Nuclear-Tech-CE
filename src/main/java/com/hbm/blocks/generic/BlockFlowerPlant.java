package com.hbm.blocks.generic;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.PlantEnums;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
        return true;
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        return true;
    }

    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        var type = (PlantEnums.EnumFlowerPlantType) this.getEnumFromState(state);
        switch (type)
        {
            case HEMP:
                worldIn.setBlockState(pos, ModBlocks.plant_tall.getDefaultState()
                        .withProperty(META, PlantEnums.EnumTallPlantType.HEMP_LOWER.ordinal()), 2);

                worldIn.setBlockState(pos.up(), ModBlocks.plant_tall.getDefaultState()
                        .withProperty(META, PlantEnums.EnumTallPlantType.HEMP_UPPER.ordinal()), 2);
                break;
            case TOBACCO:
            case NIGHTSHADE:
            case FOXGLOVE:
                worldIn.spawnEntity(new EntityItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Item.getItemFromBlock(this), 1, type.ordinal())));
                break;
            case MUSTARD_WILLOW_0:
                if(isWatered(worldIn, pos))
                    worldIn.setBlockState(pos, ModBlocks.plant_flower.getDefaultState().withProperty(META, PlantEnums.EnumFlowerPlantType.MUSTARD_WILLOW_1.ordinal()), 3);
                break;
            case MUSTARD_WILLOW_1:
                if(isWatered(worldIn, pos)) {
                    worldIn.setBlockState(pos, ModBlocks.plant_tall.getDefaultState().withProperty(META, PlantEnums.EnumTallPlantType.MUSTARD_WILLOW_2_LOWER.ordinal()), 3);
                    worldIn.setBlockState(pos.up(), ModBlocks.plant_tall.getDefaultState().withProperty(META, PlantEnums.EnumTallPlantType.MUSTARD_WILLOW_2_UPPER.ordinal()), 3);
                }
                break;
        }
    }
}
