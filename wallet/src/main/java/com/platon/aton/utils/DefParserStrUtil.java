package com.platon.aton.utils;

import android.content.Context;

import com.platon.aton.R;
import com.platon.aton.entity.WalletType;

/**
 * 枚举状态转换成对用业务状态
 */
public class DefParserStrUtil {

    public static String transformWalletType(@WalletType int walletType, Context context){

        if(walletType == WalletType.ORDINARY_WALLET){
            return context.getResources().getString(R.string.wallet_type_ordinary);
        }else{
            return context.getResources().getString(R.string.wallet_type_hd);
        }
    }

    public static @WalletType int transforInverseWalletType(String walletType, Context context){

        if(context.getResources().getString(R.string.wallet_type_ordinary).equals(walletType)){
            return WalletType.ORDINARY_WALLET;
        }else{
            return WalletType.HD_WALLET;
        }
    }






}
