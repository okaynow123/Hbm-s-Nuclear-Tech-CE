package com.hbm.render.item.weapon;

import com.hbm.lib.RefStrings;
import com.hbm.render.item.TEISRBase;
import com.hbm.render.model.ModelFatman;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11; import net.minecraft.client.renderer.GlStateManager;

public class ItemRenderFatMan extends TEISRBase {

	protected ModelFatman swordModel;
	protected static ResourceLocation man_rl = new ResourceLocation(RefStrings.MODID +":textures/models/weapons/FatmanLauncher.png");
	
	public ItemRenderFatMan() {
		swordModel = new ModelFatman();
	}
	
	@Override
	public void renderByItem(ItemStack item) {
		GlStateManager.popMatrix();
		GlStateManager.enableCull();
		Minecraft.getMinecraft().renderEngine.bindTexture(man_rl);
		switch(type){
		case FIRST_PERSON_LEFT_HAND:
			GlStateManager.translate(0.0, 0, -0.2);
		case FIRST_PERSON_RIGHT_HAND:
			GlStateManager.translate(0, -0.2, 0.2);
			GL11.glRotated(180, 1, 0, 0);
			GL11.glRotated(40, 0, 0, 1);
			if(type == TransformType.FIRST_PERSON_LEFT_HAND){
				GL11.glRotated(100, 0, 0, 1);
				GL11.glRotated(180, 1, 0, 0);
			}
			swordModel.render(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, item);
			break;
		case THIRD_PERSON_RIGHT_HAND:
		case THIRD_PERSON_LEFT_HAND:
		case HEAD:
		case FIXED:
		case GROUND:
			GlStateManager.translate(-0.1, -0.1, 0.6);
			GL11.glRotated(90, 0, 1, 0);
			GL11.glRotated(180, 0, 0, 1);
			GL11.glScaled(1.5, 1.5, 1.5);
			swordModel.render(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, item);
			break;
		default:
			break;
		}
		GlStateManager.pushMatrix();
	}
}
