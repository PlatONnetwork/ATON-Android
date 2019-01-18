package com.juzix.wallet.entity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import com.juzix.wallet.R;

/**
 * @author matrixelement
 */
public enum ShareAppInfo implements Parcelable {

    WECHAT("com.tencent.mm") {
        @Override
        public int getIconRes() {
            return R.drawable.icon_wechat;
        }

        @Override
        public int getTitleRes() {
            return R.string.wechat;
        }

    }, QQ("com.tencent.mobileqq") {
        @Override
        public int getIconRes() {
            return R.drawable.icon_qq;
        }

        @Override
        public int getTitleRes() {
            return R.string.qq;
        }

    }, WEIBO("com.sina.weibo") {
        @Override
        public int getIconRes() {
            return R.drawable.icon_weibo;
        }

        @Override
        public int getTitleRes() {
            return R.string.weibo;
        }

    }, FACEBOOK("com.facebook.katana") {
        @Override
        public int getIconRes() {
            return R.drawable.icon_facebook;
        }

        @Override
        public int getTitleRes() {
            return R.string.facebook;
        }

    }, TWITTER("com.twitter.android") {
        @Override
        public int getIconRes() {
            return R.drawable.icon_twitter;
        }

        @Override
        public int getTitleRes() {
            return R.string.twitter;
        }

    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    public static final Creator<ShareAppInfo> CREATOR = new Creator<ShareAppInfo>() {
        @Override
        public ShareAppInfo createFromParcel(Parcel in) {
            return ShareAppInfo.valueOf(in.readString());
        }

        @Override
        public ShareAppInfo[] newArray(int size) {
            return new ShareAppInfo[size];
        }
    };

    public String packageName;

    ShareAppInfo(String packageName) {
        this.packageName = packageName;
    }

    public abstract int getIconRes();

    public abstract int getTitleRes();

    public boolean actionStart(Context context) {
        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }


}
