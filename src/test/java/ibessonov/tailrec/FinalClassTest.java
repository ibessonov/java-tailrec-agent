package ibessonov.tailrec;

import org.junit.Test;

public final class FinalClassTest {

    @SuppressWarnings("WeakerAccess")
    int virtualFactorial(int n, int acc) {
        if (n <= 1) return acc;
        return virtualFactorial(n - 1, acc * n);
    }

    @Test
    public void testVirtualFactorial() {
        virtualFactorial(50_000, 1);
    }
}
