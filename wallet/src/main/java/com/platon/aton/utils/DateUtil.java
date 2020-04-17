package com.platon.aton.utils;

import com.platon.framework.utils.LogUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

    private static final String TAG = DateUtil.class.getSimpleName();

    public static final String DATETIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm";
    public static final String DATETIME_FORMAT_PATTERN2 = "yyyy/MMdd HH:mm";
    public static final String DATETIME_FORMAT_PATTERN_WITH_SECOND = "yyyy-MM-dd HH:mm:ss";


    private DateUtil() {

    }

    public static String format(long time, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        if(time > 0){
            Date date = new Date(time);
            simpleDateFormat.applyLocalizedPattern(pattern);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            return simpleDateFormat.format(date);
        }else{
            return null;
        }
    }

    public static long parse(String timeText, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        simpleDateFormat.applyPattern(pattern);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        try {
            return simpleDateFormat.parse(timeText).getTime();
        } catch (ParseException e) {
            LogUtils.e(e.getMessage(),e.fillInStackTrace());
        }
        return 0L;
    }

    public static boolean isToday(long time) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        String nowDay = sf.format(new Date());
        //对比的时间
        String day = sf.format(new Date(time));
        return day.equals(nowDay);
    }

    /**
     * 毫秒转秒
     *
     * @param millisecond
     * @return
     */
    public static long millisecondToMinutes(long millisecond) {

        long time = millisecond / (60 * 1000);

        return Math.max(1, time);

    }
}
