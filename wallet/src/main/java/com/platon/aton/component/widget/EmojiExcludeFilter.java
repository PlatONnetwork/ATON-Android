package com.platon.aton.component.widget;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * @author ziv
 * date On 2020-03-16
 */
public class EmojiExcludeFilter implements InputFilter {

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        for (int i = start; i < end; i++) {
            int type = Character.getType(source.charAt(i));
            if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL) {
                return "";
            }
        }
        return null;
    }
}
