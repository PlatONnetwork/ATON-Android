package com.platon.aton.entity;

import android.support.annotation.IntDef;

@IntDef({
        WalletDepth.DEPTH_ZERO,
        WalletDepth.DEPTH_ONE
})
public @interface WalletDepth {
    /**
     * 钱包深度0 :HD目录和普通钱包为0
     */
    int DEPTH_ZERO = 0;
    /**
     * 钱包深度1：HD子钱包为1
     */
    int DEPTH_ONE = 1;

}