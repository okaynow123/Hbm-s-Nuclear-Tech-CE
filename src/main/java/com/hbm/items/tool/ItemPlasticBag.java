package com.hbm.items.tool;

import com.hbm.inventory.container.ContainerPlasticBag;
import com.hbm.inventory.gui.GUIPlasticBag;
import com.hbm.items.ItemBakedBase;
import com.hbm.main.MainRegistry;
import com.hbm.tileentity.IGUIProvider;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;

public class ItemPlasticBag extends ItemBakedBase implements IGUIProvider {

    public ItemPlasticBag(String s) {
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
        if(!worldIn.isRemote) playerIn.openGui(MainRegistry.instance, 0, worldIn, 0, 0, 0);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public Container provideContainer(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return new ContainerPlasticBag(player.inventory, new InventoryPlasticBag(player.getHeldItemMainhand()));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiScreen provideGUI(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return new GUIPlasticBag(player.inventory, new InventoryPlasticBag(player.getHeldItemMainhand()));
    }

    public static class InventoryPlasticBag extends ItemStackHandler {

        public final ItemStack bag;

        public InventoryPlasticBag(ItemStack bag) {
            super(1);
            this.bag = bag;

            if(!bag.hasTagCompound())
                bag.setTagCompound(new NBTTagCompound());

            this.deserializeNBT(bag.getTagCompound().getCompoundTag("Inventory"));
        }

        @Override
        protected void onContentsChanged(int slot) {
            bag.getTagCompound().setTag("Inventory", this.serializeNBT());
        }

        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return 1;
        }
    }
}
