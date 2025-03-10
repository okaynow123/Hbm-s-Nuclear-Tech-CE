package com.hbm.blocks.generic;

import com.google.common.collect.ImmutableMap;
import com.hbm.blocks.BlockBase;
import com.hbm.lib.RefStrings;
import com.hbm.render.icon.RGBMutatorInterpolatedComponentRemap;
import com.hbm.render.icon.TextureAtlasSpriteMutatable;
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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.hbm.blocks.generic.BlockSellafield.*;

public class BlockSellafieldSlaked extends BlockBase {

    public static final PropertyInteger VARIANT = PropertyInteger.create("variant", 0, sellafieldTextures.length - 1);

    public BlockSellafieldSlaked(Material mat, SoundType type, String s) {
        super(mat, type, s);
    }

    @SideOnly(Side.CLIENT)
    public void bakeModel(ModelBakeEvent event) {
        try {
            IModel baseModel = ModelLoaderRegistry.getModel(new ResourceLocation("minecraft:block/cube_all"));

            for (int textureIndex = 0; textureIndex <= sellafieldTextures.length - 1; textureIndex++) {
                ImmutableMap.Builder<String, String> textureMap = ImmutableMap.builder();

                ResourceLocation spriteLoc = new ResourceLocation(RefStrings.MODID, basePath + sellafieldTextures[textureIndex]);

                // Base texture
                textureMap.put("all", spriteLoc.toString());


                IModel retexturedModel = baseModel.retexture(textureMap.build());
                IBakedModel bakedModel = retexturedModel.bake(
                        ModelRotation.X0_Y0, DefaultVertexFormats.BLOCK, ModelLoader.defaultTextureGetter()
                );

                ModelResourceLocation modelLocation = new ModelResourceLocation(getRegistryName(), "variant=" + textureIndex);
                event.getModelRegistry().putObject(modelLocation, bakedModel);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        long l = (pos.getX() * 3129871L) ^ (long) pos.getY() * 116129781L ^ (long) pos.getZ();
        l = l * l * 42317861L + l * 11L;
        int i = (int) (l >> 16 & 3L);

        int meta = stack.getMetadata();
        IBlockState newState = this.getDefaultState().withProperty(VARIANT, Math.abs(i) % TEXTURE_VARIANTS);
        world.setBlockState(pos, newState, 3);
    }

    @Override
    public int getMetaFromState(IBlockState state){
        return 0;
    }


    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, VARIANT);
    }

    @SideOnly(Side.CLIENT)
    public void registerSprite(TextureMap map) {
        for (String texture : sellafieldTextures) {
            ResourceLocation spriteLoc = new ResourceLocation(RefStrings.MODID, basePath + texture);
            map.registerSprite(spriteLoc);
        }
    }
}
