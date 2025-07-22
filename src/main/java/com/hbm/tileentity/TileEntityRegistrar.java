package com.hbm.tileentity;

import com.hbm.config.MachineDynConfig;
import com.hbm.lib.RefStrings;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TileEntityRegistrar {

    private static final String GENERATED_REGISTRAR_CLASS = "com.hbm.generated.GeneratedTERegistrar";
    private static final String REGISTRATION_METHOD = "registerAll";
    private static final String CONFIGURABLE = "configurable";
    public static final String MAP = "registryMap";
    public static ArrayList<Class<? extends IConfigurableMachine>> configurable;
    public static HashMap<Class<? extends TileEntity>, String> registryMap;
    public static void init() {
        try {
            Class<?> generatedRegistrar = Class.forName(GENERATED_REGISTRAR_CLASS);
            Method registerAllMethod = generatedRegistrar.getMethod(REGISTRATION_METHOD);
            registerAllMethod.invoke(null);
            Field configurablesList = generatedRegistrar.getDeclaredField(CONFIGURABLE);
            //noinspection unchecked
            configurable = new ArrayList<>((ArrayList<Class<? extends IConfigurableMachine>>) configurablesList.get(null));

            Field map = generatedRegistrar.getDeclaredField(MAP);
            //noinspection unchecked
            registryMap = new HashMap<>((HashMap<Class<? extends TileEntity>, String>)map.get(null));
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize TileEntity from generated registrar", e);
        }

        MachineDynConfig.initialize();
        for(Map.Entry<Class<? extends TileEntity>, String> e : registryMap.entrySet()) {
                GameRegistry.registerTileEntity(e.getKey(), new ResourceLocation(RefStrings.MODID, e.getValue()));
        }

    }
}
