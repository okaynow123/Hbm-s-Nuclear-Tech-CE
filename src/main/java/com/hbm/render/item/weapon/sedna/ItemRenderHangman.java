package com.hbm.render.item.weapon.sedna;

import com.hbm.interfaces.AutoRegister;
import com.hbm.items.weapon.sedna.ItemGunBaseNT;
import com.hbm.main.ResourceManager;
import com.hbm.render.anim.sedna.HbmAnimationsSedna;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
@AutoRegister(item = "gun_hangman")
public class ItemRenderHangman extends ItemRenderWeaponBase {

	@Override
	protected float getTurnMagnitude(ItemStack stack) { return ItemGunBaseNT.getIsAiming(stack) ? 2.5F : -0.5F; }

	@Override
	public float getViewFOV(ItemStack stack, float fov) {
		float aimingProgress = ItemGunBaseNT.prevAimingProgress + (ItemGunBaseNT.aimingProgress - ItemGunBaseNT.prevAimingProgress) * interp;
		return  fov * (1 - aimingProgress * 0.33F);
	}

	@Override
	public void setupFirstPerson(ItemStack stack) {
		GlStateManager.translate(0, 0, 0.875);

		float offset = 0.8F;
		standardAimingTransform(stack,
				-1.5F * offset, -0.875F * offset, 1.75F * offset,
				0, -1.5 / 8D, 1.25);
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {

		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		Minecraft.getMinecraft().getTextureManager().bindTexture(ResourceManager.hangman_tex);
		float offset = 0.8F;

		double[] equip = HbmAnimationsSedna.getRelevantTransformation("EQUIP");
		double[] recoil = HbmAnimationsSedna.getRelevantTransformation("RECOIL");
		double[] roll = HbmAnimationsSedna.getRelevantTransformation("ROLL");
		double[] turn = HbmAnimationsSedna.getRelevantTransformation("TURN");
		double[] smack = HbmAnimationsSedna.getRelevantTransformation("SMACK");
		double[] lid = HbmAnimationsSedna.getRelevantTransformation("LID");
		double[] mag = HbmAnimationsSedna.getRelevantTransformation("MAG");
		double[] bullets = HbmAnimationsSedna.getRelevantTransformation("BULLETS");

		GlStateManager.translate(1.5F * offset, 0, -1);
		GlStateManager.rotate((float) turn[1], 0, 1, 0);
		GlStateManager.translate(-1.5F * offset, 0, 1);

		GlStateManager.rotate((float) roll[2], 0, 0, 1);
		GlStateManager.translate(smack[0], smack[1], smack[2]);

		double scale = 0.125D;
		GlStateManager.scale(scale, scale, scale);

		GlStateManager.translate(0, -4, -10);
		GlStateManager.rotate((float) equip[0], 1, 0, 0);
		GlStateManager.translate(0, 4, 10);

		GlStateManager.translate(0, 0, recoil[2]);

		GlStateManager.shadeModel(GL11.GL_SMOOTH);

		ResourceManager.hangman.renderPart("Rifle");
		ResourceManager.hangman.renderPart("Internals");

		GlStateManager.pushMatrix();
		GlStateManager.translate(-2.1875, -1.75, 0);
		GlStateManager.rotate((float) lid[2], 0, 0, 1);
		GlStateManager.translate(2.1875, 1.75, 0);
		ResourceManager.hangman.renderPart("Lid");
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.translate(mag[0], mag[1], mag[2]);
		ResourceManager.hangman.renderPart("Magazine");
		if (bullets[0] == 0) ResourceManager.hangman.renderPart("Bullets");
		GlStateManager.popMatrix();

		double smokeScale = 1.5;

		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, 29);
		GlStateManager.rotate(90, 0, 1, 0);
		GlStateManager.scale(smokeScale, smokeScale, smokeScale);
		this.renderSmokeNodes(gun.getConfig(stack, 0).smokeNodes, 0.5D);
		GlStateManager.popMatrix();

		GlStateManager.shadeModel(GL11.GL_FLAT);

		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, 29);
		GlStateManager.rotate(90, 0, 1, 0);
		GlStateManager.rotate(90 * gun.shotRand, 1, 0, 0);
		GlStateManager.scale(2, 2, 2);
		this.renderMuzzleFlash(gun.lastShot[0], 75, 7.5);
		GlStateManager.popMatrix();
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		double scale = 0.5D;
		GlStateManager.scale(scale, scale, scale);
		GlStateManager.translate(0, 4.25, 11);
	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		double scale = 0.375D;
		GlStateManager.scale(scale, scale, scale);
		GlStateManager.rotate(25, 1, 0, 0);
		GlStateManager.rotate(45, 0, 1, 0);
		GlStateManager.translate(-0.5, 2.5, 0);
	}

	@Override
	public void setupEntity(ItemStack stack) {
		double scale = 0.0625D;
		GlStateManager.scale(scale, scale, scale);
	}

	@Override
	public void renderOther(ItemStack stack, Object type) {
		GlStateManager.enableLighting();

		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		Minecraft.getMinecraft().getTextureManager().bindTexture(ResourceManager.hangman_tex);
		ResourceManager.hangman.renderAll();
		GlStateManager.shadeModel(GL11.GL_FLAT);
	}
}

