package com.vb.keysandroid;

import android.util.Log;

import java.math.BigInteger;

/**
 * Created by bonar on 3/25/2017.
 */

public class Client {
    private static final String TAG = "Client";

    private static final int RANDOM_MULT = 100;

    private BigInteger a;
    private BigInteger p;
    private int x;
    private BigInteger y;

    public Client(String a, String p)
    {
        x = (int) (Math.random() * RANDOM_MULT);

        Log.d(TAG, "X: " + Integer.toString(x));

        this.a = new BigInteger(a);
        this.p = new BigInteger(p);

        Log.d(TAG, "A|P: " + this.a.toString() + " : " + this.p.toString());

        this.y = (this.a.pow(x).mod(this.p));

        Log.d(TAG, "Y: " + this.y.toString());
    }

    BigInteger getPublicKey()
    {
        return y;
    }

    BigInteger getSharedKey(BigInteger y)
    {
        return y.pow(x).mod(p);
    }
}
