package com.hbm.blocks.network;

import com.hbm.items.ModItems;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;

public class BlockConveyorExpress extends BlockConveyorBendable {

    public BlockConveyorExpress(Material materialIn, String s) {
        super(materialIn, s);
    }

    @Override
    public Vec3d getTravelLocation(World world, int x, int y, int z, Vec3d itemPos, double speed) {
        return super.getTravelLocation(world, x, y, z, itemPos, speed * 3);
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(ModItems.conveyor_wand, 1, 1);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return ModItems.conveyor_wand;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return 1;
    }
}
