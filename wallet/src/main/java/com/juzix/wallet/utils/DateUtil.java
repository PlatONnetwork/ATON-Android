package com.juzix.wallet.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

    private static final String TAG = DateUtil.class.getSimpleName();

    public static final String DATETIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm";
    public static final String DATETIME_FORMAT_PATTERN_WITH_SECOND = "yyyy-MM-dd HH:mm:ss";

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


    public static boolean isToday(long time){
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        String nowDay = sf.format(new Date());
        //对比的时间
        String day = sf.format(new Date(time));
        return day.equals(nowDay);
    }
}
