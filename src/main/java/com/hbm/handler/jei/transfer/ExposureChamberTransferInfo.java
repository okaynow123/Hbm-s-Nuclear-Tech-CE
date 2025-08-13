package com.hbm.handler.jei.transfer;

import com.hbm.handler.jei.JEIConfig;
import com.hbm.inventory.container.ContainerMachineExposureChamber;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.inventory.Slot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExposureChamberTransferInfo implements IRecipeTransferInfo<ContainerMachineExposureChamber> {

    @Override
    public Class<ContainerMachineExposureChamber> getContainerClass() {
        return ContainerMachineExposureChamber.class;
    }

    @Override
    public String getRecipeCategoryUid() {
        return JEIConfig.EXPOSURE;
    }

    @Override
    public List<Slot> getRecipeSlots(ContainerMachineExposureChamber container) {
        return Arrays.asList(container.getSlot(0), container.getSlot(3));
    }

    @Override
    public List<Slot> getInventorySlots(ContainerMachineExposureChamber container) {
        List<Slot> slots = new ArrayList<>();
        for (int i = 8; i < 8 + 36; i++) {
            slots.add(container.getSlot(i));
        }
        return slots;
    }

    @Override
    public boolean canHandle(ContainerMachineExposureChamber container) {
        return true;
    }
}