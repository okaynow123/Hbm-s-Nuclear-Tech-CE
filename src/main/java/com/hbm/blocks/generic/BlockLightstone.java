package com.hbm.blocks.generic;

import com.hbm.blocks.BlockEnumMeta;
import com.hbm.render.block.BlockBakeFrame;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

import java.util.Locale;
// TODO: lightstone slabs/stairs
public class BlockLightstone extends BlockEnumMeta {

    public BlockLightstone(Material mat, SoundType type, String registryName, Class<? extends Enum> theEnum, boolean multiName, boolean multiTexture) {
        super(mat, type, registryName, theEnum, multiName, multiTexture);
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
    }

    @Override
    protected BlockBakeFrame[] generateBlockFrames(String registryName) {
        Enum[] values = this.blockEnum.getEnumConstants();
        BlockBakeFrame[] frames = new BlockBakeFrame[values.length];

        for (Enum e : values) {
            int i = e.ordinal();
            String base = registryName + "." + e.name().toLowerCase(Locale.US);

            if (i >= 3) {
                frames[i] = new BlockBakeFrame(BlockBakeFrame.BlockForm.FULL_CUSTOM,
                        base + ".top", // up
                        base + ".top", // down
                        base,          // north
                        base,          // south
                        base,          // west
                        base           // east
                );
            } else {
                frames[i] = new BlockBakeFrame(base);
            }
        }
        return frames;
    }
}
