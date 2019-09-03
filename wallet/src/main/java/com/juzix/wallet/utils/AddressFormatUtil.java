package com.juzix.wallet.utils;

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
                e.printStackTrace();
            }
        }
        return text;
    }

    public static String formatTransactionAddress(String address){

        String text = "";

        if (address != null) {

            String regex = "(\\w{4})(\\w*)(\\w{4})";

            try {
                text = address.replaceAll(regex, "$1...$3");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return text;
    }
}
