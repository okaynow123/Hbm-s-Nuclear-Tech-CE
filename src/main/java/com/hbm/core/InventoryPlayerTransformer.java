package com.hbm.core;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import java.util.Arrays;
import java.util.List;

import static com.hbm.core.HbmCorePlugin.coreLogger;
import static com.hbm.core.HbmCorePlugin.runtimeDeobfEnabled;
import static org.objectweb.asm.Opcodes.*;

public class InventoryPlayerTransformer implements IClassTransformer {

    /**
     * Copied from MekanismCoreTransformer in Mek: CE Unofficial
     * @author sddsd2332
     */
    private static class ObfSafeName {
        public final String deobf, srg;
        public ObfSafeName(String deobf, String srg) {
            this.deobf = deobf;
            this.srg = srg;
        }
        public String getName() {
            return runtimeDeobfEnabled ? srg : deobf;
        }

        public boolean matches(String name) {
            return deobf.equals(name) || srg.equals(name);
        }
    }

    private static final List<ObfSafeName> FAST_PATH_METHODS = Arrays.asList(
            new ObfSafeName("setInventorySlotContents", "func_70299_a")
    );

    private static final List<ObfSafeName> SLOW_PATH_METHODS = Arrays.asList(
//            new ObfSafeName("addItemStackToInventory", "func_70441_a"),
//            new ObfSafeName("decrStackSize", "func_70298_a"),
//            new ObfSafeName("removeStackFromSlot", "func_70304_b"),
            new ObfSafeName("dropAllItems", "func_70436_m"),
            new ObfSafeName("clear", "func_174888_l"),
            new ObfSafeName("readFromNBT", "func_70443_b")
    );

    private static final ObfSafeName PLAYER_FIELD = new ObfSafeName("player", "field_70458_d");

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (!transformedName.equals("net.minecraft.entity.player.InventoryPlayer")) {
            return basicClass;
        }
        coreLogger.info("Patching class {}/{}", transformedName, name);

        try {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(basicClass);
            classReader.accept(classNode, 0);

            for (MethodNode method : classNode.methods) {
                if (isMethodInList(method.name, FAST_PATH_METHODS)) {
                    coreLogger.info("Patching fast path method: {}", method.name);
                    method.instructions.insert(createFastPathHook());
                } else if (isMethodInList(method.name, SLOW_PATH_METHODS)) {
                    coreLogger.info("Patching slow path method: {}", method.name);
                    method.instructions.insert(createSlowPathHook());
                }
            }

            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            classNode.accept(writer);
            return writer.toByteArray();
        } catch (Exception e) {
            coreLogger.fatal("Error transforming InventoryPlayer", e);
            return basicClass;
        }
    }

    private boolean isMethodInList(String methodName, List<ObfSafeName> list) {
        for (ObfSafeName obfSafeName : list) {
            if (obfSafeName.matches(methodName)) {
                return true;
            }
        }
        return false;
    }

    private InsnList createSlowPathHook() {
        InsnList toInject = new InsnList();
        toInject.add(new VarInsnNode(ALOAD, 0)); // this
        toInject.add(new FieldInsnNode(GETFIELD, "net/minecraft/entity/player/InventoryPlayer", PLAYER_FIELD.getName(), "Lnet/minecraft/entity/player/EntityPlayer;"));
        toInject.add(new MethodInsnNode(INVOKESTATIC, "com/hbm/core/InventoryHook", "onFullInventoryChange", "(Lnet/minecraft/entity/player/EntityPlayer;)V", false));
        return toInject;
    }

    private InsnList createFastPathHook() {
        InsnList toInject = new InsnList();
        toInject.add(new VarInsnNode(ALOAD, 0)); // this (InventoryPlayer)
        toInject.add(new VarInsnNode(ILOAD, 1)); // int (slot)
        toInject.add(new VarInsnNode(ALOAD, 2)); // ItemStack
        toInject.add(new MethodInsnNode(INVOKESTATIC, "com/hbm/core/InventoryHook", "onSlotChange", "(Lnet/minecraft/entity/player/InventoryPlayer;ILnet/minecraft/item/ItemStack;)V", false));
        return toInject;
    }
}