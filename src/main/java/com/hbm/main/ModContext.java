package com.hbm.main;

import net.minecraft.entity.Entity;

public class ModContext {

    /**
     * Little hack to provide context. Technically a static field would work either(As the server only has one server thread)
     *
     * @apiNote Always call remove() after launch to avoid state leaks. Warp that in a try-finally block.
     */
    public static final ThreadLocal<Entity> DETONATOR_CONTEXT = new ThreadLocal<>();
}
