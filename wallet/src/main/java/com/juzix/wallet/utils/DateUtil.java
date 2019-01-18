package com.juzix.wallet.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

    private static final String TAG = DateUtil.class.getSimpleName();

    public static final String DATETIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm";

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat();

    private DateUtil() {

    }

    public static String format(long time, String pattern) {
        Date date = new Date(time);
        if (date == null) {
            return null;
        } else {
            simpleDateFormat.applyLocalizedPattern(pattern);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            return simpleDateFormat.format(date);
        }
    }


}
