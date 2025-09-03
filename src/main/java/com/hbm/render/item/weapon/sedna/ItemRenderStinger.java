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
@AutoRegister(item = "gun_stinger")
public class ItemRenderStinger extends ItemRenderWeaponBase {

	@Override
	protected float getTurnMagnitude(ItemStack stack) { return ItemGunBaseNT.getIsAiming(stack) ? 2.5F : -0.25F; }

	@Override
	public float getViewFOV(ItemStack stack, float fov) {
		float aimingProgress = ItemGunBaseNT.prevAimingProgress + (ItemGunBaseNT.aimingProgress - ItemGunBaseNT.prevAimingProgress) * interp;
		return  fov * (1 - aimingProgress * 0.5F);
	}

	@Override
	public void setupFirstPerson(ItemStack stack) {
		GlStateManager.translate(0, 0, 0.875);

		float offset = 0.8F;
		standardAimingTransform(stack,
				-3.75F * offset, -9F * offset, -3.5F * offset,
				-2.625F * offset, -6.5, -8.5F);
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {
		if(ItemGunBaseNT.prevAimingProgress == 1 && ItemGunBaseNT.aimingProgress == 1) return;

		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		Minecraft.getMinecraft().renderEngine.bindTexture(ResourceManager.stinger_tex);
		double scale = 1.5D;
		GlStateManager.scale(scale, scale, scale);

		double[] equip = HbmAnimationsSedna.getRelevantTransformation("EQUIP");
		double[] reload = HbmAnimationsSedna.getRelevantTransformation("RELOAD");
		double[] rocket = HbmAnimationsSedna.getRelevantTransformation("ROCKET");

		GlStateManager.translate(0, -1, -1);
		GlStateManager.rotate((float)equip[0], 1, 0, 0);
		GlStateManager.translate(0, 1, 1);

		GlStateManager.translate(0, -4, -3);
		GlStateManager.rotate((float)reload[0], 1, 0, 0);
		GlStateManager.translate(0, 4, 3);

		GlStateManager.shadeModel(GL11.GL_SMOOTH);

		GlStateManager.pushMatrix();
		GlStateManager.rotate(180, 0, 1, 0);
		ResourceManager.stinger.renderAll();
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		Minecraft.getMinecraft().renderEngine.bindTexture(ResourceManager.panzerschreck_tex);
		GlStateManager.translate(rocket[0], rocket[1] + 3.5, rocket[2] - 3);
		ResourceManager.panzerschreck.renderPart("Rocket");
		GlStateManager.shadeModel(GL11.GL_FLAT);

		GlStateManager.pushMatrix();
		GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
		GlStateManager.disableLighting();
		GlStateManager.disableCull();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);

		String label = "Not accurate";
		FontRenderer font = Minecraft.getMinecraft().fontRenderer;
		float f3 = 0.04F;
		GlStateManager.translate(0.025F, -0.5F, (font.getStringWidth(label) / 2) * f3 - 3);
		GlStateManager.scale(f3, -f3, f3);
		GlStateManager.rotate(90, 0, 1, 0);
		GlStateManager.rotate(45, -1, 0, 0);
		GlStateManager.glNormal3f(0.0F, 0.0F, -1.0F * f3);
		font.drawString(label, 0, 0, 0xff0000);

		GlStateManager.enableLighting();
		GL11.glPopAttrib();
		GlStateManager.popMatrix();

		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, 6.5);
		GlStateManager.rotate(90, 0, 1, 0);
		GlStateManager.rotate(90 * gun.shotRand, 1, 0, 0);
		GlStateManager.scale(0.75, 0.75, 0.75);
		this.renderMuzzleFlash(gun.lastShot[0], 150, 7.5);
		GlStateManager.popMatrix();
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		double scale = 1.5D;
		GlStateManager.scale(scale, scale, scale);
		GlStateManager.translate(0, -2.5, -3.5);
		GlStateManager.rotate(180, 0, 1, 0);

	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		double scale = 1.0625D;
		GlStateManager.scale(scale, scale, scale);
		GlStateManager.rotate(25, 1, 0, 0);
		GlStateManager.rotate(225, 0, 1, 0);
		GlStateManager.translate(0.25, -2.5, 0);
	}

	@Override
	public void setupModTable(ItemStack stack) {
		double scale = -7.5D;
		GlStateManager.scale(scale, scale, scale);
		GlStateManager.rotate(-90, 0, 1, 0);
		GlStateManager.translate(0, -4, 0);
	}

	@Override
	public void renderOther(ItemStack stack, Object type) {
		GlStateManager.enableLighting();

		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		GlStateManager.enableCull();
		Minecraft.getMinecraft().renderEngine.bindTexture(ResourceManager.stinger_tex);
		ResourceManager.stinger.renderAll();
		GlStateManager.shadeModel(GL11.GL_FLAT);
	}
}

