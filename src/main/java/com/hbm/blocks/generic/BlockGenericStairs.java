package com.hbm.blocks.generic;

import com.google.common.collect.ImmutableMap;
import com.hbm.blocks.ModBlocks;
import com.hbm.items.IDynamicModels;
import com.hbm.render.block.BlockBakeFrame;
import com.hbm.util.I18nUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
// note: that system..
// 1. is a clusterfuck, I know
// 2. it works, however it causes "not found model" exceptions while launching the game - probably FIXME?
// that doesn't really affect the model in the game, though

// FIXME: it completely ignores connection from TWO sides when placing - it always takes the direction of the player when placing
public class BlockGenericStairs extends BlockStairs implements IDynamicModels {

	public static final List<Object[]> recipeGen = new ArrayList<>();

	protected BlockBakeFrame blockFrame;


	public BlockGenericStairs(IBlockState modelState, String s) {
		super(modelState);
		this.setTranslationKey(s);
		this.setRegistryName(s);
		this.useNeighborBrightness = true;

		ModBlocks.ALL_BLOCKS.add(this);
	}

	public BlockGenericStairs(Block block, String registryName, String texture, int recipeMeta) {
		this(block.getDefaultState(), registryName);

		this.blockFrame = new BlockBakeFrame(texture) {
			@Override
			public void putTextures(ImmutableMap.Builder<String, String> builder) {
				String sprite = this.getSpriteLoc(0).toString();
				builder.put("bottom", sprite);
				builder.put("top", sprite);
				builder.put("side", sprite);
				builder.put("particle", sprite);
			}
		};

		IDynamicModels.INSTANCES.add(this);
		if (recipeMeta >= 0) {
			recipeGen.add(new Object[]{block, recipeMeta, this});
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModel() {
		ModelLoader.setCustomStateMapper(this, getStateMapper(this.getRegistryName()));
		ModelLoader.setCustomModelResourceLocation(
				Item.getItemFromBlock(this),
				0,
				new ModelResourceLocation(this.getRegistryName(), "meta=0")
		);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerSprite(TextureMap map) {
		if (blockFrame == null) return;
		blockFrame.registerBlockTextures(map);
	}

	private static ModelRotation rotationFor(EnumFacing facing, EnumHalf half) {
		int y;
		switch (facing) {
			case EAST: y = 0; break;
			case SOUTH: y = 90; break;
			case WEST: y = 180; break;
			default: y = 270; // NORTH
		}
		int x = (half == EnumHalf.TOP) ? 180 : 0;

		if (x == 0 && y == 0) return ModelRotation.X0_Y0;
		if (x == 0 && y == 90) return ModelRotation.X0_Y90;
		if (x == 0 && y == 180) return ModelRotation.X0_Y180;
		if (x == 0 && y == 270) return ModelRotation.X0_Y270;
		if (x == 180 && y == 0) return ModelRotation.X180_Y0;
		if (x == 180 && y == 90) return ModelRotation.X180_Y90;
		if (x == 180 && y == 180) return ModelRotation.X180_Y180;
		if (x == 180 && y == 270) return ModelRotation.X180_Y270;
		return ModelRotation.X0_Y0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void bakeModel(ModelBakeEvent event) {
		IModel baseStraight;
		IModel baseInner;
		IModel baseOuter;
		try {
			baseStraight = ModelLoaderRegistry.getModel(new ResourceLocation("minecraft:block/stairs"));
			baseInner = ModelLoaderRegistry.getModel(new ResourceLocation("minecraft:block/inner_stairs"));
			baseOuter = ModelLoaderRegistry.getModel(new ResourceLocation("minecraft:block/outer_stairs"));
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		try {
			ImmutableMap.Builder<String, String> textureMap = ImmutableMap.builder();
			blockFrame.putTextures(textureMap);

			IModel straight = baseStraight.retexture(textureMap.build());
			IModel inner = baseInner.retexture(textureMap.build());
			IModel outer = baseOuter.retexture(textureMap.build());

			for (EnumHalf half : EnumHalf.values()) {
				for (EnumFacing facing : new EnumFacing[]{EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST}) {
					ModelRotation rot = rotationFor(facing, half);

					IBakedModel bakedStraight = straight.bake(rot, DefaultVertexFormats.BLOCK, ModelLoader.defaultTextureGetter());
					IBakedModel bakedInner = inner.bake(rot, DefaultVertexFormats.BLOCK, ModelLoader.defaultTextureGetter());
					IBakedModel bakedOuter = outer.bake(rot, DefaultVertexFormats.BLOCK, ModelLoader.defaultTextureGetter());

					ModelResourceLocation mrlStraight = new ModelResourceLocation(getRegistryName(),
							"half=" + half.getName() + ",facing=" + facing.getName() + ",shape=straight");
					ModelResourceLocation mrlInnerL = new ModelResourceLocation(getRegistryName(),
							"half=" + half.getName() + ",facing=" + facing.getName() + ",shape=inner_left");
					ModelResourceLocation mrlInnerR = new ModelResourceLocation(getRegistryName(),
							"half=" + half.getName() + ",facing=" + facing.getName() + ",shape=inner_right");
					ModelResourceLocation mrlOuterL = new ModelResourceLocation(getRegistryName(),
							"half=" + half.getName() + ",facing=" + facing.getName() + ",shape=outer_left");
					ModelResourceLocation mrlOuterR = new ModelResourceLocation(getRegistryName(),
							"half=" + half.getName() + ",facing=" + facing.getName() + ",shape=outer_right");

					event.getModelRegistry().putObject(mrlStraight, bakedStraight);
					event.getModelRegistry().putObject(mrlInnerL, bakedInner);
					event.getModelRegistry().putObject(mrlInnerR, bakedInner);
					event.getModelRegistry().putObject(mrlOuterL, bakedOuter);
					event.getModelRegistry().putObject(mrlOuterR, bakedOuter);
				}
			}

			IBakedModel bakedItem = straight.bake(ModelRotation.X0_Y0, DefaultVertexFormats.BLOCK, ModelLoader.defaultTextureGetter());
			ModelResourceLocation itemMrl = new ModelResourceLocation(getRegistryName(), "meta=0");
			event.getModelRegistry().putObject(itemMrl, bakedItem);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public StateMapperBase getStateMapper(net.minecraft.util.ResourceLocation loc) {
		return new StateMapperBase() {
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				EnumFacing facing = state.getValue(FACING);
				EnumHalf half = state.getValue(HALF);
				EnumShape shape = state.getValue(SHAPE);
				return new ModelResourceLocation(loc,
						"half=" + half.getName() + ",facing=" + facing.getName() + ",shape=" + shape.getName());
			}
		};
	}

	@Override
	public void addInformation(ItemStack stack, World player, List<String> tooltip, ITooltipFlag advanced) {
		float hardness = this.getExplosionResistance(null);
		if(hardness > 50){
			tooltip.add("§6" + I18nUtil.resolveKey("trait.blastres", hardness));
		}
	}
}
