package com.hbm.render.item.weapon.sedna;

import com.hbm.interfaces.AutoRegister;
import com.hbm.items.weapon.sedna.ItemGunBaseNT;
import com.hbm.main.ResourceManager;
import com.hbm.render.anim.sedna.HbmAnimationsSedna;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
@AutoRegister(item = "gun_aberrator")
public class ItemRenderAberrator extends ItemRenderWeaponBase {

	@Override
	protected float getTurnMagnitude(ItemStack stack) { return ItemGunBaseNT.getIsAiming(stack) ? 2.5F : -0.25F; }

	@Override
	public float getViewFOV(ItemStack stack, float fov) {
		float aimingProgress = ItemGunBaseNT.prevAimingProgress + (ItemGunBaseNT.aimingProgress - ItemGunBaseNT.prevAimingProgress) * interp;
		return  fov * (1 - aimingProgress * 0.33F);
	}

	@Override
	public void setupFirstPerson(ItemStack stack) {
		GlStateManager.translate(0, 0, 1);
		
		float offset = 0.8F;
		standardAimingTransform(stack,
				-1.0F * offset, -1.25F * offset, 1.25F * offset,
				0, -5.25 / 8D, 0.125);
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {

		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		Minecraft.getMinecraft().getTextureManager().bindTexture(ResourceManager.aberrator_tex);
		double scale = 0.25D;
		GlStateManager.scale(scale, scale, scale);

		double[] equip = HbmAnimationsSedna.getRelevantTransformation("EQUIP");
		double[] rise = HbmAnimationsSedna.getRelevantTransformation("RISE");
		double[] recoil = HbmAnimationsSedna.getRelevantTransformation("RECOIL");
		double[] slide = HbmAnimationsSedna.getRelevantTransformation("SLIDE");
		double[] bullet = HbmAnimationsSedna.getRelevantTransformation("BULLET");
		double[] hammer = HbmAnimationsSedna.getRelevantTransformation("HAMMER");
		double[] roll = HbmAnimationsSedna.getRelevantTransformation("ROLL");
		double[] mag = HbmAnimationsSedna.getRelevantTransformation("MAG");
		double[] magroll = HbmAnimationsSedna.getRelevantTransformation("MAGROLL");
		double[] sight = HbmAnimationsSedna.getRelevantTransformation("SIGHT");

		GlStateManager.translate(0, rise[1], 0);

		GlStateManager.translate(0, 1, -2.25);
		GlStateManager.rotate((float) equip[0], 1, 0, 0);
		GlStateManager.translate(0, -1, 2.25);

		GlStateManager.translate(0, -1, -4);
		GlStateManager.rotate((float) recoil[0], 1, 0, 0);
		GlStateManager.translate(0, 1, 4);

		GlStateManager.translate(0, 1, 0);
		GlStateManager.rotate((float) roll[2], 0, 0, 1);
		GlStateManager.translate(0, -1, 0);

		GlStateManager.shadeModel(GL11.GL_SMOOTH);

		ResourceManager.aberrator.renderPart("Gun");

		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 2.4375, -1.9375);
		GlStateManager.rotate((float) sight[0], 1, 0, 0);
		GlStateManager.translate(0, -2.4375, 1.9375);
		ResourceManager.aberrator.renderPart("Sight");
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.translate(mag[0], mag[1], mag[2]);
		GlStateManager.translate(0, 1, 0);
		GlStateManager.rotate((float) magroll[2], 0, 0, 1);
		GlStateManager.translate(0, -1, 0);
		ResourceManager.aberrator.renderPart("Magazine");
		GlStateManager.translate(bullet[0], bullet[1], bullet[2]);
		ResourceManager.aberrator.renderPart("Bullet");
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, slide[2]);
		ResourceManager.aberrator.renderPart("Slide");
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 1.25, -3.625);
		GlStateManager.rotate((float) (-45 + hammer[0]), 1, 0, 0);
		GlStateManager.translate(0, -1.25, 3.625);
		ResourceManager.aberrator.renderPart("Hammer");
		GlStateManager.popMatrix();

		double smokeScale = 0.5;

		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 2, 4);
		GlStateManager.rotate((float) recoil[0], -1, 0, 0);
		GlStateManager.rotate((float) roll[2], 0, 0, -1);
		GlStateManager.rotate(90F, 0, 1, 0);
		GlStateManager.scale(smokeScale, smokeScale, smokeScale);
		this.renderSmokeNodes(gun.getConfig(stack, 0).smokeNodes, 0.5D);
		GlStateManager.popMatrix();

		GlStateManager.shadeModel(GL11.GL_FLAT);

		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 2, 4);
		GlStateManager.rotate(90F, 0, 1, 0);
		GlStateManager.rotate(90F * gun.shotRand, 1, 0, 0);
		GlStateManager.scale(0.75, 0.75, 0.75);
		this.renderMuzzleFlash(gun.lastShot[0], 75, 7.5);
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 2, -1.5);
		GlStateManager.scale(0.5, 0.5, 0.5);
		this.renderFireball(gun.lastShot[0]);
		GlStateManager.popMatrix();

		IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(new ItemStack(Items.GOLDEN_SWORD), null, null);
		TextureAtlasSprite icon = !model.getQuads(null, null, 0).isEmpty() ? model.getQuads(null, null, 0).get(0).getSprite() : model.getParticleTexture();

		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		GlStateManager.disableCull();
		GlStateManager.disableLighting();

		float minU = icon.getMinU();
		float maxU = icon.getMaxU();
		float minV = icon.getMinV();
		float maxV = icon.getMaxV();

		GlStateManager.translate(0, 2, 4.5);
		GlStateManager.rotate((float) roll[2], 0, 0, -1);
		GlStateManager.rotate((float) recoil[0], -1, 0, 0);
		GlStateManager.rotate((float) equip[0], -1, 0, 0);
		GlStateManager.rotate((float) (System.currentTimeMillis() / 50D % 360D), 0, 0, 1);

		float aimingProgress = ItemGunBaseNT.prevAimingProgress +
				(ItemGunBaseNT.aimingProgress - ItemGunBaseNT.prevAimingProgress) * interp;
		aimingProgress = Math.min(1F, aimingProgress * 2);

		Tessellator tess = Tessellator.getInstance();
		BufferBuilder buffer = tess.getBuffer();

		GlStateManager.pushMatrix();
		int amount = 16;
		for (int i = 0; i < amount; i++) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(0, -1.5 - aimingProgress, 0);
			GlStateManager.rotate(90F * aimingProgress, 1, 0, 0);
			GlStateManager.rotate(-45F, 0, 0, 1);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
			buffer.pos(-0.5, -0.5F, -0.5).tex(maxU, maxV).normal(0F, 1F, 0F).endVertex();
			buffer.pos(0.5F, -0.5F, -0.5).tex(minU, maxV).normal(0F, 1F, 0F).endVertex();
			buffer.pos(0.5F, 0.5F, -0.5).tex(minU, minV).normal(0F, 1F, 0F).endVertex();
			buffer.pos(-0.5, 0.5F, -0.5).tex(maxU, minV).normal(0F, 1F, 0F).endVertex();
			tess.draw();
			GlStateManager.popMatrix();
			GlStateManager.rotate((float) (360D / amount), 0, 0, 1);
		}
		GlStateManager.popMatrix();
	}


	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		GlStateManager.translate(0, -1, 4);
		double scale = 1.5D;
		GlStateManager.scale(scale, scale, scale);
	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		double scale = 2.5D;
		GlStateManager.scale(scale, scale, scale);
		GlStateManager.rotate(25F, 1, 0, 0);
		GlStateManager.rotate(45F, 0, 1, 0);
		GlStateManager.translate(-0.5, -1, 0);
	}

	@Override
	public void renderOther(ItemStack stack, Object type) {
		GlStateManager.enableLighting();
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0F);
		GlStateManager.enableAlpha();

		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		Minecraft.getMinecraft().getTextureManager().bindTexture(ResourceManager.aberrator_tex);
		ResourceManager.aberrator.renderPart("Gun");
		ResourceManager.aberrator.renderPart("Hammer");
		ResourceManager.aberrator.renderPart("Magazine");
		ResourceManager.aberrator.renderPart("Slide");
		ResourceManager.aberrator.renderPart("Sight");
		GlStateManager.shadeModel(GL11.GL_FLAT);
	}

	public static void renderFireball(long lastShot) {
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder buff = tess.getBuffer();

		int flash = 150;

		if (System.currentTimeMillis() - lastShot < flash) {

			double fire = (System.currentTimeMillis() - lastShot) / (double) flash;
			double height = 5 * fire;
			double length = 10 * fire;
			double offset = 1 * fire;
			double lengthOffset = -1.125;

			Minecraft.getMinecraft().getTextureManager().bindTexture(flash_plume);
			beginFullbrightAdditive();

			buff.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

			buff.pos(height, -offset, 0).tex(0, 1).endVertex();
			buff.pos(-height, -offset, 0).tex(1, 1).endVertex();
			buff.pos(-height, -offset + length, -lengthOffset).tex(1, 0).endVertex();
			buff.pos(height, -offset + length, -lengthOffset).tex(0, 0).endVertex();

			buff.pos(height, -offset, 0).tex(0, 1).endVertex();
			buff.pos(-height, -offset, 0).tex(1, 1).endVertex();
			buff.pos(-height, -offset + length, lengthOffset).tex(1, 0).endVertex();
			buff.pos(height, -offset + length, lengthOffset).tex(0, 0).endVertex();

			tess.draw();

			endFullbrightAdditive();
		}
	}
}
