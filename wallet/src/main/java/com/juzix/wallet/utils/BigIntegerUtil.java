package com.juzix.wallet.utils;

import java.math.BigInteger;

public class BigIntegerUtil {


    private BigIntegerUtil() {

    }

    public static BigInteger toBigInteger(String value) {
        try {
            return new BigInteger(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BigInteger.ZERO;
    }
}
