package com.hbm.blocks.generic;

import com.hbm.blocks.ICustomBlockItem;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

/**
 * A hazard block with metadata support.
 * This class implements IMetaBlock to separate meta-block logic from hazard block logic.
 */
public class BlockHazardMeta extends BlockHazard implements ICustomBlockItem, IMetaBlock {

    public static final PropertyInteger META = PropertyInteger.create("meta", 0, 15);
    public final short META_COUNT;

    public BlockHazardMeta(Material mat, SoundType type, String s) {
        super(mat, type, s);
        META_COUNT = 15;
    }

    public BlockHazardMeta(Material mat, SoundType type, String s, short metaCount) {
        super(mat, type, s);
		if (metaCount < 0 || metaCount > 15) {
			throw new IllegalArgumentException(String.format("metaCount must be between 0 and 15 (inclusive), in %s", s));
		}
        META_COUNT = metaCount;
    }

    public BlockHazardMeta(SoundType type, String s) {
        super(type, s);
        META_COUNT = 15;
    }

    @Override
    public PropertyInteger getMetaProperty() {
        return META;
    }

    @Override
    public short getMetaCount() {
        return META_COUNT;
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        return MetaBlockHelper.getDrops(this, world, pos, state, fortune, META);
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return MetaBlockHelper.getPickBlock(this, state, target, world, pos, player, META);
    }

    @Override
    public void registerItem() {
        MetaBlockHelper.registerItem(this, META_COUNT, true);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        MetaBlockHelper.onBlockPlacedBy(this, world, pos, state, placer, stack, META);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return MetaBlockHelper.createBlockState(this, META);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return MetaBlockHelper.getMetaFromState(state, META);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return MetaBlockHelper.getStateFromMeta(this, meta, META);
    }
}