package com.hbm.render.item.weapon.sedna;

import com.hbm.interfaces.AutoRegister;
import com.hbm.items.weapon.sedna.ItemGunBaseNT;
import com.hbm.main.ResourceManager;
import com.hbm.render.anim.sedna.HbmAnimationsSedna;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
@AutoRegister(item = "gun_bolter")
public class ItemRenderBolter extends ItemRenderWeaponBase {

	@Override
	protected float getTurnMagnitude(ItemStack stack) {
		return ItemGunBaseNT.getIsAiming(stack) ? 2.5F : -0.25F;
	}

	@Override
	public float getViewFOV(ItemStack stack, float fov) {
		float aimingProgress = ItemGunBaseNT.prevAimingProgress +
				(ItemGunBaseNT.aimingProgress - ItemGunBaseNT.prevAimingProgress) * interp;
		return  fov * (1 - aimingProgress * 0.33F);
	}

	@Override
	public void setupFirstPerson(ItemStack stack) {
		GlStateManager.translate(0, 0, 0.875);

		float offset = 0.8F;
		standardAimingTransform(stack,
				-1.5F * offset, -2F * offset, 2.5F * offset,
				0, -10.5 / 8D, 1.25);
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {

		Minecraft.getMinecraft().getTextureManager().bindTexture(ResourceManager.bolter_tex);
		double scale = 0.5D;
		GlStateManager.scale(scale, scale, scale);

		GlStateManager.rotate(180F, 0, 1, 0);

		double[] recoil = HbmAnimationsSedna.getRelevantTransformation("RECOIL");
		GlStateManager.rotate((float) (recoil[0] * 5), 1, 0, 0);
		GlStateManager.translate(0, 0, recoil[0]);

		double[] tilt = HbmAnimationsSedna.getRelevantTransformation("TILT");
		GlStateManager.translate(0, tilt[0], 3);
		GlStateManager.rotate((float) (tilt[0] * 35), 1, 0, 0);
		GlStateManager.translate(0, 0, -3);

		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		ResourceManager.bolter.renderPart("Body");

		double[] mag = HbmAnimationsSedna.getRelevantTransformation("MAG");
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, 5);
		GlStateManager.rotate((float) (mag[0] * 60 * (mag[2] == 1 ? 2.5 : 1)), -1, 0, 0);
		GlStateManager.translate(0, 0, -5);
		ResourceManager.bolter.renderPart("Mag");
		if (mag[2] != 1) ResourceManager.bolter.renderPart("Bullet");
		GlStateManager.popMatrix();

		GlStateManager.shadeModel(GL11.GL_FLAT);

		GlStateManager.pushMatrix();
		GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
		GlStateManager.disableLighting();
		GlStateManager.disableCull();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);

		FontRenderer font = Minecraft.getMinecraft().fontRenderer;
		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		String s = gun.getConfig(stack, 0).getReceivers(stack)[0]
				.getMagazine(stack).getAmount(stack, null) + "";
		float f3 = 0.04F;
		GlStateManager.translate(0.025F - (font.getStringWidth(s) / 2) * 0.04F, 2.11F, 2.91F);
		GlStateManager.scale(f3, -f3, f3);
		GlStateManager.rotate(45F, 1, 0, 0);
		GlStateManager.glNormal3f(0.0F, 0.0F, -1.0F * f3);
		font.drawString(s, 0, 0, 0xff0000);

		GlStateManager.enableLighting();
		GL11.glPopAttrib();
		GlStateManager.popMatrix();
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		double scale = 2.5D;
		GlStateManager.scale(scale, scale, scale);
		GlStateManager.translate(0, -0.75, 1.25);
	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		double scale = 2.75D;
		GlStateManager.scale(scale, scale, scale);
		GlStateManager.rotate(25F, 1, 0, 0);
		GlStateManager.rotate(45F, 0, 1, 0);
		GlStateManager.translate(-0.25, -0.5, 0);
	}

	@Override
	public void setupModTable(ItemStack stack) {
		double scale = -12.5D;
		GlStateManager.scale(scale, scale, scale);
		GlStateManager.rotate(90, 0, 1, 0);
		GlStateManager.translate(0, -0.5, 0);
	}

	@Override
	public void renderOther(ItemStack stack, Object type) {
		GlStateManager.enableLighting();

		GlStateManager.rotate(180F, 0, 1, 0);

		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		Minecraft.getMinecraft().getTextureManager().bindTexture(ResourceManager.bolter_tex);
		ResourceManager.bolter.renderAll();
		GlStateManager.shadeModel(GL11.GL_FLAT);
	}
}

