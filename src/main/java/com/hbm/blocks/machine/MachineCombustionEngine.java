package com.hbm.blocks.machine;

import com.hbm.blocks.BlockDummyable;
import com.hbm.lib.ForgeDirection;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.hbm.tileentity.machine.TileEntityMachineCombustionEngine;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class MachineCombustionEngine extends BlockDummyable {

  public MachineCombustionEngine(Material material, String name) {
    super(material, name);
  }

  @Override
  public TileEntity createNewTileEntity(@NotNull World world, int meta) {
    if (meta >= 12) return new TileEntityMachineCombustionEngine();
    if (hasExtra(meta)) return new TileEntityProxyCombo().power().fluid();
    return null;
  }

  @Override
  public int[] getDimensions() {
    return new int[] {1, 0, 1, 0, 3, 2};
  }

  @Override
  public int getOffset() {
    return 0;
  }

  @Override
  public boolean onBlockActivated(
      @NotNull World worldIn,
      @NotNull BlockPos pos,
      @NotNull IBlockState state,
      @NotNull EntityPlayer playerIn,
      @NotNull EnumHand hand,
      @NotNull EnumFacing facing,
      float hitX,
      float hitY,
      float hitZ) {
    return this.standardOpenBehavior(worldIn, pos, playerIn, 0);
  }

  protected void fillSpace(
      @NotNull World world, int x, int y, int z, @NotNull ForgeDirection dir, int o) {
    super.fillSpace(world, x, y, z, dir, o);

    ForgeDirection rot = dir.getRotation(ForgeDirection.UP);

    this.makeExtra(world, x + rot.offsetX, y, z + rot.offsetZ);
    this.makeExtra(world, x - rot.offsetX, y, z - rot.offsetZ);
    this.makeExtra(world, x - dir.offsetX + rot.offsetX, y, z - dir.offsetZ + rot.offsetZ);
    this.makeExtra(world, x - dir.offsetX - rot.offsetX, y, z - dir.offsetZ - rot.offsetZ);
  }
}
