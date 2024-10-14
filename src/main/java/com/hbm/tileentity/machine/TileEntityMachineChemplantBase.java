package com.hbm.tileentity.machine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import api.hbm.energymk2.IEnergyReceiverMK2;
import api.hbm.fluid.IFluidUser;
import com.hbm.blocks.BlockDummyable;
import com.hbm.inventory.ChemplantRecipes;
import com.hbm.inventory.RecipesCommon.AStack;
import com.hbm.inventory.fluid.FluidStack;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTank;
import com.hbm.items.ModItems;
import com.hbm.lib.DirPos;
import com.hbm.lib.Library;
import com.hbm.lib.ForgeDirection;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.util.InventoryUtil;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.ArrayList;

public abstract class TileEntityMachineChemplantBase extends TileEntityMachineBase implements IEnergyReceiverMK2, ITickable, IFluidUser {
	public long power;
	public int[] progress;
	public int[] maxProgress;
	public boolean isProgressing;

	public FluidTank[] tanks;

	int consumption = 100;
	int speed = 100;

	public TileEntityMachineChemplantBase(int scount) {
		super(scount);

		int count = this.getRecipeCount();

		progress = new int[count];
		maxProgress = new int[count];

		tanks = new FluidTank[4 * count];
		for(int i = 0; i < 4 * count; i++) {
			tanks[i] = new FluidTank(Fluids.NONE, getTankCapacity(), i);
		}
	}

	@Override
	public void update() {
		if(!world.isRemote) {
			int count = this.getRecipeCount();

			this.isProgressing = false;
			this.power = Library.chargeTEFromItems(inventory, 0, this.power, this.getMaxPower());

			for(int idx = 0; idx < count; ++idx) {
				loadItems(idx);
				unloadItems(idx);
			}

			for(int i = 0; i < count; i++) {
				if(!canProcess(i)) {
					this.progress[i] = 0;
				} else {
					isProgressing = true;
					process(i);
				}
			}
		}
	}

	protected boolean canProcess(int index) {
		int templateIdx = getTemplateIndex(index);
		ItemStack templateStack = inventory.getStackInSlot(templateIdx);
		if(templateStack.isEmpty() || templateStack.getItem() != ModItems.chemistry_template) {
			return false;
		}

		if(!ChemplantRecipes.hasRecipe(templateStack)) {
			return false;
		}

		List<AStack> itemInputs = ChemplantRecipes.getChemInputFromTempate(templateStack);
		FluidStack[] fluidInputs = ChemplantRecipes.getFluidInputFromTempate(templateStack);
		ItemStack[] itemOutputs = ChemplantRecipes.getChemOutputFromTempate(templateStack);
		FluidStack[] fluidOutputs = ChemplantRecipes.getFluidOutputFromTempate(templateStack);

		setupTanks(fluidInputs, fluidOutputs, index);

		if(this.power < this.consumption) {
			return false;
		}

		if(!hasRequiredFluids(fluidInputs, index)) {
			return false;
		}

		if(!hasSpaceForFluids(fluidOutputs, index)) {
			return false;
		}

		if(!hasRequiredItems(itemInputs, index)) {
			return false;
		}

		if(!hasSpaceForItems(itemOutputs, index)) {
			return false;
		}

		return true;
	}

	private void setupTanks(@Nullable FluidStack[] fluidInputs, @Nullable FluidStack[] fluidOutputs, int index) {
		if(fluidInputs[0] != null) tanks[index * 4].withPressure(fluidInputs[0].pressure).setTankType(fluidInputs[0].type);		else tanks[index * 4].setTankType(Fluids.NONE);
		if(fluidInputs[1] != null) tanks[index * 4 + 1].withPressure(fluidInputs[1].pressure).setTankType(fluidInputs[1].type);	else tanks[index * 4 + 1].setTankType(Fluids.NONE);
		if(fluidOutputs[0] != null) tanks[index * 4 + 2].withPressure(fluidOutputs[0].pressure).setTankType(fluidOutputs[0].type);	else tanks[index * 4 + 2].setTankType(Fluids.NONE);
		if(fluidOutputs[1] != null) tanks[index * 4 + 3].withPressure(fluidOutputs[1].pressure).setTankType(fluidOutputs[1].type);	else tanks[index * 4 + 3].setTankType(Fluids.NONE);
	}

