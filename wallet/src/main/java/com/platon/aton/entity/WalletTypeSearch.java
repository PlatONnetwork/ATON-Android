package com.platon.aton.entity;


import android.support.annotation.IntDef;

@IntDef({
        WalletTypeSearch.WALLET_ALL,
        WalletTypeSearch.HD_WALLET,
        WalletTypeSearch.ORDINARY_WALLET,
        WalletTypeSearch.UNKNOWN_WALLET
})
public @interface WalletTypeSearch {

    /**
     * 全部钱包
     */
    int WALLET_ALL = 1;
    /**
     * HD钱包
     */
    int HD_WALLET = 2;
    /**
     * 普通钱包
     */
    int ORDINARY_WALLET = 3;
    /**
     * 其他
     */
    int UNKNOWN_WALLET = 4;


}
