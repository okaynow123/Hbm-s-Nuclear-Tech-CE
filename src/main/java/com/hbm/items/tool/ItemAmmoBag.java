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
import net.minecraftforge.items.ItemStackHandler;

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

    public static class InventoryAmmoBag extends ItemStackHandler {

        public final ItemStack box;

        public InventoryAmmoBag(ItemStack bag) {
            super(8);
            this.box = bag;

            if (!bag.hasTagCompound()) {
                bag.setTagCompound(new NBTTagCompound());
            }

            // Load inventory from the bag's NBT
            NBTTagCompound invTag = bag.getTagCompound().getCompoundTag("Inventory");
            this.deserializeNBT(invTag);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return !stack.hasTagCompound();
        }

        @Override
        protected void onContentsChanged(int slot) {
            NBTTagCompound invTag = this.serializeNBT();
            box.getTagCompound().setTag("Inventory", invTag);
        }
    }
}
