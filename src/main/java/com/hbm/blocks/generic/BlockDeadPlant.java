package com.hbm.blocks.generic;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.PlantEnums;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockDeadPlant extends BlockPlantEnumMeta {

    public BlockDeadPlant(String registryName) {
        super(registryName, PlantEnums.EnumDeadPlantType.class);

        this.PLANTABLE_BLOCKS.add(ModBlocks.waste_dirt);
        this.PLANTABLE_BLOCKS.add(ModBlocks.waste_earth);
        this.PLANTABLE_BLOCKS.add(ModBlocks.dirt_dead);
        this.PLANTABLE_BLOCKS.add(ModBlocks.dirt_oily);
        this.PLANTABLE_BLOCKS.add(Blocks.GRASS);
        this.PLANTABLE_BLOCKS.add(Blocks.DIRT);
    }

    @Override
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) { }

}