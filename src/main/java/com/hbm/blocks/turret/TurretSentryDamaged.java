package com.hbm.blocks.turret;

import com.hbm.blocks.ModBlocks;
import com.hbm.tileentity.turret.TileEntityTurretSentryDamaged;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class TurretSentryDamaged extends BlockContainer {

  public TurretSentryDamaged(Material material, String name) {
    super(material);
    this.setTranslationKey(name);
    this.setRegistryName(name);

    ModBlocks.ALL_BLOCKS.add(this);
  }

  @Override
  public TileEntity createNewTileEntity(@NotNull World world, int meta) {
    return new TileEntityTurretSentryDamaged();
  }

  public int getOffset() {
    return 0;
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
  public @NotNull Item getItemDropped(
      @NotNull IBlockState state, @NotNull Random rand, int fortune) {
    return Items.AIR;
  }
}
