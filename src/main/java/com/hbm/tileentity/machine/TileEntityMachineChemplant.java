package com.hbm.tileentity.machine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import api.hbm.energymk2.IEnergyReceiverMK2;
import api.hbm.fluid.IFluidStandardTransceiver;
import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.machine.MachineChemplant;
import com.hbm.forgefluid.FFUtils;
import com.hbm.handler.MultiblockHandler;
import com.hbm.inventory.ChemplantRecipes;
import com.hbm.inventory.RecipesCommon.AStack;
import com.hbm.inventory.fluid.FluidStack;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTank;
import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemChemistryTemplate;
import com.hbm.lib.DirPos;
import com.hbm.lib.ForgeDirection;
import com.hbm.lib.Library;
import com.hbm.packet.AuxElectricityPacket;
import com.hbm.packet.AuxParticlePacket;
import com.hbm.packet.FluidTankPacket;
import com.hbm.packet.LoopedSoundPacket;
import com.hbm.packet.PacketDispatcher;
import com.hbm.packet.TEChemplantPacket;
import com.hbm.tileentity.TileEntityMachineBase;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.oredict.OreDictionary;

public class TileEntityMachineChemplant extends TileEntityMachineBase implements IEnergyReceiverMK2, IFluidStandardTransceiver, ITickable {

	public static final long maxPower = 2000000;
	public long power;
	public int progress;
	public boolean needsProcess = true;
	public int maxProgress = 100;
	public boolean isProgressing;
	public boolean needsUpdate = false;
	public boolean needsTankTypeUpdate = false;
	public FluidTank[] tanks;
	public ItemStack previousTemplate = ItemStack.EMPTY;
	//Drillgon200: Yeah I don't even know what I was doing originally
	public ItemStack previousTemplate2 = ItemStack.EMPTY;
	int consumption = 100;
	int speed = 100;
	private long detectPower;
	private boolean detectIsProgressing;
	private FluidTank[] detectTanks = new FluidTank[]{null, null, null, null};

	public TileEntityMachineChemplant() {
		super(21);
		// Consumer<Integer> OnContentsChanged = this::OnContentsChanged;
		inventory = new ItemStackHandler(21) {
			@Override
			protected void onContentsChanged(int slot) {
				markDirty();
				OnContentsChanged(slot);
				super.onContentsChanged(slot);
			}
		};
		tanks = new FluidTank[4];
		for(int i = 0; i < 4; i++) {
			tanks[i] = new FluidTank(Fluids.NONE, 24_000);
		}
	}

	public void OnContentsChanged(int slot) {
		this.needsProcess = true;
	}

