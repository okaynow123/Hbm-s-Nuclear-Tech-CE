package com.hbm.blocks.generic;

import com.google.common.collect.ImmutableMap;
import com.hbm.blocks.BlockBase;
import com.hbm.blocks.ICustomBlockItem;
import com.hbm.interfaces.Untested;
import com.hbm.items.IDynamicModels;
import com.hbm.items.IModelRegister;
import com.hbm.lib.RefStrings;
import com.hbm.main.MainRegistry;
import com.hbm.render.icon.TextureAtlasSpriteMultipass;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class BlockMeta extends BlockBase implements ICustomBlockItem, IDynamicModels {

    //Norwood:Yes you could use strings, enums or whatever, but this is much simpler and more efficient, as well as has exactly same scope as 1.7.10
    public static final PropertyInteger META = PropertyInteger.create("meta", 0, 15);
    public final short META_COUNT;
    private boolean showMetaInCreative = true;
    public static List<BlockMeta> INSTANCES = new ArrayList<>();
    private String[] textures;

    public BlockMeta(Material mat, SoundType type, String s, short metaCount) {
        super(mat, type, s);
        META_COUNT = metaCount;
    }

    public BlockMeta(Material mat, SoundType type, String s, String... textures ) {
        super(mat, type, s);
        this.textures = textures;
        META_COUNT = (short) textures.length;
        INSTANCES.add(this);
    }


    public BlockMeta(Material m, String s) {
        super(m, s);
        META_COUNT = 15;
        INSTANCES.add(this);
    }

    public BlockMeta(Material mat, SoundType type, String s) {
        super(mat, type, s);
        META_COUNT = 15;
        INSTANCES.add(this);
    }

    public BlockMeta(Material m, String s, short metaCount, boolean showMetaInCreative) {
        super(m, s);
        META_COUNT = metaCount;
        this.showMetaInCreative = showMetaInCreative;
        INSTANCES.add(this);
    }

    public static void registerSprites(TextureMap map){for(BlockMeta item : INSTANCES) item.registerSprite(map);}

    @SideOnly(Side.CLIENT)
    public void registerSprite(TextureMap map) {
        for(String texture : this.textures) {
            ResourceLocation spriteLoc = new ResourceLocation(RefStrings.MODID, "blocks/" + texture);
            map.registerSprite(spriteLoc);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void registerModels() {
        for (BlockMeta block : INSTANCES) {
            for (int meta = 0; meta <= block.META_COUNT; meta++) {
                ModelLoader.setCustomModelResourceLocation(
                        Item.getItemFromBlock(block),
                        meta,
                        new ModelResourceLocation(block.getRegistryName(), "meta=" + meta)
                );
            }
        }
    }

    public static void bakeModels(ModelBakeEvent event) {
        for (BlockMeta block : INSTANCES) {
            block.bakeModel(event);
        }
    }

    @SideOnly(Side.CLIENT)
    private void bakeModel(ModelBakeEvent event) {
        try {
            IModel baseModel = ModelLoaderRegistry.getModel(new ResourceLocation("minecraft:block/cube_all"));

            for (int meta = 0; meta <= META_COUNT - 1; meta++) {
                ImmutableMap.Builder<String, String> textureMap = ImmutableMap.builder();
                String texture = textures[meta % textures.length];
                ResourceLocation spriteLoc = new ResourceLocation(RefStrings.MODID, "blocks/"+ texture);

                // Base texture
                textureMap.put("all", spriteLoc.toString());


                IModel retexturedModel = baseModel.retexture(textureMap.build());
                IBakedModel bakedModel = retexturedModel.bake(
                        ModelRotation.X0_Y0, DefaultVertexFormats.BLOCK, ModelLoader.defaultTextureGetter()
                );

                ModelResourceLocation modelLocation = new ModelResourceLocation(getRegistryName(), "meta=" + meta);
                event.getModelRegistry().putObject(modelLocation, bakedModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        return new BlockStateContainer(this, new IProperty[]{this.META});
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
                for (int i = 0; i <= metaBlock.META_COUNT - 1; i++) {
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
            for (int meta = 0; meta <= metaBlock.META_COUNT-1; meta++) {
                MainRegistry.logger.info("Registering model for " + this.block.getRegistryName() + " meta=" + meta);
                ModelLoader.setCustomModelResourceLocation(this, meta,
                        new ModelResourceLocation(this.getRegistryName(), "meta=" + meta));
            }
        }
    }

}
