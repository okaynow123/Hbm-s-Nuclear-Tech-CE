package com.hbm.tileentity.machine;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.hbm.api.energymk2.IEnergyProviderMK2;
import com.hbm.api.fluid.IFluidStandardTransceiver;
import com.hbm.interfaces.AutoRegister;
import com.hbm.inventory.container.ContainerMachineDiesel;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.inventory.fluid.trait.FT_Combustible;
import com.hbm.inventory.fluid.trait.FluidTrait;
import com.hbm.inventory.gui.GUIMachineDiesel;
import com.hbm.inventory.recipes.EngineRecipes;
import com.hbm.inventory.recipes.EngineRecipes.FuelGrade;
import com.hbm.lib.ForgeDirection;
import com.hbm.lib.Library;
import com.hbm.main.MainRegistry;
import com.hbm.sound.AudioWrapper;
import com.hbm.tileentity.IConfigurableMachine;
import com.hbm.tileentity.IFluidCopiable;
import com.hbm.tileentity.IGUIProvider;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;

@AutoRegister
public class TileEntityMachineDiesel extends TileEntityMachinePolluting implements ITickable, IEnergyProviderMK2, IFluidStandardTransceiver, IGUIProvider, IConfigurableMachine, IFluidCopiable {

	public long power;
	public int soundCycle = 0;

	/* CONFIGURABLE CONSTANTS */
	public static int fluidCap = 16000;
	public static long maxPower = 50000;
	public long powerCap = 50000;
	public int age = 0;
	public FluidTankNTM tank;

	public boolean wasOn = false;
	private AudioWrapper audio;

	private static final int[] slots_top = new int[] { 0 };
	private static final int[] slots_bottom = new int[] { 1, 2 };
	private static final int[] slots_side = new int[] { 2 };

	public static HashMap<FT_Combustible.FuelGrade, Double> fuelEfficiency = new HashMap<>();
	static {
		fuelEfficiency.put(FT_Combustible.FuelGrade.MEDIUM,	0.5D);
		fuelEfficiency.put(FT_Combustible.FuelGrade.HIGH, 0.75D);
		fuelEfficiency.put(FT_Combustible.FuelGrade.AERO, 0.1D);
	}

	public TileEntityMachineDiesel() {
		super(3, 100, true, true);
		tank = new FluidTankNTM(Fluids.NONE, 16000);
	}
	
	@Override
	public String getName() {
		return "container.machineDiesel";
	}
	