	public boolean isUseableByPlayer(EntityPlayer player) {
		if(world.getTileEntity(pos) != this) {
			return false;
		} else {
			return player.getDistanceSqToCenter(pos) <= 128;
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		this.power = nbt.getLong("powerTime");
		detectPower = power + 1;
		isProgressing = nbt.getBoolean("progressing");
		detectIsProgressing = !isProgressing;

		for(int i = 0; i < tanks.length; i++) {
			tanks[i].readFromNBT(nbt, "t" + i);
		}
		if(nbt.hasKey("inventory"))
			inventory.deserializeNBT((NBTTagCompound) nbt.getTag("inventory"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setLong("powerTime", power);

		nbt.setBoolean("progressing", isProgressing);

		for(int i = 0; i < tanks.length; i++) {
			tanks[i].writeToNBT(nbt, "t" + i);
		}

		NBTTagCompound inv = inventory.serializeNBT();
		nbt.setTag("inventory", inv);
		return nbt;
	}

	@Override
	public void serialize(ByteBuf buf) {
		super.serialize(buf);
		buf.writeLong(power);
		buf.writeInt(progress);
		buf.writeInt(maxProgress);
		buf.writeBoolean(isProgressing);

		for(int i = 0; i < tanks.length; i++)
			tanks[i].serialize(buf);
	}

	@Override
	public void deserialize(ByteBuf buf) {
		super.deserialize(buf);
		power = buf.readLong();
		progress = buf.readInt();
		maxProgress = buf.readInt();
		isProgressing = buf.readBoolean();

		for(int i = 0; i < tanks.length; i++)
			tanks[i].deserialize(buf);
	}

	public long getPowerScaled(long i) {
		return (power * i) / maxPower;
	}

	public int getProgressScaled(int i) {
		return (progress * i) / Math.max(10, maxProgress);
	}

	@Override
	public void update() {
		needsTankTypeUpdate = previousTemplate2 != inventory.getStackInSlot(4);
		previousTemplate2 = inventory.getStackInSlot(4);
		this.consumption = 100;
		this.speed = 100;


		double c = 100;
		double s = 100;

		for(int i = 1; i < 4; i++) {
			ItemStack stack = inventory.getStackInSlot(i);

			if(!stack.isEmpty()) {
				if(stack.getItem() == ModItems.upgrade_speed_1) {
					s *= 0.75;
					c *= 3;
				}
				if(stack.getItem() == ModItems.upgrade_speed_2) {
					s *= 0.65;
					c *= 6;
				}
				if(stack.getItem() == ModItems.upgrade_speed_3) {
					s *= 0.5;
					c *= 9;
				}
				if(stack.getItem() == ModItems.upgrade_power_1) {
					c *= 0.8;
					s *= 1.25;
				}
				if(stack.getItem() == ModItems.upgrade_power_2) {
					c *= 0.4;
					s *= 1.5;
				}
				if(stack.getItem() == ModItems.upgrade_power_3) {
					c *= 0.2;
					s *= 2;
				}
			}
		}
		this.speed = (int) s;
		this.consumption = (int) c;


		if(speed < 2)
			speed = 2;
		if(consumption < 1)
			consumption = 1;
		if(this.needsTankTypeUpdate)
			setContainers();


		if(!world.isRemote) {
			if(needsUpdate) {
				needsUpdate = false;
			}
			int meta = world.getBlockState(pos).getValue(MachineChemplant.FACING);
			isProgressing = false;

			if(world.getTotalWorldTime() % 20 == 0) {
				this.updateConnections();
			}

			power = Library.chargeTEFromItems(inventory, 0, power, maxPower);

			if(inputTankEmpty(0, 17) && inventory.getStackInSlot(19).isEmpty()){
				tanks[0].loadTank(17, 19, inventory);
				FFUtils.moveItems(inventory, 17, 19, false);
			}

			if(inputTankEmpty(1, 18) && inventory.getStackInSlot(20).isEmpty()){
				FFUtils.moveItems(inventory, 18, 20, false);
				tanks[1].loadTank(18, 20, inventory);
			}

			tanks[2].unloadTank(9, 11, inventory);
			tanks[3].unloadTank(10, 12, inventory);

			for(DirPos pos : getConPos()) {
				if(tanks[2].getFill() > 0) this.sendFluid(tanks[2], world, pos.getPos().getX(), pos.getPos().getY(), pos.getPos().getZ(), pos.getDir());
				if(tanks[3].getFill() > 0) this.sendFluid(tanks[3], world, pos.getPos().getX(), pos.getPos().getY(), pos.getPos().getZ(), pos.getDir());
			}

			
			ItemStack[] itemOutputs = ChemplantRecipes.getChemOutputFromTempate(inventory.getStackInSlot(4));
			FluidStack[] fluidOutputs = ChemplantRecipes.getFluidOutputFromTempate(inventory.getStackInSlot(4));
			
			if(needsProcess && (itemOutputs != null || !Library.isArrayEmpty(fluidOutputs))) {

				List<AStack> itemInputs = ChemplantRecipes.getChemInputFromTempate(inventory.getStackInSlot(4));
				FluidStack[] fluidInputs = ChemplantRecipes.getFluidInputFromTempate(inventory.getStackInSlot(4));
				int duration = ChemplantRecipes.getProcessTime(inventory.getStackInSlot(4));

				this.maxProgress = (duration * speed) / 100;
				if(removeItems(itemInputs, cloneItemStackProper(inventory)) && hasFluidsStored(fluidInputs)) {
					if(power >= consumption) {
						if(hasSpaceForItems(itemOutputs) && hasSpaceForFluids(fluidOutputs)) {
							progress++;
							isProgressing = true;

							if(progress >= maxProgress) {
								progress = 0;
								if(itemOutputs != null)
									addItems(itemOutputs);
								if(fluidOutputs != null)
									addFluids(fluidOutputs);

								removeItems(itemInputs, inventory);
								removeFluids(fluidInputs);
								if(inventory.getStackInSlot(0).getItem() == ModItems.meteorite_sword_machined)
									inventory.setStackInSlot(0, new ItemStack(ModItems.meteorite_sword_treated));
							}

							power -= consumption;
						}
					}
				} else {
					progress = 0;
					needsProcess = false;
				}
			} else {
				progress = 0;
			}


			TileEntity te1 = null;
			TileEntity te2 = null;

			if(meta == 2) {
				te1 = world.getTileEntity(pos.add(-2, 0, 0));
				te2 = world.getTileEntity(pos.add(3, 0, -1));
			}
			if(meta == 3) {
				te1 = world.getTileEntity(pos.add(2, 0, 0));
				te2 = world.getTileEntity(pos.add(-3, 0, 1));
			}
			if(meta == 4) {
				te1 = world.getTileEntity(pos.add(0, 0, 2));
				te2 = world.getTileEntity(pos.add(-1, 0, -3));
			}
			if(meta == 5) {
				te1 = world.getTileEntity(pos.add(0, 0, -2));
				te2 = world.getTileEntity(pos.add(1, 0, 3));
			}

			if(!isProgressing) {
				tryExchangeTemplates(te1, te2);
			}

			if(te1 != null && te1.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, MultiblockHandler.intToEnumFacing(meta).rotateY())) {
				IItemHandler cap = te1.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, MultiblockHandler.intToEnumFacing(meta).rotateY());
				int[] outputSlots = new int[]{ 5, 6, 7, 8, 11, 12, 19, 20 };
				for(int i : outputSlots) {
					tryFillContainerCap(cap, i);
				}
			}

			if(te2 != null && te2.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, MultiblockHandler.intToEnumFacing(meta).rotateY())) {
				IItemHandler cap = te2.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, MultiblockHandler.intToEnumFacing(meta).rotateY());
				int[] slots;
				if(te2 instanceof TileEntityMachineBase) {
					slots = ((TileEntityMachineBase) te2).getAccessibleSlotsFromSide(MultiblockHandler.intToEnumFacing(meta).rotateY());
					tryFillAssemblerCap(cap, slots, (TileEntityMachineBase) te2);
				} else {
					slots = new int[cap.getSlots()];
					for(int i = 0; i < slots.length; i++)
						slots[i] = i;
					tryFillAssemblerCap(cap, slots, null);
				}
			}


			if(isProgressing) {
				if(meta == 2) {
					PacketDispatcher.wrapper.sendToAllAround(new AuxParticlePacket(pos.getX() + 0.375, pos.getY() + 3, pos.getZ() - 0.625, 1),
							new TargetPoint(world.provider.getDimension(), pos.getX() + 0.375, pos.getY() + 3, pos.getZ() - 0.625, 50));
				}
				if(meta == 3) {
					PacketDispatcher.wrapper.sendToAllAround(new AuxParticlePacket(pos.getX() + 0.625, pos.getY() + 3, pos.getZ() + 1.625, 1),
							new TargetPoint(world.provider.getDimension(), pos.getX() + 0.625, pos.getY() + 3, pos.getZ() + 1.625, 50));
				}
				if(meta == 4) {
					PacketDispatcher.wrapper.sendToAllAround(new AuxParticlePacket(pos.getX() - 0.625, pos.getY() + 3, pos.getZ() + 0.625, 1),
							new TargetPoint(world.provider.getDimension(), pos.getX() - 0.625, pos.getY() + 3, pos.getZ() + 0.625, 50));
				}
				if(meta == 5) {
					PacketDispatcher.wrapper.sendToAllAround(new AuxParticlePacket(pos.getX() + 1.625, pos.getY() + 3, pos.getZ() + 0.375, 1),
							new TargetPoint(world.provider.getDimension(), pos.getX() + 1.625, pos.getY() + 3, pos.getZ() + 0.375, 50));
				}
			}
		}

	}

	public boolean tryExchangeTemplates(TileEntity te1, TileEntity te2) {
		//validateTe sees if it's a valid inventory tile entity
		boolean te1Valid = validateTe(te1);
		boolean te2Valid = validateTe(te2);

		if(te1Valid && te2Valid) {
			IItemHandler iTe1 = te1.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			IItemHandler iTe2e = te2.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			if(!(iTe2e instanceof IItemHandlerModifiable))
				return false;
			IItemHandlerModifiable iTe2 = (IItemHandlerModifiable) iTe2e;
			boolean openSlot = false;
			boolean existingTemplate = false;
			boolean filledContainer = false;
			//Check if there's an existing template and an open slot
			for(int i = 0; i < iTe1.getSlots(); i++) {
				if(iTe1.getStackInSlot(i) == null || iTe1.getStackInSlot(i) == ItemStack.EMPTY) {
					openSlot = true;

				}

			}
			if(this.inventory.getStackInSlot(4) != ItemStack.EMPTY && inventory.getStackInSlot(4).getItem() instanceof ItemChemistryTemplate) {
				existingTemplate = true;
			}
			//Check if there's a template in input
			for(int i = 0; i < iTe2.getSlots(); i++) {
				iTe2.getStackInSlot(i);
				if(iTe2.getStackInSlot(i) != ItemStack.EMPTY && iTe2.getStackInSlot(i).getItem() instanceof ItemChemistryTemplate) {
					if(openSlot && existingTemplate) {
						filledContainer = tryFillContainerCap(iTe1, 4);

					}
					if(filledContainer || !existingTemplate) {
						ItemStack copy = iTe2.getStackInSlot(i).copy();
						iTe2.setStackInSlot(i, ItemStack.EMPTY);
						this.inventory.setStackInSlot(4, copy);
					}
				}

			}
		}
		return false;

	}

	private void updateConnections() {

		for(DirPos pos : getConPos()) {
			this.trySubscribe(world, pos.getPos().getX(), pos.getPos().getY(), pos.getPos().getZ(), pos.getDir());
			this.trySubscribe(tanks[0].getTankType(), world, pos.getPos().getX(), pos.getPos().getY(), pos.getPos().getZ(), pos.getDir());
			this.trySubscribe(tanks[1].getTankType(), world, pos.getPos().getX(), pos.getPos().getY(), pos.getPos().getZ(), pos.getDir());
		}
	}

	public DirPos[] getConPos() {

		ForgeDirection dir = ForgeDirection.getOrientation(this.getBlockMetadata() - BlockDummyable.offset).getOpposite();
		ForgeDirection rot = dir.getRotation(ForgeDirection.DOWN);

		return new DirPos[] {
				new DirPos(pos.getX() + rot.offsetX * 3,				pos.getY(),	pos.getZ() + rot.offsetZ * 3,				rot),
				new DirPos(pos.getX() - rot.offsetX * 2,				pos.getY(),	pos.getZ() - rot.offsetZ * 2,				rot.getOpposite()),
				new DirPos(pos.getX() + rot.offsetX * 3 + dir.offsetX,	pos.getY(),	pos.getZ() + rot.offsetZ * 3 + dir.offsetZ, rot),
				new DirPos(pos.getX() - rot.offsetX * 2 + dir.offsetX,	pos.getY(),	pos.getZ() - rot.offsetZ * 2 + dir.offsetZ, rot.getOpposite())
		};
	}

	private boolean validateTe(TileEntity te) {
		return te != null && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
	}

	private void setContainers() {
		if(inventory.getStackInSlot(4) == ItemStack.EMPTY || (inventory.getStackInSlot(4) != ItemStack.EMPTY && !(inventory.getStackInSlot(4).getItem() instanceof ItemChemistryTemplate))) {
		} else {
			needsTankTypeUpdate = true;
			if(previousTemplate != ItemStack.EMPTY && ItemStack.areItemStacksEqual(previousTemplate, inventory.getStackInSlot(4))) {

				needsTankTypeUpdate = false;

			} else {

			}
			previousTemplate = inventory.getStackInSlot(4).copy();
			FluidStack[] fluidInputs = ChemplantRecipes.getFluidInputFromTempate(inventory.getStackInSlot(4));
			FluidStack[] fluidOutputs = ChemplantRecipes.getFluidOutputFromTempate(inventory.getStackInSlot(4));

			if(fluidInputs != null){
				tanks[0].setTankType(fluidInputs[0] == null ? null : fluidInputs[0].type);
				if(fluidInputs.length == 2){
					tanks[1].setTankType(fluidInputs[0] == null ? null : fluidInputs[0].type);
				}
			}
			if(fluidOutputs != null){
				tanks[2].setTankType(fluidOutputs[0] == null ? null : fluidOutputs[0].type);
				if(fluidOutputs.length == 2){
					tanks[3].setTankType(fluidOutputs[1] == null ? null : fluidOutputs[1].type);
				}
			}

			if(fluidInputs != null){
				if(fluidInputs[0] != null || tanks[0].getTankType() != null) {
					tanks[0].setTankType(Fluids.NONE);
					if(needsTankTypeUpdate) {
						needsTankTypeUpdate = false;
					}
				}
				if(fluidInputs.length == 2){
					if(fluidInputs[1] != null || tanks[1].getTankType() != null) {
						tanks[1].setTankType(Fluids.NONE);
						if(needsTankTypeUpdate) {
							needsTankTypeUpdate = false;
						}
					}
				}
			}
			if(fluidOutputs != null){
				if(fluidOutputs[0] != null || tanks[2].getTankType() != null) {
					tanks[2].setTankType(Fluids.NONE);
				}
				if(fluidOutputs.length == 2){
					if(fluidOutputs[1] != null || tanks[3].getTankType() != null) {
						tanks[3].setTankType(Fluids.NONE);
						if(needsTankTypeUpdate) {
							needsTankTypeUpdate = false;
						}
					}
				}
			}
		}
	}

	protected boolean inputTankEmpty(int tank, int slot) {
		if(!inventory.getStackInSlot(slot).isEmpty() && tanks[tank] != null) {
			ItemStack c = inventory.getStackInSlot(slot).copy();
			c.setCount(1);
			return FFUtils.isEmtpyFluidTank(c);
			//Drillgon200: I really hope fluid container registry comes back.
		}

		return false;
	}

	public boolean hasFluidsStored(FluidStack[] fluids) {
		if(Library.isArrayEmpty(fluids))
			return true;
		if(fluids.length == 2){
			if((fluids[0] == null || fluids[0].fill <= tanks[0].getFill()) && (fluids[1] == null || fluids[1].fill <= tanks[1].getFill()))
				return true;
		}else{
			if(fluids[0] == null || fluids[0].fill <= tanks[0].getFill())
				return true;
		}

		return false;
	}

	public boolean hasSpaceForFluids(FluidStack[] fluids) {
		if(Library.isArrayEmpty(fluids))
			return true;
		if(fluids.length == 2){
			if((fluids[0] == null || tanks[2].getFill() == fluids[0].fill) && (fluids[1] == null || fluids[1] != null && tanks[3].getFill() == fluids[1].fill))
				return true;
		}else{
			if(fluids[0] == null || tanks[2].getFill() == fluids[0].fill)
				return true;
		}
		return false;
	}

	public void removeFluids(FluidStack[] fluids) {
		if(Library.isArrayEmpty(fluids))
			return;
		tanks[0].setFill(tanks[0].getFill() - fluids[0].fill);
		if(fluids.length == 2) {
			tanks[1].setFill(tanks[1].getFill() - fluids[1].fill);
		}
	}

	public boolean hasSpaceForItems(ItemStack[] stacks) {
		if(stacks == null)
			return true;
		if(stacks != null && Library.isArrayEmpty(stacks))
			return true;

		//checking first slot
		for(int i = 0; i < stacks.length; i++){
			if(inventory.getStackInSlot(5+i) == ItemStack.EMPTY) { // is the slot empty?
				continue; // ok it is empty lets check the next one
			} else {
				
				if(Library.areItemStacksCompatible(stacks[i].copy(), inventory.getStackInSlot(5+i).copy(), false)){ // oof there is some item there - is it the same tho?
					if(inventory.getStackInSlot(5+i).getCount() + stacks[i].getCount() <= inventory.getStackInSlot(5+i).getMaxStackSize()){ // ok it is the same item but is the stack full already?
						continue;
					}
				}
				return false;
			}
		}
		return true;
	}

	public void addItems(ItemStack[] stacks) {
		if(stacks == null)
			return;
		if(stacks != null && Library.isArrayEmpty(stacks))
			return;

		for(int i = 0; i<stacks.length; i++){
			if(inventory.getStackInSlot(5+i) == ItemStack.EMPTY){ // if the slot is empty then create a new stack otherwise make the existing one bigger
				inventory.setStackInSlot(5+i, stacks[i].copy());
			}
			else{
				inventory.getStackInSlot(5+i).setCount(inventory.getStackInSlot(5+i).getCount() + stacks[i].getCount());
			}
		}
	}

	public void addFluids(FluidStack[] stacks) {

		if(stacks != null){
			tanks[2].setFill(tanks[2].getFill() + stacks[0].fill);
			if(stacks.length == 2){
				if(stacks[1] != null) {
					tanks[3].setFill(tanks[3].getFill() + stacks[1].fill);
				}
			}
		}
	}

	//private int extractIngredient(IItemHandler container)

	//I can't believe that worked.
	public IItemHandlerModifiable cloneItemStackProper(IItemHandlerModifiable array) {
		IItemHandlerModifiable stack = new ItemStackHandler(array.getSlots());

		for(int i = 0; i < array.getSlots(); i++)
			if(array.getStackInSlot(i) != null)
				stack.setStackInSlot(i, array.getStackInSlot(i).copy());
			else
				stack.setStackInSlot(i, ItemStack.EMPTY);

		return stack;
	}

	//Unloads output into chests. Capability version.
	public boolean tryFillContainerCap(IItemHandler chest, int slot) {
		//Check if we have something to output
		if(inventory.getStackInSlot(slot).isEmpty())
			return false;

		for(int i = 0; i < chest.getSlots(); i++) {
			
			ItemStack outputStack = inventory.getStackInSlot(slot).copy();
			if(outputStack.isEmpty())
				return false;

			ItemStack chestItem = chest.getStackInSlot(i).copy();
			if(chestItem.isEmpty() || (Library.areItemStacksCompatible(outputStack, chestItem, false) && chestItem.getCount() < chestItem.getMaxStackSize())) {
				inventory.getStackInSlot(slot).shrink(1);
				if(inventory.getStackInSlot(slot).isEmpty())
					inventory.setStackInSlot(slot, ItemStack.EMPTY);

				outputStack.setCount(1);
				chest.insertItem(i, outputStack, false);

				return true;
			}
		}
		//Chest is full
		return false;
	}

	private int getValidSlot(AStack nextIngredient) {
		int firstFreeSlot = -1;
		int stackCount = (int) Math.ceil(nextIngredient.count() / 64F);
		int stacksFound = 0;

		nextIngredient = nextIngredient.singulize();

		for(int k = 13; k < 17; k++) { //scaning inventory if some of the ingredients allready exist
			if(stacksFound < stackCount) {
				ItemStack assStack = inventory.getStackInSlot(k).copy();
				if(assStack.isEmpty()) {
					if(firstFreeSlot < 13)
						firstFreeSlot = k;
					continue;
				} else { // check if there are already enough filled stacks is full

					assStack.setCount(1);
					if(nextIngredient.isApplicable(assStack)) { // check if it is the right item

						if(inventory.getStackInSlot(k).getCount() < assStack.getMaxStackSize()) // is that stack full?
							return k; // found a not full slot where we already have that ingredient
						else
							stacksFound++;
					}
				}
			} else {
				return -1; // All required stacks are full
			}
		}
		if(firstFreeSlot < 13) // nothing free in assembler inventory anymore
			return -2;
		return firstFreeSlot;
	}

	public boolean tryFillAssemblerCap(IItemHandler container, int[] allowedSlots, TileEntityMachineBase te) {
		if(allowedSlots.length < 1)
			return false;
		List<AStack> recipeIngredients = ChemplantRecipes.getChemInputFromTempate(inventory.getStackInSlot(4));//Loading Ingredients
		if(recipeIngredients == null) //No recipe template found
			return false;
		else {
			Map<Integer, ItemStack> itemStackMap = new HashMap<Integer, ItemStack>();

			for(int slot : allowedSlots) {
				container.getStackInSlot(slot);
				if(container.getStackInSlot(slot).isEmpty()) { // check next slot in chest if it is empty
					continue;
				} else { // found an item in chest
					itemStackMap.put(slot, container.getStackInSlot(slot).copy());
				}
			}
			if(itemStackMap.size() == 0) {
				return false;
			}

			for(int ig = 0; ig < recipeIngredients.size(); ig++) {

				AStack nextIngredient = recipeIngredients.get(ig).copy(); // getting new ingredient

				int ingredientSlot = getValidSlot(nextIngredient);


				if(ingredientSlot < 13)
					continue; // Ingredient filled or Assembler is full

				int possibleAmount = inventory.getStackInSlot(ingredientSlot).getMaxStackSize() - inventory.getStackInSlot(ingredientSlot).getCount(); // how many items do we need to fill the stack?

				if(possibleAmount == 0) { // full
					System.out.println("This should never happen method getValidSlot broke");
					continue;
				}
				// Ok now we know what we are looking for (nexIngredient) and where to put it (ingredientSlot) - So lets see if we find some of it in containers
				for(Map.Entry<Integer, ItemStack> set :
						itemStackMap.entrySet()) {
					ItemStack stack = set.getValue();
					int slot = set.getKey();
					ItemStack compareStack = stack.copy();
					compareStack.setCount(1);

					if(isItemAcceptable(nextIngredient.getStack(), compareStack)) { // bingo found something

						int foundCount = Math.min(stack.getCount(), possibleAmount);
						if(te != null && !te.canExtractItem(slot, stack, foundCount))
							continue;
						if(foundCount > 0) {
							possibleAmount -= foundCount;
							container.extractItem(slot, foundCount, false);
							inventory.getStackInSlot(ingredientSlot);
							if(inventory.getStackInSlot(ingredientSlot).isEmpty()) {

								stack.setCount(foundCount);
								inventory.setStackInSlot(ingredientSlot, stack);

							} else {
								inventory.getStackInSlot(ingredientSlot).grow(foundCount); // transfer complete
							}
							needsProcess = true;
						} else {
							break; // ingredientSlot filled
						}
					}
				}

			}
			return true;
		}
	}

	//boolean true: remove items, boolean false: simulation mode
	public boolean removeItems(List<AStack> stack, IItemHandlerModifiable array) {

		if(stack == null)
			return true;
		for(int i = 0; i < stack.size(); i++) {
			for(int j = 0; j < stack.get(i).count(); j++) {
				AStack sta = stack.get(i).copy();
				sta.setCount(1);
				if(!canRemoveItemFromArray(sta, array))
					return false;
			}
		}

		return true;

	}

	public boolean canRemoveItemFromArray(AStack stack, IItemHandlerModifiable array) {

		AStack st = stack.copy();

		for(int i = 6; i < 18; i++) {

			if(array.getStackInSlot(i).getItem() != Items.AIR) {
				ItemStack sta = array.getStackInSlot(i).copy();
				sta.setCount(1);

				if(st.isApplicable(sta) && array.getStackInSlot(i).getCount() > 0) {
					array.getStackInSlot(i).shrink(1);
					;

					if(array.getStackInSlot(i).isEmpty())
						array.setStackInSlot(i, ItemStack.EMPTY);
					;

					return true;
				}
			}
		}

		return false;
	}

	public boolean isItemAcceptable(ItemStack stack1, ItemStack stack2) {

		if(stack1 != null && stack2 != null && stack1.getItem() != Items.AIR && stack1.getItem() != Items.AIR) {
			if(Library.areItemStacksCompatible(stack1, stack2))
				return true;

			int[] ids1 = OreDictionary.getOreIDs(stack1);
			int[] ids2 = OreDictionary.getOreIDs(stack2);

			if(ids1.length > 0 && ids2.length > 0) {
				for(int i = 0; i < ids1.length; i++)
					for(int j = 0; j < ids2.length; j++)
						if(ids1[i] == ids2[j])
							return true;
			}
		}

		return false;
	}

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
	public AxisAlignedBB getRenderBoundingBox() {
		return TileEntity.INFINITE_EXTENT_AABB;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared() {
		return 65536.0D;
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);

		return new SPacketUpdateTileEntity(pos, 0, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {

		readFromNBT(pkt.getNbtCompound());
	}

	public ItemStack getStackInSlot(int i) {
		return inventory.getStackInSlot(i);
	}

	@Override
	public String getName() {
		return "container.chemplant";
	}

	@Override
	public FluidTank[] getSendingTanks() {
		return new FluidTank[] {tanks[2], tanks[3]};
	}

	@Override
	public FluidTank[] getReceivingTanks() {
		return new FluidTank[] {tanks[0], tanks[1]};
	}

	@Override
	public FluidTank[] getAllTanks() {
		return tanks;
	}
}
