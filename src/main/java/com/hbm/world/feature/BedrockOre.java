package com.hbm.world.feature;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockBedrockOreTE;
import com.hbm.inventory.fluid.FluidStack;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BedrockOre {

    public static void generate(World world, int x, int z, ItemStack stack, FluidStack acid, int color, int tier) {
        generate(world, x, z, stack, acid, color, tier, ModBlocks.stone_depth);
    }

    public static void generate(World world, int x, int z, ItemStack stack, FluidStack acid, int color, int tier, Block depthRock) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int ix = x - 1; ix <= x + 1; ix++) {
            for (int iz = z - 1; iz <= z + 1; iz++) {
                pos.setPos(ix, 0, iz);
                IBlockState state = world.getBlockState(pos);
                Block b = state.getBlock();

                if (b == Blocks.BEDROCK) {
                    if ((ix == x && iz == z) || world.rand.nextBoolean()) {
                        world.setBlockState(pos, ModBlocks.ore_bedrock_block.getDefaultState(), 3);

                        TileEntity tile = world.getTileEntity(pos);
                        if (tile instanceof BlockBedrockOreTE.TileEntityBedrockOre) {
                            BlockBedrockOreTE.TileEntityBedrockOre ore = (BlockBedrockOreTE.TileEntityBedrockOre) tile;
                            ore.resource = stack.copy();
                            ore.color = color;
                            ore.shape = world.rand.nextInt(10);
                            ore.acidRequirement = acid;
                            ore.tier = tier;
                            ore.markDirty();
                            world.notifyBlockUpdate(pos, state, world.getBlockState(pos), 3);
                        }
                    }
                }
            }
        }

        for (int ix = x - 3; ix <= x + 3; ix++) {
            for (int iz = z - 3; iz <= z + 3; iz++) {
                for (int iy = 1; iy < 7; iy++) {
                    pos.setPos(ix, iy, iz);
                    IBlockState state = world.getBlockState(pos);
                    Block b = state.getBlock();

                    if (iy < 3 || b == Blocks.BEDROCK) {
                        if (b == Blocks.STONE || b == Blocks.BEDROCK) {
                            world.setBlockState(pos, depthRock.getDefaultState(), 2);
                        }
                    }
                }
            }
        }
    }

}
