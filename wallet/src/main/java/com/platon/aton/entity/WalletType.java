package com.platon.aton.entity;


import android.support.annotation.IntDef;

@IntDef({
        WalletType.ORDINARY_WALLET,
        WalletType.HD_WALLET,
        WalletType.HD_SUB_WALLET
})
public @interface WalletType {

    /**
     * 普通钱包
     */
    int ORDINARY_WALLET = 1;
    /**
     * HD钱包
     */
    int HD_WALLET = 2;
    /**
     * HD钱包(子钱包)
     */
    int HD_SUB_WALLET = 3;


}
