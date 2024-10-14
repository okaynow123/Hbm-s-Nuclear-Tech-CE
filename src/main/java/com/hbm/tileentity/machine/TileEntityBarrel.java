package com.hbm.tileentity.machine;

import api.hbm.fluid.*;
import com.hbm.blocks.ModBlocks;
import com.hbm.forgefluid.FFUtils;
import com.hbm.forgefluid.FluidTypeHandler;
import com.hbm.interfaces.IFluidAcceptor;
import com.hbm.interfaces.IFluidSource;
import com.hbm.interfaces.ITankPacketAcceptor;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTank;
import com.hbm.inventory.fluid.trait.FT_Corrosive;
import com.hbm.inventory.fluid.trait.FluidTrait;
import com.hbm.lib.DirPos;
import com.hbm.lib.Library;
import com.hbm.packet.FluidTankPacket;
import com.hbm.packet.PacketDispatcher;
import com.hbm.tileentity.TileEntityMachineBase;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TileEntityBarrel extends TileEntityMachineBase implements ITickable, IFluidAcceptor, IFluidSource, IFluidStandardTransceiver {

	public FluidTank tank;
	//Drillgon200: I think this would be much easier to read as an enum.
	public short mode = 0;
	public static final short modes = 4;
	private int age = 0;
	public List<IFluidAcceptor> list = new ArrayList();
	protected boolean sendingBrake = false;

	private static final int[] slots_top = new int[] {2};
	private static final int[] slots_bottom = new int[] {3, 5};
	private static final int[] slots_side = new int[] {4};
	
	public TileEntityBarrel() {
		super(6);
		tank = new FluidTank(Fluids.NONE, 0, 0);
	}
	
	public TileEntityBarrel(int cap) {
		super(6);
		tank = new FluidTank(Fluids.NONE, cap, 0);
	}

	@Override
	public long getDemand(FluidType type, int pressure) {

		if(this.mode == 2 || this.mode == 3 || this.sendingBrake)
			return 0;

		if(tank.getPressure() != pressure) return 0;

		return type == tank.getTankType() ? tank.getMaxFill() - tank.getFill() : 0;
	}

	@Override
	public long transferFluid(FluidType type, int pressure, long fluid) {
		long toTransfer = Math.min(getDemand(type, pressure), fluid);
		tank.setFill(tank.getFill() + (int) toTransfer);
		this.markDirty();
		return fluid - toTransfer;
	}

	@Override
	public void update() {
		
		if(!world.isRemote){
			tank.setType(0, 1, inventory);
			tank.loadTank(2, 3, inventory);
			tank.unloadTank(4, 5, inventory);

			this.sendingBrake = true;
			tank.setFill(transmitFluidFairly(world, tank, this, tank.getFill(), this.mode == 0 || this.mode == 1, this.mode == 1 || this.mode == 2, getConPos()));
			this.sendingBrake = false;

			age++;
			if(age >= 20)
				age = 0;
			
			if((mode == 1 || mode == 2) && (age == 9 || age == 19))
				fillFluidInit(tank.getTankType());
			
			if(tank.getFill() > 0) {
				checkFluidInteraction();
			}

			this.networkPackNT(50);
		}
	}

	@Override
	public void serialize(ByteBuf buf) {
		super.serialize(buf);
		buf.writeShort(mode);
		tank.serialize(buf);
	}

	@Override
	public void deserialize(ByteBuf buf) {
		super.deserialize(buf);
		mode = buf.readShort();
		tank.deserialize(buf);
	}

	protected DirPos[] getConPos() {
		return new DirPos[] {
				new DirPos(pos.getX() + 1, pos.getY(), pos.getZ(), Library.POS_X),
				new DirPos(pos.getX() - 1, pos.getY(), pos.getZ(), Library.NEG_X),
				new DirPos(pos.getX(), pos.getY() + 1, pos.getZ(), Library.POS_Y),
				new DirPos(pos.getX(), pos.getY() - 1, pos.getZ(), Library.NEG_Y),
				new DirPos(pos.getX(), pos.getY(), pos.getZ() + 1, Library.POS_Z),
				new DirPos(pos.getX(), pos.getY(), pos.getZ() - 1, Library.NEG_Z)
		};
	}

	protected static int transmitFluidFairly(World world, FluidTank tank, IFluidConnector that, int fill, boolean connect, boolean send, DirPos[] connections) {

		Set<IPipeNet> nets = new HashSet<>();
		Set<IFluidConnector> consumers = new HashSet<>();
		FluidType type = tank.getTankType();
		int pressure = tank.getPressure();

		for(DirPos pos : connections) {

			TileEntity te = world.getTileEntity(pos.getPos());

			if(te instanceof IFluidConductor) {
				IFluidConductor con = (IFluidConductor) te;
				if(con.getPipeNet(type) != null) {
					nets.add(con.getPipeNet(type));
					con.getPipeNet(type).unsubscribe(that);
					consumers.addAll(con.getPipeNet(type).getSubscribers());
				}

				//if it's just a consumer, buffer it as a subscriber
			} else if(te instanceof IFluidConnector) {
				consumers.add((IFluidConnector) te);
			}
		}

		consumers.remove(that);

		if(fill > 0 && send) {
			List<IFluidConnector> con = new ArrayList<>();
			con.addAll(consumers);

			con.removeIf(x -> x == null || !(x instanceof TileEntity) || ((TileEntity)x).isInvalid());

			if(PipeNet.trackingInstances == null) {
				PipeNet.trackingInstances = new ArrayList<>();
			}

			PipeNet.trackingInstances.clear();
			nets.forEach(x -> {
				if(x instanceof PipeNet) PipeNet.trackingInstances.add((PipeNet) x);
			});

			fill = (int) PipeNet.fairTransfer(con, type, pressure, fill);
		}

		//resubscribe to buffered nets, if necessary
		if(connect) {
			nets.forEach(x -> x.subscribe(that));
		}

		return fill;
	}
	
	public void checkFluidInteraction(){
		Block b = this.getBlockType();
		
		//for when you fill antimatter into a matter tank
		if(b != ModBlocks.barrel_antimatter && tank.getTankType().isAntimatter()) {
			world.destroyBlock(pos, false);
			world.newExplosion(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 5, true, true);
		}
		
		//for when you fill hot or corrosive liquids into a plastic tank
		if(b == ModBlocks.barrel_plastic && (tank.getTankType().isCorrosive() || tank.getTankType().isHot())) {
			world.destroyBlock(pos, false);
			world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 1.0F);
		}
		
		//for when you fill corrosive liquid into an iron tank
		if((b == ModBlocks.barrel_iron && tank.getTankType().isCorrosive()) ||
				(b == ModBlocks.barrel_steel && tank.getTankType().hasTrait(FT_Corrosive.class) && tank.getTankType().getTrait(FT_Corrosive.class).getRating() > 50)) {
			
			world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 1.0F);
			ItemStackHandler copy = new ItemStackHandler(this.inventory.getSlots());
			for (int i = 0; i < this.inventory.getSlots(); i++) {
				copy.setStackInSlot(i, this.inventory.getStackInSlot(i).copy());
			}

			this.inventory = new ItemStackHandler(6);
			world.setBlockState(pos, ModBlocks.barrel_corroded.getDefaultState());
			
			TileEntityBarrel barrel = (TileEntityBarrel)world.getTileEntity(pos);

			if(barrel != null) {
				barrel.tank.setTankType(tank.getTankType());
				barrel.tank.setFill(Math.min(barrel.tank.getMaxFill(), tank.getFill()));
				barrel.inventory = copy;
			}
		}

		if(b == ModBlocks.barrel_corroded ) {
			if(world.rand.nextInt(3) == 0) {
				tank.setFill(tank.getFill() - 1);
			}
			if(world.rand.nextInt(3 * 60 * 20) == 0) world.destroyBlock(pos, false);
		}
	}

	@Override
	public void setFillForSync(int fill, int index) {
		tank.setFill(fill);
	}

	@Override
	public void setTypeForSync(FluidType type, int index) {
		tank.setTankType(type);
	}

	@Override
	public int getMaxFluidFill(FluidType type) {

		if(mode == 2 || mode == 3)
			return 0;

		return type == this.tank.getTankType() ? tank.getMaxFill() : 0;
	}

	@Override
	public void fillFluidInit(FluidType type) {
		fillFluid(this.pos.getX() + 1, this.pos.getY(), this.pos.getZ(), getTact(), type);
		fillFluid(this.pos.getX() - 1, this.pos.getY(), this.pos.getZ(), getTact(), type);
		fillFluid(this.pos.getX(), this.pos.getY() + 1, this.pos.getZ(), getTact(), type);
		fillFluid(this.pos.getX(), this.pos.getY() - 1, this.pos.getZ(), getTact(), type);
		fillFluid(this.pos.getX(), this.pos.getY(), this.pos.getZ() + 1, getTact(), type);
		fillFluid(this.pos.getX(), this.pos.getY(), this.pos.getZ() - 1, getTact(), type);
	}

	@Override
	public void fillFluid(int x, int y, int z, boolean newTact, FluidType type) {
		Library.transmitFluid(x, y, z, newTact, this, world, type);
	}

	@Override
	public boolean getTact() {
		if (age >= 0 && age < 10) {
			return true;
		}

		return false;
	}

	@Override
	public int getFluidFill(FluidType type) {
		return type == this.tank.getTankType() ? tank.getFill() : 0;
	}

	@Override
	public void setFluidFill(int i, FluidType type) {
		if(type == tank.getTankType()) tank.setFill(i);
	}

	@Override
	public List<IFluidAcceptor> getFluidList(FluidType type) {
		return this.list;
	}

	@Override
	public void clearFluidList(FluidType type) {
		this.list.clear();
	}

	@Override
	public String getName() {
		return "container.barrel";
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setShort("mode", mode);
		tank.writeToNBT(compound, "tank");
		return super.writeToNBT(compound);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		mode = compound.getShort("mode");
		tank.readFromNBT(compound,"tank");
		super.readFromNBT(compound);
	}

	@Override
	public FluidTank[] getSendingTanks() {
		return (mode == 1 || mode == 2) ? new FluidTank[] {tank} : new FluidTank[0];
	}

	@Override
	public FluidTank[] getReceivingTanks() {
		return (mode == 0 || mode == 1) && !sendingBrake ? new FluidTank[] {tank} : new FluidTank[0];
	}

	@Override
	public FluidTank[] getAllTanks() {
		return new FluidTank[] { tank };
	}

	@Override
	public int[] getAccessibleSlotsFromSide(EnumFacing e) {
		int i = e.ordinal();
		return i == 0 ? slots_bottom : (i == 1 ? slots_top : slots_side);
	}
	
	@Override
	public boolean isItemValidForSlot(int i, ItemStack stack) {
		if(i == 0){
			return true;
		}
		
		if(i == 2){
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean canInsertItem(int slot, ItemStack itemStack, int amount) {
		return isItemValidForSlot(slot, itemStack);
	}
	
	@Override
	public boolean canExtractItem(int slot, ItemStack itemStack, int amount) {
		if(slot == 1){
			return true;
		}
		
		if(slot == 3){
			return true;
		}
		
		return false;
	}
}
