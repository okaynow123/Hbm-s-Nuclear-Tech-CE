package com.hbm.render.item.weapon.sedna;

import com.hbm.interfaces.AutoRegister;
import com.hbm.items.weapon.sedna.ItemGunBaseNT;
import com.hbm.main.ResourceManager;
import com.hbm.render.anim.sedna.HbmAnimationsSedna;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
@AutoRegister(item = "gun_coilgun")
public class ItemRenderCoilgun extends ItemRenderWeaponBase {

	@Override
	protected float getTurnMagnitude(ItemStack stack) {
		return ItemGunBaseNT.getIsAiming(stack) ? 2.5F : -0.25F;
	}

	@Override
	public float getViewFOV(ItemStack stack, float fov) {
		float aimingProgress = ItemGunBaseNT.prevAimingProgress +
				(ItemGunBaseNT.aimingProgress - ItemGunBaseNT.prevAimingProgress) * interp;
		return fov * (1 - aimingProgress * 0.33F);
	}

	@Override
	public void setupFirstPerson(ItemStack stack) {
		GlStateManager.translate(0, 0, 0.875);

		float offset = 0.8F;
		standardAimingTransform(stack,
				-1.25F * offset, -1.5F * offset, 2.5F * offset,
				0, -7.5 / 8D, 1);
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {

		Minecraft.getMinecraft().getTextureManager().bindTexture(ResourceManager.flaregun_tex);
		double scale = 0.75D;
		GlStateManager.scale(scale, scale, scale);

		GlStateManager.rotate(-90F, 0, 1, 0);

		double[] recoil = HbmAnimationsSedna.getRelevantTransformation("RECOIL");
		GlStateManager.translate(-1.5 - recoil[0] * 0.5, 0, 0);
		GlStateManager.rotate((float) (recoil[0] * 45), 0, 0, 1);
		GlStateManager.translate(1.5, 0, 0);

		double[] reload = HbmAnimationsSedna.getRelevantTransformation("RELOAD");
		GlStateManager.translate(-2.5, 0, 0);
		GlStateManager.rotate((float) (reload[0] * -45), 0, 0, 1);
		GlStateManager.translate(2.5, 0, 0);

		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		Minecraft.getMinecraft().getTextureManager().bindTexture(ResourceManager.coilgun_tex);
		ResourceManager.coilgun.renderAll();
		GlStateManager.shadeModel(GL11.GL_FLAT);
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		double scale = 3D;
		GlStateManager.scale(scale, scale, scale);
		GlStateManager.translate(0, 0.25, 1.25);
	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		double scale = 4D;
		GlStateManager.scale(scale, scale, scale);
		GlStateManager.rotate(25F, 1, 0, 0);
		GlStateManager.rotate(45F, 0, 1, 0);
		GlStateManager.translate(-0.25, -0.25, 0);
	}

	@Override
	public void renderOther(ItemStack stack, Object type) {
		GlStateManager.enableLighting();

		GlStateManager.rotate(-90F, 0, 1, 0);

		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		Minecraft.getMinecraft().getTextureManager().bindTexture(ResourceManager.coilgun_tex);
		ResourceManager.coilgun.renderAll();
		GlStateManager.shadeModel(GL11.GL_FLAT);
	}
}

