package com.hbm.tileentity.machine;

import api.hbm.fluid.IFluidStandardTransceiver;
import api.hbm.tile.IHeatSource;
import com.hbm.forgefluid.FFUtils;
import com.hbm.forgefluid.ModForgeFluids;
import com.hbm.interfaces.IControlReceiver;
import com.hbm.inventory.FluidCombustionRecipes;
import com.hbm.inventory.container.ContainerOilburner;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTank;
import com.hbm.inventory.fluid.trait.FT_Flammable;
import com.hbm.inventory.fluid.trait.FluidTrait;
import com.hbm.inventory.gui.GUIOilburner;
import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemForgeFluidIdentifier;
import com.hbm.lib.DirPos;
import com.hbm.lib.Library;
import com.hbm.lib.RefStrings;
import com.hbm.packet.FluidTankPacket;
import com.hbm.packet.PacketDispatcher;
import com.hbm.tileentity.IGUIProvider;
import com.hbm.tileentity.TileEntityMachineBase;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class TileEntityHeaterOilburner extends TileEntityMachineBase implements IGUIProvider, IHeatSource, IControlReceiver, IFluidStandardTransceiver, ITickable {
    public boolean isOn = false;

    public FluidTank tank;

    private int cacheHeat = 0;

    public int setting = 1;

    public int heatEnergy = 0;

    public static final int maxHeatEnergy = 1_000_000;
    public FluidTank smoke;
    public FluidTank smoke_leaded;
    public FluidTank smoke_poison;
    public int buffer;

    public TileEntityHeaterOilburner() {
        super(3);

        tank = new FluidTank(Fluids.HEATINGOIL, 16000);
        smoke = new FluidTank(Fluids.SMOKE, buffer);
        smoke_leaded = new FluidTank(Fluids.SMOKE_LEADED, buffer);
        smoke_poison = new FluidTank(Fluids.SMOKE_POISON, buffer);
    }

    public DirPos[] getConPos() {
        return new DirPos[] {
                new DirPos(pos.getX() + 2, pos.getY(), pos.getZ(), Library.POS_X),
                new DirPos(pos.getX() - 2, pos.getY(), pos.getZ(), Library.NEG_X),
                new DirPos(pos.getX(), pos.getY(), pos.getZ() + 2, Library.POS_Z),
                new DirPos(pos.getX(), pos.getY(), pos.getZ() - 2, Library.NEG_Z)
        };
    }

    @Override
    public void update() {

        if(!world.isRemote) {

            tank.loadTank(0, 1, inventory);
            tank.setType(2, inventory);

            for(DirPos pos : this.getConPos()) {
                this.trySubscribe(tank.getTankType(), world, pos.getPos().getX(), pos.getPos().getY(), pos.getPos().getZ(), pos.getDir());
            }

            boolean shouldCool = true;

            if(this.isOn && this.heatEnergy < maxHeatEnergy) {

                if(tank.getTankType().hasTrait(FT_Flammable.class)) {
                    FT_Flammable type = tank.getTankType().getTrait(FT_Flammable.class);

                    int burnRate = setting;
                    int toBurn = Math.min(burnRate, tank.getFill());

                    tank.setFill(tank.getFill() - toBurn);

                    int heat = (int)(type.getHeatEnergy() / 1000);

                    this.heatEnergy += heat * toBurn;

                    shouldCool = false;
                }
            }

            if(this.heatEnergy >= maxHeatEnergy)
                shouldCool = false;

            if(shouldCool)
                this.heatEnergy = Math.max(this.heatEnergy - Math.max(this.heatEnergy / 1000, 1), 0);

            this.networkPackNT(25);
        }
    }

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        tank.serialize(buf);

        buf.writeBoolean(isOn);
        buf.writeInt(heatEnergy);
        buf.writeByte((byte) this.setting);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        tank.deserialize(buf);

        isOn = buf.readBoolean();
        heatEnergy = buf.readInt();
        setting = buf.readByte();
    }

    @Override
    public String getName() {
        return "container.heaterOilburner";
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        tank.readFromNBT(nbt, "tank");
        isOn = nbt.getBoolean("isOn");
        heatEnergy = nbt.getInteger("heatEnergy");
        setting = nbt.getByte("setting");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        tank.writeToNBT(nbt, "tank");
        nbt.setBoolean("isOn", isOn);
        nbt.setInteger("heatEnergy", heatEnergy);
        nbt.setByte("setting", (byte) this.setting);
        return nbt;
    }

    public void toggleSettingUp() {
        setting++;

        if(setting > 100) {
            setting = 1;
        }
    }

    public void toggleSettingDown() {
        setting--;

        if(setting < 1) {
            setting = 100;
        }
    }

    @Override
    public FluidTank[] getReceivingTanks()  {
        return new FluidTank[] { tank };
    }

    @Override
    public Container provideContainer(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return new ContainerOilburner(player.inventory, this);
    }

    @SideOnly(Side.CLIENT)
    private ResourceLocation texture;

    @Override
    @SideOnly(Side.CLIENT)
    public GuiScreen provideGUI(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if(texture == null) {
            texture = new ResourceLocation(RefStrings.MODID + ":textures/gui/machine/gui_oilburner.png");
        }

        return new GUIOilburner(player.inventory, this, texture);
    }

    @Override
    public int getHeatStored() {
        return heatEnergy;
    }

    @Override
    public void useUpHeat(int heat) {
        this.heatEnergy = Math.max(0, this.heatEnergy - heat);
    }

    @Override
    public boolean hasPermission(EntityPlayer player) {
        return player.getDistanceSq(pos) <= 256;
    }

    @Override
    public void receiveControl(NBTTagCompound data) {
        if(data.hasKey("toggle")) {
            this.isOn = !this.isOn;
        }

        this.markDirty();
    }

    AxisAlignedBB bb = null;

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        if(bb == null) {
            bb = new AxisAlignedBB(pos.getX() - 1, pos.getY(), pos.getZ() - 1, pos.getX() + 2, pos.getY() + 2, pos.getZ() + 2);
        }

        return bb;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return 65536.0D;
    }

    @Override
    public FluidTank[] getAllTanks() {
        return new FluidTank[] { tank };
    }

    @Override
    public FluidTank[] getSendingTanks() {
        return new FluidTank[] {smoke, smoke_leaded, smoke_poison};
    }
}
