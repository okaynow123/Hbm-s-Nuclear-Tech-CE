package com.hbm.items.tool;

import com.hbm.inventory.container.ContainerAmmoBag;
import com.hbm.inventory.gui.GUIAmmoBag;
import com.hbm.items.ItemBakedBase;
import com.hbm.items.ModItems;
import com.hbm.main.MainRegistry;
import com.hbm.tileentity.IGUIProvider;
import com.hbm.util.ItemStackUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.Arrays;

public class ItemAmmoBag extends ItemBakedBase implements IGUIProvider {

    public ItemAmmoBag(String s) {
        super(s);
        this.setMaxStackSize(1);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 1;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (!world.isRemote) {
            player.openGui(MainRegistry.instance, 0, world, 0, 0, 0);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    @Override
    public Container provideContainer(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return new ContainerAmmoBag(player.inventory, new InventoryAmmoBag(getBagFromPlayer(player)));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiScreen provideGUI(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return new GUIAmmoBag(player.inventory, new InventoryAmmoBag(getBagFromPlayer(player)));
    }

    private static ItemStack getBagFromPlayer(EntityPlayer player) {
        ItemStack main = player.getHeldItemMainhand();
        if (!main.isEmpty() && main.getItem() instanceof ItemAmmoBag) return main;
        ItemStack off = player.getHeldItemOffhand();
        if (!off.isEmpty() && off.getItem() instanceof ItemAmmoBag) return off;
        return main;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        if (this == ModItems.ammo_bag_infinite) return false;
        return !stack.hasTagCompound() || getDurabilityForDisplay(stack) != 0;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        if (!stack.hasTagCompound()) return 1D;

        InventoryAmmoBag inv = new InventoryAmmoBag(stack);
        int capacity = 0;
        int bullets = 0;
        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack slot = inv.getStackInSlot(i);
            if (slot.isEmpty()) {
                capacity += 64;
            } else {
                capacity += slot.getMaxStackSize();
                bullets += slot.getCount();
            }
        }
        return 1D - (double) bullets / (double) capacity;
    }

    public static class InventoryAmmoBag implements IItemHandlerModifiable {

        public final ItemStack box;
        public final ItemStack[] slots;

        public InventoryAmmoBag(ItemStack bag) {
            this.box = bag;
            this.slots = new ItemStack[this.getSlots()];
            Arrays.fill(this.slots, ItemStack.EMPTY);

            if (!bag.hasTagCompound())
                bag.setTagCompound(new NBTTagCompound());

            ItemStack[] fromNBT = ItemStackUtil.readStacksFromNBT(bag);

            if (fromNBT != null) {
                for (int i = 0; i < slots.length; i++) {
                    slots[i] = fromNBT[i] == null ? ItemStack.EMPTY : fromNBT[i];
                }
            }
        }

        @Override
        public int getSlots() {
            return 8;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return slots[slot];
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (stack.isEmpty()) return ItemStack.EMPTY;
            if (stack.hasTagCompound()) return stack;

            ItemStack existing = slots[slot];

            int limit = Math.min(getSlotLimit(slot), stack.getMaxStackSize());

            if (!existing.isEmpty()) {
                if (!ItemStack.areItemsEqual(existing, stack) || !ItemStack.areItemStackTagsEqual(existing, stack)) {
                    return stack;
                }
                limit -= existing.getCount();
            }

            if (limit <= 0) return stack;

            boolean reachedLimit = stack.getCount() > limit;

            if (!simulate) {
                if (existing.isEmpty()) {
                    ItemStack toInsert = stack.copy();
                    toInsert.setCount(reachedLimit ? limit : stack.getCount());
                    slots[slot] = toInsert;
                } else {
                    existing.grow(reachedLimit ? limit : stack.getCount());
                }
                save();
            }

            if (reachedLimit) {
                ItemStack remainder = stack.copy();
                remainder.setCount(stack.getCount() - limit);
                return remainder;
            } else {
                return ItemStack.EMPTY;
            }
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (amount <= 0) return ItemStack.EMPTY;

            ItemStack existing = slots[slot];
            if (existing.isEmpty()) return ItemStack.EMPTY;

            int toExtract = Math.min(amount, existing.getCount());

            ItemStack extracted = existing.copy();
            extracted.setCount(toExtract);

            if (!simulate) {
                if (toExtract == existing.getCount()) {
                    slots[slot] = ItemStack.EMPTY;
                } else {
                    existing.shrink(toExtract);
                }
                save();
            }

            return extracted;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 64;
        }

        @Override
        public void setStackInSlot(int slot, ItemStack stack) {
            if (!stack.isEmpty()) {
                int limit = Math.min(getSlotLimit(slot), stack.getMaxStackSize());
                if (stack.getCount() > limit) {
                    stack = stack.copy();
                    stack.setCount(limit);
                }
                if (stack.hasTagCompound()) {
                    stack = ItemStack.EMPTY;
                }
            }
            slots[slot] = stack.isEmpty() ? ItemStack.EMPTY : stack;
            save();
        }

        private void save() {
            for (int i = 0; i < getSlots(); ++i) {
                ItemStack s = slots[i];
                if (!s.isEmpty() && s.getCount() == 0) {
                    slots[i] = ItemStack.EMPTY;
                }
            }
            ItemStackUtil.addStacksToNBT(box, slots);
        }
    }
}
