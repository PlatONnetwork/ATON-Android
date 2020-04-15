package com.platon.framework.utils;

import android.os.LocaleList;
import android.text.TextUtils;

import com.platon.framework.app.Constants;

import java.util.Locale;

/**
 * @author matrixelement
 */
public class LanguageUtil {


    private LanguageUtil() {

    }

    public static Locale getLocale() {

        String language = PreferenceTool.getString(Constants.Preference.KEY_LANGUAGE);

        if (TextUtils.isEmpty(language)) {
            Locale locale = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                locale = LocaleList.getDefault().get(0);
            } else {
                locale = Locale.getDefault();
            }

            if (TextUtils.equals(locale.getLanguage(), Locale.CHINESE.getLanguage())) {
                return new Locale(Locale.CHINESE.getLanguage(), "");
            } else {
                return new Locale(Locale.ENGLISH.getLanguage(), "");
            }
        } else {
            return new Locale(language, "");
        }

    }

}
