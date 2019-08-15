package com.juzhen.framework.util;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * 数字转化
 *
 * @author ziv
 */
public class NumberParserUtils {

    private NumberParserUtils() {

    }

    /**
     * 字符串 ==>> long
     *
     * @param value String
     * @return long
     */
    public static long parseLong(String value) {
        return parseLong(value, 0L);
    }

    /**
     * 字符串 ==>> long
     * 指定默认值
     *
     * @param value        String
     * @param defaultValue 默认值
     * @return long
     */
    public static long parseLong(String value, long defaultValue) {
        if (TextUtils.isEmpty(value)) {
            return defaultValue;
        }
        long resultValue = defaultValue;
        try {
            resultValue = Long.parseLong(value);
        } catch (Exception e) {
            e.printStackTrace();
            return resultValue;
        }
        return resultValue;
    }

    /**
     * 字符串 ==>> double
     *
     * @param value String
     * @return double
     */
    public static double parseDouble(String value) {
        return parseDouble(TextUtils.isEmpty(value) ? "0" : value, 0.0D);
    }

    /**
     * 字符串 ==>> double
     * 指定默认值
     *
     * @param value        String
     * @param defaultValue 默认值
     * @return double
     */
    public static double parseDouble(String value, double defaultValue) {
        if (TextUtils.isEmpty(value)) {
            return defaultValue;
        }
        double resultValue;
        try {
            resultValue = Double.parseDouble(value);
        } catch (Exception e) {
            e.printStackTrace();
            resultValue = defaultValue;
        }
        return resultValue;
    }

    /**
     * 字符串 ==>> float
     *
     * @param value String
     * @return float
     */
    public static float parseFloat(String value) {
        return parseFloat(value, 0.0F);
    }

    /**
     * 字符串 ==>> float
     * 指定默认值
     *
     * @param value        String
     * @param defaultValue 默认值
     * @return float
     */
    public static float parseFloat(String value, float defaultValue) {
        if (TextUtils.isEmpty(value)) {
            return defaultValue;
        }
        float resultValue;
        try {
            resultValue = Float.parseFloat(value);
        } catch (Exception e) {
            e.printStackTrace();
            resultValue = defaultValue;
        }
        return resultValue;
    }

    /**
     * 字符串 ==>> int
     *
     * @param value String
     * @return int
     */
    public static int parseInt(String value) {
        return parseInt(value, 0);
    }

    /**
     * 字符串 ==>> int
     * 指定默认值
     *
     * @param value        String
     * @param defaultValue 默认值
     * @return int
     */
    public static int parseInt(String value, int defaultValue) {
        if (TextUtils.isEmpty(value)) {
            return defaultValue;
        }
        int resultValue;
        try {
            resultValue = Integer.parseInt(value);
        } catch (Exception e) {
            e.printStackTrace();
            resultValue = defaultValue;
        }
        return resultValue;
    }

    /**
     * Object ==>> int
     *
     * @param object Object
     * @return int
     */
    public static int parseInt(Object object) {
        return parseInt(String.valueOf(object));
    }

    /**
     * Object ==>> double
     *
     * @param object Object
     * @return int
     */
    public static double parseDouble(Object object) {
        return parseDouble(String.valueOf(object));
    }

    public static String parseDoubleToPrettyNumber(double value) {
        String bigDecimalStr;
        try {//当number==NaN，会throw "Infinity or NaN",所以要catch
            bigDecimalStr = BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
        } catch (NumberFormatException e) {
            bigDecimalStr = "-1";
        }
        if (TextUtils.isEmpty(bigDecimalStr)) {
            return "0";
        }
        if (bigDecimalStr.endsWith(".00") || bigDecimalStr.endsWith(".0")) {
            return bigDecimalStr.substring(0, bigDecimalStr.lastIndexOf("."));
        }
        return bigDecimalStr;
    }

    public static String parseStringWithoutTwoDecimals(String value) {
        String price = String.format("%d", parseInt(value) / 100);
        return getPrettyNumber(price);
    }

