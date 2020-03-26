package com.platon.aton.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.TextUtils;

import com.platon.aton.component.ui.view.MainActivity;
import com.platon.framework.app.Constants;
import com.platon.framework.utils.PreferenceTool;

import java.util.Locale;

/**
 * @author matrixelement
 */
public class LanguageUtil {


    private LanguageUtil() {

    }

    public static Locale getLocale(Context context) {

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();

        String language = PreferenceTool.getString(Constants.Preference.KEY_LANGUAGE);

        if (TextUtils.isEmpty(language)) {
            Locale locale = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                locale = configuration.getLocales().get(0);
            } else {
                locale = configuration.locale;
            }
            return locale;
        } else {
            return new Locale(language, "");
        }

    }

    public static void switchLanguage(Context context, Locale locale) {

        PreferenceTool.putString(Constants.Preference.KEY_LANGUAGE, locale.getLanguage());

        MainActivity.restart(context);

    }

}
