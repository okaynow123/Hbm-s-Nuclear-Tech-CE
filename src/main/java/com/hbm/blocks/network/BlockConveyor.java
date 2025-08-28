package com.hbm.blocks.network;

import com.hbm.api.block.IToolable;
import com.hbm.blocks.ModBlocks;
import com.hbm.items.ModItems;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.Random;

public class BlockConveyor extends BlockConveyorBendable {

    public BlockConveyor(Material materialIn, String name) {
        super(materialIn, name);
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(ModItems.conveyor_wand, 1, 0);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return ModItems.conveyor_wand;
    }

    @Override
    public boolean onScrew(World world, EntityPlayer player, int x, int y, int z, EnumFacing side, float fX, float fY, float fZ, EnumHand hand,
                           IToolable.ToolType tool) {
        if (tool != IToolable.ToolType.SCREWDRIVER) {
            return false;
        }
        BlockPos pos = new BlockPos(x, y, z);
        IBlockState state = world.getBlockState(pos);
        if (!player.isSneaking()) {
            world.setBlockState(pos, state.withRotation(Rotation.CLOCKWISE_90), 3);
        } else {
            CurveType curve = state.getValue(CURVE);
            EnumFacing facing = state.getValue(FACING);
            if (curve == CurveType.RIGHT) {
                IBlockState liftState = ModBlocks.conveyor_lift.getDefaultState().withProperty(FACING, facing);
                world.setBlockState(pos, liftState, 3);
            } else {
                CurveType newCurve = (curve == CurveType.STRAIGHT) ? CurveType.LEFT : CurveType.RIGHT;
                world.setBlockState(pos, state.withProperty(CURVE, newCurve), 3);
            }
        }
        return true;
    }
}