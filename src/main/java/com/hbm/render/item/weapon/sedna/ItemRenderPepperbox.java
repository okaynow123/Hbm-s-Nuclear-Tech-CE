package com.hbm.render.item.weapon.sedna;

import com.hbm.interfaces.AutoRegister;
import com.hbm.items.weapon.sedna.ItemGunBaseNT;
import com.hbm.main.ResourceManager;
import com.hbm.render.anim.sedna.HbmAnimationsSedna;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
@AutoRegister(item = "gun_pepperbox")
public class ItemRenderPepperbox extends ItemRenderWeaponBase {

	@Override
	protected float getTurnMagnitude(ItemStack stack) {
		return ItemGunBaseNT.getIsAiming(stack) ? 2.5F : -0.5F;
	}

	@Override
	public float getViewFOV(ItemStack stack, float fov) {
		float aimingProgress = ItemGunBaseNT.prevAimingProgress +
				(ItemGunBaseNT.aimingProgress - ItemGunBaseNT.prevAimingProgress) * interp;
		return fov * (1 - aimingProgress * 0.33F);
	}

	@Override
	public void setupFirstPerson(ItemStack stack) {
		GlStateManager.translate(0, 0, 1.5);

		float offset = 0.8F;
		standardAimingTransform(stack,
				-1.25F * offset, -0.75F * offset, 1F * offset,
				0, -2.5 / 8D, 0.5);
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {

		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();

		double scale = 0.25D;
		GlStateManager.scale(scale, scale, scale);

		GlStateManager.shadeModel(GL11.GL_SMOOTH);

		double[] recoil = HbmAnimationsSedna.getRelevantTransformation("RECOIL");
		double[] cylinder = HbmAnimationsSedna.getRelevantTransformation("ROTATE");
		double[] hammer = HbmAnimationsSedna.getRelevantTransformation("HAMMER");
		double[] trigger = HbmAnimationsSedna.getRelevantTransformation("TRIGGER");
		double[] translate = HbmAnimationsSedna.getRelevantTransformation("TRANSLATE");
		double[] loader = HbmAnimationsSedna.getRelevantTransformation("LOADER");
		double[] shot = HbmAnimationsSedna.getRelevantTransformation("SHOT");

		GlStateManager.translate(translate[0], translate[1], translate[2]);

		GlStateManager.translate(0, 0, -5);
		GlStateManager.rotate((float) recoil[0], -1, 0, 0);
		GlStateManager.translate(0, 0, 5);

		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0.5, 7);
		GlStateManager.rotate(90, 0, 1, 0);
		this.renderSmokeNodes(gun.getConfig(stack, 0).smokeNodes, 0.5D);
		GlStateManager.popMatrix();

		Minecraft.getMinecraft().getTextureManager().bindTexture(ResourceManager.pepperbox_tex);

		if(loader[0] != 0 || loader[1] != 0 || loader[2] != 0) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(loader[0], loader[1], loader[2]);
			ResourceManager.pepperbox.renderPart("Speedloader");
			if(shot[0] != 0) ResourceManager.pepperbox.renderPart("Shot");
			GlStateManager.popMatrix();
		}

		ResourceManager.pepperbox.renderPart("Grip");

		GlStateManager.pushMatrix();
		GlStateManager.rotate((float) cylinder[0], 0, 0, 1);
		ResourceManager.pepperbox.renderPart("Cylinder");
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0.375, -1.875);
		GlStateManager.rotate((float) hammer[0], 1, 0, 0);
		GlStateManager.translate(0, -0.375, 1.875);
		ResourceManager.pepperbox.renderPart("Hammer");
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, -trigger[0] * 0.5);
		ResourceManager.pepperbox.renderPart("Trigger");
		GlStateManager.popMatrix();

		GlStateManager.shadeModel(GL11.GL_FLAT);

		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0.5, 7);
		GlStateManager.scale(0.5, 0.5, 0.5);
		GlStateManager.rotate(90, 0, 1, 0);
		GlStateManager.rotate(90 * gun.shotRand, 1, 0, 0);
		this.renderMuzzleFlash(gun.lastShot[0]);
		GlStateManager.rotate(45, 1, 0, 0);
		this.renderMuzzleFlash(gun.lastShot[0]);
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
		GlStateManager.translate(0.5, 0.5, 0);
	}

	@Override
	public void renderOther(ItemStack stack, Object type) {

		GlStateManager.enableLighting();

		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		Minecraft.getMinecraft().getTextureManager().bindTexture(ResourceManager.pepperbox_tex);
		ResourceManager.pepperbox.renderPart("Grip");
		ResourceManager.pepperbox.renderPart("Cylinder");
		ResourceManager.pepperbox.renderPart("Hammer");
		ResourceManager.pepperbox.renderPart("Trigger");
		GlStateManager.shadeModel(GL11.GL_FLAT);
	}
}

