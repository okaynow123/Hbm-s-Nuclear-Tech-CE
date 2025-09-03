package com.hbm.core;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import static com.hbm.core.HbmCorePlugin.coreLogger;
import static com.hbm.core.HbmCorePlugin.fail;
import static org.objectweb.asm.Opcodes.*;

public class GlStateManagerTransformer implements IClassTransformer {

    private static final String METHOD_NAME = "rotate";
    private static final String METHOD_DESC = "(DFFF)V";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (!transformedName.equals("net.minecraft.client.renderer.GlStateManager")) {
            return basicClass;
        }
        coreLogger.info("Patching class {} / {}", transformedName, name);

        try {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(basicClass);
            classReader.accept(classNode, 0);
            MethodNode targetMethod = null;
            for (MethodNode method : classNode.methods) {
                if (method.name.equals(METHOD_NAME) && method.desc.equals(METHOD_DESC)) {
                    targetMethod = method;
                    break;
                }
            }

            if (targetMethod != null) {
                targetMethod.instructions.clear();
            } else {
                targetMethod = new MethodNode(ACC_PUBLIC | ACC_STATIC, METHOD_NAME, METHOD_DESC, null, null);
                classNode.methods.add(targetMethod);
            }
            populate(targetMethod);
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(writer);
            return writer.toByteArray();
        } catch (Throwable t) {
            fail("net.minecraft.client.renderer.GlStateManager", t);
            return basicClass;
        }
    }

    private void populate(MethodNode methodNode) {
        InsnList instructions = methodNode.instructions;
        instructions.add(new VarInsnNode(DLOAD, 0));
        instructions.add(new VarInsnNode(FLOAD, 2));
        instructions.add(new InsnNode(F2D));
        instructions.add(new VarInsnNode(FLOAD, 3));
        instructions.add(new InsnNode(F2D));
        instructions.add(new VarInsnNode(FLOAD, 4));
        instructions.add(new InsnNode(F2D));

        instructions.add(new MethodInsnNode(INVOKESTATIC, "org/lwjgl/opengl/GL11", "glRotated", "(DDDD)V", false));
        instructions.add(new InsnNode(RETURN));
    }
}
