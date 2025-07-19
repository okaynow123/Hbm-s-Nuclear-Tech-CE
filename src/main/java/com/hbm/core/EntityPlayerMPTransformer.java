package com.hbm.core;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import static com.hbm.core.HbmCorePlugin.coreLogger;
import static org.objectweb.asm.Opcodes.*;

public class EntityPlayerMPTransformer implements IClassTransformer {

    private static final ObfSafeName SEND_SLOT_CONTENTS = new ObfSafeName("sendSlotContents", "func_71111_a");
    private static final ObfSafeName SEND_ALL_CONTENTS = new ObfSafeName("sendAllContents", "func_71110_a");

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (!transformedName.equals("net.minecraft.entity.player.EntityPlayerMP")) {
            return basicClass;
        }
        coreLogger.info("Patching class {} / {}", transformedName, name);

        try {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(basicClass);
            classReader.accept(classNode, 0);

            for (MethodNode method : classNode.methods) {
                if (SEND_SLOT_CONTENTS.matches(method.name)) {
                    coreLogger.info("Patching server delta method: {} / {}", SEND_SLOT_CONTENTS.mcp, method.name);
                    method.instructions.insert(createDeltaHook());
                } else if (SEND_ALL_CONTENTS.matches(method.name)) {
                    coreLogger.info("Patching server complex method: {} / {}", SEND_ALL_CONTENTS.mcp, method.name);
                    method.instructions.insert(createComplexHook());
                }
            }

            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            classNode.accept(writer);
            return writer.toByteArray();
        } catch (Exception e) {
            coreLogger.fatal("Error transforming EntityPlayerMP", e);
            return basicClass;
        }
    }

    private InsnList createDeltaHook() {
        InsnList toInject = new InsnList();
        toInject.add(new VarInsnNode(ALOAD, 0)); // this
        toInject.add(new VarInsnNode(ALOAD, 1)); // Container
        toInject.add(new VarInsnNode(ILOAD, 2)); // slot index
        toInject.add(new VarInsnNode(ALOAD, 3)); // new ItemStack
        toInject.add(new MethodInsnNode(INVOKESTATIC, "com/hbm/core/InventoryHook", "onServerSlotChange", "(Lnet/minecraft/entity/player/EntityPlayerMP;Lnet/minecraft/inventory/Container;ILnet/minecraft/item/ItemStack;)V", false));
        return toInject;
    }

    private InsnList createComplexHook() {
        InsnList toInject = new InsnList();
        toInject.add(new VarInsnNode(ALOAD, 0)); // this
        toInject.add(new MethodInsnNode(INVOKESTATIC, "com/hbm/core/InventoryHook", "onServerFullSync", "(Lnet/minecraft/entity/player/EntityPlayer;)V", false));
        return toInject;
    }
}