package com.hbm.blocks.machine.albion;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ITooltipProvider;
import com.hbm.lib.ForgeDirection;
import com.hbm.main.MainRegistry;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.hbm.tileentity.machine.albion.TileEntityPADipole;
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

import javax.annotation.Nullable;
import java.util.List;

public class BlockPADipole extends BlockDummyable implements ITooltipProvider {

    public BlockPADipole(String s) {
        super(Material.IRON, s);
        this.setCreativeTab(MainRegistry.machineTab);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        if (meta >= 12) return new TileEntityPADipole();
        if (meta >= 6) return new TileEntityProxyCombo().power().fluid();
        return null;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing,
                                    float hitX, float hitY, float hitZ) {
        return standardOpenBehavior(worldIn, pos, playerIn, 0);
    }

    @Override
    public int[] getDimensions() {
        return new int[]{1, 1, 1, 1, 1, 1};
    }

    @Override
    public int getOffset() {
        return 0;
    }

    @Override
    public int getHeightOffset() {
        return 1;
    }

    @Override
    public void fillSpace(World world, int x, int y, int z, ForgeDirection dir, int o) {
        super.fillSpace(world, x, y, z, dir, o);
        this.makeExtra(world, x + 1, y - 1, z);
        this.makeExtra(world, x - 1, y - 1, z);
        this.makeExtra(world, x, y - 1, z + 1);
        this.makeExtra(world, x, y - 1, z - 1);
        this.makeExtra(world, x + 1, y + 1, z);
        this.makeExtra(world, x - 1, y + 1, z);
        this.makeExtra(world, x, y + 1, z + 1);
        this.makeExtra(world, x, y + 1, z - 1);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        addStandardInfo(tooltip);
    }
}
