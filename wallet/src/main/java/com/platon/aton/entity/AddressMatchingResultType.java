package com.platon.aton.entity;


import android.support.annotation.IntDef;

@IntDef({
        AddressMatchingResultType.ADDRESS_MAINNET_MATCHING,
        AddressMatchingResultType.ADDRESS_MAINNET_MISMATCHING,
        AddressMatchingResultType.ADDRESS_TESTNET_MATCHING,
        AddressMatchingResultType.ADDRESS_TESTNET_MISMATCHING
})

public @interface AddressMatchingResultType {

    /**
     * 地址匹配主网
     */
    int ADDRESS_MAINNET_MATCHING = 1;
    /**
     *地址不匹配主网
     */
    int ADDRESS_MAINNET_MISMATCHING = 2;
    /**
     *地址匹配测试网
     */
    int ADDRESS_TESTNET_MATCHING = 3;
    /**
     * 地址不匹配测试网
     */
    int ADDRESS_TESTNET_MISMATCHING = 4;


}
