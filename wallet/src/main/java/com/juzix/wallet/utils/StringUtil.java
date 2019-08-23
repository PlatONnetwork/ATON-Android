/*
 * Copyright (C) 2014 venshine.cn@gmail.com
 *
 * Licensed under the Apache License, VersionInfo 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.juzix.wallet.utils;

import android.content.Context;

import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * 字符串工具类
 *
 * @author matrixelement
 */
public class StringUtil {

    private final static int[] SIZE_TABLE = {9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999,
            Integer.MAX_VALUE};

    /**
     * calculate the size of an integer number
     *
     * @param x
     * @return
     */
    public static int sizeOfInt(int x) {
        for (int i = 0; ; i++)
            if (x <= SIZE_TABLE[i]) {
                return i + 1;
            }
    }

    /**
     * Judge whether each character of the string equals
     *
     * @param str
     * @return
     */
    public static boolean isCharEqual(String str) {
        return str.replace(str.charAt(0), ' ').trim().length() == 0;
    }

    /**
     * Determines if the string is a digit
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Judge whether the string is whitespace, empty ("") or null.
     *
     * @param str
     * @return
     */
    public static boolean equalsNull(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0 || str.equalsIgnoreCase("null")) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 字符串数字显示按千分位显示
     */
    public static String formatBalance(double price, boolean halfUp) {
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(8);//设置最大的小数位数
        decimalFormat.setMinimumFractionDigits(2);
        decimalFormat.setGroupingSize(3);//设置分组大小，也就是显示逗号的位置
        decimalFormat.setRoundingMode(halfUp ? RoundingMode.HALF_UP : RoundingMode.FLOOR);
        return decimalFormat.format(new BigDecimal(NumberParserUtils.getPrettyNumber(price, 8)));
    }

    /**
     * 字符串数字显示按千分位显示
     */
    public static String formatBalance(double price) {
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(8);//设置最大的小数位数
        decimalFormat.setMinimumFractionDigits(2);
        decimalFormat.setGroupingSize(3);//设置分组大小，也就是显示逗号的位置
        return decimalFormat.format(new BigDecimal(NumberParserUtils.getPrettyNumber(price, 8)));
    }


    /**
     * 获取转账金额的数量级描述
     *
     * @param transferAmount
     * @return
     */
    public static String getAmountMagnitudes(Context context, String transferAmount) {
        double amount = NumberParserUtils.parseDouble(transferAmount);
        //万亿
        if (amount >= Constants.Magnitudes.TRILLION) {
            return context.getString(R.string.msg_trillion);
        } else if (amount >= Constants.Magnitudes.HUNDRED_BILLION) {
            return context.getString(R.string.msg_hundred_billion);
        } else if (amount >= Constants.Magnitudes.TEN_BILLION) {
            return context.getString(R.string.msg_ten_billion);
        } else if (amount >= Constants.Magnitudes.BILLION) {
            return context.getString(R.string.msg_billion);
        } else if (amount >= Constants.Magnitudes.HUNDRED_MILLION) {
            return context.getString(R.string.msg_hundred_million);
        } else if (amount >= Constants.Magnitudes.TEN_MILLION) {
            return context.getString(R.string.msg_ten_million);
        } else if (amount >= Constants.Magnitudes.MILLION) {
            return context.getString(R.string.msg_million);
        } else if (amount >= Constants.Magnitudes.HUNDRED_THOUSAND) {
            return context.getString(R.string.msg_hundred_thousand);
        } else if (amount >= Constants.Magnitudes.TEN_THOUSAND) {
            return context.getString(R.string.msg_ten_thousand);
        } else if (amount >= Constants.Magnitudes.THOUSAND) {
            return context.getString(R.string.msg_thousand);
        } else if (amount >= Constants.Magnitudes.HUNDRED) {
            return context.getString(R.string.msg_hundred);
        } else {
            return "";
        }
    }


}
