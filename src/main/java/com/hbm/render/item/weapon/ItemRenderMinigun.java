package com.hbm.render.item.weapon;

import com.hbm.items.ModItems;
import com.hbm.items.weapon.ItemGunBase;
import com.hbm.lib.RefStrings;
import com.hbm.render.item.TEISRBase;
import com.hbm.render.model.ModelLacunae;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11; import net.minecraft.client.renderer.GlStateManager;

public class ItemRenderMinigun extends TEISRBase {

	protected ModelLacunae lacunae;
	
	protected static ResourceLocation minigun_rl = new ResourceLocation(RefStrings.MODID +":textures/models/weapons/ModelLacunae.png");
	protected static ResourceLocation avenger_rl = new ResourceLocation(RefStrings.MODID +":textures/models/weapons/ModelLacunaeAvenger.png");
	protected static ResourceLocation lacunae_rl = new ResourceLocation(RefStrings.MODID +":textures/models/weapons/ModelLacunaeReal.png");
	
	public ItemRenderMinigun() {
		lacunae = new ModelLacunae();
	}
	
	@Override
	public void renderByItem(ItemStack item) {
		float f = ItemGunBase.readNBT(item, "rot");
		if(item.getItem() == ModItems.gun_minigun)
			Minecraft.getMinecraft().renderEngine.bindTexture(minigun_rl);
		if(item.getItem() == ModItems.gun_avenger)
			Minecraft.getMinecraft().renderEngine.bindTexture(avenger_rl);
		if(item.getItem() == ModItems.gun_lacunae)
			Minecraft.getMinecraft().renderEngine.bindTexture(lacunae_rl);
		
		switch(type){
		case FIRST_PERSON_LEFT_HAND:
			GlStateManager.translate(-0.25, 0, 0);
		case FIRST_PERSON_RIGHT_HAND:
			GL11.glScaled(0.5, 0.5, 0.5);
			GlStateManager.translate(1.3, 0.8, 1.3);
			if(type == TransformType.FIRST_PERSON_RIGHT_HAND){
				GL11.glRotated(-10, 0, 1, 0);
				GL11.glRotated(-40, 0, 0, 1);
				GL11.glRotated(180, 1, 0, 0);
			} else {
				GL11.glRotated(180, 0, 1, 0);
				GL11.glRotated(180, 1, 0, 0);
				GL11.glRotated(40, 0, 0, 1);
			}
			GlStateManager.rotate(-15.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.rotate(180, 0, 1, 0);
			GlStateManager.translate(0.5F, 0.3F, -0.2F);
			
			lacunae.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, f);
			break;
		case THIRD_PERSON_LEFT_HAND:
		case THIRD_PERSON_RIGHT_HAND:
		case HEAD:
			GlStateManager.translate(0.4, 0.5, -0.3);
			GL11.glRotated(-90, 0, 1, 0);
			GL11.glRotated(180, 1, 0, 0);
			
			GlStateManager.rotate(5.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.rotate(185, 0, 1, 0);
			GlStateManager.translate(0.5F, 0.6F, 0.2F);
			
			lacunae.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, f);
			break;
		case FIXED:
		case GROUND:
			GlStateManager.translate(0.5, 0.5, 0.5);
			GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
			
			GlStateManager.translate(0, -1, 0);
			GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
			
			lacunae.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, f);
			break;
		default:
			break;
		}
	}
}
