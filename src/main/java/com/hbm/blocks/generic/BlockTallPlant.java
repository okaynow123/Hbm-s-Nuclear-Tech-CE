package com.hbm.blocks.generic;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.PlantEnums;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

import static com.hbm.blocks.PlantEnums.EnumTallPlantType;
import static com.hbm.blocks.PlantEnums.EnumTallPlantType.*;

public class BlockTallPlant extends BlockPlantEnumMeta implements IGrowable {


    public BlockTallPlant(String registryName) {
        super(registryName, EnumTallPlantType.class);

        PLANTABLE_BLOCKS.add(Blocks.GRASS);
        PLANTABLE_BLOCKS.add(Blocks.DIRT);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return FULL_BLOCK_AABB;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state,
                                EntityLivingBase placer, ItemStack stack) {
        EnumTallPlantType type = values()[state.getValue(META)];

        if (type.name().endsWith("_LOWER")) {
            EnumTallPlantType upper = valueOf(type.name().replace("_LOWER", "_UPPER"));
            IBlockState upperState = this.getDefaultState().withProperty(META, upper.ordinal());
            worldIn.setBlockState(pos.up(), upperState, 2);
        }
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos,
                                            EnumFacing facing, float hitX, float hitY, float hitZ, int meta,
                                            EntityLivingBase placer, EnumHand hand) {
        EnumTallPlantType type = values()[meta];
        if (!type.name().endsWith("_LOWER")) {
            type = valueOf(type.name().replace("_UPPER", "_LOWER"));
        }
        return this.getDefaultState().withProperty(META, type.ordinal());
    }


    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        EnumTallPlantType type = values()[state.getValue(META)];

        if (type.name().endsWith("_UPPER")) {
            BlockPos below = pos.down();
            IBlockState belowState = world.getBlockState(below);

            if (belowState.getBlock() != this ||
                    !belowState.getValue(META).equals(valueOf(type.name().replace("_UPPER", "_LOWER")).ordinal())) {
                world.setBlockToAir(pos); // Break orphaned upper half
            }
        }

        if (type.name().endsWith("_LOWER")) {
            BlockPos above = pos.up();
            BlockPos below = pos.down();
            IBlockState aboveState = world.getBlockState(above);
            IBlockState belowState = world.getBlockState(below);

            if (aboveState.getBlock() != this ||
                    !aboveState.getValue(META).equals(valueOf(type.name().replace("_LOWER", "_UPPER")).ordinal())) {
                world.setBlockState(pos, ModBlocks.plant_flower.getDefaultState().withProperty(META, 1)); // Break orphaned lower half
            }
                checkAndDropBlock(world, pos, state);


        }
    }

    public void growToTallPlant(World world, BlockPos pos, EnumTallPlantType lower, EnumTallPlantType upper) {
        if (world.isAirBlock(pos.up())) {
            world.setBlockState(pos, this.getDefaultState().withProperty(META, lower.ordinal()), 2);
            world.setBlockState(pos.up(), this.getDefaultState().withProperty(META, upper.ordinal()), 2);
        }
    }

    protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!this.canBlockStay(worldIn, pos, state))
        {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
        }
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (EnumTallPlantType type : values()) {
            if (type.name().endsWith("_LOWER")) {
                list.add(new ItemStack(this, 1, type.ordinal()));
            }
        }
    }

    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
        EnumTallPlantType type = EnumTallPlantType.values()[state.getValue(META)];
        if (worldIn.isAirBlock(pos.up())) {
            return type == MUSTARD_WILLOW_2_LOWER || type == MUSTARD_WILLOW_3_LOWER;
        }
        return false;

    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        return true;
    }

    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        if (!canGrow(worldIn, pos, state, false)) return;
        var type = (PlantEnums.EnumTallPlantType) this.getEnumFromState(state);

        switch (type) {
            case HEMP_LOWER:
                worldIn.setBlockState(pos, ModBlocks.plant_tall.getDefaultState()
                        .withProperty(META, HEMP_LOWER.ordinal()), 2);

                worldIn.setBlockState(pos.up(), ModBlocks.plant_tall.getDefaultState()
                        .withProperty(META, HEMP_UPPER.ordinal()), 2);
                break;
            case MUSTARD_WILLOW_2_LOWER:
                worldIn.setBlockState(pos, ModBlocks.plant_tall.getDefaultState()
                        .withProperty(META, MUSTARD_WILLOW_2_LOWER.ordinal()), 2);

                worldIn.setBlockState(pos.up(), ModBlocks.plant_tall.getDefaultState()
                        .withProperty(META, MUSTARD_WILLOW_2_UPPER.ordinal()), 2);
                break;
            case MUSTARD_WILLOW_3_UPPER:
                worldIn.setBlockState(pos, ModBlocks.plant_tall.getDefaultState()
                        .withProperty(META, MUSTARD_WILLOW_3_LOWER.ordinal()), 2);

                worldIn.setBlockState(pos.up(), ModBlocks.plant_tall.getDefaultState()
                        .withProperty(META, MUSTARD_WILLOW_3_UPPER.ordinal()), 2);
                break;
        }
    }


}
