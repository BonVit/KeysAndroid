package com.vb.keysandroid;

/**
 * Created by bonar on 3/25/2017.
 */

public class Algorithm {
    public static boolean isPrime(int n) {
        for (int i = 2; i < Math.sqrt(n) + 1; i++)
            if (n % i == 0)
                return false;
        return true;
    }
}
