package com.hbm.blocks.machine;

import com.hbm.api.energymk2.IEnergyConnectorBlock;
import com.hbm.blocks.ITooltipProvider;
import com.hbm.blocks.generic.BlockBakeBase;
import com.hbm.lib.ForgeDirection;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;
// TODO: implement proper rotatable models, for now it can't be rotated properly as the model is just a pillar.. and I want it to be baked not .json-ified
// you can copypaste facing shit from MachineCapacitor probably
public class MachineCapacitorBus extends BlockBakeBase implements IEnergyConnectorBlock, ITooltipProvider {


    public MachineCapacitorBus(String s) {
        super(Material.IRON, s, "capacitor_bus_out", "capacitor_bus_side");
    }

    @Override
    public boolean canConnect(IBlockAccess world, BlockPos pos, ForgeDirection dir) {
        int meta = world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos));
        ForgeDirection busDir = ForgeDirection.getOrientation(meta);
        return dir == busDir;
    }

    @Override
    public void addInformation(ItemStack stack, World player, List<String> tooltip, ITooltipFlag advanced) {
        this.addStandardInfo(tooltip);
    }

}
