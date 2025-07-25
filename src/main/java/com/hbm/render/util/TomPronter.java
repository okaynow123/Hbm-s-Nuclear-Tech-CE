package com.hbm.render.util;

import com.hbm.main.ResourceManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import org.lwjgl.opengl.GL11; import net.minecraft.client.renderer.GlStateManager;

import java.util.Random;

public class TomPronter {

	
	public static void prontTom(int type) {
		GlStateManager.pushMatrix();

		GlStateManager.disableCull();
		GlStateManager.disableLighting();
		GlStateManager.rotate(180F, 1F, 0F, 0F);
		GlStateManager.scale(2F, 4F, 2F);
		
		TextureManager tex = Minecraft.getMinecraft().getTextureManager();
		
		tex.bindTexture(ResourceManager.tom_main_tex);
		ResourceManager.tom_main.renderAll();
		
    	HmfController.setMod(50000D, 2500D);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GlStateManager.disableAlpha();

        float rot = -System.currentTimeMillis() / 10 % 360;
		//GlStateManager.scale(1.2F, 2F, 1.2F);
		GlStateManager.scale(0.8F, 5F, 0.8F);
		
		Random rand = new Random(0);

		if(type == 0)
			tex.bindTexture(ResourceManager.tom_flame_tex);
		if(type == 1)
			tex.bindTexture(ResourceManager.tom_flame_o_tex);
		
        for(int i = 0; i < 20/*10*/; i++) {
			
			int r = rand.nextInt(90);
			
			GlStateManager.rotate(rot + r, 0, 1, 0);
			
			ResourceManager.tom_flame.renderAll();
			
			GlStateManager.rotate(rot, 0, -1, 0);
			
			GlStateManager.scale(-1.015F, 0.9F, 1.015F);
        }
		
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();

		GlStateManager.enableCull();
		GlStateManager.enableLighting();
        HmfController.resetMod();
		
		GlStateManager.popMatrix();
	}

}