	private boolean hasRequiredFluids(@Nullable FluidStack[] fluidInputs, int index) {
		if(fluidInputs[0] != null && tanks[index * 4].getFill() < fluidInputs[0].fill) return false;
		if(fluidInputs[1] != null && tanks[index * 4 + 1].getFill() < fluidInputs[1].fill) return false;
		return true;
	}

	private boolean hasSpaceForFluids(@Nullable FluidStack[] fluidOutputs, int index) {
		if(fluidOutputs[0] != null && tanks[index * 4 + 2].getFill() + fluidOutputs[0].fill > tanks[index * 4 + 2].getMaxFill()) return false;
		if(fluidOutputs[1] != null && tanks[index * 4 + 3].getFill() + fluidOutputs[1].fill > tanks[index * 4 + 3].getMaxFill()) return false;
		return true;
	}

	private boolean hasRequiredItems(@Nullable List<AStack> inputs, int index) {
		if(inputs == null) {
			return true;
		}

		int[] indices = getSlotIndicesFromIndex(index);
		return InventoryUtil.doesArrayHaveIngredients(inventory, indices[0], indices[1], inputs);
	}

	private boolean hasSpaceForItems(@Nullable ItemStack[] outputs, int index) {
		if(outputs == null) {
			return true;
		}

		int[] indices = getSlotIndicesFromIndex(index);

		return InventoryUtil.doesArrayHaveSpace(inventory, indices[2], indices[3], outputs);
	}

	protected void process(int index) {
		this.power -= this.consumption;
		this.progress[index]++;

		if(inventory.getStackInSlot(0).getItem() == ModItems.meteorite_sword_machined) {
			inventory.setStackInSlot(0, new ItemStack(ModItems.meteorite_sword_machined));
		}

		int templateIdx = getTemplateIndex(index);
		ItemStack templateStack = inventory.getStackInSlot(templateIdx);

		List<AStack> itemInputs = ChemplantRecipes.getChemInputFromTempate(templateStack);
		FluidStack[] fluidInputs = ChemplantRecipes.getFluidInputFromTempate(templateStack);
		ItemStack[] itemOutputs = ChemplantRecipes.getChemOutputFromTempate(templateStack);
		FluidStack[] fluidOutputs = ChemplantRecipes.getFluidOutputFromTempate(templateStack);

		this.maxProgress[index] = ChemplantRecipes.getProcessTime(templateStack) * this.speed / 100;

		if(this.progress[index] >= this.maxProgress[index]) {
			consumeFluids(fluidInputs, index);
			produceFluids(fluidOutputs, index);
			consumeItems(itemInputs, index);
			produceItems(itemOutputs, index);
			this.progress[index] = 0;
			this.markDirty();
		}
	}

	private void consumeFluids(@Nullable FluidStack[] inputs, int index) {
		if(inputs[0] != null) tanks[index * 4].setFill(tanks[index * 4].getFill() - inputs[0].fill);
		if(inputs[1] != null) tanks[index * 4 + 1].setFill(tanks[index * 4 + 1].getFill() - inputs[1].fill);
	}

	private void produceFluids(@Nullable FluidStack[] outputs, int index) {
		if(outputs[0] != null) tanks[index * 4 + 2].setFill(tanks[index * 4 + 2].getFill() + outputs[0].fill);
		if(outputs[1] != null) tanks[index * 4 + 3].setFill(tanks[index * 4 + 3].getFill() + outputs[1].fill);
	}

	private void consumeItems(@Nullable List<AStack> inputs, int index) {
		if(inputs == null) {
			return;
		}

		int[] indices = getSlotIndicesFromIndex(index);

		for(AStack in : inputs) {
			if(in != null)
				InventoryUtil.tryConsumeAStack(inventory, indices[0], indices[1], in);
		}
	}

	private void produceItems(@Nullable ItemStack[] outputs, int index) {
		if(outputs == null) {
			return;
		}

		int[] indices = getSlotIndicesFromIndex(index);

		for(ItemStack out : outputs) {
			if(out != null)
				InventoryUtil.tryAddItemToInventory(inventory, indices[2], indices[3], out.copy());
		}
	}

