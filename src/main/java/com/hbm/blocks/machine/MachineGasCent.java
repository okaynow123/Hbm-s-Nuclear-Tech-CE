package com.hbm.blocks.machine;

import com.hbm.blocks.ModBlocks;
import com.hbm.handler.MultiblockHandler;
import com.hbm.interfaces.IMultiBlock;
import com.hbm.lib.InventoryHelper;
import com.hbm.main.MainRegistry;
import com.hbm.tileentity.machine.TileEntityMachineGasCent;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class MachineGasCent extends BlockContainer implements IMultiBlock {

  public static final PropertyDirection FACING = BlockHorizontal.FACING;

  public MachineGasCent(Material materialIn, String s) {
    super(materialIn);
    this.setTranslationKey(s);
    this.setRegistryName(s);

    ModBlocks.ALL_BLOCKS.add(this);
  }

  @Override
  public @NotNull EnumBlockRenderType getRenderType(@NotNull IBlockState state) {
    return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
  }

  @Override
  public boolean isOpaqueCube(@NotNull IBlockState state) {
    return false;
  }

  @Override
  public boolean isBlockNormalCube(@NotNull IBlockState state) {
    return false;
  }

  @Override
  public boolean isNormalCube(@NotNull IBlockState state) {
    return false;
  }

  @Override
  public boolean isNormalCube(
      @NotNull IBlockState state, @NotNull IBlockAccess world, @NotNull BlockPos pos) {
    return false;
  }

  @Override
  public boolean shouldSideBeRendered(
      @NotNull IBlockState blockState,
      @NotNull IBlockAccess blockAccess,
      @NotNull BlockPos pos,
      @NotNull EnumFacing side) {
    return false;
  }

  @Override
  public TileEntity createNewTileEntity(@NotNull World worldIn, int meta) {
    return new TileEntityMachineGasCent();
  }

  @Override
  public @NotNull Item getItemDropped(
      @NotNull IBlockState state, @NotNull Random rand, int fortune) {
    return Item.getItemFromBlock(ModBlocks.machine_gascent);
  }

  @Override
  public void onBlockPlacedBy(
      World world,
      @NotNull BlockPos pos,
      IBlockState state,
      EntityLivingBase placer,
      @NotNull ItemStack stack) {
    world.setBlockState(
        pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);

    if (MultiblockHandler.checkSpace(world, pos, MultiblockHandler.centDimension)) {
      MultiblockHandler.fillUp(
          world, pos, MultiblockHandler.centDimension, ModBlocks.dummy_block_gascent);

    } else {
      world.destroyBlock(pos, true);
    }
  }

  @Override
  public void breakBlock(World worldIn, @NotNull BlockPos pos, @NotNull IBlockState state) {
    TileEntity tileEntity = worldIn.getTileEntity(pos);
    if (tileEntity instanceof TileEntityMachineGasCent) {
      InventoryHelper.dropInventoryItems(worldIn, pos, tileEntity);
      worldIn.updateComparatorOutputLevel(pos, this);
    }
    super.breakBlock(worldIn, pos, state);
  }

  @Override
  public boolean onBlockActivated(
      World world,
      @NotNull BlockPos pos,
      @NotNull IBlockState state,
      @NotNull EntityPlayer player,
      @NotNull EnumHand hand,
      @NotNull EnumFacing facing,
      float hitX,
      float hitY,
      float hitZ) {
    if (world.isRemote) {
      return true;
    } else if (!player.isSneaking()) {
      TileEntityMachineGasCent entity = (TileEntityMachineGasCent) world.getTileEntity(pos);
      if (entity != null) {
        player.openGui(
            MainRegistry.instance,
            ModBlocks.guiID_gascent,
            world,
            pos.getX(),
            pos.getY(),
            pos.getZ());
      }
      return true;
    } else {
      return false;
    }
  }

  @Override
  public @NotNull IBlockState getStateForPlacement(
      @NotNull World world,
      @NotNull BlockPos pos,
      @NotNull EnumFacing facing,
      float hitX,
      float hitY,
      float hitZ,
      int meta,
      EntityLivingBase placer,
      @NotNull EnumHand hand) {
    return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
  }

  @Override
  protected @NotNull BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, FACING);
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    return state.getValue(FACING).getIndex();
  }

  @Override
  public @NotNull IBlockState getStateFromMeta(int meta) {
    EnumFacing enumfacing = EnumFacing.byIndex(meta);

    if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
      enumfacing = EnumFacing.NORTH;
    }

    return this.getDefaultState().withProperty(FACING, enumfacing);
  }

  @Override
  public @NotNull IBlockState withRotation(IBlockState state, Rotation rot) {
    return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
  }

  @Override
  public @NotNull IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
    return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
  }
}
