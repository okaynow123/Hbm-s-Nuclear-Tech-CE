package com.hbm.tileentity.machine;

import com.hbm.items.ModItems;
import com.hbm.items.tool.ItemKeyPin;
import com.hbm.lib.HBMSoundHandler;
import com.hbm.tileentity.IGUIProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public abstract class TileEntityCrateBase extends TileEntityLockableBase implements IGUIProvider {

    public ItemStackHandler inventory;
    @SideOnly(Side.CLIENT)
    public boolean inventoryContentsChanged = true;
    @SideOnly(Side.CLIENT)
    public float cachedFillPercentage = 0.0F;
    protected String customName;
    protected String name;

    public TileEntityCrateBase(int scount, String name) {
        inventory = new ItemStackHandler(scount) {
            @Override
            protected void onContentsChanged(int slot) {
                markDirty();
                if (world != null && world.isRemote) {
                    inventoryContentsChanged = true;
                }
            }
        };
        this.name = name;
    }

    public static void openInventory(EntityPlayer player) {
        player.world.playSound(player.posX, player.posY, player.posZ, HBMSoundHandler.crateOpen, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
    }

    public static void closeInventory(EntityPlayer player) {
        player.world.playSound(player.posX, player.posY, player.posZ, HBMSoundHandler.crateClose, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
    }

    @Override
    public boolean canAccess(EntityPlayer player) {

        if (!this.isLocked() || player == null) {
            return true;
        } else {
            ItemStack stack = player.getHeldItemMainhand();

            if (stack.getItem() instanceof ItemKeyPin && ItemKeyPin.getPins(stack) == this.lock) {
                world.playSound(null, player.posX, player.posY, player.posZ, HBMSoundHandler.lockOpen, SoundCategory.BLOCKS, 1.0F, 1.0F);
                return true;
            }

            if (stack.getItem() == ModItems.key_red) {
                world.playSound(null, player.posX, player.posY, player.posZ, HBMSoundHandler.lockOpen, SoundCategory.BLOCKS, 1.0F, 1.0F);
                return true;
            }

            return this.tryPick(player);
        }
    }

    public String getInventoryName() {
        return this.hasCustomInventoryName() ? this.customName : name;
    }

    public boolean hasCustomInventoryName() {
        return this.customName != null && !this.customName.isEmpty();
    }

    public void setCustomName(String name) {
        this.customName = name;
    }

    public boolean isUseableByPlayer(EntityPlayer player) {
        if (world.getTileEntity(pos) != this) {
            return false;
        } else {
            return player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64;
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("inventory")) {
            inventory.deserializeNBT(compound.getCompoundTag("inventory"));
            if (this.world != null && this.world.isRemote) {
                this.inventoryContentsChanged = true;
            }
        }
    }

    @Override
    public @NotNull NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("inventory", inventory.serializeNBT());
        return compound;
    }

    @Override
    public boolean hasCapability(@NotNull Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && !isLocked() || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(@NotNull Capability<T> capability, EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && !isLocked() ?
                CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inventory) : super.getCapability(capability, facing);
    }
}
