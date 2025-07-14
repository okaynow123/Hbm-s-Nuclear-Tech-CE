package com.hbm.blocks.machine;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ITooltipProvider;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.hbm.tileentity.machine.TileEntityAshpit;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class MachineAshpit extends BlockDummyable implements ITooltipProvider {
    public MachineAshpit(String s) {
        super(Material.IRON, s);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        if(meta >= 12) return new TileEntityAshpit();
        return new TileEntityProxyCombo().inventory();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return this.standardOpenBehavior(world, pos, player, 0);
    }

    @Override
    public int[] getDimensions() {
        return new int[] {0, 0, 1, 1, 1, 1};
    }

    @Override
    public int getOffset() {
        return 1;
    }

    @Override
    public void addInformation(ItemStack stack, World player, List<String> tooltip, ITooltipFlag advanced) {
        this.addStandardInfo(tooltip);
    }
}
