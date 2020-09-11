package com.platon.aton.entity;

import android.support.annotation.IntDef;

@IntDef({
        MainTab.TAB_ASSETS,
        MainTab.TAB_DELEGATE,
        MainTab.TAB_ME
})
public @interface MainTab {
    /**
     * 钱包页签
     */
    int TAB_ASSETS = 0;
    /**
     * 委托页签
     */
    int TAB_DELEGATE = 1;
    /**
     * 我的页签
     */
    int TAB_ME = 2;
}