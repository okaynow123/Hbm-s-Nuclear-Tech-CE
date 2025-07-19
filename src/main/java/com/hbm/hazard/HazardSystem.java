package com.hbm.hazard;

import com.github.bsideup.jabel.Desugar;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.hbm.config.GeneralConfig;
import com.hbm.config.RadiationConfig;
import com.hbm.hazard.modifier.HazardModifier;
import com.hbm.hazard.transformer.HazardTransformerBase;
import com.hbm.hazard.type.HazardTypeBase;
import com.hbm.interfaces.Untested;
import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.main.MainRegistry;
import com.hbm.util.ContaminationUtil;
import com.hbm.util.ItemStackUtil;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.PlayerArmorInvWrapper;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;
import net.minecraftforge.items.wrapper.PlayerOffhandInvWrapper;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Untested
public class HazardSystem {

    /**
     * Map for OreDict entries, always evaluated first. Avoid registering HazardData with 'doesOverride', as internal order is based on the item's
     * ore dict keys.
     */
    public static final HashMap<String, HazardData> oreMap = new HashMap<>();
    /**
     * Map for items, either with wildcard meta or stuff that's expected to have a variety of damage values, like tools.
     */
    public static final HashMap<Item, HazardData> itemMap = new HashMap<>();
    /**
     * Very specific stacks with item and meta matching. ComparableStack does not support NBT matching, to scale hazards with NBT please use
     * HazardModifiers.
     */
    public static final HashMap<ComparableStack, HazardData> stackMap = new HashMap<>();
    /**
     * For items that should, for whichever reason, be completely exempt from the hazard system.
     */
    public static final HashSet<ComparableStack> stackBlacklist = new HashSet<>();
    public static final HashSet<String> dictBlacklist = new HashSet<>();
    /**
     * List of hazard transformers, called in order before and after unrolling all the HazardEntries.
     */
    public static final List<HazardTransformerBase> trafos = new ArrayList<>();

    private static final ConcurrentHashMap<ComparableStack, List<HazardEntry>> hazardCalculationCache = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<UUID, PlayerHazardData> playerHazardDataMap = new ConcurrentHashMap<>();
    private static final ExecutorService hazardScanExecutor = Executors.newFixedThreadPool(Math.max(1,
            Runtime.getRuntime().availableProcessors() - 1),
            new ThreadFactoryBuilder().setNameFormat("HBM-Hazard-Scanner-%d").setDaemon(true).build());
    private static final Set<UUID> playersToUpdate = ConcurrentHashMap.newKeySet();
    private static CompletableFuture<Void> scanFuture = CompletableFuture.completedFuture(null);
    private static long tickCounter = 0;

    public static void schedulePlayerUpdate(EntityPlayer player) {
        playersToUpdate.add(player.getUniqueID());
    }