	private void loadItems(int index) {
		int templateIdx = getTemplateIndex(index);
		ItemStack templateStack = inventory.getStackInSlot(templateIdx);
		if(templateStack.isEmpty() || templateStack.getItem() != ModItems.chemistry_template) {
			return;
		}

		if(ChemplantRecipes.hasRecipe(templateStack)) {
			List<AStack> itemInputs = ChemplantRecipes.getChemInputFromTempate(templateStack);
			if(itemInputs == null) {
				return;
			}

			DirPos[] positions = getInputPositions();
			int[] indices = getSlotIndicesFromIndex(index);

			for(DirPos pos1 : positions) {
				BlockPos pos = pos1.getPos();
				TileEntity te = world.getTileEntity(pos);

				if(te != null && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH)) {
					IItemHandler cap = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH);
					int[] slots;
					if(te instanceof TileEntityMachineBase) {
						ForgeDirection dir = ForgeDirection.getOrientation(this.getBlockMetadata() - BlockDummyable.offset).getOpposite();
						slots = ((TileEntityMachineBase) te).getAccessibleSlotsFromSide(dir.toEnumFacing());
						tryFillAssemblerCap(cap, slots, (TileEntityMachineBase) te, indices[0], indices[1], itemInputs);
					} else {
						slots = new int[cap.getSlots()];
						for(int i = 0; i < slots.length; i++)
							slots[i] = i;
						tryFillAssemblerCap(cap, slots, null, indices[0], indices[1], itemInputs);
					}
				}	
			}
		}
	}

	private void unloadItems(int index) {
		DirPos[] positions = getOutputPositions();
		int[] indices = getSlotIndicesFromIndex(index);

		for(DirPos pos1 : positions) {
			BlockPos pos = pos1.getPos();
			TileEntity te = world.getTileEntity(pos);
			if(te != null && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH)) {
				IItemHandler cap = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH);
				for(int i = indices[2]; i <= indices[3]; i++) {
					tryFillContainerCap(cap, i);
				}
			}
		}
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

				outputStack.setCount(1);
				chest.insertItem(i, outputStack, false);

				return true;
			}
		}

		return false;
	}

	public boolean tryFillAssemblerCap(IItemHandler container, int[] allowedSlots, TileEntityMachineBase te, int minSlot, int maxSlot, List<AStack> recipeIngredients) {
		if(allowedSlots.length < 1)
			return false;

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

				int ingredientSlot = getValidSlot(nextIngredient, minSlot, maxSlot);


				if(ingredientSlot < minSlot)
					continue; // Ingredient filled or Assembler is full

				int possibleAmount = inventory.getStackInSlot(ingredientSlot).getMaxStackSize() - inventory.getStackInSlot(ingredientSlot).getCount(); // how many items do we need to fill the stack?

				if(possibleAmount == 0) { // full
					System.out.println("This should never happen method getValidSlot broke");
					continue;
				}
				// Ok now we know what we are looking for(nexIngredient) and where to put it (ingredientSlot) - So lets see if we find some of it in containers
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
						} else {
							break; // ingredientSlot filled
						}
					}
				}

			}
			return true;
		}
	}

	private int getValidSlot(AStack nextIngredient, int minSlot, int maxSlot) {
		int firstFreeSlot = -1;
		int stackCount = (int) Math.ceil(nextIngredient.count() / 64F);
		int stacksFound = 0;

		nextIngredient = nextIngredient.singulize();

		for(int k = minSlot; k <= maxSlot; k++) { //scaning inventory if some of the ingredients allready exist
			if(stacksFound < stackCount) {
				ItemStack assStack = inventory.getStackInSlot(k).copy();
				if(assStack.isEmpty()) {
					if(firstFreeSlot < minSlot)
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
		if(firstFreeSlot < minSlot) // nothing free in assembler inventory anymore
			return -2;
		return firstFreeSlot;
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
		return this.power;
	}

	@Override
	public void setPower(long power) {
		this.power = power;
	}

	public int getMaxFluidFill(FluidType type) {

		int maxFill = 0;

		for(FluidTank tank : inTanks()) {
			if(tank.getTankType() == type) {
				maxFill += tank.getMaxFill();
			}
		}

		return maxFill;
	}

	protected List<FluidTank> inTanks() {

		List<FluidTank> inTanks = new ArrayList();

		for(int i = 0; i < tanks.length; i++) {
			FluidTank tank = tanks[i];
			if(tank.index % 4 < 2) {
				inTanks.add(tank);
			}
		}

		return inTanks;
	}

	public int getFluidFillForTransfer(FluidType type, int pressure) {

		int fill = 0;

		for(FluidTank tank : outTanks()) {
			if(tank.getTankType() == type && tank.getPressure() == pressure) {
				fill += tank.getFill();
			}
		}

		return fill;
	}

	public void transferFluid(int amount, FluidType type, int pressure) {

		/*
		 * this whole new fluid mumbo jumbo extra abstraction layer might just be a bandaid
		 * on the gushing wound that is the current fluid systemm but i'll be damned if it
		 * didn't at least do what it's supposed to. half a decade and we finally have multi
		 * tank support for tanks with matching fluid types!!
		 */
		if(amount <= 0)
			return;

		List<FluidTank> send = new ArrayList();

		for(FluidTank tank : outTanks()) {
			if(tank.getTankType() == type && tank.getPressure() == pressure) {
				send.add(tank);
			}
		}

		if(send.size() == 0)
			return;

		int offer = 0;
		List<Integer> weight = new ArrayList();

		for(FluidTank tank : send) {
			int fillWeight = tank.getFill();
			offer += fillWeight;
			weight.add(fillWeight);
		}

		int tracker = amount;

		for(int i = 0; i < send.size(); i++) {

			FluidTank tank = send.get(i);
			int fillWeight = weight.get(i);
			int part = amount * fillWeight / offer;

			tank.setFill(tank.getFill() - part);
			tracker -= part;
		}

		//making sure to properly deduct even the last mB lost by rounding errors
		for(int i = 0; i < 100 && tracker > 0; i++) {

			FluidTank tank = send.get(i % send.size());

			if(tank.getFill() > 0) {
				int total = Math.min(tank.getFill(), tracker);
				tracker -= total;
				tank.setFill(tank.getFill() - total);
			}
		}
	}

	protected List<FluidTank> outTanks() {

		List<FluidTank> outTanks = new ArrayList();

		for(int i = 0; i < tanks.length; i++) {
			FluidTank tank = tanks[i];
			if(tank.index % 4 > 1) {
				outTanks.add(tank);
			}
		}

		return outTanks;
	}

	@Override
	public FluidTank[] getAllTanks() {
		return tanks;
	}

	@Override
	public long transferFluid(FluidType type, int pressure, long fluid) {
		int amount = (int) fluid;

		if(amount <= 0)
			return 0;

		List<FluidTank> rec = new ArrayList();

		for(FluidTank tank : inTanks()) {
			if(tank.getTankType() == type && tank.getPressure() == pressure) {
				rec.add(tank);
			}
		}

		if(rec.size() == 0)
			return fluid;

		int demand = 0;
		List<Integer> weight = new ArrayList();

		for(FluidTank tank : rec) {
			int fillWeight = tank.getMaxFill() - tank.getFill();
			demand += fillWeight;
			weight.add(fillWeight);
		}

		for(int i = 0; i < rec.size(); i++) {

			if(demand <= 0)
				break;

			FluidTank tank = rec.get(i);
			int fillWeight = weight.get(i);
			int part = (int) (Math.min((long)amount, (long)demand) * (long)fillWeight / (long)demand);

			tank.setFill(tank.getFill() + part);
			fluid -= part;
		}

		return fluid;
	}

	@Override
	public long getDemand(FluidType type, int pressure) {
		return getMaxFluidFill(type) - getFluidFillForTransfer(type, pressure);
	}

	@Override
	public long getTotalFluidForSend(FluidType type, int pressure) {
		return getFluidFillForTransfer(type, pressure);
	}

	@Override
	public void removeFluidForTransfer(FluidType type, int pressure, long amount) {
		this.transferFluid((int) amount, type, pressure);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		this.power = nbt.getLong("power");
		this.progress = nbt.getIntArray("progress");

		if(progress.length == 0)
			progress = new int[this.getRecipeCount()];

		for(int i = 0; i < tanks.length; i++) {
			tanks[i].readFromNBT(nbt, "t" + i);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		nbt.setLong("power", power);
		nbt.setIntArray("progress", progress);

		for(int i = 0; i < tanks.length; i++) {
			tanks[i].writeToNBT(nbt, "t" + i);
		}
		return nbt;
	}

	public abstract int getRecipeCount();

	public abstract int getTankCapacity();

	public abstract int getTemplateIndex(int index);

	/**
	 * @param index
	 * @return A size 4 int array containing min input, max input, min output and max output indices in that order.
	 */
	public abstract int[] getSlotIndicesFromIndex(int index);

	public abstract DirPos[] getInputPositions();

	public abstract DirPos[] getOutputPositions();
}
