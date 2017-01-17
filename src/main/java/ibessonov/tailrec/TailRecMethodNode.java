package ibessonov.tailrec;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static ibessonov.tailrec.AsmUtil.*;
import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.tree.AbstractInsnNode.LABEL;
import static org.objectweb.asm.tree.AbstractInsnNode.METHOD_INSN;

/**
 * @author ibessonov
 */
class TailRecMethodNode extends MethodNode {

    private final AtomicBoolean methodChanged;
    private final String className;
    private final int classAccess;
    private final MethodVisitor outerMv;

    TailRecMethodNode(AtomicBoolean methodChanged, String className, int classAccess, int access,
                      String name, String desc, String signature, String[] exceptions, MethodVisitor outerMv) {
        super(ASM5, access, name, desc, signature, exceptions);
        this.methodChanged = methodChanged;
        this.className     = className;
        this.classAccess   = classAccess;
        this.outerMv       = outerMv;
    }

    @Override
    public void visitEnd() {
        boolean methodIsStatic = (super.access & ACC_STATIC) != 0;
        //noinspection PointlessBooleanExpression
        if (false || methodIsStatic || (super.access & ACC_PRIVATE) != 0
                  || (super.access & ACC_FINAL) != 0 || (this.classAccess & ACC_FINAL) != 0) {
            Map<? super AbstractInsnNode, LabelNode> tryCatchBlocksMap = new HashMap<>();
            //noinspection RedundantCast // cast is redundant in asm-debug-all only
            for (TryCatchBlockNode block : (List<TryCatchBlockNode>) tryCatchBlocks) {
                tryCatchBlocksMap.put(block.start, block.end);
            }

            Class<?>[] paramTypes = getParameterTypes(super.desc);
            int[] offsets = new int[paramTypes.length];
            offsets[0] = methodIsStatic ? 0 : 1;
            for (int i = 1; i < offsets.length; i++) {
                offsets[i] = offsets[i - 1]
                           + (paramTypes[i - 1] == long.class || paramTypes[i - 1] == double.class ? 2 : 1);
            }

            LabelNode startLabel;
            AbstractInsnNode firstNode = super.instructions.getFirst();
            if (firstNode.getType() == LABEL) {
                startLabel = (LabelNode) firstNode;
            } else {
                super.instructions.insertBefore(firstNode, startLabel = new LabelNode());
            }

            LabelNode tryBlockEnd = null;
            for (AbstractInsnNode node = startLabel.getNext(); node != null; node = node.getNext()) {
                int nodeType = node.getType();
                if (nodeType == LABEL) {
                    if (tryBlockEnd == null) {
                        tryBlockEnd = tryCatchBlocksMap.get(node);
                    } else if (node == tryBlockEnd) {
                        tryBlockEnd = null;
                    }
                } else if (tryBlockEnd == null) { // not in try block
                    if (nodeType == METHOD_INSN) {
                        MethodInsnNode methodInstruction = (MethodInsnNode) node;

                        //noinspection PointlessBooleanExpression
                        if (true && methodInstruction.name.equals(super.name)
                                 && methodInstruction.desc.equals(super.desc)
                                 && methodInstruction.owner.equals(this.className)) {

                            AbstractInsnNode returnInstruction = findNextInstruction(node);
                            if (!isReturnInstruction(returnInstruction)) continue;

                            node = node.getPrevious();
                            for (int i = paramTypes.length - 1; i >= 0; i--) {
                                node = new IntInsnNode(storeOpcode(paramTypes[i]), offsets[i]);
                                super.instructions.insertBefore(methodInstruction, node);
                            }

                            if (!methodIsStatic) {
                                node = new IntInsnNode(ASTORE, 0); // let's get dangerous
                                super.instructions.insertBefore(methodInstruction, node);
                            }

                            super.instructions.remove(methodInstruction);

                            super.instructions.insertBefore(returnInstruction, new JumpInsnNode(GOTO, startLabel));
                            super.instructions.remove(returnInstruction);

                            this.methodChanged.set(true);
                        }
                    }
                }
            }
        }
        super.accept(outerMv);
    }
}
