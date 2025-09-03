package com.hbm.render.item.weapon.sedna;

import com.hbm.interfaces.AutoRegister;
import com.hbm.items.weapon.sedna.GunConfig;
import com.hbm.items.weapon.sedna.ItemGunBaseNT;
import com.hbm.main.ResourceManager;
import com.hbm.render.anim.sedna.HbmAnimationsSedna;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
@AutoRegister(item = "gun_liberator")
public class ItemRenderLiberator extends ItemRenderWeaponBase {

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
				-1.5F * offset, -1.25F * offset, 1.25F * offset,
				0, -4.625 / 8D, 0.25);
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {

		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		Minecraft.getMinecraft().getTextureManager().bindTexture(ResourceManager.liberator_tex);
		double scale = 0.375D;
		GlStateManager.scale(scale, scale, scale);

		double[] equip = HbmAnimationsSedna.getRelevantTransformation("EQUIP");
		double[] recoil = HbmAnimationsSedna.getRelevantTransformation("RECOIL");
		double[] lift = HbmAnimationsSedna.getRelevantTransformation("LIFT");
		double[] latch = HbmAnimationsSedna.getRelevantTransformation("LATCH");
		double[] brk = HbmAnimationsSedna.getRelevantTransformation("BREAK");
		double[] shell1 = HbmAnimationsSedna.getRelevantTransformation("SHELL1");
		double[] shell2 = HbmAnimationsSedna.getRelevantTransformation("SHELL2");
		double[] shell3 = HbmAnimationsSedna.getRelevantTransformation("SHELL3");
		double[] shell4 = HbmAnimationsSedna.getRelevantTransformation("SHELL4");

		GlStateManager.translate(0, -1, -3);
		GlStateManager.rotate((float) equip[0], 1, 0, 0);
		GlStateManager.translate(0, 1, 3);

		GlStateManager.translate(0, -3, -3);
		GlStateManager.rotate((float) lift[0], 1, 0, 0);
		GlStateManager.translate(0, 3, 3);

		GlStateManager.translate(recoil[0] * 2, recoil[1], recoil[2]);
		GlStateManager.rotate((float) (recoil[2] * 10), 1, 0, 0);

		GlStateManager.shadeModel(GL11.GL_SMOOTH);

		ResourceManager.liberator.renderPart("Gun");

		GlStateManager.pushMatrix();

		GlStateManager.translate(0, -0.5, 0.75);
		GlStateManager.rotate((float) brk[0], 1, 0, 0);
		GlStateManager.translate(0, 0.5, -0.75);
		ResourceManager.liberator.renderPart("Barrel");

		GlStateManager.pushMatrix();
		GlStateManager.translate(shell1[0], shell1[1], shell1[2]);
		ResourceManager.liberator.renderPart("Shell1");
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.translate(shell2[0], shell2[1], shell2[2]);
		ResourceManager.liberator.renderPart("Shell2");
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.translate(shell3[0], shell3[1], shell3[2]);
		ResourceManager.liberator.renderPart("Shell3");
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.translate(shell4[0], shell4[1], shell4[2]);
		ResourceManager.liberator.renderPart("Shell4");
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 1.15625, 0.75);
		GlStateManager.rotate((float) latch[0], 1, 0, 0);
		GlStateManager.translate(0, -1.15625, -0.75);
		ResourceManager.liberator.renderPart("Latch");
		GlStateManager.popMatrix();
		GlStateManager.popMatrix();

		double smokeScale = 0.375;

		GunConfig cfg = gun.getConfig(stack, 0);

		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0.25, 7.25);
		GlStateManager.rotate(90, 0, 1, 0);
		GlStateManager.scale(smokeScale, smokeScale, smokeScale);
		GlStateManager.translate(0, 0, 0.25 / smokeScale);
		this.renderSmokeNodes(cfg.smokeNodes, 1D);
		GlStateManager.translate(0, 0, -0.5 / smokeScale);
		this.renderSmokeNodes(cfg.smokeNodes, 1D);
		GlStateManager.translate(0, 0.5 / smokeScale, 0);
		this.renderSmokeNodes(cfg.smokeNodes, 1D);
		GlStateManager.translate(0, 0, 0.5 / smokeScale);
		this.renderSmokeNodes(cfg.smokeNodes, 1D);
		GlStateManager.popMatrix();

		GlStateManager.shadeModel(GL11.GL_FLAT);

		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0.5, 8);
		GlStateManager.rotate(90, 0, 1, 0);
		GlStateManager.rotate(90 * gun.shotRand, 1, 0, 0);
		GlStateManager.scale(1.5, 1.5, 1.5);
		this.renderMuzzleFlash(gun.lastShot[0], 75, 5);
		GlStateManager.popMatrix();
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		GlStateManager.translate(0, 1, 3);
	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		double scale = 1.5D;
		GlStateManager.scale(scale, scale, scale);
		GlStateManager.rotate(25, 1, 0, 0);
		GlStateManager.rotate(45, 0, 1, 0);
		GlStateManager.translate(-0.5, 0.5, 0);
	}
	@Override
	public void setupModTable(ItemStack stack) {
		double scale = -8.75D;
		GlStateManager.scale(scale, scale, scale);
		GlStateManager.rotate(90, 0, 1, 0);
	}


	@Override
	public void renderOther(ItemStack stack, Object type) {
		GlStateManager.enableLighting();

		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		Minecraft.getMinecraft().getTextureManager().bindTexture(ResourceManager.liberator_tex);
		ResourceManager.liberator.renderAll();
		GlStateManager.shadeModel(GL11.GL_FLAT);
	}
}

