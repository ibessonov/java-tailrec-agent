package ibessonov.tailrec;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TailrecTest {

    @SuppressWarnings("WeakerAccess")
    int virtualFactorial(int n, int acc) {
        if (n <= 1) return acc;
        return virtualFactorial(n - 1, acc * n);
    }

    @Test(expected = StackOverflowError.class)
    public void testVirtualFactorial() {
        virtualFactorial(50_000, 1);
    }


    private static int exceptionalFactorial(int n, int acc) {
        try {
            try {
                if (n <= 1) return acc;
            } finally {
                acc *= n;
            }
            return exceptionalFactorial(n - 1, acc);
        } catch (Exception e) {
            return -1;
        }
    }

    @Test(expected = StackOverflowError.class)
    public void testExceptionalFactorial() {
        exceptionalFactorial(50_000, 1);
    }


    private static int nonTailRecFactorial(int n) {
        if (n <= 1) return 1;
        return n * nonTailRecFactorial(n - 1);
    }

    @Test(expected = StackOverflowError.class)
    public void testNonTailRecFactorial() {
        nonTailRecFactorial(50_000);
    }


    private static int staticFactorial(int n, int acc) {
        if (n <= 1) return acc;
        return staticFactorial(n - 1, acc * n);
    }

    @Test
    public void testStaticFactorial() {
        staticFactorial(50_000, 1);
    }


    private int thisFactorial(int n, int acc) {
        if (n <= 1) return acc;
        return thisFactorial(n - 1, acc * n);
    }

    @Test
    public void testThisFactorial() {
        thisFactorial(50_000, 1);
    }


    private int newThisFactorial(int n, int acc) {
        if (n <= 1) return acc;
        return new TailrecTest().newThisFactorial(n - 1, acc * n);
    }

    @Test
    public void testNewThisFactorial() {
        newThisFactorial(50_000, 1);
    }


    @SuppressWarnings("WeakerAccess")
    final int finalFactorial(int n, int acc) {
        if (n <= 1) return acc;
        return finalFactorial(n - 1, acc * n);
    }

    @Test
    public void testFinalFactorial() {
        finalFactorial(50_000, 1);
    }


    @SuppressWarnings("WeakerAccess")
    static int add(int x, int y) {
        if (y == 0) return x;
        return add(x ^ y, (x & y) << 1);
    }

    @Test
    public void testAdd() {
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                if (i + j != add(i, j)) {
                    assertEquals(i + j, add(i, j));
                }
            }
        }
    }


    @SuppressWarnings("WeakerAccess")
    static int gcd(int n, int m) {
        if (m == 0) return n;
        return gcd(m, n % m);
    }

    @Test
    public void testGcd() {
        assertEquals(3 * 7, gcd(2 * 3 * 5 * 7, 3 * 3 * 7 * 11));
    }
}
