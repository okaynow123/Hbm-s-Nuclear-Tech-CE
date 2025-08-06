package com.hbm.handler;

import com.hbm.capability.HbmCapability;
import com.hbm.items.IKeybindReceiver;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class HbmKeybindsServer {

    /** Can't put this in HbmKeybinds because it's littered with clientonly stuff */
    public static void onPressedServer(EntityPlayer player, HbmKeybinds.EnumKeybind key, boolean state) {

        // EXTPROP HANDLING
        HbmCapability.IHBMData props = HbmCapability.getData(player);
        props.setKeyPressed(key, state);

        // ITEM HANDLING
        ItemStack held = player.getHeldItemMainhand();
        if(!held.isEmpty() && held.getItem() instanceof IKeybindReceiver) {
            IKeybindReceiver rec = (IKeybindReceiver) held.getItem();
            if(rec.canHandleKeybind(player, held, key)) rec.handleKeybind(player, held, key, state);
        }
    }
}

