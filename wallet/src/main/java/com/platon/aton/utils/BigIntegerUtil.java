package com.platon.aton.utils;

import com.platon.framework.utils.LogUtils;

import java.math.BigInteger;

public class BigIntegerUtil {


    private BigIntegerUtil() {

    }

    public static BigInteger toBigInteger(String value) {
        try {
            return new BigInteger(value);
        } catch (Exception e) {
            LogUtils.e(e.getMessage(),e.fillInStackTrace());
        }
        return BigInteger.ZERO;
    }

    public static BigInteger max(BigInteger value1, BigInteger value2) {

        return value1.compareTo(value2) > 0 ? value1 : value2;
    }

    public static String toString(BigInteger bigInteger) {
        if (bigInteger == null) {
            return BigInteger.ZERO.toString(10);
        }
        return bigInteger.toString(10);
    }

    public static String mul(String aValue, String bValue) {
        return mul(toBigInteger(aValue), toBigInteger(bValue));
    }

    public static String mul(BigInteger aValue, BigInteger bValue) {

        return aValue != null && bValue != null ? aValue.multiply(bValue).toString(10) : BigInteger.ZERO.toString(10);
    }

}
