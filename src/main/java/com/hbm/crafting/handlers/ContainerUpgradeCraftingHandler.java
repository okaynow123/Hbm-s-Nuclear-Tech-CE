package com.hbm.crafting.handlers;

import com.hbm.blocks.generic.BlockStorageCrate;
import com.hbm.blocks.machine.BlockMassStorage;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class ContainerUpgradeCraftingHandler extends ShapedOreRecipe {

    public ContainerUpgradeCraftingHandler(ResourceLocation group, ItemStack result, Object... items) {
        super(group, result, items);
    }

    public ContainerUpgradeCraftingHandler(ItemStack result, Object... recipe) {
        this(null, result, recipe);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventoryCrafting) {
        ItemStack source = getFirstContainer(inventoryCrafting);
        ItemStack result = super.getCraftingResult(inventoryCrafting);
        if (!source.isEmpty() && source.hasTagCompound()) {
            NBTTagCompound tag = source.getTagCompound().copy();
            result.setTagCompound(tag);
        }
        return result;
    }

    private static ItemStack getFirstContainer(InventoryCrafting inventoryCrafting) {
        for (int i = 0; i < inventoryCrafting.getSizeInventory(); ++i) {
            ItemStack itemstack = inventoryCrafting.getStackInSlot(i);
            if (itemstack.isEmpty()) continue;

            Block block = Block.getBlockFromItem(itemstack.getItem());
            if (block == Blocks.AIR) continue;
            if(block instanceof BlockStorageCrate || block instanceof BlockMassStorage) return itemstack;
        }
        return null;
    }
}
