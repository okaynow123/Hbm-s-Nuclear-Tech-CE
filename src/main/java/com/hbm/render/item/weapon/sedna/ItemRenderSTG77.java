package com.hbm.render.item.weapon.sedna;

import com.hbm.interfaces.AutoRegister;
import com.hbm.items.weapon.sedna.ItemGunBaseNT;
import com.hbm.main.ResourceManager;
import com.hbm.render.anim.sedna.HbmAnimationsSedna;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
@AutoRegister(item = "gun_stg77")
public class ItemRenderSTG77 extends ItemRenderWeaponBase {

	@Override
	protected float getTurnMagnitude(ItemStack stack) { return ItemGunBaseNT.getIsAiming(stack) ? 0.5F : -0.25F; }

	@Override
	public void setupFirstPerson(ItemStack stack) {
		GlStateManager.translate(0, 0, 0.875);

		float offset = 0.8F;
		standardAimingTransform(stack,
				-1.5F * offset, -1F * offset, 2.5F * offset,
				0, -5.75 / 8D, 2);
	}

	@Override
	public float getViewFOV(ItemStack stack, float fov) {
		float aimingProgress = ItemGunBaseNT.prevAimingProgress + (ItemGunBaseNT.aimingProgress - ItemGunBaseNT.prevAimingProgress) * interp;
		return  fov * (1 - aimingProgress * 0.66F);
	}

	@Override
	protected float getBaseFOV(ItemStack stack) {
		float aimingProgress = ItemGunBaseNT.prevAimingProgress + (ItemGunBaseNT.aimingProgress - ItemGunBaseNT.prevAimingProgress) * interp;
		return 70F - aimingProgress * 65;
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {
		if(ItemGunBaseNT.prevAimingProgress == 1 && ItemGunBaseNT.aimingProgress == 1) return;

		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		Minecraft.getMinecraft().renderEngine.bindTexture(ResourceManager.stg77_tex);
		double scale = 0.5D;
		GlStateManager.scale(scale, scale, scale);

		double[] equip = HbmAnimationsSedna.getRelevantTransformation("EQUIP");
		double[] lift = HbmAnimationsSedna.getRelevantTransformation("LIFT");
		double[] recoil = HbmAnimationsSedna.getRelevantTransformation("RECOIL");
		double[] bolt = HbmAnimationsSedna.getRelevantTransformation("BOLT");
		double[] handle = HbmAnimationsSedna.getRelevantTransformation("HANDLE");
		double[] safety = HbmAnimationsSedna.getRelevantTransformation("SAFETY");

		double[] inspectGun = HbmAnimationsSedna.getRelevantTransformation("INSPECT_GUN");
		double[] inspectBarrel = HbmAnimationsSedna.getRelevantTransformation("INSPECT_BARREL");
		double[] inspectMove = HbmAnimationsSedna.getRelevantTransformation("INSPECT_MOVE");
		double[] inspectLever = HbmAnimationsSedna.getRelevantTransformation("INSPECT_LEVER");

		GlStateManager.translate(0, -1, -4);
		GlStateManager.rotate((float)equip[0], 1, 0, 0);
		GlStateManager.translate(0, 1, 4);

		GlStateManager.translate(0, 0, -4);
		GlStateManager.rotate((float)lift[0], 1, 0, 0);
		GlStateManager.translate(0, 0, 4);

		GlStateManager.translate(0, 0, recoil[2]);

		GlStateManager.shadeModel(GL11.GL_SMOOTH);

		GlStateManager.pushMatrix();

		GlStateManager.rotate((float)inspectGun[2], 0, 0, 1);
		GlStateManager.rotate((float)inspectGun[0], 1, 0, 0);

		HbmAnimationsSedna.applyRelevantTransformation("Gun");
		ResourceManager.stg77.renderPart("Gun");

		GlStateManager.pushMatrix();
		HbmAnimationsSedna.applyRelevantTransformation("Magazine");
		ResourceManager.stg77.renderPart("Magazine");
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.rotate((float)inspectLever[2], 0, 0, 1);
		HbmAnimationsSedna.applyRelevantTransformation("Lever");
		ResourceManager.stg77.renderPart("Lever");
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, bolt[2]);
		GlStateManager.pushMatrix();
		HbmAnimationsSedna.applyRelevantTransformation("Breech");
		ResourceManager.stg77.renderPart("Breech");
		GlStateManager.popMatrix();
		GlStateManager.translate(0.125, 0, 0);
		GlStateManager.rotate((float)handle[2], 0, 0, 1);
		GlStateManager.translate(-0.125, 0, 0);
		HbmAnimationsSedna.applyRelevantTransformation("Handle");
		ResourceManager.stg77.renderPart("Handle");
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.translate(safety[0], 0, 0);
		HbmAnimationsSedna.applyRelevantTransformation("Safety");
		ResourceManager.stg77.renderPart("Safety");
		GlStateManager.popMatrix();

		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();

		GlStateManager.translate(inspectMove[0], inspectMove[1], inspectMove[2]);
		GlStateManager.rotate((float)inspectBarrel[0], 1, 0, 0);
		GlStateManager.rotate((float)inspectBarrel[2], 0, 0, 1);
		HbmAnimationsSedna.applyRelevantTransformation("Gun");
		HbmAnimationsSedna.applyRelevantTransformation("Barrel");
		ResourceManager.stg77.renderPart("Barrel");
		GlStateManager.popMatrix();

		double smokeScale = 0.75;

		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, 8);
		GlStateManager.rotate(90, 0, 1, 0);
		GlStateManager.scale(smokeScale, smokeScale, smokeScale);
		this.renderSmokeNodes(gun.getConfig(stack, 0).smokeNodes, 0.5D);
		GlStateManager.popMatrix();

		GlStateManager.shadeModel(GL11.GL_FLAT);

		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, 7.5);
		GlStateManager.rotate(90, 0, 1, 0);
		GlStateManager.scale(0.25, 0.25, 0.25);
		GlStateManager.rotate(-5 + gun.shotRand * 10, 1, 0, 0);
		this.renderGapFlash(gun.lastShot[0]);
		GlStateManager.popMatrix();
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		double scale = 1.5D;
		GlStateManager.scale(scale, scale, scale);
		GlStateManager.translate(0, 1, 2);

	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		double scale = 1.375D;
		GlStateManager.scale(scale, scale, scale);
		GlStateManager.rotate(25, 1, 0, 0);
		GlStateManager.rotate(45, 0, 1, 0);
		GlStateManager.translate(-0.5, 0.5, 0);
	}

	@Override
	public void renderOther(ItemStack stack, Object type) {
		GlStateManager.enableLighting();

		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		Minecraft.getMinecraft().renderEngine.bindTexture(ResourceManager.stg77_tex);
		ResourceManager.stg77.renderPart("Gun");
		ResourceManager.stg77.renderPart("Barrel");
		ResourceManager.stg77.renderPart("Lever");
		ResourceManager.stg77.renderPart("Magazine");
		ResourceManager.stg77.renderPart("Safety");
		ResourceManager.stg77.renderPart("Handle");
		ResourceManager.stg77.renderPart("Breech");
		GlStateManager.shadeModel(GL11.GL_FLAT);
	}
}

