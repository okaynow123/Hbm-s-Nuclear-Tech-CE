package com.hbm.tileentity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class TileEntityRegistrar {

    private static final String GENERATED_REGISTRAR_CLASS = "com.hbm.generated.GeneratedTERegistrar";
    private static final String REGISTRATION_METHOD = "registerAll";
    private static final String CONFIGURABLE = "configurable";
    public static ArrayList<Class<? extends IConfigurableMachine>> configurable;

    public static void init() {
        try {
            Class<?> generatedRegistrar = Class.forName(GENERATED_REGISTRAR_CLASS);
            Method registerAllMethod = generatedRegistrar.getMethod(REGISTRATION_METHOD);
            registerAllMethod.invoke(null);
            Field configurablesList = generatedRegistrar.getDeclaredField(CONFIGURABLE);
            //noinspection unchecked
            configurable = new ArrayList<>((ArrayList<Class<? extends IConfigurableMachine>>) configurablesList.get(null));
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize TileEntity from generated registrar", e);
        }
    }
}
