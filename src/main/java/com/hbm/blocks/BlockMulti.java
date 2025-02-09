package com.hbm.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

//Hell yeah, should be compatible with 1.7 metablocks while using 1.8+ blockstates
public abstract class BlockMulti extends BlockBase implements IBlockMulti {

    public BlockMulti(Material mat) {
        super(mat);
        this.setDefaultState(this.getBlockState().getBaseState().withProperty(getVariantProperty(), 0));
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(getVariantProperty());
    }

    @Override
    public void getSubBlocks(@NotNull CreativeTabs tab, @NotNull NonNullList<ItemStack> items) {
        for (int i = 0; i < getSubCount(); ++i) {
            items.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(getVariantProperty(), rectify(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(getVariantProperty());
    }

    @Override
    protected @NotNull BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, getVariantProperty());
    }

    @Override
    public IBlockState getStateForPlacement(@NotNull World world, @NotNull BlockPos pos, @NotNull EnumFacing facing,
                                            float hitX, float hitY, float hitZ, int meta,
                                            @NotNull EntityLivingBase placer, @NotNull EnumHand hand) {
        return this.getDefaultState().withProperty(getVariantProperty(), rectify(meta));
    }
}
