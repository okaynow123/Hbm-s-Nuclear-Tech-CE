package com.hbm.blocks.generic;

import com.hbm.blocks.BlockBase;
import com.hbm.blocks.ICustomBlockItem;
import com.hbm.interfaces.Untested;
import com.hbm.items.IModelRegister;
import com.hbm.main.MainRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collections;
import java.util.List;


@Untested
public class BlockMeta extends BlockBase implements ICustomBlockItem {

    //Norwood:Yes you could use strings, enums or whatever, but this is much simpler and more efficient, as well as has exactly same scope as 1.7.10
    public static final PropertyInteger META = PropertyInteger.create("meta", 0, 15);
    public final short META_COUNT;
    private boolean showMetaInCreative = true;

    public BlockMeta(Material mat, SoundType type, String s, short metaCount) {
        super(mat, type, s);
        META_COUNT = metaCount;
    }

    public BlockMeta(Material m, String s) {
        super(m, s);
        META_COUNT = 15;
    }

    public BlockMeta(Material mat, SoundType type, String s) {
        super(mat, type, s);
        META_COUNT = 15;
    }

    public BlockMeta(Material m, String s, short metaCount, boolean showMetaInCreative) {
        super(m, s);
        META_COUNT = metaCount;
        this.showMetaInCreative = showMetaInCreative;
    }



    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        int meta = state.getValue(META);
        return Collections.singletonList(new ItemStack(Item.getItemFromBlock(this), 1, meta));
    }

    public void registerItem() {
        ItemBlock itemBlock = new MetaBlockItem(this);
        itemBlock.setRegistryName(this.getRegistryName());
        if(showMetaInCreative) itemBlock.setCreativeTab(this.getCreativeTab());
        ForgeRegistries.ITEMS.register(itemBlock);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        int meta = stack.getMetadata();
        world.setBlockState(pos, this.getStateFromMeta(meta), 3);
    }



    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{META});
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(META);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(META, meta);
    }


    public class MetaBlockItem extends ItemBlock implements IModelRegister {
        BlockMeta metaBlock = (BlockMeta) this.block;
        public MetaBlockItem(Block block) {
            super(block);
            this.hasSubtypes = true;
            this.canRepair = false;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list){
            if (this.isInCreativeTab(tab)) {
                for (int i = 0; i <= metaBlock.META_COUNT; i++) {
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
            for (int meta = 0; meta <= metaBlock.META_COUNT; meta++) {
                MainRegistry.logger.info("Registering model for " + this.block.getRegistryName() + " meta=" + meta);
                ModelLoader.setCustomModelResourceLocation(this, meta,
                        new ModelResourceLocation(this.getRegistryName(), "meta=" + meta));
            }
        }
    }

}
