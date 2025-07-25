package com.hbm.render.misc;

import com.hbm.main.ResourceManager;
import com.hbm.render.util.HmfController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.texture.TextureManager;
import org.lwjgl.opengl.GL11; import net.minecraft.client.renderer.GlStateManager;

import java.util.Random;

public class TomPronter2 {

	public static void prontTom() {
		GlStateManager.pushMatrix();

		GlStateManager.disableCull();
		GlStateManager.disableLighting();
		GlStateManager.scale(100F, 100F, 100F);
		
		TextureManager tex = Minecraft.getMinecraft().getTextureManager();
		
		tex.bindTexture(ResourceManager.tom_main_tex);
		ResourceManager.tom_main.renderAll();
		
    	HmfController.setMod(50000D, 2500D);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
        GlStateManager.disableAlpha();

        float rot = -System.currentTimeMillis() / 10 % 360;
		//GlStateManager.scale(1.2F, 2F, 1.2F);
		GlStateManager.scale(0.8F, 5F, 0.8F);
		
		Random rand = new Random(0);
		
        for(int i = 0; i < 20/*10*/; i++) {
			tex.bindTexture(ResourceManager.tom_flame_tex);
			
			int r = rand.nextInt(90);
			
			GlStateManager.rotate(rot + r, 0, 1, 0);
			
			ResourceManager.tom_flame.renderAll();
			
			GlStateManager.rotate(rot, 0, -1, 0);
			
			GlStateManager.scale(-1.015F, 0.9F, 1.015F);
        }
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();

		GlStateManager.enableCull();
		GlStateManager.enableLighting();
        HmfController.resetMod();
		
		GlStateManager.popMatrix();
	}
}
