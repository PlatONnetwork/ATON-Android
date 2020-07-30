package com.platon.aton.entity;

import android.support.annotation.StringDef;

@StringDef({
        MainTabTag.TAG_ASSETS,
        MainTabTag.TAG_DELEGATE,
        MainTabTag.TAG_ME
})
public  @interface MainTabTag {

    /**
     * 钱包页签tag
     */
    String TAG_ASSETS = "tag_assets";
    /**
     * 委托页签tag
     */
    String TAG_DELEGATE = "tag_delegate";
    /**
     * 我的页签tag
     */
    String TAG_ME = "tag_me";
}
