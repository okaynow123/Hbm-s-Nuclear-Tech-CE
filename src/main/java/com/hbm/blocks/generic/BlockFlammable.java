package com.hbm.blocks.generic;

import com.hbm.lib.ForgeDirection;
import com.hbm.render.block.BlockBakeFrame;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class BlockFlammable extends BlockMeta {

    public int encouragement;
    public int flammability;

    public BlockFlammable(Material m, String s, int en, int flam) {
        super(m, s);
        this.encouragement = en;
        this.flammability = flam;
    }

    public BlockFlammable(Material m, String s, int en, int flam, BlockBakeFrame... frames) {
        super(m, s, frames);
        this.encouragement = en;
        this.flammability = flam;
    }

    public BlockFlammable(Material m, String s, int en, int flam, SoundType type, short metaCount) {
        super(m, type, s, metaCount);
        this.encouragement = en;
        this.flammability = flam;
    }

    @Override
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return flammability;
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return encouragement;
    }

    @Override
    public boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return true;
    }

    @Override
    public boolean isFireSource(World world, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    public void registerItem() {
        ItemBlock itemBlock = new BlockFuelMetaItem(this);
        itemBlock.setRegistryName(this.getRegistryName());
        if (getShowMetaInCreative()) itemBlock.setCreativeTab(this.getCreativeTab());
        ForgeRegistries.ITEMS.register(itemBlock);
    }

    public boolean shouldIgnite(World world, BlockPos pos) {
        if (flammability == 0) return false;

        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (world.getBlockState(new BlockPos(pos.getX() + dir.offsetX, pos.getY() + dir.offsetY, pos.getZ() + dir.offsetZ)).getBlock() == Blocks.FIRE) {
                return true;
            }
        }

        return false;
    }

    public static class BlockFuelMetaItem extends BlockMeta.MetaBlockItem {
        public BlockFuelMetaItem(Block block) {
            super(block);
        }

        @Override
        public String getTranslationKey(ItemStack stack) {
            return this.block.getTranslationKey();
        }
    }
}
