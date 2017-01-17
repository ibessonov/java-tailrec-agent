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

    final int finalFactorial(int n, int acc) {
        if (n <= 1) return acc;
        return finalFactorial(n - 1, acc * n);
    }

    // bad

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

        try {
            new Main().virtualFactorial(50_000, 1);
            throw new AssertionError();
        } catch (StackOverflowError ignored) {
        }

        try {
            exceptionalFactorial(50_000, 1);
            throw new AssertionError();
        } catch (StackOverflowError ignored) {
        }

        try {
            nonTailRecFactorial(50_000);
            throw new AssertionError();
        } catch (StackOverflowError ignored) {
        }

        staticFactorial(50_000, 1);

        new Main().thisFactorial(50_000, 1);

        new Main().finalFactorial(50_000, 1);

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (i + j != add(i, j)) {
                    throw new AssertionError(i + " + " + j);
                }
            }
        }
    }

    static int add(int x, int y) {
        if (y == 0) return x;
        return add(x ^ y, (x & y) << 1);
    }
}
