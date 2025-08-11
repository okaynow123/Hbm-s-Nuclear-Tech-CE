package com.hbm.capability;

import com.hbm.api.energymk2.IEnergyHandlerMK2;
import com.hbm.api.energymk2.IEnergyProviderMK2;
import com.hbm.api.energymk2.IEnergyReceiverMK2;
import com.hbm.config.GeneralConfig;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

/**
 * This class is a wrapper for {@link IEnergyHandlerMK2}, exposing it as {@link IEnergyStorage} for Forge Energy compatibility.
 *
 * @author mlbv
 */
public class NTMEnergyCapabilityWrapper implements IEnergyStorage {

    private final IEnergyHandlerMK2 handler;
    private final IEnergyReceiverMK2 receiver;
    private final IEnergyProviderMK2 provider;

    public NTMEnergyCapabilityWrapper(@NotNull IEnergyHandlerMK2 handler) {
        this.handler = handler;
        this.receiver = handler instanceof IEnergyReceiverMK2 ? (IEnergyReceiverMK2) handler : null;
        this.provider = handler instanceof IEnergyProviderMK2 ? (IEnergyProviderMK2) handler : null;
    }

    public static NTMEnergyCapabilityWrapper from(TileEntity handler) {
        if (!(handler instanceof IEnergyHandlerMK2 energyHandlerMK2)) throw new IllegalArgumentException("Handler must implement EnergyHandlerMK2");
        return new NTMEnergyCapabilityWrapper(energyHandlerMK2);
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive() || maxReceive <= 0 || GeneralConfig.conversionRateHeToRF <= 0) return 0;
        long heToOffer = (long) Math.floor(maxReceive / GeneralConfig.conversionRateHeToRF);
        if (heToOffer <= 0) return simulate ? 1 : 0;
        long leftoverHE = receiver.transferPower(heToOffer, simulate);
        long acceptedHE = heToOffer - leftoverHE;
        long acceptedFE = Math.round(acceptedHE * GeneralConfig.conversionRateHeToRF);
        return (int) Math.min(maxReceive, Math.min(Integer.MAX_VALUE, acceptedFE));
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract() || maxExtract <= 0 || GeneralConfig.conversionRateHeToRF <= 0) return 0;
        long heBudget = (long) Math.floor(maxExtract / GeneralConfig.conversionRateHeToRF);
        if (heBudget <= 0) return simulate ? 1 : 0;
        long availableHE = Math.min(provider.getPower(), provider.getProviderSpeed());
        long heToExtract = Math.min(heBudget, availableHE);
        if (heToExtract <= 0) return 0;
        if (!simulate) provider.usePower(heToExtract);
        long feExtracted = Math.round(heToExtract * GeneralConfig.conversionRateHeToRF);
        return (int) Math.min(maxExtract, Math.min(Integer.MAX_VALUE, feExtracted));
    }

    @Override
    public int getEnergyStored() {
        return (int) Math.min(Integer.MAX_VALUE, Math.round(handler.getPower() * GeneralConfig.conversionRateHeToRF));
    }

    @Override
    public int getMaxEnergyStored() {
        return (int) Math.min(Integer.MAX_VALUE, Math.round(handler.getMaxPower() * GeneralConfig.conversionRateHeToRF));
    }

    @Override
    public boolean canExtract() {
        return provider != null && provider.getPower() > 0;
    }

    @Override
    public boolean canReceive() {
        return receiver != null && receiver.getPower() < receiver.getMaxPower();
    }
}
