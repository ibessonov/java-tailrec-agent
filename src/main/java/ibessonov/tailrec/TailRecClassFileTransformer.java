package ibessonov.tailrec;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;
import static org.objectweb.asm.Opcodes.ASM5;

/**
 * @author ibessonov
 */
class TailRecClassFileTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) {

        AtomicBoolean classWasTransformed = new AtomicBoolean(false);

        ClassReader cr = new ClassReader(classfileBuffer);
        ClassWriter cw = new ClassWriter(cr, COMPUTE_MAXS | COMPUTE_FRAMES);

        ClassVisitor cv = new ClassVisitor(ASM5, cw) {

            private int classAccess;

            @Override
            public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                this.classAccess = access;
                super.visit(version, access, name, signature, superName, interfaces);
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

                return new TailRecMethodNode(
                    classWasTransformed, className, classAccess, access, name, desc, signature, exceptions,
                    super.visitMethod(access, name, desc, signature, exceptions)
                );
            }
        };

        cr.accept(cv, 0);
        return classWasTransformed.get() ? cw.toByteArray() : null;
    }
}
