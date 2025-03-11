package com.hbm.items;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Used in items that require custom textures that cannot be assigned during compile time
 */
public interface IDynamicSprites {
    @SideOnly(Side.CLIENT)
    public static void registerSprites(TextureMap map){}
}
