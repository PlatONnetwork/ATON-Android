package com.platon.aton.utils;

import com.platon.framework.utils.LogUtils;

/**
 * @author matrixelement
 */
public class AddressFormatUtil {

    private AddressFormatUtil() {

    }

    public static String formatAddress(String address) {

        String text = "";

        if (address != null) {

            String regex = "(\\w{10})(\\w*)(\\w{10})";

            try {
                text = address.replaceAll(regex, "$1...$3");
            } catch (Exception e) {
                LogUtils.e(e.getMessage(),e.fillInStackTrace());
            }
        }
        return text;
    }

    public static String formatTransactionAddress(String address) {

        String text = "";

        if (address != null) {

            String regex = "(\\w{4})(\\w*)(\\w{4})";

            try {
                text = address.replaceAll(regex, "$1...$3");
            } catch (Exception e) {
                LogUtils.e(e.getMessage(),e.fillInStackTrace());
            }
        }
        return text;
    }

    public static String formatClaimRewardRecordAddress(String address) {

        String text = "";

        if (address != null) {

            String regex = "(\\w{6})(\\w*)(\\w{8})";

            try {
                text = address.replaceAll(regex, "$1...$3");
            } catch (Exception e) {
                LogUtils.e(e.getMessage(),e.fillInStackTrace());
            }
        }
        return text;
    }
}
