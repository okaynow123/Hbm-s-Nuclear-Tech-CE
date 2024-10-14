package com.hbm.tileentity.machine;

import api.hbm.energymk2.IEnergyReceiverMK2;
import api.hbm.fluid.IFluidStandardTransceiver;
import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.machine.MachineBoiler;
import com.hbm.inventory.MachineRecipes;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTank;
import com.hbm.lib.ForgeDirection;
import com.hbm.lib.Library;
import com.hbm.packet.AuxElectricityPacket;
import com.hbm.packet.AuxGaugePacket;
import com.hbm.packet.PacketDispatcher;
import com.hbm.tileentity.TileEntityMachineBase;

import api.hbm.energymk2.IBatteryItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class TileEntityMachineBoilerElectric extends TileEntityMachineBase implements ITickable, IFluidStandardTransceiver, IEnergyReceiverMK2 {

	public long power;
	public int heat = 2000;
	public static final long maxPower = 10000;
	public static final int maxHeat = 80000;
	public FluidTank[] tanks;

	private static final int[] slots_top = new int[] {4};
	private static final int[] slots_bottom = new int[] {6};
	private static final int[] slots_side = new int[] {4};

	public TileEntityMachineBoilerElectric() {
		super(7);
		tanks = new FluidTank[2];
		tanks[0] = new FluidTank(Fluids.OIL, 16000, 0);
		tanks[1] = new FluidTank(Fluids.HOTOIL, 16000, 1);
	}
	
	@Override
	public String getName(){
		return "container.machineElectricBoiler";
	}

	public boolean isUseableByPlayer(EntityPlayer player) {
		if(world.getTileEntity(pos) != this) {
			return false;
		} else {
			return player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64;
		}
	}

	@Override
	public int[] getAccessibleSlotsFromSide(EnumFacing e){
		int i = e.ordinal();
		return i == 0 ? slots_bottom : (i == 1 ? slots_top : slots_side);
	}
	
	@Override
	public boolean canInsertItem(int slot, ItemStack itemStack, int amount){
		return this.isItemValidForSlot(slot, itemStack);
	}
	
	@Override
	public boolean canExtractItem(int slot, ItemStack itemStack, int amount){
		return false;
	}
	
	public boolean isItemValidForSlot(int i, ItemStack stack) {
		if(i == 4)
			if(stack != null && stack.getItem() instanceof IBatteryItem)
				return true;
		return false;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		heat = nbt.getInteger("heat");
		power = nbt.getLong("power");
		if(nbt.hasKey("inventory"))
			inventory.deserializeNBT(nbt.getCompoundTag("inventory"));
		tanks[0].readFromNBT(nbt, "water");
		tanks[1].readFromNBT(nbt, "steam");
		super.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("heat", heat);
		nbt.setLong("power", power);
		nbt.setTag("inventory", inventory.serializeNBT());
		tanks[0].writeToNBT(nbt, "water");
		tanks[1].writeToNBT(nbt, "steam");
		return super.writeToNBT(nbt);
	}

	public int getHeatScaled(int i) {
		return (heat * i) / maxHeat;
	}

	public long getPowerScaled(int i) {
		return (power * i) / maxPower;
	}

	@Override
	public void update() {
		boolean mark = false;

		if(!world.isRemote) {
			for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
				this.trySubscribe(world, pos.getX() + dir.offsetX, pos.getY() + dir.offsetY, pos.getZ() + dir.offsetZ, dir);
			this.subscribeToAllAround(tanks[0].getTankType(), this);
			this.sendFluidToAll(tanks[1], this);

			power = Library.chargeTEFromItems(inventory, 4, power, maxPower);

			tanks[0].setType(0, 1, inventory);
			tanks[0].loadTank(2, 3, inventory);

			Object[] outs = MachineRecipes.getBoilerOutput(tanks[0].getTankType());

			if(outs == null) {
				tanks[1].setTankType(Fluids.NONE);
			} else {
				tanks[1].setTankType((FluidType) outs[0]);
			}

			if(heat > 2000) {
				heat -= 30;
			}

			if(power > 0) {
				heat += Math.min(((double) power / (double) maxPower * 300), 150);
				power = Math.max(power-150, 0);
			} else {
				heat -= 100;
			}

			if(power <= 0 && world.getBlockState(pos).getBlock() == ModBlocks.machine_boiler_electric_on) {
				power = 0;
				MachineBoiler.updateBlockState(false, world, pos);
			}

			if(heat > maxHeat)
				heat = maxHeat;

			if(power > 0 && world.getBlockState(pos).getBlock() == ModBlocks.machine_boiler_electric_off) {
				MachineBoiler.updateBlockState(true, world, pos);
				mark = true;
			}

			if(outs != null) {

				for(int i = 0; i < (heat / ((Integer)outs[3]).intValue()); i++) {
					if(tanks[0].getFill() >= ((Integer)outs[2]).intValue() && tanks[1].getFill() + ((Integer)outs[1]).intValue() <= tanks[1].getMaxFill()) {
						tanks[0].setFill(tanks[0].getFill() - ((Integer)outs[2]).intValue());
						tanks[1].setFill(tanks[1].getFill() + ((Integer)outs[1]).intValue());

						if(i == 0)
							heat -= 35;
						else
							heat -= 50;
					}
				}
			}

			if(heat < 2000) {
				heat = 2000;
			}

			PacketDispatcher.wrapper.sendToAllAround(new AuxElectricityPacket(pos.getX(), pos.getY(), pos.getZ(), power), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 50));
			PacketDispatcher.wrapper.sendToAllAround(new AuxGaugePacket(pos.getX(), pos.getY(), pos.getZ(), heat, 0), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 50));
		}
		if(mark) {
			this.markDirty();
		}
	}

	@Override
	public void setPower(long i) {
		power = i;
	}

	@Override
	public long getPower() {
		return power;

	}

	@Override
	public long getMaxPower() {
		return maxPower;
	}

	@Override
	public FluidTank[] getSendingTanks() {
		return new FluidTank[] {tanks[1]};
	}

	@Override
	public FluidTank[] getReceivingTanks() {
		return new FluidTank[] {tanks[0]};
	}

	@Override
	public FluidTank[] getAllTanks() {
		return tanks;
	}

}
