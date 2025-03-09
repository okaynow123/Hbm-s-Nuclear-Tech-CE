package com.hbm.blocks.generic;

import com.google.common.collect.ImmutableMap;
import com.hbm.blocks.BlockBase;
import com.hbm.blocks.ICustomBlockItem;
import com.hbm.blocks.ModBlocks;
import com.hbm.handler.RadiationSystemNT;
import com.hbm.items.IModelRegister;
import com.hbm.lib.RefStrings;
import com.hbm.main.MainRegistry;
import com.hbm.render.icon.RGBMutatorInterpolatedComponentRemap;
import com.hbm.render.icon.TextureAtlasSpriteMutatable;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
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

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BlockSellafield extends BlockBase  implements ICustomBlockItem {

    public static final String[] sellafieldTextures = new String[]{"sellafield_slaked", "sellafield_slaked_1", "sellafield_slaked_2", "sellafield_slaked_3"};
    public static final int SELLAFIETE_LEVELS = 6;
    public static final int TEXTURE_VARIANTS = sellafieldTextures.length;
    public static final PropertyInteger VARIANT = PropertyInteger.create("variant", 0, TEXTURE_VARIANTS - 1);
    public static final PropertyInteger META = PropertyInteger.create("meta", 0, SELLAFIETE_LEVELS - 1);
    public static final String basePath = "blocks/";
    public static final float rad = 0.5f;
    public static final int[][] colors = new int[][]{
            {0x4C7939, 0x41463F},
            {0x418223, 0x3E443B},
            {0x338C0E, 0x3B5431},
            {0x1C9E00, 0x394733},
            {0x02B200, 0x37492F},
            {0x00D300, 0x324C26}
    };
    public BlockSellafield(Material mat, SoundType type, String s) {
        super(mat, type, s);
    }


    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        int meta   = state.getValue(META);
        float netRad = rad * ( meta + 1);
        RadiationSystemNT.incrementRad(world, pos, netRad , netRad );
        if (rand.nextInt(meta == 0 ? 25 : 15) == 0) {
            if (meta > 0)
                world.setBlockState(pos, world.getBlockState(pos).withProperty(META, meta - 1), 3);
            else
                world.setBlockState(pos, ModBlocks.sellafield_slaked.getDefaultState());
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, META, VARIANT);
    }

    @SideOnly(Side.CLIENT)
    public void registerModel() {
            for (int meta = 0; meta <= SELLAFIETE_LEVELS - 1; meta++) {
                ModelLoader.setCustomModelResourceLocation(
                        Item.getItemFromBlock(this),
                        meta,
                        new ModelResourceLocation(this.getRegistryName(), "meta=" + meta + ",variant=0")
                );
            }
    }


    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        long l = (pos.getX() * 3129871L) ^ (long)pos.getY() * 116129781L ^ (long)pos.getZ();
        l = l * l * 42317861L + l * 11L;
        int i = (int)(l >> 16 & 3L);

        int meta = stack.getMetadata();
        IBlockState newState = this.getStateFromMeta(meta).withProperty(VARIANT, Math.abs(i) % TEXTURE_VARIANTS );
        world.setBlockState(pos, newState, 3);
    }

    @SideOnly(Side.CLIENT)
    public void registerSprite(TextureMap map) {
        for (int level = 0; level <= SELLAFIETE_LEVELS - 1; level++) {
            int[] tint = colors[level];
            for (String texture : sellafieldTextures) {
                ResourceLocation spriteLoc = new ResourceLocation(RefStrings.MODID, basePath + texture + "-" + level);
                TextureAtlasSpriteMutatable mutatedTexture = new TextureAtlasSpriteMutatable(spriteLoc.toString(), new RGBMutatorInterpolatedComponentRemap(0x858384, 0x434343, tint[0], tint[1]));
                map.setTextureEntry(mutatedTexture);
            }
        }
    }

    public int getMetaFromState(IBlockState state) {
        return state.getValue(META);
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(META, meta);
    }


    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        int meta = state.getValue(META);
        return Collections.singletonList(new ItemStack(Item.getItemFromBlock(this), 1, meta));
    }


    public void registerItem() {
        ItemBlock itemBlock = new ItemBlockSellafield(this);
        itemBlock.setRegistryName(this.getRegistryName());
         itemBlock.setCreativeTab(this.getCreativeTab());
        ForgeRegistries.ITEMS.register(itemBlock);
    }



    @SideOnly(Side.CLIENT)
    public void bakeModel(ModelBakeEvent event) {
        try {
            IModel baseModel = ModelLoaderRegistry.getModel(new ResourceLocation("minecraft:block/cube_all"));

            for (int level = 0; level <= SELLAFIETE_LEVELS - 1; level++) {
                for (int textureIndex = 0; textureIndex <= sellafieldTextures.length - 1; textureIndex++) {
                    ImmutableMap.Builder<String, String> textureMap = ImmutableMap.builder();

                    ResourceLocation spriteLoc = new ResourceLocation(RefStrings.MODID, basePath + sellafieldTextures[textureIndex] + "-" + level);

                    // Base texture
                    textureMap.put("all", spriteLoc.toString());


                    IModel retexturedModel = baseModel.retexture(textureMap.build());
                    IBakedModel bakedModel = retexturedModel.bake(
                            ModelRotation.X0_Y0, DefaultVertexFormats.BLOCK, ModelLoader.defaultTextureGetter()
                    );

                    ModelResourceLocation modelLocation = new ModelResourceLocation(getRegistryName(), "meta=" + level + ",variant=" + textureIndex);
                    event.getModelRegistry().putObject(modelLocation, bakedModel);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class ItemBlockSellafield extends ItemBlock implements IModelRegister {
        BlockSellafield metaBlock = (BlockSellafield) this.block;
        public ItemBlockSellafield(Block block) {
            super(block);
            this.hasSubtypes = true;
            this.canRepair = false;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list){
            if (this.isInCreativeTab(tab)) {
                for (int i = 0; i <= SELLAFIETE_LEVELS - 1; i++) {
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
            for (int meta = 0; meta <= SELLAFIETE_LEVELS -1; meta++) {
                ModelLoader.setCustomModelResourceLocation(this, meta,
                        new ModelResourceLocation(this.getRegistryName(), "meta=" + meta + ",variant=0"));
            }
        }
    }
}