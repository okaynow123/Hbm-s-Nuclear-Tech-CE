package com.hbm.blocks.generic;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.PlantEnums;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Property;

public class BlockTallPlant extends BlockPlantEnumMeta {

    //public static final PropertyEnum<BlockTallPlant.HalfEnum> HALF = PropertyEnum.create("half", BlockTallPlant.HalfEnum.class);

    public BlockTallPlant(String registryName) {
        super(registryName, PlantEnums.EnumTallPlantType.class);

        PLANTABLE_BLOCKS.add(Blocks.GRASS);
        PLANTABLE_BLOCKS.add(Blocks.DIRT);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return FULL_BLOCK_AABB;
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return true;
    }

//    @Override
//    public boolean canBlockStay(World world, BlockPos pos, IBlockState state) {
//        return (
//                    world.getBlockState(pos.up()).getBlock() == Blocks.AIR &&
//                    super.canBlockStay(world, pos, state) &&
//                    getMetaFromState(state) % 2 == 0
//                ) || (
//                    world.getBlockState(pos.down()).getBlock() == ModBlocks.plant_tall &&
//                    getMetaFromState(state) % 2 == 1
//        );
//    }

//    public static enum HalfEnum implements IStringSerializable {
//        LOWER,
//        UPPER;
//
//        @Override
//        public String getName() {
//            return this.toString().toLowerCase();
//        }
//    }
}
