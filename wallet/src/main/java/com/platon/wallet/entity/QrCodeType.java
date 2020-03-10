package com.platon.wallet.entity;

import android.support.annotation.IntDef;

@IntDef({
        QrCodeType.NONE,
        QrCodeType.TRANSACTION_AUTHORIZATION,
        QrCodeType.TRANSACTION_SIGNATURE,
        QrCodeType.WALLET_ADDRESS,
        QrCodeType.WALLET_KEYSTORE,
        QrCodeType.WALLET_MNEMONIC,
        QrCodeType.WALLET_PRIVATEKEY
})
public @interface QrCodeType {

    int NONE = -1;
    /**
     * 交易认证
     */
    int TRANSACTION_AUTHORIZATION = 0;
    /**
     * 交易签名
     */
    int TRANSACTION_SIGNATURE = 1;
    /**
     * 钱包地址
     */
    int WALLET_ADDRESS = 2;
    /**
     * 钱包keystore
     */
    int WALLET_KEYSTORE = 3;
    /**
     * 钱包助记词
     */
    int WALLET_MNEMONIC = 4;
    /**
     * 钱包私钥
     */
    int WALLET_PRIVATEKEY = 5;
}
