package com.hbm.blocks.generic;

import com.google.common.collect.ImmutableMap;
import com.hbm.blocks.BlockEnums;
import com.hbm.blocks.ICustomBlockItem;
import com.hbm.items.IDynamicModels;
import com.hbm.lib.RefStrings;
import com.hbm.render.block.BlockBakeFrame;
import com.hbm.render.icon.TextureAtlasSpriteMultipass;
import com.hbm.render.model.VariantBakedModel;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BlockSellafieldOre extends BlockSellafieldSlaked implements ICustomBlockItem, IDynamicModels {

    public BlockEnums.OreType oreType;

    public BlockSellafieldOre(String s, BlockEnums.OreType oreType) {
        super(Material.ROCK, SoundType.STONE, s);
        this.oreType = oreType;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerSprite(TextureMap map) {
        for (int i = 0; i < sellafieldTextures.length; i++) {
            ResourceLocation spriteLoc = new ResourceLocation(RefStrings.MODID, BlockBakeFrame.ROOT_PATH + this.getRegistryName().getPath() + "_" + i);
            TextureAtlasSpriteMultipass layeredSprite = new TextureAtlasSpriteMultipass(spriteLoc.toString(), "blocks/" + sellafieldTextures[i].textureArray[0], "blocks/" + "ore_overlay_" + oreType.getName());
            map.setTextureEntry(layeredSprite);
        }
    }

    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        Random rand = ((World) world).rand;
        int variant = state.getValue(SHADE);
        int meta = variant + 1;
        if (oreType.oreEnum == null)
            return Collections.singletonList(new ItemStack(Item.getItemFromBlock(this), 1, meta));
        else {
            int count = oreType.oreEnum.quantityFunction.apply(state, fortune, rand);
            List<ItemStack> drop = new ArrayList<>(count);
            for (int i = 0; i < count; i++)
                drop.add(oreType.oreEnum.dropFunction.apply(state, rand));
            return drop;
        }
    }

    @SideOnly(Side.CLIENT)
    public void bakeModel(ModelBakeEvent event) {
        var models = new IBakedModel[META_COUNT];
        for (int i = 0; i < sellafieldTextures.length; i++) {
            BlockBakeFrame blockFrame = sellafieldTextures[i % sellafieldTextures.length];
            try {
                IModel baseModel = ModelLoaderRegistry.getModel(new ResourceLocation(blockFrame.getBaseModel()));
                ImmutableMap.Builder<String, String> textureMap = ImmutableMap.builder();

                ResourceLocation spriteLoc = new ResourceLocation(RefStrings.MODID, BlockBakeFrame.ROOT_PATH + this.getRegistryName().getPath() + "_" + i );
                textureMap.put("all", spriteLoc.toString());
                IModel retexturedModel = baseModel.retexture(textureMap.build());
                IBakedModel bakedModel = retexturedModel.bake(
                        ModelRotation.X0_Y0, DefaultVertexFormats.BLOCK, ModelLoader.defaultTextureGetter()
                );

                models[i] = bakedModel;

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        event.getModelRegistry().putObject(new ModelResourceLocation(this.getRegistryName(), "normal"), new VariantBakedModel(models, models[0], VARIANT));
        event.getModelRegistry().putObject(new ModelResourceLocation(this.getRegistryName(), "inventory"), models[0]);
    }

}
