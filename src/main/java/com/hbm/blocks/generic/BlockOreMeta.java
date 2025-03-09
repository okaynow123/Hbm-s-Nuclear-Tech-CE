package com.hbm.blocks.generic;

import com.google.common.collect.ImmutableMap;
import com.hbm.blocks.BlockBase;
import com.hbm.inventory.material.Mats;
import com.hbm.inventory.material.NTMMaterial;
import com.hbm.items.IDynamicModels;
import com.hbm.items.IModelRegister;
import com.hbm.items.special.ItemAutogen;
import com.hbm.lib.RefStrings;
import com.hbm.main.MainRegistry;
import com.hbm.render.icon.RGBMutatorInterpolatedComponentRemap;
import com.hbm.render.icon.TextureAtlasSpriteMultipass;
import com.hbm.render.icon.TextureAtlasSpriteMutatable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
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
import com.hbm.blocks.ICustomBlockItem;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class BlockOreMeta extends BlockBase implements IDynamicModels, ICustomBlockItem  {

    public static final PropertyInteger META = PropertyInteger.create("meta", 0, 15);
    public final short META_COUNT;
    public final  boolean showMetaInCreative;
    public static final List<BlockOreMeta> INSTANCES = new ArrayList<>();
    public final String baseTextureName;
    public final String[] overlayNames;

    public BlockOreMeta(Material material, String name, String baseTexture, String... overlays) {
        super(material, name);
        this.baseTextureName = baseTexture;
        this.overlayNames = overlays;
        META_COUNT = (short)overlays.length;
        INSTANCES.add(this);
        showMetaInCreative = true;
    }

    public static void bakeModels(ModelBakeEvent event) {
        for (BlockOreMeta block : INSTANCES) {
            block.bakeModel(event);
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, META);
    }


    public static void registerSprites(TextureMap map){for(BlockOreMeta item : INSTANCES) item.registerSprite(map);}
    @SideOnly(Side.CLIENT)
    public void registerSprite(TextureMap map) {
                for(String overlay : this.overlayNames) {
                    ResourceLocation spriteLoc = new ResourceLocation(RefStrings.MODID, "blocks/" + this.getRegistryName().getPath() + "-" + overlay);
                    TextureAtlasSpriteMultipass layeredSprite = new TextureAtlasSpriteMultipass(spriteLoc.toString(), "blocks/"+baseTextureName, "blocks/" + overlay);
                    map.setTextureEntry(layeredSprite);
                }
//                ResourceLocation baseLoc = new ResourceLocation(RefStrings.MODID, "blocks/" + this.baseTextureName);
//                map.registerSprite(baseLoc);
//                for(String overlay : this.overlayNames){
//                    ResourceLocation overlayLoc = new ResourceLocation(RefStrings.MODID, "blocks/" + overlay);
//                    map.registerSprite(overlayLoc);
//                }

    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(META);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(META, meta);
    }

    @SideOnly(Side.CLIENT)
    public static void registerModels() {
        for (BlockOreMeta block : INSTANCES) {
            for (int meta = 0; meta <= block.META_COUNT; meta++) {
                ModelLoader.setCustomModelResourceLocation(
                        Item.getItemFromBlock(block),
                        meta,
                        new ModelResourceLocation(block.getRegistryName(), "meta=" + meta)
                );
            }
        }
    }

    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        int meta = state.getValue(META);
        return Collections.singletonList(new ItemStack(Item.getItemFromBlock(this), 1, meta));
    }

    public void registerItem() {
        ItemBlock itemBlock = new BlockOreMeta.MetaBlockOreItem(this);
        itemBlock.setRegistryName(Objects.requireNonNull(this.getRegistryName()));
        if(showMetaInCreative) itemBlock.setCreativeTab(this.getCreativeTab());
        ForgeRegistries.ITEMS.register(itemBlock);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        int meta = stack.getMetadata();
        world.setBlockState(pos, this.getStateFromMeta(meta), 3);
    }

    @SideOnly(Side.CLIENT)
    private void bakeModel(ModelBakeEvent event) {
        try {
            IModel baseModel = ModelLoaderRegistry.getModel(new ResourceLocation("minecraft:block/cube_all"));
            ResourceLocation baseTexturePath = new ResourceLocation(RefStrings.MODID,  "blocks/" + this.baseTextureName);

            for (int meta = 0; meta <= META_COUNT - 1; meta++) {
                ImmutableMap.Builder<String, String> textureMap = ImmutableMap.builder();
                String overlay = overlayNames[meta % overlayNames.length];
                ResourceLocation spriteLoc = new ResourceLocation(RefStrings.MODID, "blocks/" + this.getRegistryName().getPath() + "-" + overlay);

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

    public class MetaBlockOreItem extends ItemBlock implements IModelRegister {
        BlockOreMeta metaBlock = (BlockOreMeta) this.block;

        public MetaBlockOreItem(Block block) {
            super(block);
            this.hasSubtypes = true;
            this.canRepair = false;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list){
            if (this.isInCreativeTab(tab)) {
                for (int i = 0; i <= metaBlock.META_COUNT-1; i++) {
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
