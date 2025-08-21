package com.hbm.blocks.generic;

import com.google.common.collect.ImmutableMap;
import com.hbm.blocks.ModBlocks;
import com.hbm.render.block.BlockBakeFrame;
import net.minecraft.block.SoundType;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;

public class BlockConcreteColoredStairs extends BlockStairsEnumMeta {

    public BlockConcreteColoredStairs() {
        super(ModBlocks.concrete_colored, SoundType.STONE, "concrete_colored_stairs", EnumDyeColor.class, true, true);
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
    }
    // to be honest, the only reason I'd do a separated class for colored concrete is because of textures - they are named like concrete_red not concrete.red
    // and I'm lazy af to rename this
    @Override
    protected BlockBakeFrame[] generateBlockFrames(String registryName) {
        BlockBakeFrame[] frames = new BlockBakeFrame[16];
        for (int meta = 0; meta < 16; meta++) {
            String color = EnumDyeColor.byMetadata(meta).getName();
            final String tex = "hbm:blocks/concrete_" + color;

            frames[meta] = new BlockBakeFrame(tex) {
                @Override
                public String getBaseModel() {
                    return "minecraft:block/stairs";
                }

                @Override
                public void registerBlockTextures(TextureMap map) {
                    map.registerSprite(new ResourceLocation(tex));
                }

                @Override
                public void putTextures(ImmutableMap.Builder<String, String> builder) {
                    builder.put("bottom", tex);
                    builder.put("top", tex);
                    builder.put("side", tex);
                    builder.put("particle", tex);
                }
            };
        }
        return frames;
    }
}
