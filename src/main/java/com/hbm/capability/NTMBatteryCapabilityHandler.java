package com.hbm.capability;

import com.hbm.api.energymk2.IBatteryItem;
import com.hbm.config.GeneralConfig;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Attaches Forge Energy capabilities to any item that implements IBatteryItem.
 *
 * @author mlbv
 */
public class NTMBatteryCapabilityHandler {

    public static final ResourceLocation HBM_BATTERY_CAPABILITY = new ResourceLocation("hbm", "battery_wrapper");

    public static void initialize() {
        MinecraftForge.EVENT_BUS.register(new NTMBatteryCapabilityHandler());
    }

    @SubscribeEvent
    public void onAttachCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStack stack = event.getObject();
        if (stack.isEmpty() || !(stack.getItem() instanceof IBatteryItem batteryItem)) return;
        event.addCapability(HBM_BATTERY_CAPABILITY, new ICapabilityProvider() {
            private IEnergyStorage instance;

            @Override
            public boolean hasCapability(@NotNull Capability<?> capability, @Nullable EnumFacing facing) {
                return capability == CapabilityEnergy.ENERGY;
            }

            @Nullable
            @Override
            public <T> T getCapability(@NotNull Capability<T> capability, @Nullable EnumFacing facing) {
                if (capability != CapabilityEnergy.ENERGY) return null;
                if (instance == null) {
                    instance = new IEnergyStorage() {
                        @Override
                        public int receiveEnergy(int maxReceive, boolean simulate) {
                            if (!canReceive() || maxReceive <= 0 || GeneralConfig.conversionRateHeToRF <= 0) return 0;
                            long powerToOfferInHE = (long) (maxReceive / GeneralConfig.conversionRateHeToRF);
                            if (powerToOfferInHE <= 0) return 0;
                            long spaceInBattery = batteryItem.getMaxCharge() - batteryItem.getCharge(stack);
                            long batteryCanTakeHE = Math.min(spaceInBattery, batteryItem.getChargeRate());
                            long actualToReceiveHE = Math.min(powerToOfferInHE, batteryCanTakeHE);
                            if (actualToReceiveHE > 0 && !simulate) {
                                batteryItem.chargeBattery(stack, actualToReceiveHE);
                            }
                            long receivedFE = (long) (actualToReceiveHE * GeneralConfig.conversionRateHeToRF);
                            return (int) Math.min(Integer.MAX_VALUE, receivedFE);
                        }

                        @Override
                        public int extractEnergy(int maxExtract, boolean simulate) {
                            if (!canExtract() || maxExtract <= 0 || GeneralConfig.conversionRateHeToRF <= 0) {
                                return 0;
                            }
                            long maxExtractHE = (long) (maxExtract / GeneralConfig.conversionRateHeToRF);
                            if (maxExtractHE <= 0) {
                                return 0;
                            }
                            long batteryCanProvideHE = Math.min(batteryItem.getCharge(stack), batteryItem.getDischargeRate());
                            long actualToExtractHE = Math.min(maxExtractHE, batteryCanProvideHE);
                            if (actualToExtractHE > 0 && !simulate) {
                                batteryItem.dischargeBattery(stack, actualToExtractHE);
                            }
                            long extractedFE = (long) (actualToExtractHE * GeneralConfig.conversionRateHeToRF);
                            return (int) Math.min(Integer.MAX_VALUE, extractedFE);
                        }

                        @Override
                        public int getEnergyStored() {
                            if (GeneralConfig.conversionRateHeToRF <= 0) return 0;
                            long storedFE = (long) (batteryItem.getCharge(stack) * GeneralConfig.conversionRateHeToRF);
                            return (int) Math.min(Integer.MAX_VALUE, storedFE);
                        }

                        @Override
                        public int getMaxEnergyStored() {
                            if (GeneralConfig.conversionRateHeToRF <= 0) return 0;
                            long maxStoredHE = batteryItem.getMaxCharge();
                            long maxStoredFE = (long) (maxStoredHE * GeneralConfig.conversionRateHeToRF);
                            return (int) Math.min(Integer.MAX_VALUE, maxStoredFE);
                        }

                        @Override
                        public boolean canExtract() {
                            return batteryItem.getDischargeRate() > 0 && batteryItem.getCharge(stack) > 0;
                        }

                        @Override
                        public boolean canReceive() {
                            return batteryItem.getChargeRate() > 0 && batteryItem.getCharge(stack) < batteryItem.getMaxCharge();
                        }
                    };
                }
                return CapabilityEnergy.ENERGY.cast(instance);
            }
        });
    }

}