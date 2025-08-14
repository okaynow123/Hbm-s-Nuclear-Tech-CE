package com.hbm.blocks.network;

import com.hbm.api.conveyor.IConveyorBelt;
import com.hbm.api.conveyor.IEnterableBlock;
import com.hbm.blocks.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BlockConveyorLift extends BlockConveyorChute {

    public BlockConveyorLift(Material materialIn, String s) {
        super(materialIn, s);
    }

    @Override
    public EnumFacing getInputDirection(World world, BlockPos pos) {
        return world.getBlockState(pos).getValue(FACING);
    }

    @Override
    public EnumFacing getOutputDirection(World world, BlockPos pos) {
        return EnumFacing.UP;
    }

    @Override
    public EnumFacing getTravelDirection(World world, BlockPos pos, Vec3d itemPos) {
        IBlockState state = world.getBlockState(pos);
        Block blockAbove = world.getBlockState(pos.up()).getBlock();
        boolean isTop = !(blockAbove instanceof BlockConveyorLift) && !(blockAbove instanceof IEnterableBlock);

        if (isTop) {
            return state.getValue(FACING);
        } else {
            return EnumFacing.DOWN;
        }
    }

    @Override
    public Vec3d getClosestSnappingPosition(World world, BlockPos pos, Vec3d itemPos) {
        EnumFacing travelDirection = getTravelDirection(world, pos, itemPos);

        if (travelDirection.getAxis() == EnumFacing.Axis.Y) {
            return new Vec3d(pos.getX() + 0.5, itemPos.y, pos.getZ() + 0.5);
        } else {
            return super.getClosestSnappingPosition(world, pos, itemPos);
        }
    }

    @Override
    public boolean onScrew(World world, EntityPlayer player, int x, int y, int z, EnumFacing side, float fX, float fY, float fZ, EnumHand hand,
                           ToolType tool) {
        if (tool != ToolType.SCREWDRIVER) {
            return false;
        }

        BlockPos pos = new BlockPos(x, y, z);
        IBlockState state = world.getBlockState(pos);

        if (!player.isSneaking()) {
            world.setBlockState(pos, state.withRotation(Rotation.CLOCKWISE_90), 3);
        } else {
            IBlockState chuteState = ModBlocks.conveyor_chute.getDefaultState().withProperty(FACING, state.getValue(FACING));
            world.setBlockState(pos, chuteState, 3);
        }
        return true;
    }

    @Override
    public int getUpdatedType(World world, BlockPos pos, EnumFacing facing) {
        boolean isBottom = !(world.getBlockState(pos.down()).getBlock() instanceof BlockConveyorLift);

        if (isBottom) {
            Block inputBlock = world.getBlockState(pos.offset(facing.getOpposite())).getBlock();
            boolean isFed = (inputBlock instanceof IConveyorBelt || inputBlock instanceof IEnterableBlock);
            return isFed ? 2 : 0;
        } else {
            return 1;
        }
    }
}