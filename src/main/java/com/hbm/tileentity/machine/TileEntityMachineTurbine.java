package com.hbm.tileentity.machine;

import api.hbm.energymk2.IEnergyProviderMK2;
import api.hbm.fluid.IFluidStandardTransceiver;
import com.hbm.inventory.container.ContainerMachineTurbine;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTank;
import com.hbm.inventory.fluid.trait.FT_Coolable;
import com.hbm.inventory.gui.GUIMachineTurbine;
import com.hbm.items.ModItems;
import com.hbm.lib.ForgeDirection;
import com.hbm.lib.Library;
import com.hbm.packet.AuxElectricityPacket;
import com.hbm.packet.PacketDispatcher;
import com.hbm.tileentity.IGUIProvider;
import com.hbm.tileentity.TileEntityLoadedBase;

import api.hbm.energymk2.IBatteryItem;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;

public class TileEntityMachineTurbine extends TileEntityLoadedBase implements ITickable, IEnergyProviderMK2, IFluidStandardTransceiver, IGUIProvider {

	public ItemStackHandler inventory;

	public long power;
	public static final long maxPower = 1000000;
	public int age = 0;
	public FluidTank[] tanks;
	//Drillgon200: Not even used but I'm too lazy to remove them

	// private static final int[] slots_top = new int[] {4};
	// private static final int[] slots_bottom = new int[] {6};
	// private static final int[] slots_side = new int[] {4};

	private String customName;

	public TileEntityMachineTurbine() {
		inventory = new ItemStackHandler(7) {
			@Override
			protected void onContentsChanged(int slot) {
				super.onContentsChanged(slot);
				markDirty();
			}

			@Override
			public boolean isItemValid(int slot, ItemStack stack) {
				if(slot == 0)
					return stack != null && stack.getItem() == ModItems.forge_fluid_identifier;
				if(slot == 4)
					if(stack != null && stack.getItem() instanceof IBatteryItem)
						return true;

				return slot != 4 && stack != null;
			}

			@Override
			public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
				if(this.isItemValid(slot, stack))
					return super.insertItem(slot, stack, simulate);
				return ItemStack.EMPTY;
			}
		};
		tanks = new FluidTank[2];
		tanks[0] = new FluidTank(Fluids.STEAM, 64000, 0);
		tanks[1] = new FluidTank(Fluids.SPENTSTEAM, 128000, 1);
	}

	@Override
	public void update() {
		if(!world.isRemote) {

			age++;
			if(age >= 2) {
				age = 0;
			}

			this.subscribeToAllAround(tanks[0].getTankType(), this);

			for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
				this.tryProvide(world, pos.getX() + dir.offsetX, pos.getY() + dir.offsetY, pos.getZ() + dir.offsetZ, dir);

			tanks[0].setType(0, 1, inventory);
			tanks[0].loadTank(2, 3, inventory);
			power = Library.chargeItemsFromTE(inventory, 4, power, maxPower);

			FluidType in = tanks[0].getTankType();
			boolean valid = false;
			if(in.hasTrait(FT_Coolable.class)) {
				FT_Coolable trait = in.getTrait(FT_Coolable.class);
				double eff = trait.getEfficiency(FT_Coolable.CoolingType.TURBINE) * 0.85D; //small turbine is only 85% efficient
				if(eff > 0) {
					tanks[1].setTankType(trait.coolsTo);
					int inputOps = tanks[0].getFill() / trait.amountReq;
					int outputOps = (tanks[1].getMaxFill() - tanks[1].getFill()) / trait.amountProduced;
					int cap = 6_000 / trait.amountReq;
					int ops = Math.min(inputOps, Math.min(outputOps, cap));
					tanks[0].setFill(tanks[0].getFill() - ops * trait.amountReq);
					tanks[1].setFill(tanks[1].getFill() + ops * trait.amountProduced);
					this.power += (ops * trait.heatEnergy * eff);
					valid = true;
				}
			}
			if(!valid) tanks[1].setTankType(Fluids.NONE);
			if(power > maxPower) power = maxPower;

			this.sendFluidToAll(tanks[1], this);

			tanks[1].unloadTank(5, 6, inventory);

			for(int i = 0; i < 2; i++)
				tanks[i].updateTank(pos.getX(), pos.getY(), pos.getZ(), world.provider.getDimension());

			PacketDispatcher.wrapper.sendToAllAround(new AuxElectricityPacket(pos.getX(), pos.getY(), pos.getZ(), power), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 50));
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		tanks[0].readFromNBT(nbt, "water");
		tanks[1].readFromNBT(nbt, "steam");
		power = nbt.getLong("power");

		NBTTagList list = nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND);

		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound nbt1 = list.getCompoundTagAt(i);
			int slot = nbt1.getByte("Slot") & 255;
			if (slot >= 0 && slot < inventory.getSlots()) {
				inventory.setStackInSlot(slot, new ItemStack(nbt1));
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		tanks[0].writeToNBT(nbt, "water");
		tanks[1].writeToNBT(nbt, "steam");
		nbt.setLong("power", power);

		NBTTagList list = new NBTTagList();
		for (int i = 0; i < inventory.getSlots(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (!stack.isEmpty()) {
				NBTTagCompound nbt1 = new NBTTagCompound();
				nbt1.setByte("Slot", (byte)i);
				stack.writeToNBT(nbt1);
				list.appendTag(nbt1);
			}
		}
		nbt.setTag("Items", list);
		return nbt;
	}

	public String getInventoryName() {
		return this.hasCustomInventoryName() ? this.customName : "container.machineTurbine";
	}

	public boolean hasCustomInventoryName() {
		return this.customName != null && this.customName.length() > 0;
	}

	public void setCustomName(String name) {
		this.customName = name;
	}

	public boolean isUseableByPlayer(EntityPlayer player) {
		if(world.getTileEntity(pos) != this) {
			return false;
		} else {
			return player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64;
		}
	}

	public long getPowerScaled(int i) {
		return (power * i) / maxPower;
	}

	private long detectPower;
	private FluidTank[] detectTanks = new FluidTank[] { null, null };
	private Fluid[] detectFluids = new Fluid[] { null, null };

	@Override
	public long getPower() {
		return power;
	}

	@Override
	public void setPower(long i) {
		power = i;
	}

	@Override
	public long getMaxPower() {
		return maxPower;
	}

	@Override
	public Container provideContainer(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return new ContainerMachineTurbine(player.inventory, this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen provideGUI(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return new GUIMachineTurbine(player.inventory, this);
	}

	@Override
	public FluidTank[] getSendingTanks() {
		return new FluidTank[] { tanks[1] };
	}

	@Override
	public FluidTank[] getReceivingTanks() {
		return new FluidTank[] { tanks[0] };
	}

	@Override
	public FluidTank[] getAllTanks() {
		return tanks;
	}
}
