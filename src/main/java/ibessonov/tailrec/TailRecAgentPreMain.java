package ibessonov.tailrec;

import java.lang.instrument.Instrumentation;

/**
 * @author ibessonov
 */
public final class TailRecAgentPreMain {

    public static void premain(String args, Instrumentation instrumentation) {
        instrumentation.addTransformer(new TailRecClassFileTransformer());
    }
}
