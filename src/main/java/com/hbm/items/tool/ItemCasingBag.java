package com.hbm.items.tool;

import com.hbm.inventory.container.ContainerCasingBag;
import com.hbm.inventory.gui.GUICasingBag;
import com.hbm.items.ItemBakedBase;
import com.hbm.main.MainRegistry;
import com.hbm.tileentity.IGUIProvider;
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
import net.minecraftforge.items.ItemStackHandler;

public class ItemCasingBag extends ItemBakedBase implements IGUIProvider {

    public ItemCasingBag(String s){
        super(s);
        this.setMaxStackSize(1);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 1;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if(!worldIn.isRemote) playerIn.openGui(MainRegistry.instance, 0, worldIn, handIn.ordinal(), 0, 0);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public Container provideContainer(int ID, EntityPlayer player, World world, int x, int y, int z) {
        EnumHand hand = EnumHand.values()[x];
        ItemStack held = player.getHeldItem(hand);
        return new ContainerCasingBag(player.inventory, new InventoryCasingBag(held), hand);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiScreen provideGUI(int ID, EntityPlayer player, World world, int x, int y, int z) {
        EnumHand hand = EnumHand.values()[x];
        ItemStack held = player.getHeldItem(hand);
        return new GUICasingBag(player.inventory, new InventoryCasingBag(held));
    }

    /**
     * Returns true if ammo was able to be added
     * @param bag
     * @param casing
     * @param amount
     * @return
     */
    // Th3_Sl1ze: that's meant for weapon rework
    public static boolean pushCasing(ItemStack bag, ItemStack casing, float amount) {
        if(!bag.hasTagCompound()) bag.setTagCompound(new NBTTagCompound());
        String name = casing.getTranslationKey() + "@" + casing.getMetadata();
        float current = bag.getTagCompound().getFloat(name);
        boolean ret = false;
        if(current < 1) {
            ret = true;
            bag.getTagCompound().setFloat(name, current + amount);
        }
        if(bag.getTagCompound().getFloat(name) >= 1) {
            InventoryCasingBag inv = new InventoryCasingBag(bag);
            while(bag.getTagCompound().getFloat(name) >= 1) {
                ItemStack toAdd = casing.copy();
                toAdd.setCount(1);
                boolean didSomething = false;
                // merge into existing
                for(int i = 0; i < inv.getSlots(); i++) {
                    ItemStack slot = inv.getStackInSlot(i);
                    if(!slot.isEmpty() && slot.isItemEqual(toAdd) && ItemStack.areItemStackTagsEqual(slot, toAdd)) {
                        int max = slot.getMaxStackSize();
                        int am = Math.min(toAdd.getCount(), max - slot.getCount());
                        if(am > 0) {
                            ItemStack newSlot = slot.copy();
                            newSlot.setCount(newSlot.getCount() + am);
                            inv.setStackInSlot(i, newSlot);
                            toAdd.setCount(toAdd.getCount() - am);
                            didSomething = true;
                        }
                    }
                    if(toAdd.isEmpty()) break;
                }
                // place into empty
                for(int i = 0; i < inv.getSlots(); i++) {
                    ItemStack slot = inv.getStackInSlot(i);
                    if(slot.isEmpty()) {
                        inv.setStackInSlot(i, toAdd);
                        didSomething = true;
                        break;
                    }
                    if(toAdd.isEmpty()) break;
                }
                if(didSomething) {
                    bag.getTagCompound().setFloat(name, bag.getTagCompound().getFloat(name) - 1F);
                    ret = true;
                } else {
                    break;
                }
            }
        }
        return ret;
    }

    public static class InventoryCasingBag extends ItemStackHandler {

        public final ItemStack box;

        public InventoryCasingBag(ItemStack box) {
            super(15);
            this.box = box;

            if(!box.hasTagCompound())
                box.setTagCompound(new NBTTagCompound());

            this.deserializeNBT(box.getTagCompound().getCompoundTag("Inventory"));
        }

        @Override
        protected void onContentsChanged(int slot) {
            box.getTagCompound().setTag("Inventory", this.serializeNBT());
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return false;
        }
    }
}
