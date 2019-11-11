package com.juzix.wallet.utils;

import android.content.Context;

import org.spongycastle.util.encoders.Hex;

import java.io.InputStream;

public class FileUtil {

    public static String getStringFromAssets(Context context, String fileName) {
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return Hex.toHexString(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getAssets(Context context, String fileName) {
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
