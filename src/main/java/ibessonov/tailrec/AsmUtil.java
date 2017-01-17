package ibessonov.tailrec;

import org.objectweb.asm.tree.AbstractInsnNode;

import java.util.ArrayList;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;

/**
 * @author ibessonov
 */
class AsmUtil {

    static AbstractInsnNode findNextInstruction(AbstractInsnNode node) {
        do {
            node = node.getNext();
        } while (node.getOpcode() == -1);
        return node;
    }

    static boolean isReturnInstruction(AbstractInsnNode instruction) {
        int opcode = instruction.getOpcode();
        return IRETURN <= opcode && opcode <= RETURN;
    }

    static Class<?>[] getParameterTypes(String descr) {
        List<Class<?>> classes = new ArrayList<>();

        for (int index = 1; descr.charAt(index) != ')'; index++) {
            boolean array = false;
            while (descr.charAt(index) == '[') {
                array = true;
                index++;
            }
            char type = descr.charAt(index);
            classes.add(array ? Object.class : parseType(type));
            if (type == 'L') index = descr.indexOf(';', index);
        }

        return classes.toArray(new Class[classes.size()]);
    }

    static int storeOpcode(Class<?> type) {
        if (type == Object.class) return ASTORE;
        if (type == long.class)   return LSTORE;
        if (type == float.class)  return FSTORE;
        if (type == double.class) return DSTORE;
                                  return ISTORE;
    }

    private static Class<?> parseType(char type) {
        switch (type) {
            case 'Z': return boolean.class;
            case 'B': return byte.class;
            case 'C': return char.class;
            case 'S': return short.class;
            case 'I': return int.class;
            case 'J': return long.class;
            case 'F': return float.class;
            case 'D': return double.class;
            case 'V': return void.class;
            default:  return Object.class;
        }
    }
}
