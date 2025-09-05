package com.hbm.blocks.generic;

import com.hbm.blocks.ModBlocks;
import com.hbm.lib.RefStrings;
import com.hbm.tileentity.deco.TileEntityBarrier;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlockBarrier extends BlockContainer implements IBakedModel {
  public static final PropertyDirection FACING =
      PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

  private static final AxisAlignedBB POS_X = new AxisAlignedBB(0, 0, 0, 0.125, 1, 1);
  private static final AxisAlignedBB NEG_X = new AxisAlignedBB(0.875, 0, 0, 1, 1, 1);
  private static final AxisAlignedBB POS_Z = new AxisAlignedBB(0, 0, 0, 1, 1, 0.125);
  private static final AxisAlignedBB NEG_Z = new AxisAlignedBB(0, 0, 0.875, 1, 1, 1);

  public BlockBarrier(Material mat, String name) {
    super(mat);

    this.setRegistryName(name);
    this.setTranslationKey(name);

    this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));

    ModBlocks.ALL_BLOCKS.add(this);
  }

  @Override
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, FACING);
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    return state.getValue(FACING).getHorizontalIndex();
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    return getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta));
  }

  @Override
  public boolean isOpaqueCube(IBlockState state) {
    return false;
  }

  @Override
  public boolean isFullCube(IBlockState state) {
    return false;
  }

  @Override
  public boolean isNormalCube(IBlockState state) {
    return false;
  }

  @Override
  public void onBlockPlacedBy(
      World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {

    EnumFacing facing = placer.getHorizontalFacing().getOpposite();
    worldIn.setBlockState(pos, state.withProperty(FACING, facing), 2);
  }

  @Override
  public IBlockState getStateForPlacement(
      World world,
      BlockPos pos,
      EnumFacing facing,
      float hitX,
      float hitY,
      float hitZ,
      int meta,
      EntityLivingBase placer) {
    return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
  }

  @Override
  public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {

    EnumFacing facing = state.getValue(FACING);

    return switch (facing) {
      case EAST -> POS_X;
      case WEST -> NEG_X;
      case SOUTH -> POS_Z;
      case NORTH -> NEG_Z;
      default -> FULL_BLOCK_AABB;
    };
  }

  @Override
  public void addCollisionBoxToList(
      IBlockState state,
      World worldIn,
      BlockPos pos,
      AxisAlignedBB entityBox,
      List<AxisAlignedBB> collidingBoxes,
      Entity entityIn,
      boolean isActualState) {

    EnumFacing facing = state.getValue(FACING);

    IBlockState nx = worldIn.getBlockState(pos.west());
    IBlockState px = worldIn.getBlockState(pos.east());
    IBlockState nz = worldIn.getBlockState(pos.north());
    IBlockState pz = worldIn.getBlockState(pos.south());

    List<AxisAlignedBB> bbs = new ArrayList<>();

    if (nx.isOpaqueCube() || nx.isNormalCube() || facing == EnumFacing.EAST)
      bbs.add(new AxisAlignedBB(0, 0, 0, 0.125, 1, 1));
    if (nz.isOpaqueCube() || nz.isNormalCube() || facing == EnumFacing.SOUTH)
      bbs.add(new AxisAlignedBB(0, 0, 0, 1, 1, 0.125));
    if (px.isOpaqueCube() || px.isNormalCube() || facing == EnumFacing.WEST)
      bbs.add(new AxisAlignedBB(0.875, 0, 0, 1, 1, 1));
    if (pz.isOpaqueCube() || pz.isNormalCube() || facing == EnumFacing.NORTH)
      bbs.add(new AxisAlignedBB(0, 0, 0.875, 1, 1, 1));

    for (AxisAlignedBB bb : bbs) {
      AxisAlignedBB offsetBB = bb.offset(pos);
      if (entityBox.intersects(offsetBB)) {
        collidingBoxes.add(offsetBB);
      }
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean shouldSideBeRendered(
      @NotNull IBlockState blockState,
      @NotNull IBlockAccess blockAccess,
      @NotNull BlockPos pos,
      @NotNull EnumFacing side) {
    return true;
  }

  @Override
  public @Nullable TileEntity createNewTileEntity(World worldIn, int meta) {
    return new TileEntityBarrier();
  }

  @Override
  public @NotNull List<BakedQuad> getQuads(
      @Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
    return Collections.emptyList();
  }

  @Override
  public boolean isAmbientOcclusion() {
    return false;
  }

  @Override
  public boolean isGui3d() {
    return false;
  }

  @Override
  public boolean isBuiltInRenderer() {
    return true;
  }

  @Override
  public @NotNull TextureAtlasSprite getParticleTexture() {
    return Minecraft.getMinecraft()
        .getTextureMapBlocks()
        .getAtlasSprite(
            new ResourceLocation(RefStrings.MODID + ":textures/blocks/wood_barrier.png")
                .toString());
  }

  @Override
  public @NotNull ItemOverrideList getOverrides() {
    return ItemOverrideList.NONE;
  }
}
