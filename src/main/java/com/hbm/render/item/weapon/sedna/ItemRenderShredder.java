package com.hbm.render.item.weapon.sedna;

import com.hbm.items.ModItems;
import com.hbm.items.weapon.sedna.ItemGunBaseNT;
import com.hbm.main.ResourceManager;
import com.hbm.render.anim.sedna.HbmAnimationsSedna;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ItemRenderShredder extends ItemRenderWeaponBase {

	protected ResourceLocation texture;

	public ItemRenderShredder(ResourceLocation texture) {
		this.texture = texture;
	}

	@Override
	protected float getTurnMagnitude(ItemStack stack) {
		return ItemGunBaseNT.getIsAiming(stack) ? 2.5F : -0.5F;
	}

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
				-1.5F * offset, -1.25F * offset, 1.5F * offset,
				0, -6.25 / 8D, 0.5);
	}

	protected static String label = "[> <]";

	@Override
	public void renderFirstPerson(ItemStack stack) {

		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		EntityPlayer player = Minecraft.getMinecraft().player;
		double scale = 0.25D;
		GlStateManager.scale(scale, scale, scale);

		double[] equip = HbmAnimationsSedna.getRelevantTransformation("EQUIP");
		double[] lift = HbmAnimationsSedna.getRelevantTransformation("LIFT");
		double[] recoil = HbmAnimationsSedna.getRelevantTransformation("RECOIL");
		double[] mag = HbmAnimationsSedna.getRelevantTransformation("MAG");
		double[] speen = HbmAnimationsSedna.getRelevantTransformation("SPEEN");
		double[] cycle = HbmAnimationsSedna.getRelevantTransformation("CYCLE");

		GlStateManager.translate(0, -2, -6);
		GlStateManager.rotate((float) equip[0], 1, 0, 0);
		GlStateManager.translate(0, 2, 6);

		GlStateManager.translate(0, 0, -4);
		GlStateManager.rotate((float) lift[0], 1, 0, 0);
		GlStateManager.translate(0, 0, 4);

		GlStateManager.translate(0, 0, recoil[2]);

		GlStateManager.shadeModel(GL11.GL_SMOOTH);

		boolean sexy = stack.getItem() == ModItems.gun_autoshotgun_sexy;

		if(sexy || (gun.prevAimingProgress >= 1F && gun.aimingProgress >= 1F)) {

			GlStateManager.pushMatrix();
			GlStateManager.pushAttrib();
			GlStateManager.disableLighting();
			GlStateManager.disableCull();
			OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
			FontRenderer font = Minecraft.getMinecraft().fontRenderer;
			float f3 = 0.04F;
			GlStateManager.translate((font.getStringWidth(label) / 2) * f3, 3.25F, -1.75F);
			GlStateManager.scale(f3, -f3, f3);
			GlStateManager.rotate(180F, 0, 1, 0);
			GlStateManager.glNormal3f(0.0F, 0.0F, -1.0F * f3);
			float variance = 0.9F + player.getRNG().nextFloat() * 0.1F;
			font.drawString(label, 0, 0, new Color(sexy ? variance : 0F, sexy ? 0F : variance, 0F).getRGB());
			GlStateManager.color(1F, 1F, 1F);

			GlStateManager.enableLighting();
			GlStateManager.popAttrib();
			GlStateManager.popMatrix();

			int brightness = player.world.getCombinedLight(new BlockPos(player.posX, player.posY, player.posZ), 0);
			int j = brightness % 65536;
			int k = brightness / 65536;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);
		}

		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);

		ResourceManager.shredder.renderPart("Gun");

		GlStateManager.pushMatrix();
		GlStateManager.translate(mag[0], mag[1], mag[2]);
		GlStateManager.translate(0, -1, -0.5);
		GlStateManager.rotate((float) speen[0], 1, 0, 0);
		GlStateManager.translate(0, 1, 0.5);
		ResourceManager.shredder.renderPart("Magazine");
		GlStateManager.translate(0, -1, -0.5);
		GlStateManager.rotate((float) cycle[2], 0, 0, 1);
		GlStateManager.translate(0, 1, 0.5);
		ResourceManager.shredder.renderPart("Shells");
		GlStateManager.popMatrix();

		double smokeScale = 0.75;

		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 1, 7.5);
		GlStateManager.rotate(90, 0, 1, 0);
		GlStateManager.scale(smokeScale, smokeScale, smokeScale);
		this.renderSmokeNodes(gun.getConfig(stack, 0).smokeNodes, 0.5D);
		GlStateManager.popMatrix();

		GlStateManager.shadeModel(GL11.GL_FLAT);

		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 1, 7.5);
		GlStateManager.rotate(90, 0, 1, 0);
		GlStateManager.rotate(gun.shotRand * 90, 1, 0, 0);
		GlStateManager.scale(0.75, 0.75, 0.75);
		this.renderMuzzleFlash(gun.lastShot[0], 75, 7.5);
		GlStateManager.popMatrix();
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		double scale = 1.5D;
		GlStateManager.scale(scale, scale, scale);
		GlStateManager.translate(0, 0.5, 4);
	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		double scale = 1.25D;
		GlStateManager.scale(scale, scale, scale);
		GlStateManager.rotate(25, 1, 0, 0);
		GlStateManager.rotate(45, 0, 1, 0);
		GlStateManager.translate(-1.5, 0, 0);
	}

	@Override
	public void renderOther(ItemStack stack, Object type) {
		GlStateManager.enableLighting();

		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		ResourceManager.shredder.renderAll();
		GlStateManager.shadeModel(GL11.GL_FLAT);
	}
}

