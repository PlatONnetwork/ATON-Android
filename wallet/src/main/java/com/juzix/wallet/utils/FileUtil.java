package com.juzix.wallet.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import org.spongycastle.util.encoders.Hex;

import java.io.File;
import java.io.InputStream;

public class FileUtil {

    public static String getStringFromAssets(Context context, String filename){
        try {
            InputStream is   = context.getAssets().open(filename);
            int         size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return Hex.toHexString(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getAssets(Context context, String filename){
        try {
            InputStream is   = context.getAssets().open(filename);
            int         size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void install(Context context, File apkFile) {
        Intent intent  = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(
                    context
                    , context.getPackageName() + ".fileprovider"
                    , apkFile);

            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }
}
