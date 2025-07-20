package com.hbm.api.energymk2;

import com.hbm.config.GeneralConfig;
import com.hbm.lib.ForgeDirection;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class VirtualFEReceiver implements IEnergyReceiverMK2 {
    private final World world;
    private final BlockPos pos;
    private final EnumFacing facing;

    public VirtualFEReceiver(World world, BlockPos pos, EnumFacing facing) {
        this.world = world;
        this.pos = pos;
        this.facing = facing;
    }

    private IEnergyStorage getStorage() {
        TileEntity te = world.getTileEntity(pos);
        return (te != null && !te.isInvalid()) ? te.getCapability(CapabilityEnergy.ENERGY, facing) : null;
    }

    @Override
    public long transferPower(long he, boolean simulate) {
        IEnergyStorage storage = getStorage();
        if (storage == null || !storage.canReceive() || he <= 0 || GeneralConfig.conversionRateHeToRF <= 0) return he;

        long feBudget = Math.round(he * GeneralConfig.conversionRateHeToRF);
        if (feBudget <= 0) return he;

        int feAccepted = storage.receiveEnergy((int) Math.min(feBudget, Integer.MAX_VALUE), simulate);
        long heAccepted = (long) Math.floor(feAccepted / GeneralConfig.conversionRateHeToRF);
        return he - heAccepted;
    }

    @Override
    public long getReceiverSpeed() {
        return Long.MAX_VALUE; // Нет лимита со стороны кабеля, лимит — в FE-блоке
    }

    @Override
    public ConnectionPriority getPriority() {
        return ConnectionPriority.NORMAL;
    }

    @Override
    public long getPower() {
        IEnergyStorage storage = getStorage();
        return (storage != null) ? (long) Math.floor(storage.getEnergyStored() / GeneralConfig.conversionRateHeToRF) : 0;
    }

    @Override
    public long getMaxPower() {
        IEnergyStorage storage = getStorage();
        return (storage != null) ? (long) Math.floor(storage.getMaxEnergyStored() / GeneralConfig.conversionRateHeToRF) : 0;
    }

    @Override
    public void setPower(long power) {
    }

    @Override
    public boolean canConnect(ForgeDirection dir) {
        return true;
    }

    @Override
    public Vec3d getDebugParticlePosMK2() {
        return new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    public boolean isLoaded() { return true; }
}
