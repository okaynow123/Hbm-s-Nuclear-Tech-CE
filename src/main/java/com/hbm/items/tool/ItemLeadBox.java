package com.hbm.items.tool;

import com.hbm.inventory.container.ContainerLeadBox;
import com.hbm.inventory.gui.GUILeadBox;
import com.hbm.items.ItemBakedBase;
import com.hbm.items.ModItems;
import com.hbm.lib.HBMSoundHandler;
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
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;

public class ItemLeadBox extends ItemBakedBase implements IGUIProvider {

	public ItemLeadBox(String s){
		super(s);
		this.setMaxStackSize(1);
	}
	
	// Without this method, your inventory will NOT work!!!
	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 1; // return any value greater than zero
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if(!world.isRemote) player.openGui(MainRegistry.instance, 0, world, 0, 0, 0);
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public Container provideContainer(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return new ContainerLeadBox(player.inventory, new InventoryLeadBox(player, player.getHeldItemMainhand()));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen provideGUI(int ID, EntityPlayer player, World world, int x, int y, int z) {
		EnumHand hand = EnumHand.values()[x];
		ItemStack held = player.getHeldItem(hand);
		return new GUILeadBox(player.inventory, new InventoryLeadBox(player, held));
	}

	public static class InventoryLeadBox extends ItemStackHandler {

		public final EntityPlayer player;
		public final ItemStack box;

		public InventoryLeadBox(EntityPlayer player, ItemStack box) {
			super(20);
			this.player = player;
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
		protected int getStackLimit(int slot, ItemStack stack) {
			return 1;
		}

		public void openInventory() {
			if(!player.world.isRemote)
				player.world.playSound(null, player.posX, player.posY, player.posZ, HBMSoundHandler.crateOpen, SoundCategory.BLOCKS, 1.0F, 0.8F);
		}

		public void closeInventory() {
			if(!player.world.isRemote)
				player.world.playSound(null, player.posX, player.posY, player.posZ, HBMSoundHandler.crateClose, SoundCategory.BLOCKS, 1.0F, 0.8F);
		}
	}
}
