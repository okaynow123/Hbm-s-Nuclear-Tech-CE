package com.hbm.items.special;

import com.hbm.util.I18nUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemDepletedFuel extends ItemNuclearWaste {

    public ItemDepletedFuel(String s) {
        super(s);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list){
        for(int i = 0; i < 2; i++) {
            list.add(new ItemStack(this, 1, i));
        }
    }



    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn){
        if (stack.getItemDamage() > 0)
            tooltip.add(TextFormatting.GOLD + I18nUtil.resolveKey("desc.item.wasteCooling"));
    }

    @SideOnly(Side.CLIENT)
    public void registerColorHandler() {
        ItemColors itemColors = Minecraft.getMinecraft().getItemColors();
        itemColors.registerItemColorHandler(new ColorHandler(), this);
    }

    private static class ColorHandler implements IItemColor {
        @Override
        public int colorMultiplier(ItemStack stack, int tintIndex) {
            return stack.getMetadata() > 0 ? 0xFFBFA5 : 0xFFFFFF;
        }

    }
}
