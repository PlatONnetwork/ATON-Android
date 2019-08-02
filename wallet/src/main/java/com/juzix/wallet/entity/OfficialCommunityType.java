package com.juzix.wallet.entity;


import android.support.annotation.IntDef;

@IntDef({
        OfficialCommunityType.WECHAT,
        OfficialCommunityType.TELEGRAM,
        OfficialCommunityType.GITHUB,
        OfficialCommunityType.TWITTER,
        OfficialCommunityType.FACEBOOK
})
public @interface OfficialCommunityType {

    int WECHAT = 0;
    int TELEGRAM = 1;
    int GITHUB = 2;
    int TWITTER = 3;
    int FACEBOOK = 4;

}
