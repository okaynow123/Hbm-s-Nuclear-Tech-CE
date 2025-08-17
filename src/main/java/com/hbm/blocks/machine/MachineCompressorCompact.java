package com.hbm.blocks.machine;

import com.hbm.blocks.BlockDummyable;
import com.hbm.lib.ForgeDirection;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.hbm.tileentity.machine.TileEntityMachineCompressorCompact;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MachineCompressorCompact extends BlockDummyable {

    public MachineCompressorCompact(String s) {
        super(Material.IRON, s);
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int meta) {
        if(meta >= 12) return new TileEntityMachineCompressorCompact();
        if(meta >= 6) return new TileEntityProxyCombo(false, true, true);
        return null;
    }

    @Override public int[] getDimensions() { return new int[] {2, 0, 1, 1, 3, 3}; }
    @Override public int getOffset() { return 1; }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return this.standardOpenBehavior(world, pos, player, 0);
    }

    @Override
    public void fillSpace(World world, int x, int y, int z, ForgeDirection dir, int o) {
        super.fillSpace(world, x, y, z, dir, o);

        x = x + dir.offsetX * o;
        z = z + dir.offsetZ * o;

        ForgeDirection rot = dir.getRotation(ForgeDirection.UP);

        this.makeExtra(world, x + rot.offsetX * 3, y + 1, z + rot.offsetZ * 3);
        this.makeExtra(world, x - rot.offsetX * 3, y + 1, z - rot.offsetZ * 3);
        this.makeExtra(world, x + dir.offsetX + rot.offsetX, y + 1, z + dir.offsetZ + rot.offsetZ);
        this.makeExtra(world, x + dir.offsetX - rot.offsetX, y + 1, z + dir.offsetZ - rot.offsetZ);
        this.makeExtra(world, x - dir.offsetX + rot.offsetX, y + 1, z - dir.offsetZ + rot.offsetZ);
        this.makeExtra(world, x - dir.offsetX - rot.offsetX, y + 1, z - dir.offsetZ - rot.offsetZ);
    }
}
