package com.hbm.blocks.machine;

import com.hbm.blocks.BlockDummyable;
import com.hbm.handler.MultiblockHandlerXR;
import com.hbm.lib.ForgeDirection;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.hbm.tileentity.machine.TileEntityMachineExposureChamber;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MachineExposureChamber extends BlockDummyable {

    public MachineExposureChamber(Material mat, String s) {
        super(mat, s);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        if (meta >= 12) return new TileEntityMachineExposureChamber();
        if (meta >= 6) return new TileEntityProxyCombo().inventory().power();
        return null;
    }

    @Override
    public int[] getDimensions() {
        return new int[]{4, 0, 2, 2, 2, 2};
    }

    @Override
    public int getOffset() {
        return 2;
    }

    @Override
    public void fillSpace(World world, int x, int y, int z, ForgeDirection dir, int o) {
        super.fillSpace(world, x, y, z, dir, o);

        x += dir.offsetX * o;
        z += dir.offsetZ * o;

        ForgeDirection rot = dir.getRotation(ForgeDirection.UP).getOpposite();

        MultiblockHandlerXR.fillSpace(world, x, y, z, new int[]{3, 0, 0, 0, -3, 8}, this, dir);
        MultiblockHandlerXR.fillSpace(world, x, y + 2, z, new int[]{0, 0, 1, -1, -3, 6}, this, dir);
        MultiblockHandlerXR.fillSpace(world, x, y + 2, z, new int[]{0, 0, -1, 1, -3, 6}, this, dir);
        MultiblockHandlerXR.fillSpace(world, x + rot.offsetX * 7, y, z + rot.offsetZ * 7, new int[]{3, 0, 1, -1, 0, 1}, this, dir);
        MultiblockHandlerXR.fillSpace(world, x + rot.offsetX * 7, y, z + rot.offsetZ * 7, new int[]{3, 0, -1, 1, 0, 1}, this, dir);

        this.makeExtra(world, x + rot.offsetX * 7 + dir.offsetX, y, z + rot.offsetZ * 7 + dir.offsetZ);
        this.makeExtra(world, x + rot.offsetX * 7 - dir.offsetX, y, z + rot.offsetZ * 7 - dir.offsetZ);
        this.makeExtra(world, x + rot.offsetX * 8 + dir.offsetX, y, z + rot.offsetZ * 8 + dir.offsetZ);
        this.makeExtra(world, x + rot.offsetX * 8 - dir.offsetX, y, z + rot.offsetZ * 8 - dir.offsetZ);
        this.makeExtra(world, x + rot.offsetX * 8, y, z + rot.offsetZ * 8);
    }

    @Override
    public boolean checkRequirement(World world, int x, int y, int z, ForgeDirection dir, int o) {

        x += dir.offsetX * o;
        z += dir.offsetZ * o;

        ForgeDirection rot = dir.getRotation(ForgeDirection.UP).getOpposite();

        if (!MultiblockHandlerXR.checkSpace(world, x, y, z, getDimensions(), x, y, z, dir)) return false;
        if (!MultiblockHandlerXR.checkSpace(world, x, y, z, new int[]{3, 0, 0, 0, -3, 8}, x, y, z, dir)) return false;
        if (!MultiblockHandlerXR.checkSpace(world, x, y, z, new int[]{0, 0, 1, -1, -3, 6}, x, y, z, dir)) return false;
        if (!MultiblockHandlerXR.checkSpace(world, x, y, z, new int[]{0, 0, -1, 1, -3, 6}, x, y, z, dir)) return false;
        if (!MultiblockHandlerXR.checkSpace(world, x + rot.offsetX * 7, y, z + rot.offsetZ * 7, new int[]{3, 0, 1, -1, 0, 1}, x, y, z, dir))
            return false;
        return MultiblockHandlerXR.checkSpace(world, x + rot.offsetX * 7, y, z + rot.offsetZ * 7, new int[]{3, 0, -1, 1, 0, 1}, x, y, z, dir);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX
            , float hitY, float hitZ) {
        return super.standardOpenBehavior(world, pos.getX(), pos.getY(), pos.getZ(), player, 0);
    }
}
