package com.hbm.blocks.network;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ITooltipProvider;
import com.hbm.tileentity.deco.TileEntitySpinnyLight;
import com.hbm.tileentity.network.energy.TileEntityPylonBase;
import com.hbm.tileentity.network.energy.TileEntityPylonMedium;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

public class PylonMedium extends BlockDummyable implements ITooltipProvider {

	public PylonMedium(Material mat, String s) {
		super(mat, s);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {

		if (meta >= 12) return new TileEntityPylonMedium();
		return null;
	}

	@Override
	public void addInformation(ItemStack stack, World player, List<String> tooltip, ITooltipFlag advanced) {
		tooltip.add("§6" + "Connection Type: " + "§e" + "Triple");
		tooltip.add("§6" + "Connection Range: " + "§e" + "45m");
	}

	@Override
	public int[] getDimensions() {
		return new int[]{6, 0, 0, 0, 0, 0};
	}

	@Override
	public int getOffset() {
		return 0;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityPylonBase) ((TileEntityPylonBase) te).disconnectAll();
		super.breakBlock(world, pos, state);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (world.isRemote) {
			return true;
		} else if (!player.isSneaking()) {
			int[] pos1 = this.findCore(world, pos.getX(), pos.getY(), pos.getZ());
			TileEntityPylonBase te = (TileEntityPylonBase) world.getTileEntity(new BlockPos(pos1[0], pos1[1], pos1[2]));
			int[] ores = OreDictionary.getOreIDs(player.getHeldItem(hand));
			for (int ore : ores) {
				String name = OreDictionary.getOreName(ore);
				//Why are these ones named differently
				if (name.equals("dyeLightBlue"))
					name = "dyeLight_Blue";
				if (name.equals("dyeLightGray"))
					name = "dyeSilver";
				if (name.length() > 3 && name.startsWith("dye")) {
					try {
						EnumDyeColor color = EnumDyeColor.valueOf(name.substring(3, name.length()).toUpperCase());
						TileEntitySpinnyLight ent = (TileEntitySpinnyLight) world.getTileEntity(pos);
						ent.color = color;
						ent.markDirty();
						world.notifyBlockUpdate(pos, state, state, 2 | 4);
						if (!player.isCreative())
							player.getHeldItem(hand).shrink(1);
						return true;
					} catch (IllegalArgumentException e) {
					}
				}
			}
		}
			return super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
	}
}
