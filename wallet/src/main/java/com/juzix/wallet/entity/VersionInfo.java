package com.juzix.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.alibaba.fastjson.annotation.JSONField;

import jnr.constants.platform.PRIO;

/**
 * @author matrixelement
 */
public class VersionInfo {

    @JSONField(name = "android")
    private AndroidVersionInfo androidVersionInfo;
    @JSONField(name = "ios")
    private IosVersionInfo iosVersionInfo;

    public VersionInfo() {
        super();
    }

    public AndroidVersionInfo getAndroidVersionInfo() {
        return androidVersionInfo;
    }

    public void setAndroidVersionInfo(AndroidVersionInfo androidVersionInfo) {
        this.androidVersionInfo = androidVersionInfo;
    }

    public IosVersionInfo getIosVersionInfo() {
        return iosVersionInfo;
    }

    public void setIosVersionInfo(IosVersionInfo iosVersionInfo) {
        this.iosVersionInfo = iosVersionInfo;
    }


    public String getVersion() {
        if (androidVersionInfo != null) {
            return androidVersionInfo.getVersion();
        }
        return null;
    }

    public String getDownloadUrl() {
        if (androidVersionInfo != null) {
            return androidVersionInfo.getDownloadUrl();
        }
        return null;
    }

}
