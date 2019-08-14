package com.juzix.wallet.entity;


import android.support.annotation.IntDef;

@IntDef({
        OfficialCommunityType.WECHAT,
        OfficialCommunityType.TWITTER,
        OfficialCommunityType.FACEBOOK,
        OfficialCommunityType.GITHUB,
        OfficialCommunityType.REDDIT,
        OfficialCommunityType.MEDIUM,
        OfficialCommunityType.LINKEDIN,
        OfficialCommunityType.TELEGRAM,
        OfficialCommunityType.BI_HU,
        OfficialCommunityType.BABIT
})
public @interface OfficialCommunityType {

    int WECHAT = 0;
    int TWITTER = 1;
    int FACEBOOK = 2;
    int GITHUB = 3;
    int REDDIT = 4;
    int MEDIUM = 5;
    int LINKEDIN = 6;
    int TELEGRAM = 7;
    int BI_HU = 8;
    int BABIT = 9;

}
