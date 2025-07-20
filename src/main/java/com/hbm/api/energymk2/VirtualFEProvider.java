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

public class VirtualFEProvider implements IEnergyProviderMK2 {
    private final World world;
    private final BlockPos pos;
    private final EnumFacing facing;

    public VirtualFEProvider(World world, BlockPos pos, EnumFacing facing) {
        this.world = world;
        this.pos = pos;
        this.facing = facing;
    }

    private IEnergyStorage getStorage() {
        TileEntity te = world.getTileEntity(pos);
        return (te != null && !te.isInvalid()) ? te.getCapability(CapabilityEnergy.ENERGY, facing) : null;
    }

    @Override
    public void usePower(long he) {
        IEnergyStorage storage = getStorage();
        if (storage == null || !storage.canExtract() || he <= 0 || GeneralConfig.conversionRateHeToRF <= 0) return;

        long feToExtract = Math.round(he * GeneralConfig.conversionRateHeToRF);
        storage.extractEnergy((int) Math.min(feToExtract, Integer.MAX_VALUE), false);
    }

    @Override
    public long getProviderSpeed() {
        return Long.MAX_VALUE; // Нет лимита со стороны кабеля
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
        // Аналогично VirtualFEReceiver: no-op или реализуйте, если нужно
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
