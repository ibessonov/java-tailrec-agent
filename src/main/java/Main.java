class Main {

    // good

    private static int manualFactorial(int n, int acc) {
        while (n > 1) {
            acc = acc * n;
            n   = n - 1;
        }
        return acc;
    }

    private static int staticFactorial(int n, int acc) {
        if (n <= 1) return acc;
        return staticFactorial(n - 1, acc * n);
    }

    private int thisFactorial(int n, int acc) {
        if (n <= 1) return acc;
        return thisFactorial(n - 1, acc * n);
    }

    private int thisNewFactorial(int n, int acc) {
        if (n <= 1) return acc;
        return new Main().thisNewFactorial(n - 1, acc * n);
    }

    @SuppressWarnings("WeakerAccess")
    final int finalFactorial(int n, int acc) {
        if (n <= 1) return acc;
        return finalFactorial(n - 1, acc * n);
    }

    @SuppressWarnings("WeakerAccess")
    static int add(int x, int y) {
        if (y == 0) return x;
        return add(x ^ y, (x & y) << 1);
    }

    @SuppressWarnings("WeakerAccess")
    static int gcd(int n, int m) {
        if (m == 0) return n;
        return gcd(m, n % m);
    }

    // bad

    @SuppressWarnings("WeakerAccess")
    int virtualFactorial(int n, int acc) {
        if (n <= 1) return acc;
        return virtualFactorial(n - 1, acc * n);
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

    private static int nonTailRecFactorial(int n) {
        if (n <= 1) return 1;
        return n * nonTailRecFactorial(n - 1);
    }

    public static void main(String[] args) {

        stackOverflowExpected(() -> new Main().virtualFactorial(50_000, 1));
        stackOverflowExpected(() -> exceptionalFactorial(50_000, 1));
        stackOverflowExpected(() -> nonTailRecFactorial(50_000));

        staticFactorial(50_000, 1);
        new Main().thisFactorial(50_000, 1);
        new Main().thisNewFactorial(50_000, 1);
        new Main().finalFactorial(50_000, 1);

        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                if (i + j != add(i, j)) {
                    throw new AssertionError(i + " + " + j);
                }
            }
        }

        if (3 * 7 != gcd(2 * 3 * 5 * 7, 3 * 3 * 7 * 11)) {
            throw new AssertionError();
        }
    }

    private static void stackOverflowExpected(Runnable r) {
        try {
            r.run();
            throw new AssertionError();
        } catch (StackOverflowError ignored) {
        }
    }
}
