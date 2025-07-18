package com.hbm.render.tileentity;

import com.hbm.blocks.machine.MachineCapacitor;
import com.hbm.hfr.render.loader.WavefrontObjVBO;
import com.hbm.main.ResourceManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import org.lwjgl.opengl.GL11;

public class RenderCapacitor extends TileEntitySpecialRenderer<MachineCapacitor.TileEntityCapacitor> {

    @Override
    public void render(MachineCapacitor.TileEntityCapacitor te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
        MachineCapacitor capacitor = (MachineCapacitor) te.getBlockType();

        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        /*renderPartWithIcon((WavefrontObjVBO) ResourceManager.capacitor, "Top", capacitor.iconTop);
        renderPartWithIcon((WavefrontObjVBO) ResourceManager.capacitor, "Side", capacitor.iconSide);
        renderPartWithIcon((WavefrontObjVBO) ResourceManager.capacitor, "Bottom", capacitor.iconBottom);
        renderPartWithIcon((WavefrontObjVBO) ResourceManager.capacitor, "InnerTop", capacitor.iconInnerTop);
        renderPartWithIcon((WavefrontObjVBO) ResourceManager.capacitor, "InnerSide", capacitor.iconInnerSide);*/

        GlStateManager.popMatrix();
    }

    public static void renderPartWithIcon(WavefrontObjVBO model, String partName, TextureAtlasSprite icon) {
        if (icon == null) return;

        GlStateManager.matrixMode(GL11.GL_TEXTURE);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        float minU = icon.getMinU();
        float minV = icon.getMinV();
        float maxU = icon.getMaxU();
        float maxV = icon.getMaxV();
        GlStateManager.translate(minU, minV, 0);
        GlStateManager.scale(maxU - minU, maxV - minV, 1);

        model.renderPart(partName);

        GlStateManager.popMatrix();
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
    }
}
