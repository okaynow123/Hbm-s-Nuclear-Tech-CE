package com.hbm.render.item.weapon;

import com.hbm.items.ModItems;
import com.hbm.items.weapon.ItemGunBase;
import com.hbm.main.ResourceManager;
import com.hbm.render.anim.HbmAnimations;
import com.hbm.render.item.TEISRBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.lwjgl.opengl.GL11; import net.minecraft.client.renderer.GlStateManager;

public class ItemRenderWeaponQuadro extends TEISRBase {

	@Override
	public void renderByItem(ItemStack item) {
		GlStateManager.disableCull();

		EntityPlayer player = Minecraft.getMinecraft().player;

		switch(type) {
		case FIRST_PERSON_LEFT_HAND:
		case FIRST_PERSON_RIGHT_HAND:
			if(item.getItem() == ModItems.gun_quadro) {
				/*GlStateManager.translate(0.75F, 0.0F, -0.15F);
				GlStateManager.rotate(90F, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(-25F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(-10F, 0.0F, 1.0F, 0.0F);
				GL11.glScaled(0.5, 0.5, 0.5);
				
				if(player.isSneaking()) {
					GlStateManager.rotate(5F, 0.0F, 1.0F, 0.0F);
					GlStateManager.translate(1.0F, 0.5F, 0.3F);
				}*/
				if(type == TransformType.FIRST_PERSON_RIGHT_HAND) {
					GlStateManager.translate(-1.0, -0.5, 0);
					if(this.entity != null && entity.isSneaking()) {
						GlStateManager.translate(0.5, 0.2, 1.5);
						GL11.glRotated(5, 0, 1, 0);
					}
					GL11.glRotated(-90, 0, 1, 0);
					GL11.glRotated(-27, 1, 0, 0);
					GL11.glRotated(-5, 0, 1, 0);
				} else {
					GlStateManager.translate(2.75, -0.5, 0);
					GL11.glRotated(10, 0, 1, 0);
					GlStateManager.translate(-1.0, 0.0, -0.2);
					GL11.glRotated(-180, 0, 1, 0);
					GL11.glRotated(-90, 0, 1, 0);
					GL11.glRotated(-25, 1, 0, 0);
					GL11.glRotated(-5, 0, 1, 0);
				}

				double[] recoil = HbmAnimations.getRelevantTransformation("QUADRO_RECOIL", type == TransformType.FIRST_PERSON_LEFT_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
				GlStateManager.translate(0, 0, recoil[2]);

				double[] reload = HbmAnimations.getRelevantTransformation("QUADRO_RELOAD_ROTATE", type == TransformType.FIRST_PERSON_LEFT_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
				GL11.glRotated(reload[2], 1, 0, 0);
			}
			break;
		case THIRD_PERSON_LEFT_HAND:
		case THIRD_PERSON_RIGHT_HAND:
		case HEAD:
		case GROUND:
		case FIXED:
			if(item.getItem() == ModItems.gun_quadro) {
				GL11.glScaled(0.75, 0.75, 0.75);
				GlStateManager.translate(0.75, -0.4, 0.5);
			}
			break;
		case GUI:
			GlStateManager.enableLighting();

			if(item.getItem() == ModItems.gun_quadro) {
				GL11.glScaled(0.25, 0.25, 0.25);
				GlStateManager.translate(1.4F, 1.1F, 0.0F);
				GlStateManager.rotate(-90F, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(-40F, 1.0F, 0.0F, 0.0F);
			}
			break;
		default:
			break;
		}

		if(item.getItem() == ModItems.gun_quadro) {
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
			Minecraft.getMinecraft().renderEngine.bindTexture(ResourceManager.quadro_tex);
			ResourceManager.quadro.renderPart("Launcher");

			if(ItemGunBase.getMag(item) > 0 || ItemGunBase.getIsReloading(item) && type != TransformType.GUI) {
				GlStateManager.pushMatrix();

				GlStateManager.translate(0, -1, 0);

				double[] push = HbmAnimations.getRelevantTransformation("QUADRO_RELOAD_PUSH", type == TransformType.FIRST_PERSON_LEFT_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
				GlStateManager.translate(0, 3, 0);
				GL11.glRotated(push[1] * 30, 1, 0, 0);
				GlStateManager.translate(0, -3, 0);
				GlStateManager.translate(0, 0, push[0] * 3);

				Minecraft.getMinecraft().renderEngine.bindTexture(ResourceManager.quadro_rocket_tex);
				ResourceManager.quadro.renderPart("Rockets");
				GlStateManager.popMatrix();
			}

			GlStateManager.shadeModel(GL11.GL_FLAT);
		}

		GlStateManager.enableCull();
	}
}
