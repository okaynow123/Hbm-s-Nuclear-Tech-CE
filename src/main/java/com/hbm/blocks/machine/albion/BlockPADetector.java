package com.hbm.blocks.machine.albion;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ITooltipProvider;
import com.hbm.lib.ForgeDirection;
import com.hbm.main.MainRegistry;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.hbm.tileentity.machine.albion.TileEntityPADetector;
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

public class BlockPADetector extends BlockDummyable implements ITooltipProvider {

    public BlockPADetector(String s) {
        super(Material.IRON, s);
        this.setCreativeTab(MainRegistry.machineTab);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        if (meta >= 12) return new TileEntityPADetector();
        if (meta >= 6) return new TileEntityProxyCombo().inventory().power().fluid();
        return null;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing,
                                    float hitX, float hitY, float hitZ) {
        return standardOpenBehavior(worldIn, pos, playerIn, 0);
    }

    @Override
    public int[] getDimensions() {
        return new int[]{2, 2, 2, 2, 4, 4};
    }

    @Override
    public int getOffset() {
        return 0;
    }

    @Override
    public int getHeightOffset() {
        return 2;
    }

    @Override
    public void fillSpace(World world, int x, int y, int z, ForgeDirection dir, int o) {
        super.fillSpace(world, x, y, z, dir, o);

        ForgeDirection rot = dir.getRotation(ForgeDirection.UP);

        this.makeExtra(world, x - rot.offsetX * 4, y, z - rot.offsetZ * 4);
        this.makeExtra(world, x - rot.offsetX * 4, y + 1, z - rot.offsetZ * 4);
        this.makeExtra(world, x - rot.offsetX * 4, y - 1, z - rot.offsetZ * 4);
        this.makeExtra(world, x - rot.offsetX * 4 + dir.offsetX, y, z - rot.offsetZ * 4 + dir.offsetZ);
        this.makeExtra(world, x - rot.offsetX * 4 - dir.offsetX, y, z - rot.offsetZ * 4 - dir.offsetZ);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        addStandardInfo(tooltip);
    }
}
