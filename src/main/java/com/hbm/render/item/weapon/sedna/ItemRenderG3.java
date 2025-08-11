package com.hbm.render.item.weapon.sedna;

import com.hbm.interfaces.AutoRegister;
import com.hbm.items.weapon.sedna.ItemGunBaseNT;
import com.hbm.main.ResourceManager;
import com.hbm.render.anim.sedna.HbmAnimationsSedna;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
@AutoRegister(item = "gun_g3")
public class ItemRenderG3 extends ItemRenderWeaponBase {

	@Override
	protected float getTurnMagnitude(ItemStack stack) { return ItemGunBaseNT.getIsAiming(stack) ? 2.5F : -0.25F; }

	@Override
	public float getViewFOV(ItemStack stack, float fov) {
		float aimingProgress = ItemGunBaseNT.prevAimingProgress + (ItemGunBaseNT.aimingProgress - ItemGunBaseNT.prevAimingProgress) * interp;
		return fov * (1 - aimingProgress * 0.33F);
	}

	@Override
	public void setupFirstPerson(ItemStack stack) {
		GlStateManager.translate(0, 0, 0.875);

		float offset = 0.8F;
		standardAimingTransform(stack,
				-1.25F * offset, -1F * offset, 2.75F * offset,
				0, -3.5625 / 8D, 1.75);
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {

		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		Minecraft.getMinecraft().getTextureManager().bindTexture(ResourceManager.g3_tex);
		double scale = 0.375D;
		GlStateManager.scale(scale, scale, scale);

		double[] equip = HbmAnimationsSedna.getRelevantTransformation("EQUIP");
		double[] lift = HbmAnimationsSedna.getRelevantTransformation("LIFT");
		double[] recoil = HbmAnimationsSedna.getRelevantTransformation("RECOIL");
		double[] mag = HbmAnimationsSedna.getRelevantTransformation("MAG");
		double[] speen = HbmAnimationsSedna.getRelevantTransformation("SPEEN");
		double[] bolt = HbmAnimationsSedna.getRelevantTransformation("BOLT");
		double[] handle = HbmAnimationsSedna.getRelevantTransformation("HANDLE");
		double[] bullet = HbmAnimationsSedna.getRelevantTransformation("BULLET");

		GlStateManager.translate(0, -2, -6);
		GlStateManager.rotate(equip[0], 1, 0, 0);
		GlStateManager.translate(0, 2, 6);

		GlStateManager.translate(0, 0, -4);
		GlStateManager.rotate(lift[0], 1, 0, 0);
		GlStateManager.translate(0, 0, 4);

		GlStateManager.translate(0, 0, recoil[2]);

		GlStateManager.shadeModel(GL11.GL_SMOOTH);

		ResourceManager.g3.renderPart("Rifle");
		ResourceManager.g3.renderPart("Stock");
		ResourceManager.g3.renderPart("Flash_Hider");
		ResourceManager.g3.renderPart("Trigger_Rifle.002");

		GlStateManager.pushMatrix();
		GlStateManager.translate(mag[0], mag[1], mag[2]);
		GlStateManager.translate(0, -1.75, -0.5);
		GlStateManager.rotate(speen[2], 0, 0, 1);
		GlStateManager.rotate(speen[1], 0, 1, 0);
		GlStateManager.translate(0, 1.75, 0.5);
		ResourceManager.g3.renderPart("Magazine");
		if(bullet[0] == 0) ResourceManager.g3.renderPart("Bullet");
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, bolt[2]);
		ResourceManager.g3.renderPart("Bolt");
		GlStateManager.translate(0, 0.625, 0);
		GlStateManager.rotate(handle[2], 0, 0, 1);
		GlStateManager.translate(0, -0.625, 0);
		ResourceManager.g3.renderPart("Handle");
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.translate(0, -0.875, -3.5);
		GlStateManager.rotate(-30 * (1 - ItemGunBaseNT.getMode(stack, 0)), 1, 0, 0);
		GlStateManager.translate(0, 0.875, 3.5);
		ResourceManager.g3.renderPart("Selector_Rifle.001");
		GlStateManager.popMatrix();

		double smokeScale = 0.75;

		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, 13);
		GlStateManager.rotate(90, 0, 1, 0);
		GlStateManager.scale(smokeScale, smokeScale, smokeScale);
		this.renderSmokeNodes(gun.getConfig(stack, 0).smokeNodes, 0.5D);
		GlStateManager.popMatrix();

		GlStateManager.shadeModel(GL11.GL_FLAT);

		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, 12);
		GlStateManager.rotate(90, 0, 1, 0);
		GlStateManager.rotate(-25 + gun.shotRand * 10, 1, 0, 0);
		GlStateManager.scale(0.75, 0.75, 0.75);
		this.renderMuzzleFlash(gun.lastShot[0], 75, 10);
		GlStateManager.popMatrix();
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		double scale = 1D;
		GlStateManager.scale(scale, scale, scale);
		GlStateManager.translate(0, 2, 4);
	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		double scale = 0.875D;
		GlStateManager.scale(scale, scale, scale);
		GlStateManager.rotate(25, 1, 0, 0);
		GlStateManager.rotate(45, 0, 1, 0);
		GlStateManager.translate(-0.5, 0.5, 0);
	}

	@Override
	public void renderOther(ItemStack stack, Object type) {
		GlStateManager.enableLighting();

		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		Minecraft.getMinecraft().getTextureManager().bindTexture(ResourceManager.g3_tex);
		ResourceManager.g3.renderPart("Rifle");
		ResourceManager.g3.renderPart("Stock");
		ResourceManager.g3.renderPart("Magazine");
		ResourceManager.g3.renderPart("Flash_Hider");
		ResourceManager.g3.renderPart("Bolt");
		ResourceManager.g3.renderPart("Handle");
		ResourceManager.g3.renderPart("Trigger_Rifle.002");

		GlStateManager.pushMatrix();
		GlStateManager.translate(0, -0.875, -3.5);
		GlStateManager.rotate(-30, 1, 0, 0);
		GlStateManager.translate(0, 0.875, 3.5);
		ResourceManager.g3.renderPart("Selector_Rifle.001");
		GlStateManager.popMatrix();

		GlStateManager.shadeModel(GL11.GL_FLAT);
	}
}

