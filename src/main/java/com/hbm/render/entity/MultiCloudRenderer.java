package com.hbm.render.entity;

import com.hbm.entity.particle.*;
import com.hbm.interfaces.AutoRegister;
import com.hbm.items.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11;

import java.util.Random;
@AutoRegister(entity = EntityOrangeFX.class, factory = "FACTORY_1")
@AutoRegister(entity = EntityCloudFX.class, factory = "FACTORY_2")
@AutoRegister(entity = EntityPinkCloudFX.class, factory = "FACTORY_3")
@AutoRegister(entity = EntityChlorineFX.class, factory = "FACTORY_4")
@AutoRegister(entity = EntitySmokeFX.class, factory = "FACTORY_5")
@AutoRegister(entity = EntityBSmokeFX.class, factory = "FACTORY_6")
public class MultiCloudRenderer extends Render<EntityModFX> {

	public static final IRenderFactory<EntityOrangeFX> FACTORY_1 = (RenderManager man) -> {return new MultiCloudRenderer(new Item[]{ModItems.orange1, ModItems.orange2, ModItems.orange3, ModItems.orange4, ModItems.orange5, ModItems.orange6, ModItems.orange7, ModItems.orange8}, 0, man);};
	public static final IRenderFactory<EntityCloudFX> FACTORY_2 = (RenderManager man) -> {return new MultiCloudRenderer(new Item[]{ModItems.cloud1, ModItems.cloud2, ModItems.cloud3, ModItems.cloud4, ModItems.cloud5, ModItems.cloud6, ModItems.cloud7, ModItems.cloud8}, 0, man);};
	public static final IRenderFactory<EntityPinkCloudFX> FACTORY_3 = (RenderManager man) -> {return new MultiCloudRenderer(new Item[]{ModItems.pc1, ModItems.pc2, ModItems.pc3, ModItems.pc4, ModItems.pc5, ModItems.pc6, ModItems.pc7, ModItems.pc8}, 0, man);};
	public static final IRenderFactory<EntityChlorineFX> FACTORY_4 = (RenderManager man) -> {return new MultiCloudRenderer(new Item[]{ModItems.chlorine1, ModItems.chlorine2, ModItems.chlorine3, ModItems.chlorine4, ModItems.chlorine5, ModItems.chlorine6, ModItems.chlorine7, ModItems.chlorine8}, 0, man);};
	public static final IRenderFactory<EntitySmokeFX> FACTORY_5 = (RenderManager man) -> {return new MultiCloudRenderer(new Item[]{ModItems.smoke1, ModItems.smoke2, ModItems.smoke3, ModItems.smoke4, ModItems.smoke5, ModItems.smoke6, ModItems.smoke7, ModItems.smoke8}, 0, man);};
	public static final IRenderFactory<EntityBSmokeFX> FACTORY_6 = (RenderManager man) -> {return new MultiCloudRenderer(new Item[]{ModItems.b_smoke1, ModItems.b_smoke2, ModItems.b_smoke3, ModItems.b_smoke4, ModItems.b_smoke5, ModItems.b_smoke6, ModItems.b_smoke7, ModItems.b_smoke8}, 0, man);};

	private TextureAtlasSprite tex;
	private Item[] textureItems;
	private int meta;
	
	public MultiCloudRenderer(Item[] items, RenderManager renderManager) {
		super(renderManager);
		textureItems = items;
		meta = 0;
	}
	public MultiCloudRenderer(Item[] items, int m, RenderManager renderManager) {
		super(renderManager);
		textureItems = items;
		meta = m;
	}
	//Trash code, should probably fix later
	@Override
	public void doRender(EntityModFX fx, double x, double y, double z, float entityYaw, float partialTicks) {
		this.bindEntityTexture(fx);
		if (tex != null) {
			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GlStateManager.disableLighting();
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.translate((float) x, (float) y, (float) z);
			GlStateManager.enableRescaleNormal();
			GlStateManager.scale(0.5F, 0.5F, 0.5F);
			GlStateManager.scale(7.5F, 7.5F, 7.5F);
			
			////
			Random randy = new Random(fx.hashCode());
			////
			
			Random rand = new Random(100);
			
			for(int i = 0; i < 5; i++) {
				
				float d = randy.nextInt(10) * 0.05F;
				GlStateManager.color(1 - d, 1 - d, 1 - d);

				double dX = (rand.nextGaussian() - 1D) * 0.15D;
				double dY = (rand.nextGaussian() - 1D) * 0.15D;
				double dZ = (rand.nextGaussian() - 1D) * 0.15D;
				double size = rand.nextDouble() * 0.5D + 0.25D;
				
				GlStateManager.translate((float) dX, (float) dY, (float) dZ);
				GL11.glScaled(size, size, size);

				GlStateManager.pushMatrix();
				Tessellator tessellator = Tessellator.getInstance();
				this.render(tessellator, tex);
				GlStateManager.popMatrix();

				GL11.glScaled(1/size, 1/size, 1/size);
				GlStateManager.translate((float) -dX, (float) -dY, (float) -dZ);
			}
			
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.enableLighting();
			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityModFX fx) {
		Item item = textureItems[0];
		
		if (fx.particleAge <= fx.maxAge && fx.particleAge >= fx.maxAge / 8 * 7) {
			item = textureItems[7];
		}

		if (fx.particleAge < fx.maxAge / 8 * 7 && fx.particleAge >= fx.maxAge / 8 * 6) {
			item = textureItems[6];
		}

		if (fx.particleAge < fx.maxAge / 8 * 6 && fx.particleAge >= fx.maxAge / 8 * 5) {
			item = textureItems[5];
		}

		if (fx.particleAge < fx.maxAge / 8 * 5 && fx.particleAge >= fx.maxAge / 8 * 4) {
			item = textureItems[4];
		}

		if (fx.particleAge < fx.maxAge / 8 * 4 && fx.particleAge >= fx.maxAge / 8 * 3) {
			item = textureItems[3];
		}

		if (fx.particleAge < fx.maxAge / 8 * 3 && fx.particleAge >= fx.maxAge / 8 * 2) {
			item = textureItems[2];
		}

		if (fx.particleAge < fx.maxAge / 8 * 2 && fx.particleAge >= fx.maxAge / 8 * 1) {
			item = textureItems[1];
		}

		if (fx.particleAge < fx.maxAge / 8 && fx.particleAge >= 0) {
			item = textureItems[0];
		}
		tex = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(new ItemStack(item, 1, meta), null, null).getParticleTexture();
		return TextureMap.LOCATION_BLOCKS_TEXTURE;
	}

	private void render(Tessellator tes, TextureAtlasSprite tas) {
		BufferBuilder buf = tes.getBuffer();
		float f = tas.getMinU();
		float f1 = tas.getMaxU();
		float f2 = tas.getMinV();
		float f3 = tas.getMaxV();
		float f4 = 1.0F;
		float f5 = 0.5F;
		float f6 = 0.25F;
		GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
		buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		buf.pos(0.0F - f5, 0.0F - f6, 0.0D).tex(f, f3).endVertex();
		buf.pos(f4 - f5, 0.0F - f6, 0.0D).tex(f1, f3).endVertex();
		buf.pos(f4 - f5, f4 - f6, 0.0D).tex(f1, f2).endVertex();
		buf.pos(0.0F - f5, f4 - f6, 0.0D).tex(f, f2).endVertex();
		tes.draw();
	}
}
