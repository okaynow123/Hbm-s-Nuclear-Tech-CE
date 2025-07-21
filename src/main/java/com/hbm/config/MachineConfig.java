package com.hbm.config;

import net.minecraftforge.common.config.Configuration;

public class MachineConfig {

    protected static boolean scaleRTGPower = false;
    protected static boolean doRTGsDecay = true;
    protected static boolean disableMachines = false;
    //TODO: handle like on 1.7
    public static int crateByteSize = 8;
    public static int rbmkJumpTemp = 1250;

    public static void loadFromConfig(Configuration config) {

        final String CATEGORY_MACHINE = CommonConfig.CATEGORY_MACHINES;

        scaleRTGPower = CommonConfig.createConfigBool(config, CATEGORY_MACHINE, "9.01_scaleRTGPower", "Should RTG/Betavoltaic fuel power scale down as it decays?", false);
        doRTGsDecay = CommonConfig.createConfigBool(config, CATEGORY_MACHINE, "9.02_doRTGsDecay", "Should RTG/Betavoltaic fuel decay at all?", true);
        disableMachines = CommonConfig.createConfigBool(config, CATEGORY_MACHINE, "9.00_disableMachines", "Prevent mod from registering any Machines? (WARNING: THIS WILL BREAK PREEXISTING WORLDS)", false);
    }
}
