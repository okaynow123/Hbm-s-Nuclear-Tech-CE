package com.hbm.lib;

import com.hbm.main.MainRegistry;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import org.jetbrains.annotations.Nullable;

public class NTMBlockContainer extends BlockContainer {

  protected NTMBlockContainer(Material materialIn) {
    super(materialIn);
  }

  @Nullable
  @Override
  public TileEntity createNewTileEntity(World world, int meta) {
    return null;
  }

  protected boolean standardOpenBehavior(World world, BlockPos pos, EntityPlayer player, int id) {
    return this.standardOpenBehavior(world, pos.getX(), pos.getY(), pos.getZ(), player, id);
  }

  protected boolean standardOpenBehavior(
      World world, int x, int y, int z, EntityPlayer player, int id) {

    if (world.isRemote) {
      return true;
    } else if (!player.isSneaking()) {
      FMLNetworkHandler.openGui(player, MainRegistry.instance, 0, world, x, y, z);
      return true;
    } else {
      return false;
    }
  }
}
