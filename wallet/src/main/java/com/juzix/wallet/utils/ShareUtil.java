package com.juzix.wallet.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class ShareUtil {
    public static final String QQ       = "com.tencent.mobileqq";
    public static final String MM       = "com.tencent.mm";
    public static final String SINA     = "com.sina.weibo";
    public static final String FACEBOOK = "com.facebook.katana";
    public static final String TWITTER  = "com.twitter.android";

    public static void shareApp(Context context, String name) {
        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(name);
            context.startActivity(intent);
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    public static void shareUrl(Context context, String url){
        try {
            Intent intent= new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.setData(Uri.parse(url));
            context.startActivity(intent);
        }catch (Exception exp){
            exp.printStackTrace();
        }
    }
}
