package com.hbm.render.tileentity;

import com.hbm.interfaces.AutoRegister;
import com.hbm.render.NTMRenderHelper;
import com.hbm.tileentity.machine.TileEntityICFStruct;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

import static com.hbm.render.util.SmallBlockPronter.renderSimpleBlockAt;
import static com.hbm.render.util.SmallBlockPronter.startDrawing;

@AutoRegister
public class RenderICFMultiblock extends TileEntitySpecialRenderer<TileEntityICFStruct> {

    public static TextureAtlasSprite componentSprite0;
    public static TextureAtlasSprite componentSprite2;
    public static TextureAtlasSprite componentSprite4;


    @Override
    public void render(TileEntityICFStruct te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y, z + 0.5);
        switch (te.getBlockMetadata()) {
            case 2 -> GlStateManager.rotate(270, 0F, 1F, 0F);
            case 3 -> GlStateManager.rotate(90, 0F, 1F, 0F);
            case 5 -> GlStateManager.rotate(180, 0F, 1F, 0F);
        }
        GlStateManager.translate(-0.5, 0, -0.5);
        startDrawing();
        NTMRenderHelper.bindBlockTexture();
        NTMRenderHelper.startDrawingTexturedQuads();
        for (int i = -8; i <= 8; i++) {
            renderSimpleBlockAt(componentSprite0, 1F, 0F, i);
            if (i != 0) renderSimpleBlockAt(componentSprite0, 0F, 0F, i);
            renderSimpleBlockAt(componentSprite0, -1F, 0F, i);
            renderSimpleBlockAt(componentSprite2, 0F, 3F, i);
            TextureAtlasSprite ringSprite = Math.abs(i) <= 2 ? componentSprite2 : componentSprite4;
            for (int j = -1; j <= 1; j++) renderSimpleBlockAt(ringSprite, j, 1F, i);
            for (int j = -2; j <= 2; j++) renderSimpleBlockAt(ringSprite, j, 2F, i);
            for (int j = -2; j <= 2; j++) if (j != 0) renderSimpleBlockAt(ringSprite, j, 3F, i);
            for (int j = -2; j <= 2; j++) renderSimpleBlockAt(ringSprite, j, 4F, i);
            for (int j = -1; j <= 1; j++) renderSimpleBlockAt(ringSprite, j, 5F, i);
        }
        NTMRenderHelper.draw();
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }
}
