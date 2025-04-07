package com.hbm.blocks.generic;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.PlantEnums;
import net.minecraft.init.Blocks;

public class BlockFlowerPlant extends BlockPlantEnumMeta {

    public BlockFlowerPlant(String registryName) {
        super(registryName, PlantEnums.EnumFlowerPlantType.class);

        this.PLANTABLE_BLOCKS.add(Blocks.GRASS);
        this.PLANTABLE_BLOCKS.add(Blocks.DIRT);
    }

}
