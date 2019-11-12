package com.juzix.wallet.utils;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

public class UMEventUtil {

    private UMEventUtil() {

    }

    public static void onEventCount(Context context, String eventId) {
        Map<String, Object> map = new HashMap<>();
        map.put("click", 1);
        MobclickAgent.onEventObject(context, eventId, map);
    }

    public static void onEventCount(){

    }
}
