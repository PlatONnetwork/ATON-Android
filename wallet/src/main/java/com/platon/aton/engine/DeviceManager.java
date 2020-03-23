package com.platon.aton.engine;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.annotation.StringDef;
import android.text.TextUtils;

import com.platon.aton.BuildConfig;
import com.platon.aton.config.AppSettings;
import com.platon.aton.utils.MD5Utils;
import com.platon.aton.utils.SystemUtil;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * 设备管理类
 *
 * @author matrixelement
 */
public class DeviceManager {

    private String OS;
    private String deviceID;
    private String channel;

    @StringDef({Channel.GOOGLE_PLAY, Channel.PLATON_NETWORK})
    public @interface Channel {

        String GOOGLE_PLAY = "GooglePlay";

        String PLATON_NETWORK = "PlatONNetwork";
    }

    private static final class Singleton {
        private static final DeviceManager DEVICE_MANAGER = new DeviceManager();
    }

    private DeviceManager() {

    }

    public static DeviceManager getInstance() {
        return Singleton.DEVICE_MANAGER;
    }

    public void init(Context context, String channel) {
        // 部分写死的配置项
        this.OS = "android";
        this.channel = channel;

        if (TextUtils.isEmpty(deviceID)) {
            synchronized (DeviceManager.class) {
                if (deviceID == null) {
                    final String id = AppSettings.getInstance().getDeviceId();
                    if (!TextUtils.isEmpty(id)) {
                        // Use the ids previously computed and stored in the prefs file
                        deviceID = UUID.fromString(id).toString();
                    } else {
                        final String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                        // Use the Android ID unless it's broken, in which case fallback on deviceId,
                        // unless it's not available, then fallback on a random number which we store
                        // to a prefs file
                        try {
                            if (!"9774d56d682e549c".equals(androidId) && !TextUtils.isEmpty(androidId)) {
                                deviceID = UUID.nameUUIDFromBytes(androidId.getBytes("utf8")).toString();
                            } else {
                                deviceID = System.currentTimeMillis() + new String(MD5Utils.encode(SystemUtil.getMacAddr().getBytes()), "utf-8");
                            }
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                        // Write the value out to the prefs file
                        AppSettings.getInstance().setDeviceId(deviceID);
                    }
                }
            }
        }
    }

    public String getOS() {
        return OS;
    }

    public void setOS(String OS) {
        this.OS = OS;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
        AppSettings.getInstance().setDeviceId(deviceID);
    }

    public String getChannel() {
        return TextUtils.isEmpty(channel) ? BuildConfig.CHANNEL : channel;
    }

    public boolean isGooglePlayChannel() {
        return Channel.GOOGLE_PLAY.equals(getChannel());
    }

    //获取版本名
    public static String getVersionName(Context context) {
        PackageInfo packageInfo = getPackageInfo(context);
        return packageInfo != null ? packageInfo.versionName : null;
    }

    //获取版本号
    public static int getVersionCode(Context context) {
        PackageInfo packageInfo = getPackageInfo(context);
        return packageInfo != null ? packageInfo.versionCode : 0;
    }

    //通过PackageInfo得到的想要启动的应用的包名
    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pInfo = null;
        try {
            //通过PackageManager可以得到PackageInfo
            PackageManager pManager = context.getPackageManager();
            pInfo = pManager.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);
            return pInfo;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pInfo;
    }


}
