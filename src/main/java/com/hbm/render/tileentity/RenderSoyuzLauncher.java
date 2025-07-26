package com.hbm.render.tileentity;

import com.hbm.render.misc.SoyuzLauncherPronter;
import com.hbm.render.misc.SoyuzPronter;
import com.hbm.tileentity.machine.TileEntitySoyuzLauncher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import org.lwjgl.opengl.GL11; import net.minecraft.client.renderer.GlStateManager;

public class RenderSoyuzLauncher extends TileEntitySpecialRenderer<TileEntitySoyuzLauncher> {

	@Override
	public boolean isGlobalRenderer(TileEntitySoyuzLauncher te) {
		return true;
	}
	
	@Override
	public void render(TileEntitySoyuzLauncher soyuzLauncher, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x + 0.5F, (float) y-4, (float) z + 0.5F);

        double open = 45D;
		int timer = 20;
		
		double rot = open;
		
		if(soyuzLauncher.rocketType >=0)
			rot = 0;
		
		if(soyuzLauncher.starting && soyuzLauncher.countdown < timer) {
			
			rot = (timer - soyuzLauncher.countdown + partialTicks) * open / timer;
		}
		
		SoyuzLauncherPronter.prontLauncher(rot);
		
		if(soyuzLauncher.rocketType >= 0) {
			GlStateManager.translate(0.0F, 5.0F, 0.0F);
			SoyuzPronter.prontSoyuz(soyuzLauncher.rocketType);
		}
		
		GlStateManager.popMatrix();
	}
}
