package com.platon.wallet.component.widget;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * @author matrixelement
 */
public class PointLengthFilter implements InputFilter {

    private static final int DECIMAL_DIGITS = 8;

    @Override
    public CharSequence filter(CharSequence source, int start, int end,
                               Spanned dest, int dstart, int dend) {
        // 删除等特殊字符，直接返回
        if ("".equals(source.toString())) {
            return null;
        }
        String dValue = dest.toString();
        String[] splitArray = dValue.split("\\.");
        if (splitArray.length > 1) {
            String dotValue = splitArray[1];
            int diff = dotValue.length() + 1 - DECIMAL_DIGITS;
            if (diff > 0) {
                return source.subSequence(start, end - diff);
            }
        }
        return source;
    }
}
