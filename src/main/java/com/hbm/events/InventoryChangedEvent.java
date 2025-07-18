package com.hbm.events;

import com.hbm.core.InventoryHook;
import com.hbm.core.InventoryPlayerTransformer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.jetbrains.annotations.NotNull;

/**
 * InventoryChangedEvent is fired whenever a player's inventory changes. <br>
 * This event is fired via {@link InventoryPlayerTransformer},
 * which is executed by {@link InventoryHook}<br>
 * <br>
 * {@link #type} contains the type of change that occurred. <br>
 * {@link #player} contains the player that caused the change. <br>
 * {@link #getOldStack()} contains the ItemStack that was in the slot *before* the change. Only applicable to {@link EventType#DELTA}.<br>
 * {@link #getNewStack()} contains the ItemStack that is in the slot *after* the change. Only applicable to {@link EventType#DELTA}.<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
public class InventoryChangedEvent extends Event {

    private final EventType type;
    private final EntityPlayer player;
    private final ItemStack oldStack;
    private final ItemStack newStack;
    public InventoryChangedEvent(EntityPlayer player) {
        this.type = EventType.COMPLEX;
        this.player = player;
        this.oldStack = ItemStack.EMPTY;
        this.newStack = ItemStack.EMPTY;
    }

    public InventoryChangedEvent(EntityPlayer player, @NotNull ItemStack oldStack, @NotNull ItemStack newStack) {
        this.type = EventType.DELTA;
        this.player = player;
        this.oldStack = oldStack;
        this.newStack = newStack;
    }

    public EventType getType() {
        return type;
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    /**
     * @return The ItemStack that was in the slot *before* the change. Never null.
     * Returns {@link ItemStack#EMPTY} for {@link EventType#COMPLEX} events.
     */
    @NotNull
    public ItemStack getOldStack() {
        return oldStack;
    }

    /**
     * @return The ItemStack that is in the slot *after* the change. Never null.
     * Returns {@link ItemStack#EMPTY} for {@link EventType#COMPLEX} events.
     */
    @NotNull
    public ItemStack getNewStack() {
        return newStack;
    }

    public enum EventType {
        /**
         * A single slot was changed. Use {@link #getOldStack()} and {@link #getNewStack()}.
         */
        DELTA,
        /**
         * A complex or unknown change occurred. A full inventory rescan is recommended.
         */
        COMPLEX
    }
}