    public static String parseStringWithoutTwoDecimalsToInt(String value) {
        String price = String.format("%d", parseInt(value) / 100);
        return getPrettyNumber(price);
    }

    // 去除数字里多余的0
    public static String getPrettyNumber(String number) {
        String bigDecimalStr;
        try {//当number==NaN，会throw "Infinity or NaN",所以要catch
            bigDecimalStr = BigDecimal.valueOf(parseDouble(number)).stripTrailingZeros().toPlainString();
        } catch (NumberFormatException e) {
            bigDecimalStr = "-1";
        }
        if (TextUtils.isEmpty(bigDecimalStr)) {
            return "0";
        }

        if (bigDecimalStr.endsWith(".00") || bigDecimalStr.endsWith(".0")) {
            return bigDecimalStr.substring(0, bigDecimalStr.lastIndexOf("."));
        }
        return bigDecimalStr;
    }

    public static String getPrettyBalance(String balance) {

        String bigDecimalStr = null;
        try {//当number==NaN，会throw "Infinity or NaN",所以要catch
            String value = String.format("%.8f", parseDouble(balance));
            BigDecimal bigDecimal = new BigDecimal(value);
            if (bigDecimal.doubleValue() != 0) {
                bigDecimalStr = bigDecimal.stripTrailingZeros().toPlainString();
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(bigDecimalStr)) {
            return "0.00";
        }

        if (!bigDecimalStr.contains(".")) {
            return bigDecimalStr.concat(".00");
        }

        return bigDecimalStr;
    }

    public static String getPrettyBalance(double balance) {
        return getPrettyNumber(balance, 8);
    }

    public static String getPrettyDetailBalance(double balance) {
        return getPrettyNumber(balance, 12);
    }

    public static String getPrettyNumber(double value, int maxDigit) {

        String bigDecimalStr = null;
        try {//当number==NaN，会throw "Infinity or NaN",所以要catch
            // TODO: 2018/11/6 判断是否为0
            BigDecimal bigDecimal = new BigDecimal(String.valueOf(value));
            bigDecimal = bigDecimal.setScale(maxDigit, BigDecimal.ROUND_DOWN);
            if (bigDecimal.doubleValue() != 0) {
                bigDecimalStr = bigDecimal.stripTrailingZeros().toPlainString();
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(bigDecimalStr)) {
            return "0";
        }

        if (maxDigit > 0 && !bigDecimalStr.contains(".")) {
            return bigDecimalStr.concat(".00");
        }

        return bigDecimalStr;
    }

    public static String getPrettyNumber(double value, int maxDigit, int roundingMode) {

        String bigDecimalStr = null;
        try {//当number==NaN，会throw "Infinity or NaN",所以要catch
            // TODO: 2018/11/6 判断是否为0
            BigDecimal bigDecimal = new BigDecimal(String.valueOf(value));
            bigDecimal = bigDecimal.setScale(maxDigit, roundingMode);
            if (bigDecimal.doubleValue() != 0) {
                bigDecimalStr = bigDecimal.stripTrailingZeros().toPlainString();
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(bigDecimalStr)) {
            return "0";
        }

        if (maxDigit > 0 && !bigDecimalStr.contains(".")) {
            return bigDecimalStr.concat(".00");
        }

        return bigDecimalStr;
    }

    public static String getPrettyNumber(String value, int maxDigit) {

        String bigDecimalStr = null;
        try {//当number==NaN，会throw "Infinity or NaN",所以要catch
            // TODO: 2018/11/6 判断是否为0
            BigDecimal bigDecimal = new BigDecimal(value);
            bigDecimal = bigDecimal.setScale(maxDigit, BigDecimal.ROUND_DOWN);
            if (bigDecimal.doubleValue() != 0) {
                bigDecimalStr = bigDecimal.stripTrailingZeros().toPlainString();
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(bigDecimalStr)) {
            return "0";
        }

        if (maxDigit > 0 && !bigDecimalStr.contains(".")) {
            return bigDecimalStr.concat(".00");
        }

        return bigDecimalStr;
    }
}
