package com.hbm.core;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import static com.hbm.core.HbmCorePlugin.coreLogger;
import static org.objectweb.asm.Opcodes.*;

public class ContainerTransformer implements IClassTransformer {
    private static final ObfSafeName DETECT_AND_SEND_CHANGES = new ObfSafeName("detectAndSendChanges", "func_75142_b");
    private static final ObfSafeName ARE_ITEM_STACKS_EQUAL = new ObfSafeName("areItemStacksEqual", "func_77989_b");

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (!transformedName.equals("net.minecraft.inventory.Container")) {
            return basicClass;
        }
        coreLogger.info("Patching class {} / {}", transformedName, name);

        try {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(basicClass);
            classReader.accept(classNode, 0);

            for (MethodNode method : classNode.methods) {
                if (DETECT_AND_SEND_CHANGES.matches(method.name) && method.desc.equals("()V")) {
                    coreLogger.info("Patching method: {} / {}", DETECT_AND_SEND_CHANGES.mcp, method.name);
                    injectHook(method);
                }
            }

            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            classNode.accept(writer);
            return writer.toByteArray();
        } catch (Exception e) {
            coreLogger.fatal("Error transforming Container", e);
            return basicClass;
        }
    }

    private void injectHook(MethodNode method) {
        AbstractInsnNode targetNode = null;
        // ALOAD, ALOAD, INVOKESTATIC, IFNE
        for (AbstractInsnNode instruction : method.instructions.toArray()) {
            if (instruction.getOpcode() == INVOKESTATIC) {
                MethodInsnNode methodInsn = (MethodInsnNode) instruction;
                if (methodInsn.owner.equals("net/minecraft/item/ItemStack") && ARE_ITEM_STACKS_EQUAL.matches(methodInsn.name)) {
                    targetNode = instruction.getNext().getNext(); // instruction -> IFNE -> LabelNode
                    break;
                }
            }
        }

        if (targetNode != null) {
            InsnList toInject = new InsnList();
            toInject.add(new VarInsnNode(ALOAD, 0)); // this
            toInject.add(new VarInsnNode(ILOAD, 1)); // index
            toInject.add(new VarInsnNode(ALOAD, 3)); // previousStack
            toInject.add(new VarInsnNode(ALOAD, 2)); // newStack
            toInject.add(new MethodInsnNode(INVOKESTATIC,
                    "com/hbm/core/InventoryHook",
                    "onContainerChange",
                    "(Lnet/minecraft/inventory/Container;ILnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)V",
                    false));

            method.instructions.insert(targetNode, toInject);
            coreLogger.info("Successfully injected hook into Container.detectAndSendChanges");
        } else {
            coreLogger.error("Failed to find injection point in Container.detectAndSendChanges!");
        }
    }
}
