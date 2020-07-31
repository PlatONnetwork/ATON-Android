package com.platon.aton.entity;


import android.support.annotation.IntDef;

@IntDef({
        WalletSelectedIndex.UNSELECTED,
        WalletSelectedIndex.SELECTED
})
public @interface WalletSelectedIndex {

    /**
     * 未选中
     */
    int UNSELECTED = 0;
    /**
     * 选中
     */
    int SELECTED = 1;



}
