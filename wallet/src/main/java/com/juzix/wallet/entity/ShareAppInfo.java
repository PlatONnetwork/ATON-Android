package com.juzix.wallet.entity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.juzix.wallet.R;
import com.juzix.wallet.utils.AppUtil;
import com.juzix.wallet.utils.ToastUtil;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.io.File;

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

        @Override
        public SHARE_MEDIA getShareMedia() {
            return SHARE_MEDIA.WEIXIN;
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

        @Override
        public SHARE_MEDIA getShareMedia() {
            return SHARE_MEDIA.QQ;
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

        @Override
        public SHARE_MEDIA getShareMedia() {
            return SHARE_MEDIA.SINA;
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

        @Override
        public SHARE_MEDIA getShareMedia() {
            return SHARE_MEDIA.FACEBOOK;
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

        @Override
        public SHARE_MEDIA getShareMedia() {
            return SHARE_MEDIA.TWITTER;
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

    public abstract SHARE_MEDIA getShareMedia();

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

    public void share(Activity activity, Bitmap bitmap, UMShareListener umShareListener) {
        SHARE_MEDIA shareMedia = getShareMedia();
        if (isInstall(activity, shareMedia)) {
            if (shareMedia == SHARE_MEDIA.QQ || shareMedia == SHARE_MEDIA.WEIXIN) {
                actionStart(activity);
            } else {
                new ShareAction(activity).setPlatform(shareMedia).withMedia(new UMImage(activity, bitmap)).setCallback(umShareListener).share();
            }
        } else {
            ToastUtil.showLongToast(activity, R.string.msg_not_installed);
        }

    }

    public boolean isInstall(Activity activity, SHARE_MEDIA shareMedia) {

        if (shareMedia == SHARE_MEDIA.QQ || shareMedia == SHARE_MEDIA.WEIXIN) {
            return AppUtil.isInstall(activity, this);
        } else {
            return UMShareAPI.get(activity).isInstall(activity, shareMedia);
        }


    }


}
