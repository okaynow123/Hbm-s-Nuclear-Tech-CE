package com.hbm.capability;

import api.hbm.energymk2.IBatteryItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Attaches Forge Energy capabilities to any item that implements IBatteryItem.
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
        if (stack.isEmpty() || !(stack.getItem() instanceof IBatteryItem)) return;
        event.addCapability(HBM_BATTERY_CAPABILITY, new ICapabilityProvider() {
            private NTMBatteryWrapper instance;
            @Override
            public boolean hasCapability(@NotNull Capability<?> capability, @Nullable EnumFacing facing) {
                return capability == CapabilityEnergy.ENERGY;
            }
            @Nullable
            @Override
            public <T> T getCapability(@NotNull Capability<T> capability, @Nullable EnumFacing facing) {
                if (capability == CapabilityEnergy.ENERGY) {
                    if (instance == null) instance = new NTMBatteryWrapper(stack);
                    return CapabilityEnergy.ENERGY.cast(instance);
                }
                return null;
            }
        });
    }
}