    /**
     * Main entry point, called from ServerTickEvent.
     * Applies active hazards and processes any players whose inventories have changed.
     */
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        tickCounter++;
        if (tickCounter % RadiationConfig.hazardRate != 2) return;
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null) return;
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) {
            if (player.isDead) continue;
            PlayerHazardData phd = playerHazardDataMap.get(player.getUniqueID());
            if (phd == null || phd.player != player) {
                if (GeneralConfig.enableExtendedLogging) MainRegistry.logger.info("Player {} changed", player.getName());
                phd = new PlayerHazardData(player);
                playerHazardDataMap.put(player.getUniqueID(), phd);
            }
            phd.applyActiveHazards();
        }
        if (scanFuture.isDone() && !playersToUpdate.isEmpty()) {
            final List<EntityPlayer> playersForThisRun = new ArrayList<>();
            Iterator<UUID> iterator = playersToUpdate.iterator();
            while (iterator.hasNext()) {
                UUID uuid = iterator.next();
                EntityPlayer player = server.getPlayerList().getPlayerByUUID(uuid);
                playersForThisRun.add(player);
                iterator.remove();
            }

            if (!playersForThisRun.isEmpty()) {
                scanFuture = CompletableFuture.runAsync(() -> {
                    Map<UUID, List<Consumer<EntityPlayer>>> results =
                            playersForThisRun.parallelStream().collect(Collectors.toConcurrentMap(EntityPlayer::getUniqueID,
                                    PlayerHazardData::calculateApplicatorsForPlayer));

                    server.addScheduledTask(() -> results.forEach((uuid, applicators) -> {
                        PlayerHazardData phd = playerHazardDataMap.get(uuid);
                        if (phd != null) {
                            phd.setActiveHazardApplicators(applicators);
                        }
                    }));
                }, hazardScanExecutor);
            }
        }
    }

    public static boolean isStackHazardous(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        return !getHazardsFromStack(stack).isEmpty();
    }

    public static void onPlayerLogout(EntityPlayer player) {
        playersToUpdate.remove(player.getUniqueID());
        playerHazardDataMap.remove(player.getUniqueID());
    }

    public static void register(final Object o, final HazardData data) {

        if (o instanceof String) oreMap.put((String) o, data);
        if (o instanceof Item) itemMap.put((Item) o, data);
        if (o instanceof Block) itemMap.put(Item.getItemFromBlock((Block) o), data);
        if (o instanceof ItemStack) stackMap.put(ItemStackUtil.comparableStackFrom((ItemStack) o), data);
        if (o instanceof ComparableStack) stackMap.put((ComparableStack) o, data);
    }

    /**
     * Prevents the stack from returning any HazardData
     */
    public static void blacklist(final Object o) {

        if (o instanceof ItemStack) {
            stackBlacklist.add(ItemStackUtil.comparableStackFrom((ItemStack) o).makeSingular());
        } else if (o instanceof String) {
            dictBlacklist.add((String) o);
        }
    }

    public static boolean isItemBlacklisted(final ItemStack stack) {

        if (stackBlacklist.contains(ItemStackUtil.comparableStackFrom(stack).makeSingular())) return true;

        final int[] ids = OreDictionary.getOreIDs(stack);
        for (final int id : ids) {
            final String name = OreDictionary.getOreName(id);

            if (dictBlacklist.contains(name)) return true;
        }

        return false;
    }

    /**
     * Will return a full list of applicable HazardEntries for this stack.
     * <br><br>ORDER:
     * <ol>
     * <li>ore dict (if multiple keys, in order of the ore dict keys for this stack)
     * <li>item
     * <li>item stack
     * </ol>
     * <p>
     * "Applicable" means that entries that are overridden or excluded via mutex are not in this list.
     * Entries that are marked as "overriding" will delete all fetched entries that came before it.
     * Entries that use mutex will prevent subsequent entries from being considered, shall they collide. The mutex system already assumes that
     * two keys are the same in priority, so the flipped order doesn't matter.
     */
    private static List<HazardEntry> getHazardsFromStack(final ItemStack stack) {

        if (stack.isEmpty() || isItemBlacklisted(stack)) {
            return Collections.emptyList();
        }

        return hazardCalculationCache.computeIfAbsent(ItemStackUtil.comparableStackFrom(stack).makeSingular(), compStack -> {
            final List<HazardData> chronological = new ArrayList<>();

            // ORE DICT
            final int[] ids = OreDictionary.getOreIDs(stack);
            for (final int id : ids) {
                final String name = OreDictionary.getOreName(id);
                final HazardData hazardData = oreMap.get(name);
                if (hazardData != null) {
                    chronological.add(hazardData);
                }
            }

            // ITEM
            final HazardData itemHazardData = itemMap.get(stack.getItem());
            if (itemHazardData != null) {
                chronological.add(itemHazardData);
            }

            // STACK
            final HazardData stackHazardData = stackMap.get(compStack);
            if (stackHazardData != null) {
                chronological.add(stackHazardData);
            }

            final List<HazardEntry> entries = new ArrayList<>();

            // Pre-transformations
            for (final HazardTransformerBase trafo : trafos) {
                trafo.transformPre(stack, entries);
            }

            int mutex = 0;

            for (final HazardData data : chronological) {
                if (data.doesOverride) {
                    entries.clear();
                }
                if ((data.getMutex() & mutex) == 0) {
                    entries.addAll(data.entries);
                    mutex |= data.getMutex();
                }
            }

            // Post-transformations
            for (final HazardTransformerBase trafo : trafos) {
                trafo.transformPost(stack, entries);
            }

            return Collections.unmodifiableList(entries);
        });
    }

    public static float getHazardLevelFromStack(ItemStack stack, HazardTypeBase hazard) {
        List<HazardEntry> entries = getHazardsFromStack(stack);

        for (HazardEntry entry : entries) {
            if (entry.type == hazard) {
                return HazardModifier.evalAllModifiers(stack, null, entry.baseLevel, entry.mods);
            }
        }

        return 0F;
    }

    public static float getRawRadsFromBlock(Block b) {
        return getHazardLevelFromStack(new ItemStack(Item.getItemFromBlock(b)), HazardRegistry.RADIATION);
    }

    public static float getRawRadsFromStack(ItemStack stack) {
        return getHazardLevelFromStack(stack, HazardRegistry.RADIATION);
    }

    public static float getTotalRadsFromStack(ItemStack stack) {
        return getHazardLevelFromStack(stack, HazardRegistry.RADIATION) + ContaminationUtil.getNeutronRads(stack);
    }

    public static void applyHazards(Block b, EntityLivingBase entity) {
        applyHazards(new ItemStack(Item.getItemFromBlock(b)), entity);
    }

    /**
     * Will grab and iterate through all assigned hazards of the given stack and apply their effects to the holder.
     */
    public static void applyHazards(ItemStack stack, EntityLivingBase entity) {
        List<HazardEntry> hazards = getHazardsFromStack(stack);

        for (HazardEntry hazard : hazards) {
            hazard.applyHazard(stack, entity);
        }
    }

    public static void updateLivingInventory(EntityLivingBase entity) {

        for (EntityEquipmentSlot i : EntityEquipmentSlot.values()) {
            ItemStack stack = entity.getItemStackFromSlot(i);

            if (!stack.isEmpty()) {
                applyHazards(stack, entity);
            }
        }
    }

    public static void updateDroppedItem(EntityItem entity) {
        if (entity.isDead) return;
        ItemStack stack = entity.getItem();

        if (stack.isEmpty() || stack.getCount() <= 0) return;

        List<HazardEntry> hazards = getHazardsFromStack(stack);
        for (HazardEntry entry : hazards) {
            entry.type.updateEntity(entity, HazardModifier.evalAllModifiers(stack, null, entry.baseLevel, entry.mods));
        }
    }

    public static void addHazardInfo(ItemStack stack, EntityPlayer player, List<String> list, ITooltipFlag flagIn) {

        List<HazardEntry> hazards = getHazardsFromStack(stack);

        for (HazardEntry hazard : hazards) {
            hazard.type.addHazardInformation(player, list, hazard.baseLevel, stack, hazard.mods);
        }
    }

    private static class PlayerHazardData {
        private final EntityPlayer player;
        private List<Consumer<EntityPlayer>> activeHazardApplicators = Collections.emptyList();

        PlayerHazardData(EntityPlayer player) {
            this.player = player;
            schedulePlayerUpdate(player);
        }

        static List<Consumer<EntityPlayer>> calculateApplicatorsForPlayer(EntityPlayer player) {
            List<SlottedStack> inventorySnapshot = snapshotInventories(player);
            return inventorySnapshot.stream().map(slottedStack -> {
                List<HazardEntry> hazards = getHazardsFromStack(slottedStack.stack);
                if (hazards.isEmpty()) return null;
                return (Consumer<EntityPlayer>) p -> {
                    ItemStack liveStack = slottedStack.handler.getStackInSlot(slottedStack.slot);
                    if (!liveStack.isEmpty() && liveStack.getItem() == slottedStack.stack.getItem()) {
                        applyHazards(liveStack, p);
                        if (liveStack.getCount() <= 0 && slottedStack.handler instanceof PlayerMainInvWrapper) {
                            IInventory inv = ((PlayerMainInvWrapper) slottedStack.handler).getInventoryPlayer();
                            inv.setInventorySlotContents(slottedStack.slot, ItemStack.EMPTY);
                        }
                    }
                };
            }).filter(Objects::nonNull).collect(Collectors.toList());
        }

        private static List<SlottedStack> snapshotInventories(EntityPlayer player) {
            List<InventoryWrapper> inventories = new ArrayList<>();
            inventories.add(new InventoryWrapper("main", new PlayerMainInvWrapper(player.inventory)));
            inventories.add(new InventoryWrapper("armor", new PlayerArmorInvWrapper(player.inventory)));
            inventories.add(new InventoryWrapper("offhand", new PlayerOffhandInvWrapper(player.inventory)));
            if (player.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
                IItemHandler cap = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                if (cap != null && !(cap instanceof PlayerMainInvWrapper || cap instanceof PlayerArmorInvWrapper || cap instanceof PlayerOffhandInvWrapper)) {
                    inventories.add(new InventoryWrapper("capability", cap));
                }
            }

            List<SlottedStack> snapshot = new ArrayList<>();
            for (InventoryWrapper wrapper : inventories) {
                IItemHandler handler = wrapper.handler;
                for (int i = 0; i < handler.getSlots(); i++) {
                    ItemStack stack = handler.getStackInSlot(i);
                    if (!stack.isEmpty()) {
                        snapshot.add(new SlottedStack(handler, i, stack.copy()));
                    }
                }
            }
            return snapshot;
        }

        void applyActiveHazards() {
            if (!activeHazardApplicators.isEmpty()) {
                activeHazardApplicators.forEach(applier -> applier.accept(this.player));
                if (this.player.inventoryContainer != null) {
                    this.player.inventoryContainer.detectAndSendChanges();
                }
            }
        }

        void setActiveHazardApplicators(List<Consumer<EntityPlayer>> applicators) {
            this.activeHazardApplicators = applicators;
        }

        @Desugar
        private record SlottedStack(IItemHandler handler, int slot, ItemStack stack) {
        }

        @Desugar
        private record InventoryWrapper(String name, IItemHandler handler) {
        }
    }
}
