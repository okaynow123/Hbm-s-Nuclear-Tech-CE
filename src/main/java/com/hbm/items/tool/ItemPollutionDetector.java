package com.hbm.items.tool;

import com.hbm.handler.pollution.PollutionHandler;
import com.hbm.handler.pollution.PollutionHandler.PollutionData;
import com.hbm.handler.pollution.PollutionHandler.PollutionType;
import com.hbm.items.ItemBakedBase;
import com.hbm.items.ModItems;
import com.hbm.packet.PacketDispatcher;
import com.hbm.packet.PlayerInformPacket;

import com.hbm.packet.PlayerInformPacketLegacy;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemPollutionDetector extends ItemBakedBase {
    public ItemPollutionDetector(String s) {
        super(s);
    }
    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!(entityIn instanceof EntityPlayerMP) || worldIn.getTotalWorldTime() % 10 != 0) {
            return;
        }
        PollutionData data = PollutionHandler.getPollutionData(worldIn, entityIn.getPosition());
        if (data == null) {
            data = new PollutionData();
        }

        float soot = ((int) (data.pollution[PollutionType.SOOT.ordinal()] * 100)) / 100F;
        float poison = ((int) (data.pollution[PollutionType.POISON.ordinal()] * 100)) / 100F;
        float heavymetal = ((int) (data.pollution[PollutionType.HEAVYMETAL.ordinal()] * 100)) / 100F;
        float fallout = ((int) (data.pollution[PollutionType.FALLOUT.ordinal()] * 100)) / 100F;

        PacketDispatcher.wrapper.sendTo(new PlayerInformPacketLegacy(new TextComponentString(TextFormatting.YELLOW + "Soot: " + soot), 100, 4000), (EntityPlayerMP) entityIn);
        PacketDispatcher.wrapper.sendTo(new PlayerInformPacketLegacy(new TextComponentString(TextFormatting.YELLOW + "Poison: " + poison), 101, 4000), (EntityPlayerMP) entityIn);
        PacketDispatcher.wrapper.sendTo(new PlayerInformPacketLegacy(new TextComponentString(TextFormatting.YELLOW + "Heavy metal: " + heavymetal), 102, 4000), (EntityPlayerMP) entityIn);
        PacketDispatcher.wrapper.sendTo(new PlayerInformPacketLegacy(new TextComponentString(TextFormatting.YELLOW + "Fallout: " + fallout), 103, 4000), (EntityPlayerMP) entityIn);
    }
}
