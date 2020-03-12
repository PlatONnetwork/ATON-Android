package com.platon.aton.utils;

import org.web3j.crypto.Sign;

/**
 * 签名数据编解码
 */
public class SignCodeUtils {

    public static Sign.SignatureData decode(byte[] sign){
        byte[] v = new byte[1];
        byte[] r = new byte[32];
        byte[] s = new byte[32];

        System.arraycopy(sign,0,v,0,1);
        System.arraycopy(sign,1,r,0,32);
        System.arraycopy(sign,33,s,0,32);

        return new Sign.SignatureData(v, r, s);
    }

    public static byte[] encode(Sign.SignatureData signatureData){
        byte[] v = signatureData.getV();
        byte[] r = signatureData.getR();
        byte[] s = signatureData.getS();

        // 1 header + 32 bytes for R + 32 bytes for S
        byte[] sign = new byte[65];
        System.arraycopy(v,0,sign,0,1);
        System.arraycopy(r,0,sign,1,32);
        System.arraycopy(s,0,sign,33,32);

        return sign;
    }
}
