package com.hbm.capability;

import com.hbm.api.fluidmk2.IFluidProviderMK2;
import com.hbm.api.fluidmk2.IFluidReceiverMK2;
import com.hbm.api.fluidmk2.IFluidUserMK2;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.hbm.capability.NTMFluidCapabilityHandler.getFluidType;

public class NTMFluidHandlerWrapper implements IFluidHandler {
    @Nullable
    private final IFluidReceiverMK2 receiver;
    @Nullable
    private final IFluidProviderMK2 provider;
    @NotNull
    private final IFluidUserMK2 user;
    @Nullable
    private final BlockPos accessor;

    public NTMFluidHandlerWrapper(@NotNull TileEntity handler, @Nullable BlockPos pos) {
        if (handler instanceof IFluidProviderMK2 providerMK2) this.provider = providerMK2;
        else provider = null;
        if (handler instanceof IFluidReceiverMK2 receiverMK2) this.receiver = receiverMK2;
        else receiver = null;
        if (receiver == null && provider == null)
            throw new IllegalArgumentException("TileEntity must implement IFluidReceiverMK2 or IFluidProviderMK2");
        user = (IFluidUserMK2) handler;
        this.accessor = pos;
    }

    public NTMFluidHandlerWrapper(@NotNull TileEntity handler) {
        this(handler, null);
    }

    private static int clampToInt(long v) {
        if (v <= 0) return 0;
        return v > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) v;
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        List<IFluidTankProperties> properties = new ArrayList<>();
        for (FluidTankNTM tank : user.getAllTanks()) {
            Collections.addAll(properties, tank.getTankProperties());
        }
        return properties.toArray(new IFluidTankProperties[0]);
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (resource == null || resource.amount <= 0 || receiver == null) return 0;
        FluidType type = getFluidType(resource.getFluid());
        if (type == null) return 0;
        long demand = receiver.getDemand(type, 0);
        if (demand <= 0) return 0;
        int offer = Math.min(resource.amount, clampToInt(demand));
        if (!doFill) return offer;
        int remainder = (int) receiver.transferFluid(type, 0, offer);
        return offer - remainder;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        if (resource == null || resource.amount <= 0 || provider == null) return null;
        FluidType type = getFluidType(resource.getFluid());
        if (type == null) return null;
        long available = provider.getFluidAvailable(type, 0);
        if (available <= 0) return null;
        int toDrain = Math.min(resource.amount, clampToInt(available));
        if (toDrain <= 0) return null;
        if (doDrain) provider.useUpFluid(type, 0, toDrain);
        FluidStack out = resource.copy();
        out.amount = toDrain;
        return out;
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        if (maxDrain <= 0 || provider == null) return null;
        for (FluidTankNTM tank : provider.getAllTanks()) {
            FluidType type = tank.getTankType();
            long available = provider.getFluidAvailable(type, 0);
            if (available <= 0) continue;
            int toDrain = Math.min(maxDrain, clampToInt(available));
            if (toDrain <= 0) continue;
            FluidStack exemplar = tank.drain(toDrain, false);
            if (exemplar == null || exemplar.getFluid() == null) continue;
            exemplar.amount = toDrain;
            if (doDrain) provider.useUpFluid(type, 0, toDrain);
            return exemplar;
        }
        return null;
    }
}
