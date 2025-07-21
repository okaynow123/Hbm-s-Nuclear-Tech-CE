package com.hbm.tileentity;

import com.hbm.interfaces.AutoRegisterTE;
import com.hbm.lib.RefStrings;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.reflections.Reflections;

import java.util.Set;

public class TileEntityRegistrar {
  public static void init() {
    Reflections reflections = new Reflections("com.hbm");
    Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(AutoRegisterTE.class);
    for (Class<?> clazz : annotated) {
      String name = clazz.getSimpleName();
      String pathIn = getTileEntity(name);

      ResourceLocation resourceLocation = new ResourceLocation(RefStrings.MODID, pathIn);
      GameRegistry.registerTileEntity(clazz.asSubclass(TileEntity.class), resourceLocation);
    }
  }

  private static String getTileEntity(String name) {
    name = name.replaceFirst("^TileEntity", "");
    return "tileentity_" + name.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
  }
}
