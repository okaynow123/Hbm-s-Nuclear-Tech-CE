package com.hbm.items.armor;

import com.hbm.handler.ArmorModHandler;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.List;

public class ItemModShield extends ItemArmorMod{

    public final float shield;

    public ItemModShield(float shield, String s) {
        super(ArmorModHandler.kevlar, false, true, false, false, s);
        this.shield = shield;
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> list, ITooltipFlag flagIn) {
        String color = "" + (System.currentTimeMillis() % 1000 < 500 ? TextFormatting.YELLOW : TextFormatting.GOLD);
        list.add(color + "+" + (Math.round(shield * 10) * 0.1) + " shield");
        list.add("");
        super.addInformation(stack, worldIn, list, flagIn);
    }

    @Override
    public void addDesc(List<String> list, ItemStack stack, ItemStack armor) {
        String color = "" + (System.currentTimeMillis() % 1000 < 500 ? TextFormatting.YELLOW : TextFormatting.GOLD);
        list.add(color + "  " + stack.getDisplayName() + " (+" + (Math.round(shield * 10) * 0.1) + " health)");
    }
}
