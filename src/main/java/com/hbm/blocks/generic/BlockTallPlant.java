package com.hbm.blocks.generic;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.PlantEnums;
import com.hbm.inventory.OreDictManager;
import com.hbm.items.ModItems;
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

import java.util.List;
import java.util.Random;

import static com.hbm.blocks.PlantEnums.EnumFlowerPlantType.HEMP;
import static com.hbm.blocks.PlantEnums.EnumFlowerPlantType.MUSTARD_WILLOW_0;
import static com.hbm.blocks.PlantEnums.EnumTallPlantType;
import static com.hbm.blocks.PlantEnums.EnumTallPlantType.*;

public class BlockTallPlant extends BlockPlantEnumMeta implements IGrowable {


    public static void initPlacables(){
        PLANTABLE_BLOCKS.add(ModBlocks.dirt_dead);
        PLANTABLE_BLOCKS.add(ModBlocks.dirt_oily);
        PLANTABLE_BLOCKS.add(Blocks.GRASS);
        PLANTABLE_BLOCKS.add(Blocks.DIRT);
    }

    public BlockTallPlant(String registryName) {
        super(registryName, EnumTallPlantType.class);

    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return FULL_BLOCK_AABB;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        EnumTallPlantType type = values()[state.getValue(META)];

        if (type.name().endsWith("_LOWER")) {
            EnumTallPlantType upper = valueOf(type.name().replace("_LOWER", "_UPPER"));
            IBlockState upperState = this.getDefaultState().withProperty(META, upper.ordinal());
            worldIn.setBlockState(pos.up(), upperState, 2);
        }
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        EnumTallPlantType type = values()[meta];
        if (!type.name().endsWith("_LOWER")) {
            type = valueOf(type.name().replace("_UPPER", "_LOWER"));
        }
        return this.getDefaultState().withProperty(META, type.ordinal());
    }

    public int quantityDropped(int meta, int fortune, Random random) {
        return 1;
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (worldIn.isRemote) return;
        EnumTallPlantType type = EnumTallPlantType.values()[state.getValue(META)];

    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        EnumTallPlantType type = values()[state.getValue(META)];

        if (type.name().endsWith("_UPPER")) {
            BlockPos below = pos.down();
            IBlockState belowState = world.getBlockState(below);

            if (belowState.getBlock() != this || !belowState.getValue(META).equals(valueOf(type.name().replace("_UPPER", "_LOWER")).ordinal())) {
                world.setBlockToAir(pos); // Break orphaned upper half
            }
        }

        if (type.name().endsWith("_LOWER")) {
            BlockPos above = pos.up();
            BlockPos below = pos.down();
            IBlockState aboveState = world.getBlockState(above);
            IBlockState belowState = world.getBlockState(below);

            if (aboveState.getBlock() != this || !aboveState.getValue(META).equals(valueOf(type.name().replace("_LOWER", "_UPPER")).ordinal())) {
                world.setBlockState(pos, ModBlocks.plant_flower.getDefaultState().withProperty(META, type == HEMP_LOWER ? HEMP.ordinal() : MUSTARD_WILLOW_0.ordinal())); // Break orphaned lower half
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

    protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!this.canBlockStay(worldIn, pos, state)) {
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
        switch (type) {
            case MUSTARD_WILLOW_2_LOWER:
                if (!isWatered(worldIn, pos)) return false;
                break;
            case MUSTARD_WILLOW_3_LOWER:
                if (!isWatered(worldIn, pos) || (type.needsOil && !isOiled(worldIn, pos))) return false;
                break;
            case MUSTARD_WILLOW_2_UPPER:
                if (!isWatered(worldIn, pos.down())) return false;
                break;
            case MUSTARD_WILLOW_3_UPPER:
                if (!isWatered(worldIn, pos.down()) || (type.needsOil && !isOiled(worldIn, pos.down()))) return false;
                break;
            default:
                return false;
        }
        return true;


    }

    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        NonNullList<ItemStack> ret = NonNullList.create();
        getDrops(ret, world, pos, state, fortune);
        return ret;
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
            case MUSTARD_WILLOW_2_LOWER:
                worldIn.setBlockState(pos, ModBlocks.plant_tall.getDefaultState().withProperty(META, MUSTARD_WILLOW_3_LOWER.ordinal()), 2);

                worldIn.setBlockState(pos.up(), ModBlocks.plant_tall.getDefaultState().withProperty(META, MUSTARD_WILLOW_3_UPPER.ordinal()), 2);
                break;
            case MUSTARD_WILLOW_3_LOWER:
                worldIn.setBlockState(pos, ModBlocks.plant_tall.getDefaultState().withProperty(META, MUSTARD_WILLOW_4_LOWER.ordinal()), 2);

                worldIn.setBlockState(pos.up(), ModBlocks.plant_tall.getDefaultState().withProperty(META, MUSTARD_WILLOW_4_UPPER.ordinal()), 2);

                worldIn.setBlockState(pos.down(2), Blocks.DIRT.getDefaultState(), 3);
                break;
        }
    }

    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess blockAccess, BlockPos pos, IBlockState state, int fortune) {
        List<ItemStack> ret = super.getDrops(blockAccess, pos, state, fortune);
        World world = (World) blockAccess;

        if (getEnumFromState(state) == MUSTARD_WILLOW_4_UPPER) {
            ret.add(OreDictManager.DictFrame.fromOne(ModItems.plant_item, com.hbm.items.ItemEnums.EnumPlantType.MUSTARDWILLOW, 3 + world.rand.nextInt(4)));
        }

    }


}
