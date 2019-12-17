package com.juzix.wallet.utils;

import android.text.TextUtils;

import com.juzhen.framework.util.NumberParserUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author matrixelement
 */
public class BigDecimalUtil {

    private static final int DEF_DIV_SCALE = 8;

    private BigDecimalUtil() {

    }

    public static double add(long v1, long v2) {
        return add(NumberParserUtils.parseDouble(v1), NumberParserUtils.parseDouble(v2));
    }

    /**
     * 提供精确的加法运算。
     *
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */
    public static double add(double v1, double v2) {
        double result = 0D;
        try {
            BigDecimal b1 = new BigDecimal(Double.toString(v1));
            BigDecimal b2 = new BigDecimal(Double.toString(v2));
            result = b1.add(b2).doubleValue();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;

    }


    /**
     * 提供精确的加法运算。
     *
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */
    public static BigDecimal add(String v1, String v2) {
        BigDecimal result = BigDecimal.ZERO;
        try {
            BigDecimal b1 = new BigDecimal(v1);
            BigDecimal b2 = new BigDecimal(v2);
            result = b1.add(b2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;

    }

    public static double add(double v1, double v2, int scale, RoundingMode roundingMode) {
        double result = 0D;
        try {
            BigDecimal b1 = new BigDecimal(Double.toString(v1));
            BigDecimal b2 = new BigDecimal(Double.toString(v2));
            BigDecimal b3 = b1.add(b2);
            result = b3.setScale(scale, roundingMode).doubleValue();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 提供精确的减法运算。
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */
    public static double sub(double v1, double v2) {
        double result = 0D;
        try {
            BigDecimal b1 = new BigDecimal(Double.toString(v1));
            BigDecimal b2 = new BigDecimal(Double.toString(v2));
            result = b1.subtract(b2).doubleValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 提供精确的减法运算。
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */
    public static BigDecimal sub(String v1, String v2) {
        BigDecimal result = BigDecimal.ZERO;
        try {
            BigDecimal b1 = new BigDecimal(v1);
            BigDecimal b2 = new BigDecimal(v2);
            result = b1.subtract(b2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 提供精确的乘法运算。
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static double mul(double v1, double v2) {
        double result = 0D;
        try {
            BigDecimal b1 = new BigDecimal(Double.toString(v1));
            BigDecimal b2 = new BigDecimal(Double.toString(v2));
            result = b1.multiply(b2).doubleValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 提供精确的乘法运算。
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static BigDecimal mul(String v1, String v2) {
        BigDecimal result = BigDecimal.ZERO;
        if (TextUtils.isEmpty(v1) || TextUtils.isEmpty(v2)) {
            return result;
        }
        try {
            BigDecimal b1 = new BigDecimal(v1);
            BigDecimal b2 = new BigDecimal(v2);
            result = b1.multiply(b2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String div(String v1, String v2) {
        return div(v1, v2, DEF_DIV_SCALE);
    }

    /**
     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到 小数点以后10位，以后的数字四舍五入。
     *
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static double div(double v1, double v2) {
        return div(v1, v2, DEF_DIV_SCALE);
    }

    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static double div(double v1, double v2, int scale) {
        double result = 0D;
        try {
            BigDecimal b1 = new BigDecimal(Double.toString(v1));
            BigDecimal b2 = new BigDecimal(Double.toString(v2));
            result = b1.divide(b2, scale, BigDecimal.ROUND_FLOOR).doubleValue();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static String div(String v1, String v2, int scale) {
        String result = BigDecimal.ZERO.toPlainString();
        try {
            BigDecimal b1 = new BigDecimal(v1);
            BigDecimal b2 = new BigDecimal(v2);
            result = b1.divide(b2, scale, BigDecimal.ROUND_FLOOR).toPlainString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static String div(String v1, String v2, int scale, int roundingMode) {
        String result = BigDecimal.ZERO.toPlainString();
        try {
            BigDecimal b1 = new BigDecimal(v1);
            BigDecimal b2 = new BigDecimal(v2);
            result = b1.divide(b2, scale, roundingMode).toPlainString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 提供精确的小数位四舍五入处理。
     *
     * @param v     需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static double round(double v, int scale) {
        double result = 0D;
        try {
            BigDecimal b = new BigDecimal(Double.toString(v));
            BigDecimal one = new BigDecimal("1");
            result = b.divide(one, scale, BigDecimal.ROUND_FLOOR).doubleValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 提供精确的类型转换(Float)
     *
     * @param v 需要被转换的数字
     * @return 返回转换结果
     */
    public static float convertsToFloat(double v) {
        float result = 0f;
        try {
            BigDecimal b = BigDecimal.valueOf(v);
            result = b.floatValue();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 提供精确的类型转换(Float)
     *
     * @param v 需要被转换的数字
     * @return 返回转换结果
     */
    public static float convertsToFloat(String v) {
        float result = 0f;
        try {
            BigDecimal b = new BigDecimal(v);
            result = b.setScale(2, RoundingMode.HALF_UP).floatValue();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 提供精确的类型转换(Int)不进行四舍五入
     *
     * @param v 需要被转换的数字
     * @return 返回转换结果
     */
    public static int convertsToInt(double v) {
        int result = 0;
        try {
            BigDecimal b = new BigDecimal(v);
            result = b.intValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 提供精确的类型转换(Long)
     *
     * @param v 需要被转换的数字
     * @return 返回转换结果
     */
    public static long convertsToLong(double v) {
        long result = 0L;
        try {
            BigDecimal b = new BigDecimal(v);
            result = b.longValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 返回两个数中大的一个值
     *
     * @param v1 需要被对比的第一个数
     * @param v2 需要被对比的第二个数
     * @return 返回两个数中大的一个值
     */
    public static double returnMax(double v1, double v2) {
        double result = 0D;
        try {
            BigDecimal b1 = new BigDecimal(v1);
            BigDecimal b2 = new BigDecimal(v2);
            result = b1.max(b2).doubleValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 返回两个数中小的一个值
     *
     * @param v1 需要被对比的第一个数
     * @param v2 需要被对比的第二个数
     * @return 返回两个数中小的一个值
     */
    public static double returnMin(double v1, double v2) {
        double result = 0D;
        try {
            BigDecimal b1 = new BigDecimal(v1);
            BigDecimal b2 = new BigDecimal(v2);
            result = b1.min(b2).doubleValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 精确对比两个数字
     *
     * @param v1 需要被对比的第一个数
     * @param v2 需要被对比的第二个数
     * @return 如果两个数一样则返回0，如果第一个数比第二个数大则返回1，反之返回-1
     */
    public static int compareTo(double v1, double v2) {
        int result = 0;
        try {
            BigDecimal b1 = new BigDecimal(v1);
            BigDecimal b2 = new BigDecimal(v2);
            result = b1.compareTo(b2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String parseString(double value) {
        try {
            return BigDecimal.valueOf(value).toPlainString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean isBigger(String val1, String val2) {
        if (TextUtils.isEmpty(val1) || TextUtils.isEmpty(val2)) {
            return false;
        }
        try {
            return new BigDecimal(val1).compareTo(new BigDecimal(val2)) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isNotSmaller(String val1, String val2) {
        if (TextUtils.isEmpty(val1) || TextUtils.isEmpty(val2)) {
            return false;
        }
        try {
            return new BigDecimal(val1).compareTo(new BigDecimal(val2)) >= 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isBiggerThanZero(String val1) {
        if (TextUtils.isEmpty(val1)) {
            return false;
        }
        try {
            return new BigDecimal(val1).compareTo(BigDecimal.ZERO) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isEqualsZero(BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            return true;
        }

        return bigDecimal.compareTo(BigDecimal.ZERO) == 0;
    }

    public static BigDecimal toBigDecimal(String value) {
        try {
            return new BigDecimal(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }
}
