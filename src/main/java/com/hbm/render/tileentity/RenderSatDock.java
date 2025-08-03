package com.hbm.render.tileentity;

import com.hbm.blocks.ModBlocks;
import com.hbm.interfaces.AutoRegister;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.TileEntityMachineSatDock;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
@AutoRegister
public class RenderSatDock extends TileEntitySpecialRenderer<TileEntityMachineSatDock> implements IItemRendererProvider {

	@Override
	public boolean isGlobalRenderer(TileEntityMachineSatDock te) {
		return true;
	}
	
	@Override
	public void render(TileEntityMachineSatDock te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GlStateManager.rotate(180, 0F, 0F, 1F);
		GlStateManager.enableLighting();
		
		GlStateManager.rotate(180, 0F, 0F, 1F);
		GlStateManager.translate(0, -1.5F, 0);
		
    	bindTexture(ResourceManager.satdock_tex);
    	ResourceManager.satDock.renderAll();
    	GlStateManager.popMatrix();
	}

	@Override
	public Item getItemForRenderer() {
		return Item.getItemFromBlock(ModBlocks.sat_dock);
	}

	@Override
	public ItemRenderBase getRenderer(Item item) {
		return new ItemRenderBase() {
			public void renderInventory() {
				GlStateManager.scale(3, 3, 3);
			}

			public void renderCommon() {
				GlStateManager.rotate(90, 0, -1, 0);
				bindTexture(ResourceManager.satdock_tex);
				ResourceManager.satDock.renderAll();
			}
		};
	}
}
