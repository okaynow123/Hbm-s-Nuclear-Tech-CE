package com.hbm.blocks.machine;

import com.hbm.blocks.BlockBase;
import com.hbm.blocks.ModBlocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
// I'm not sure if we even need this..
public class MachineTransformer extends BlockBase {

	public MachineTransformer(Material mat, String s, long max, boolean pointingUp) {
		super(mat);
		this.setTranslationKey(s);
		this.setRegistryName(s);
		ModBlocks.ALL_BLOCKS.add(this);
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}
}