	@Override
	public @NotNull NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setLong("powerTime", power);
		compound.setLong("powerCap", powerCap);
		tank.writeToNBT(compound, "tank");
		return super.writeToNBT(compound);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		this.power = compound.getLong("powerTime");
		this.powerCap = compound.getLong("powerCap");
		tank.readFromNBT(compound, "tank");
		super.readFromNBT(compound);
	}
	
	@Override
	public int[] getAccessibleSlotsFromSide(EnumFacing e) {
		int p_94128_1_ = e.ordinal();
		return p_94128_1_ == 0 ? slots_bottom : (p_94128_1_ == 1 ? slots_top : slots_side);
	}
	
	public long getPowerScaled(long i) {
		return (power * i) / powerCap;
	}
	
	@Override
	public void update() {
		if (!world.isRemote) {
			for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
				this.tryProvide(world, pos.getX() + dir.offsetX, pos.getY() + dir.offsetY, pos.getZ() + dir.offsetZ, dir);
				this.sendSmoke(pos.getX() + dir.offsetX, pos.getX() + dir.offsetY, pos.getX() + dir.offsetZ, dir);
			}

			//Tank Management
			tank.loadTank(0, 1, inventory);
			if(tank.getTankType() == Fluids.NITAN)
				powerCap = maxPower * 10;
			else
				powerCap = maxPower;
			
			// Battery Item
			power = Library.chargeItemsFromTE(inventory, 2, power, powerCap);
			generate();
			this.networkPackNT(50);
		} else {

			if(wasOn) {

				if(audio == null) {
					audio = createAudioLoop();
					audio.startSound();
				} else if(!audio.isPlaying()) {
					audio = rebootAudio(audio);
				}

				audio.keepAlive();
				audio.updateVolume(this.getVolume(1F));

			} else {

				if(audio != null) {
					audio.stopSound();
					audio = null;
				}
			}
		}
	}

	@Override
	public AudioWrapper createAudioLoop() {
		return MainRegistry.proxy.getLoopedSound(new SoundEvent(new ResourceLocation("hbm:block.engine")), SoundCategory.BLOCKS, pos.getX(), pos.getY(), pos.getZ(), 1.0F, 10F, 1.0F, 10);
	}

	@Override
	public void onChunkUnload() {
		if(audio != null) {
			audio.stopSound();
			audio = null;
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		if(audio != null) {
			audio.stopSound();
			audio = null;
		}
	}

	@Override
	public void serialize(ByteBuf buf){
		super.serialize(buf);
		buf.writeLong(power);
		buf.writeLong(powerCap);
		tank.serialize(buf);
	}

	@Override
	public void deserialize(ByteBuf buf){
		super.deserialize(buf);
		power = buf.readLong();
		powerCap = buf.readLong();
		tank.deserialize(buf);
	}

	public boolean hasAcceptableFuel() {
		return getHEFromFuel() > 0;
	}
	
	public long getHEFromFuel() {
		if(tank.getFluid() == null) return 0;
		return getHEFromFuel(tank.getFluid().getFluid());
	}
	
	public static long getHEFromFuel(Fluid type) {
		if(EngineRecipes.hasFuelRecipe(type)) {
			FuelGrade grade = EngineRecipes.getFuelGrade(type);
			double efficiency = fuelEfficiency.containsKey(grade) ? fuelEfficiency.get(grade) : 0;
			return (long) (EngineRecipes.getEnergy(type) / 1000L * efficiency);
		}
		
		return 0;
	}

	public void generate() {
		if (hasAcceptableFuel()) {
			if (tank.getFluidAmount() > 0) {
				if (soundCycle == 0) {
					this.world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_FIREWORK_BLAST, SoundCategory.BLOCKS, 0.5F, -100.0F);
				}
				soundCycle++;

				if (soundCycle >= 5)
					soundCycle = 0;

				tank.drain(1, true);

				if(world.getTotalWorldTime() % 5 == 0) {
					super.pollute(tank.getTankType(), FluidTrait.FluidReleaseType.BURN, 5F);
				}

				if (power + getHEFromFuel() <= powerCap) {
					power += getHEFromFuel();
				} else {
					power = powerCap;
				}
			}
		}
	}
	protected boolean inputValidForTank(int tank, int slot){
		if(!inventory.getStackInSlot(slot).isEmpty()){
			if(isValidFluid(FluidUtil.getFluidContained(inventory.getStackInSlot(slot)))){
				return true;	
			}
		}
		return false;
	}

	private boolean isValidFluid(FluidStack stack) {
		if(stack == null)
			return false;
		return getHEFromFuel(stack.getFluid()) > 0;
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
	public FluidTankNTM[] getSendingTanks() {
		return this.getSmokeTanks();
	}

	@Override
	public FluidTankNTM[] getReceivingTanks() {
		return new FluidTankNTM[]{tank};
	}

	@Override
	public FluidTankNTM[] getAllTanks() {
		return new FluidTankNTM[]{tank};
	}

	@Override
	public String getConfigName() {
		return "dieselgen";
	}

	@Override
	public void readIfPresent(JsonObject obj) {
		maxPower = IConfigurableMachine.grab(obj, "L:powerCap", maxPower);
		fluidCap = IConfigurableMachine.grab(obj, "I:fuelCap", fluidCap);

		if(obj.has("D[:efficiency")) {
			JsonArray array = obj.get("D[:efficiency").getAsJsonArray();
			for(FT_Combustible.FuelGrade grade : FT_Combustible.FuelGrade.values()) {
				fuelEfficiency.put(grade, array.get(grade.ordinal()).getAsDouble());
			}
		}
	}

	@Override
	public void writeConfig(JsonWriter writer) throws IOException {
		writer.name("L:powerCap").value(maxPower);
		writer.name("I:fuelCap").value(fluidCap);

		String info = "Fuel grades in order: ";
		for(FT_Combustible.FuelGrade grade : FT_Combustible.FuelGrade.values()) info += grade.name() + " ";
		info = info.trim();
		writer.name("INFO").value(info);

		writer.name("D[:efficiency").beginArray().setIndent("");
		for(FT_Combustible.FuelGrade grade : FT_Combustible.FuelGrade.values()) {
			double d = fuelEfficiency.containsKey(grade) ? fuelEfficiency.get(grade) : 0.0D;
			writer.value(d);
		}
		writer.endArray().setIndent("  ");
	}

	@Override
	public Container provideContainer(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return new ContainerMachineDiesel(player.inventory, this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen provideGUI(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return new GUIMachineDiesel(player.inventory, this);
	}
}
