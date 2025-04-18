package com.hbm.blocks.generic;

import com.hbm.items.IModelRegister;
import com.hbm.main.MainRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collections;
import java.util.List;

/**
 * Helper class for blocks with metadata.
 * This class provides utility methods for implementing IMetaBlock.
 */
public class MetaBlockHelper {
    /**
     * Creates a BlockStateContainer for a meta-block.
     * @param block The block.
     * @param metaProperty The PropertyInteger used for metadata.
     * @return The BlockStateContainer.
     */
    public static BlockStateContainer createBlockState(Block block, PropertyInteger metaProperty) {
        return new BlockStateContainer(block, metaProperty);
    }

    /**
     * Gets the metadata value from the block state.
     * @param state The block state.
     * @param metaProperty The PropertyInteger used for metadata.
     * @return The metadata value.
     */
    public static int getMetaFromState(IBlockState state, PropertyInteger metaProperty) {
        return state.getValue(metaProperty);
    }

    /**
     * Gets the block state from the metadata value.
     * @param block The block.
     * @param meta The metadata value.
     * @param metaProperty The PropertyInteger used for metadata.
     * @return The block state.
     */
    public static IBlockState getStateFromMeta(Block block, int meta, PropertyInteger metaProperty) {
        return block.getDefaultState().withProperty(metaProperty, meta);
    }

    /**
     * Gets the drops for the block.
     * @param block The block.
     * @param world The world.
     * @param pos The block position.
     * @param state The block state.
     * @param fortune The fortune level.
     * @param metaProperty The PropertyInteger used for metadata.
     * @return The drops.
     */
    public static List<ItemStack> getDrops(Block block, IBlockAccess world, BlockPos pos, IBlockState state, int fortune, PropertyInteger metaProperty) {
        int meta = state.getValue(metaProperty);
        return Collections.singletonList(new ItemStack(Item.getItemFromBlock(block), 1, meta));
    }

    /**
     * Gets the item stack when the block is picked.
     * @param block The block.
     * @param state The block state.
     * @param target The ray trace result.
     * @param world The world.
     * @param pos The block position.
     * @param player The player.
     * @param metaProperty The PropertyInteger used for metadata.
     * @return The item stack.
     */
    public static ItemStack getPickBlock(Block block, IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player, PropertyInteger metaProperty) {
        return new ItemStack(block, 1, state.getValue(metaProperty));
    }

    /**
     * Called when the block is placed.
     * @param block The block.
     * @param world The world.
     * @param pos The block position.
     * @param state The block state.
     * @param placer The entity that placed the block.
     * @param stack The item stack.
     * @param metaProperty The PropertyInteger used for metadata.
     */
    public static void onBlockPlacedBy(Block block, World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack, PropertyInteger metaProperty) {
        int meta = stack.getMetadata();
        world.setBlockState(pos, block.getDefaultState().withProperty(metaProperty, meta), 3);
    }

    /**
     * Registers the item for a meta-block.
     * @param block The block.
     * @param metaCount The maximum metadata value.
     * @param showMetaInCreative Whether to show metadata variants in the creative tab.
     */
    public static void registerItem(Block block, short metaCount, boolean showMetaInCreative) {
        ItemBlock itemBlock = new MetaBlockItem(block, metaCount);
        itemBlock.setRegistryName(block.getRegistryName());
        if (showMetaInCreative) itemBlock.setCreativeTab(block.getCreativeTab());
        ForgeRegistries.ITEMS.register(itemBlock);
    }

    /**
     * ItemBlock class for meta blocks.
     */
    public static class MetaBlockItem extends ItemBlock implements IModelRegister {
        private final short metaCount;

        public MetaBlockItem(Block block, short metaCount) {
            super(block);
            this.metaCount = metaCount;
            this.hasSubtypes = true;
            this.canRepair = false;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
            if (this.isInCreativeTab(tab)) {
                for (int i = 0; i <= metaCount; i++) {
                    list.add(new ItemStack(this, 1, i));
                }
            }
        }

        @Override
        public String getTranslationKey(ItemStack stack) {
            return super.getTranslationKey() + "_" + stack.getItemDamage();
        }

        @Override
        public void registerModels() {
            for (int meta = 0; meta <= metaCount; meta++) {
                MainRegistry.logger.info("Registering model for " + this.block.getRegistryName() + " meta=" + meta);
                ModelLoader.setCustomModelResourceLocation(this, meta,
                        new ModelResourceLocation(this.getRegistryName(), "meta=" + meta));
            }
        }
    }
}