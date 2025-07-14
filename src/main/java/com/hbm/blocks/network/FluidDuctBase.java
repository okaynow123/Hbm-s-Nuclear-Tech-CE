package com.hbm.blocks.network;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.ModSoundTypes;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.items.machine.IItemFluidIdentifier;
import com.hbm.items.machine.ItemFluidIDMulti;
import com.hbm.lib.ForgeDirection;
import com.hbm.main.MainRegistry;
import com.hbm.tileentity.network.TileEntityPipeBaseNT;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FluidDuctBase extends BlockContainer implements IBlockFluidDuct {
    public FluidDuctBase(Material mat, String s) {
        super(mat);
        this.setTranslationKey(s);
        this.setRegistryName(s);
        this.setCreativeTab(MainRegistry.controlTab);
        this.setSoundType(ModSoundTypes.pipe);
        this.useNeighborBrightness = true;

        ModBlocks.ALL_BLOCKS.add(this);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityPipeBaseNT();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

        if(!player.getHeldItem(hand).isEmpty() && player.getHeldItem(hand).getItem() instanceof IItemFluidIdentifier) {
            IItemFluidIdentifier id = (IItemFluidIdentifier) player.getHeldItem(hand).getItem();
            FluidType type = id.getType(world, pos.getX(), pos.getY(), pos.getZ(), player.getHeldItem(hand));

            if(!player.isSneaking()) {

                TileEntity te = world.getTileEntity(pos);

                if(te instanceof TileEntityPipeBaseNT) {
                    TileEntityPipeBaseNT pipe = (TileEntityPipeBaseNT) te;

                        Item item = player.getHeldItem(hand).getItem();
                        if (item instanceof ItemFluidIDMulti) {
                            if (id.getType(world, pos.getX(), pos.getY(), pos.getZ(), player.getHeldItem(hand)) != pipe.getType()) {
                                ItemFluidIDMulti.setType(player.getHeldItem(hand), pipe.getType(), true);
                                world.playSound(player, pos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.25F, 0.75F);
                                return true;
                            }
                        }

                    if(pipe.getType() != type) {
                        pipe.setType(type);
                        return true;
                    }
                }
            } else {

                TileEntity te = world.getTileEntity(pos);

                if(te instanceof TileEntityPipeBaseNT) {
                    TileEntityPipeBaseNT pipe = (TileEntityPipeBaseNT) te;

                    //if(HbmPlayerProps.getData(player).getKeyPressed(HbmKeybinds.EnumKeybind.TOOL_ALT)) {
                        Item item = player.getHeldItem(hand).getItem();
                        if (item instanceof ItemFluidIDMulti) {
                            if (id.getType(world, pos.getX(), pos.getY(), pos.getZ(), player.getHeldItem(hand)) != pipe.getType()) {
                                ItemFluidIDMulti.setType(player.getHeldItem(hand), pipe.getType(), true);
                                world.playSound(player, pos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.25F, 0.75F);
                                return true;
                            }
                        }
                    //}

                    changeTypeRecursively(world, pos.getX(), pos.getY(), pos.getZ(), pipe.getType(), type, 64);
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void changeTypeRecursively(World world, int x, int y, int z, FluidType prevType, FluidType type, int loopsRemaining) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity te = world.getTileEntity(pos);

        if(te instanceof TileEntityPipeBaseNT) {
            TileEntityPipeBaseNT pipe = (TileEntityPipeBaseNT) te;

            if(pipe.getType() == prevType && pipe.getType() != type) {
                pipe.setType(type);

                if(loopsRemaining > 0) {
                    for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                        Block b = world.getBlockState(pos).getBlock();

                        if(b instanceof IBlockFluidDuct) {
                            ((IBlockFluidDuct) b).changeTypeRecursively(world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, prevType, type, loopsRemaining - 1);
                        }
                    }
                }
            }
        }
    }
}
