package com.hbm.tileentity;

import java.lang.reflect.Method;

public class TileEntityRegistrar {

    private static final String GENERATED_REGISTRAR_CLASS = "com.hbm.generated.GeneratedTERegistrar";
    private static final String REGISTRATION_METHOD = "registerAll";

    public static void init() {
        try {
            Class<?> generatedRegistrar = Class.forName(GENERATED_REGISTRAR_CLASS);
            Method registerAllMethod = generatedRegistrar.getMethod(REGISTRATION_METHOD);
            registerAllMethod.invoke(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize TileEntity from generated registrar", e);
        }
    }
}
