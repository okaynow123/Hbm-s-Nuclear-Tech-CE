package com.hbm.render.item.weapon.sedna;

import com.hbm.interfaces.AutoRegister;
import com.hbm.items.weapon.sedna.ItemGunBaseNT;
import com.hbm.main.ResourceManager;
import com.hbm.render.anim.sedna.HbmAnimationsSedna;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
@AutoRegister(item = "gun_tau")
public class ItemRenderTau extends ItemRenderWeaponBase {

	@Override
	protected float getTurnMagnitude(ItemStack stack) { return ItemGunBaseNT.getIsAiming(stack) ? 2.5F : -0.5F; }

	@Override
	public void setupFirstPerson(ItemStack stack) {
		GlStateManager.translate(0, 0, 0.875);

		float offset = 0.8F;
		standardAimingTransform(stack,
				-1.75F * offset, -1.75F * offset, 3.5F * offset,
				-1.75F * offset, -1.75F * offset, 3.5F * offset);
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {

		Minecraft.getMinecraft().renderEngine.bindTexture(ResourceManager.tau_tex);
		double scale = 0.75D;
		GlStateManager.scale(scale, scale, scale);

		double[] equip = HbmAnimationsSedna.getRelevantTransformation("EQUIP");
		double[] recoil = HbmAnimationsSedna.getRelevantTransformation("RECOIL");
		double[] rotate = HbmAnimationsSedna.getRelevantTransformation("ROTATE");

		GlStateManager.translate(0, -1, -4);
		GlStateManager.rotate((float)equip[0], 1, 0, 0);
		GlStateManager.translate(0, 1, 4);

		GlStateManager.translate(0, 0, recoil[2]);

		GlStateManager.translate(0, 0, -2);
		GlStateManager.rotate((float)(recoil[2] * 5), 1, 0, 0);
		GlStateManager.translate(0, 0, 2);

		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		GlStateManager.disableCull();

		ResourceManager.tau.renderPart("Body");

		GlStateManager.pushMatrix();
		GlStateManager.translate(0, -0.25, 0);
		GlStateManager.rotate((float)rotate[2], 0, 0, 1);
		GlStateManager.translate(0, 0.25, 0);
		ResourceManager.tau.renderPart("Rotor");
		GlStateManager.popMatrix();

		GlStateManager.enableCull();
		GlStateManager.shadeModel(GL11.GL_FLAT);
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		double scale = 2.5D;
		GlStateManager.scale(scale, scale, scale);
		GlStateManager.translate(0, 1, 2);
	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		double scale = 2D;
		GlStateManager.scale(scale, scale, scale);
		GlStateManager.rotate(25, 1, 0, 0);
		GlStateManager.rotate(45, 0, 1, 0);
		GlStateManager.translate(-0.25, 0.5, 0);
	}

	@Override
	public void renderOther(ItemStack stack, Object type) {
		GlStateManager.enableLighting();

		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		GlStateManager.disableCull();
		Minecraft.getMinecraft().renderEngine.bindTexture(ResourceManager.tau_tex);
		ResourceManager.tau.renderAll();
		GlStateManager.enableCull();
		GlStateManager.shadeModel(GL11.GL_FLAT);
	}
}

