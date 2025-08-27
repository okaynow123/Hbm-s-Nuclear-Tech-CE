package com.hbm.util;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.hbm.lib.RefStrings;
import com.hbm.main.MainRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Mod.EventBusSubscriber(modid = RefStrings.MODID)
public final class DelayedTick {

    private static final ConcurrentHashMap<Integer, ConcurrentLinkedQueue<Ticket>> SERVER_TASKS = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer, ConcurrentHashMap<Long, ConcurrentLinkedQueue<Ticket>>> WORLD_TASKS = new ConcurrentHashMap<>();

    private DelayedTick() {
    }

    @CanIgnoreReturnValue
    public static Ticket scheduleServer(MinecraftServer server, int delayTicks, Runnable task) {
        if (server == null || task == null) return null;
        final int when = server.getTickCounter() + Math.max(1, delayTicks);
        final Ticket t = new Ticket(task);
        SERVER_TASKS.computeIfAbsent(when, k -> new ConcurrentLinkedQueue<>()).add(t);
        return t;
    }

    @CanIgnoreReturnValue
    public static Ticket scheduleServer(World world, int delayTicks, Runnable task) {
        if (world == null || world.isRemote) return null;
        return scheduleServer(world.getMinecraftServer(), delayTicks, task);
    }

    @CanIgnoreReturnValue
    public static Ticket scheduleWorld(World world, int delayTicks, Runnable task) {
        if (world == null || world.isRemote || task == null) return null;
        final long runAt = world.getTotalWorldTime() + Math.max(1, delayTicks);
        final int dim = world.provider.getDimension();
        final Ticket t = new Ticket(task);
        WORLD_TASKS.computeIfAbsent(dim, d -> new ConcurrentHashMap<>()).computeIfAbsent(runAt, r -> new ConcurrentLinkedQueue<>()).add(t);
        return t;
    }

    @CanIgnoreReturnValue
    public static Ticket nextServerTick(MinecraftServer server, Runnable task) {
        return scheduleServer(server, 1, task);
    }

    @CanIgnoreReturnValue
    public static Ticket nextWorldTick(World world, Runnable task) {
        return scheduleWorld(world, 1, task);
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        final MinecraftServer srv = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (srv == null) return;

        final int now = srv.getTickCounter();
        final Queue<Ticket> q = SERVER_TASKS.remove(now);
        runAll(q);
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent e) {
        if (e.world.isRemote || e.phase != TickEvent.Phase.END) return;

        final int dim = e.world.provider.getDimension();
        final long now = e.world.getTotalWorldTime();

        final ConcurrentHashMap<Long, ConcurrentLinkedQueue<Ticket>> byTime = WORLD_TASKS.get(dim);
        if (byTime == null) return;

        final Queue<Ticket> q = byTime.remove(now);
        runAll(q);

        if (byTime.isEmpty()) {
            WORLD_TASKS.remove(dim, byTime);
        }
    }

    private static void runAll(Queue<Ticket> q) {
        if (q == null) return;
        Ticket t;
        while ((t = q.poll()) != null) {
            t.runIfNotCancelled();
        }
    }

    public static final class Ticket {
        private final Runnable task;
        private volatile boolean cancelled;

        private Ticket(Runnable task) {
            this.task = task;
        }

        public void cancel() {
            cancelled = true;
        }

        private void runIfNotCancelled() {
            if (!cancelled) {
                try {
                    task.run();
                } catch (Throwable t) {
                    MainRegistry.logger.error("Exception in delayed task", t);
                }
            }
        }
    }
}
