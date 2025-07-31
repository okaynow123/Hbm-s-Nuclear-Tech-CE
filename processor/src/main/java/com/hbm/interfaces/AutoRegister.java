package com.hbm.interfaces;

import java.lang.annotation.*;

/**
 * Annotation for registering entities, tile entities, and their renderers
 * <li> For entity renderer that extends Render<E>:<br>
 * entity:  the entity class to bind the renderer onto.<br>
 * factory: the class that contains the static final IRenderFactory field, which MUST be named "FACTORY".
 * <li> For itemStack renderer that inherits TEISRBase: <br>
 * item: the static field name of the item in the ModItems class (e.g., "hf_sword" for ModItems.hf_sword)
 * <li> For tileentity renderer that extends TileEntitySpecialRenderer<T>: <br>
 * tileentity: the tileentity class to bind the renderer onto
 * <li> For entity: <br>
 * name: custom name for the entity. must not be empty.
 * trackingRange: the tracking range for the entity.
 * updateFrequency: the update frequency for the entity.
 * sendVelocityUpdates: whether to send velocity updates for the entity.
 * <li> For tileentity: <br>
 * name: custom name for the tileentity. If empty, a name will be generated from the class name.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
@Repeatable(AutoRegisterContainer.class)
public @interface AutoRegister {
    /**
     * The entity class to bind the annotated renderer onto.
     */
    Class<?> entity() default Object.class;

    /**
     * The field name of the Item to bind the TEISR onto (e.g., "my_item" for ModItems.my_item).
     */
    String item() default "";

    /**
     * The TileEntity class to bind the annotated renderer onto.
     */
    Class<?> tileentity() default Object.class;

    /**
     * The Class containing the static "FACTORY" instance for an entity renderer.
     */
    String factory() default "";

    /**
     * Custom registration name for an entity or tile entity.
     */
    String name() default "";

    /**
     * Entity tracking range.
     */
    int trackingRange() default 250;

    /**
     * Entity update frequency.
     */
    int updateFrequency() default 1;

    /**
     * Whether the entity sends velocity updates.
     */
    boolean sendVelocityUpdates() default true;
}
