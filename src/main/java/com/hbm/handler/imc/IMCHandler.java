package com.hbm.handler.imc;

import com.hbm.main.MainRegistry;

import java.util.HashMap;
import java.util.Set;

import static net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;

public abstract class IMCHandler {
    public static void init(){
        IMCHandler.registerHandler("blastfurnace", new IMCBlastFurnace());
        IMCHandler.registerHandler("crystallizer", new IMCCrystallizer());
        IMCHandler.registerHandler("centrifuge", new IMCCentrifuge());

    }

    private static final HashMap<String, IMCHandler> handlers = new HashMap();


    protected static String getHandlerName() {
        throw new UnsupportedOperationException("IMCHandler must implement  getHandlerName");
    }

    public static void registerHandler(String name, IMCHandler handler) {
        handlers.put(name, handler);
    }

    public static IMCHandler getHandler(String name) {
        return handlers.get(name);
    }

    public abstract void process(IMCMessage message);

    public void printError(IMCMessage message, String error) {
        MainRegistry.logger.error("[" + this.getClass().getSimpleName() + "] Error reading IMC message from " + message.getSender() + ": " + error);
    }
}
