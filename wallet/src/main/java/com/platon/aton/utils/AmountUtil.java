package com.platon.aton.utils;

import android.text.TextUtils;

import com.platon.framework.utils.LogUtils;

import java.math.BigDecimal;

/**
 * 金额处理工具类
 */
public class AmountUtil {

    private final static String VALUE_1E18 = "1e18";
    private final static String VALUE_1E2 = "1e2";

    private AmountUtil() {

    }

    /**
     * 处理余额，最多保留八位小数，多余部分截断显示
     *
     * @param value
     * @param maxDigit
     * @return
     */
    public static String getPrettyBalance(String value, int maxDigit) {

        if (TextUtils.isEmpty(value)) {
            return BigDecimal.ZERO.toPlainString();
        }

        if (maxDigit < 0) {
            throw new RuntimeException("unsupported scale");
        }

        try {
            //value除以10的10次方然后取整就是保留八位小数截断显示的值。因为value是真实值乘以10的18次方后的值
            BigDecimal bigDecimal = new BigDecimal(value).divide(new BigDecimal(10).pow(18 - maxDigit));
            bigDecimal = bigDecimal.setScale(0, BigDecimal.ROUND_DOWN);
            return bigDecimal.multiply(new BigDecimal(10).pow(18 - maxDigit)).stripTrailingZeros().toPlainString();
        } catch (Exception exp) {
            LogUtils.e(exp.getMessage(),exp.fillInStackTrace());
        }
        return BigDecimal.ZERO.toPlainString();
    }

    /**
     * 处理手续费，最多保留八位小数，多余部分截断，并根据多余部分是否有值，有值则加1
     * 例如0.123456789 --> 0.12345679
     * 例如0.999999999 --> 1.00
     *
     * @param value
     * @param maxDigit
     * @return
     */
    public static String getPrettyFee(String value, int maxDigit) {

        if (TextUtils.isEmpty(value)) {
            return BigDecimal.ZERO.toPlainString();
        }

        if (maxDigit < 0) {
            throw new RuntimeException("unsupported scale");
        }

        try {
            //value除以10的10次方然后取整就是保留八位小数截断显示的值。因为value是真实值乘以10的18次方后的值
            BigDecimal bigDecimal = new BigDecimal(value).divide(new BigDecimal(10).pow(18 - maxDigit));
            //是否有小数位
            BigDecimal resultBigDecimal = bigDecimal.setScale(0, BigDecimal.ROUND_DOWN);
            //有小数位
            if (bigDecimal.compareTo(resultBigDecimal) > 0) {
                resultBigDecimal = resultBigDecimal.add(BigDecimal.ONE);
            }
            return resultBigDecimal.multiply(new BigDecimal(10).pow(18 - maxDigit)).stripTrailingZeros().toPlainString();
        } catch (Exception e) {
            LogUtils.e(e.getMessage(),e.fillInStackTrace());
        }
        return BigDecimal.ZERO.toPlainString();
    }

    /**
     * von to lat  value除以10的18次方，并且保留小数点后八位
     *
     * @param value
     * @return
     */
    public static String convertVonToLat(String value, int max) {
        return NumberParserUtils.getPrettyNumber(BigDecimalUtil.div(value, VALUE_1E18), max);
    }

    public static String convertVonToLatWithFractionDigits(String value, int fractionDigits) {
        return NumberParserUtils.parseStringWithFractionDigits(BigDecimalUtil.div(value, VALUE_1E18, fractionDigits), fractionDigits);
    }

    /**
     * von to lat  value除以10的18次方，默认保留小数点后八位
     *
     * @param value
     * @return
     */
    public static String convertVonToLat(String value) {
        return NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(value, VALUE_1E18));
    }

    /**
     * 格式化金额文本
     *
     * @param amount
     * @return
     */
    public static String formatAmountText(String amount) {
        return TextUtils.isEmpty(amount) ? "— —" : StringUtil.formatBalance(BigDecimalUtil.div(amount, VALUE_1E18));
    }

    /**
     * 格式化金额文本
     *
     * @param amount
     * @return
     */
    public static String formatAmountText(String amount, int maxFractionDigits) {
        return TextUtils.isEmpty(amount) ? "— —" : StringUtil.formatBalance(BigDecimalUtil.div(amount, VALUE_1E18, maxFractionDigits), maxFractionDigits);
    }
}
