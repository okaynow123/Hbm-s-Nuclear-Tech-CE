package com.hbm.render.item;

import com.hbm.interfaces.AutoRegister;
import com.hbm.items.special.ItemBedrockOre;
import com.hbm.render.NTMRenderHelper;
import com.hbm.util.BobMathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
@AutoRegister(item = "ore_bedrock", constructorArgs = {"0x575757", "0.2F"})
@AutoRegister(item = "ore_bedrock_centrifuged", constructorArgs = {"0x676767", "0.25F"})
@AutoRegister(item = "ore_bedrock_cleaned", constructorArgs = {"0x8E8E5B", "0.3F"})
@AutoRegister(item = "ore_bedrock_separated", constructorArgs = {"0x7B7B7B", "0.35F"})
@AutoRegister(item = "ore_bedrock_deepcleaned", constructorArgs = {"0x9A9D76", "0.4F"})
@AutoRegister(item = "ore_bedrock_purified", constructorArgs = {"0x858689", "0.5F"})
@AutoRegister(item = "ore_bedrock_nitrated", constructorArgs = {"0x95795A", "0.6F"})
@AutoRegister(item = "ore_bedrock_nitrocrystalline", constructorArgs = {"0x79797F", "0.7F"})
@AutoRegister(item = "ore_bedrock_seared", constructorArgs = {"0xAAACAF", "0.8F"})
@AutoRegister(item = "ore_bedrock_exquisite", constructorArgs = {"0x797D81", "0.9F"})
@AutoRegister(item = "ore_bedrock_perfect", constructorArgs = {"0x6C6E70", "1F"})
@AutoRegister(item = "ore_bedrock_enriched", constructorArgs = {"0x55595D", "1F"})
public class ItemRendererBedrockOre extends TEISRBase {

	public static final double HALF_A_PIXEL = 0.03125;
	public static final double PIX = 0.0625;
	public TextureAtlasSprite layerTex;

	private int dirtyColor = 0;
	private float cleanliness = 0F;

	public ItemRendererBedrockOre(int dirtyColor, float cleanliness){
		this.dirtyColor = dirtyColor;
		this.cleanliness = cleanliness;
	}

	@Override
	public void renderByItem(ItemStack stack) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.5, 0.5, 0.5);
		Minecraft.getMinecraft().getRenderItem().renderItem(stack, itemModel);
		if(stack.getItem() instanceof ItemBedrockOre){
			ItemBedrockOre oreItem = (ItemBedrockOre)stack.getItem();
			int color = BobMathUtil.interpolateColor(this.dirtyColor, ItemBedrockOre.getColor(stack), this.cleanliness);
			
			if(layerTex == null){
				layerTex = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("hbm:items/ore_bedrock_layer");
			}

			GlStateManager.translate(-0.5, -0.5, -HALF_A_PIXEL);
			NTMRenderHelper.setColor(color);
        	NTMRenderHelper.startDrawingTexturedQuads();
			NTMRenderHelper.drawFullTexture(layerTex, 0, 0, 1, 1, 0, false);
			NTMRenderHelper.drawFullTexture(layerTex, 0, 0, 1, 1, PIX, true);
			NTMRenderHelper.draw();
        	NTMRenderHelper.resetColor();
		}
		GlStateManager.popMatrix();
		super.renderByItem(stack);
	}
}
