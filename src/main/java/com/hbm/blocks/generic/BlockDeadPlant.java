package com.hbm.blocks.generic;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.PlantEnums;
import net.minecraft.init.Blocks;

public class BlockDeadPlant extends BlockPlantEnumMeta {

    static {
        PLANTABLE_BLOCKS.add(ModBlocks.waste_dirt);
        PLANTABLE_BLOCKS.add(ModBlocks.waste_earth);
        PLANTABLE_BLOCKS.add(ModBlocks.dirt_dead);
        PLANTABLE_BLOCKS.add(ModBlocks.dirt_oily);
        PLANTABLE_BLOCKS.add(Blocks.GRASS);
        PLANTABLE_BLOCKS.add(Blocks.DIRT);
    }


    public BlockDeadPlant(String registryName) {
        super(registryName, PlantEnums.EnumDeadPlantType.class);
    }
}