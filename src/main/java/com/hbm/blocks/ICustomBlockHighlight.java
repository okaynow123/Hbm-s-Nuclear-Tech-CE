package com.hbm.blocks;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11; import net.minecraft.client.renderer.GlStateManager;

public interface ICustomBlockHighlight {

    @SideOnly(Side.CLIENT) public boolean shouldDrawHighlight(World world, BlockPos pos);
    @SideOnly(Side.CLIENT) public void drawHighlight(DrawBlockHighlightEvent event, World world, BlockPos pos);

    @SideOnly(Side.CLIENT)
    public static void setup() {
        GlStateManager.enableBlend();
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GlStateManager.color(0.0F, 0.0F, 0.0F, 0.4F);
        GlStateManager.glLineWidth(2.0F);
        GlStateManager.disableTexture2D();
        GL11.glDepthMask(false);
    }

    @SideOnly(Side.CLIENT)
    public static void cleanup() {
        GL11.glDepthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
}
