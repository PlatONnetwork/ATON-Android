package com.platon.framework.utils.crypt;

import android.annotation.SuppressLint;

import java.security.Security;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author ziv
 */
public final class AESUtils {

    private AESUtils() {

    }

    @SuppressLint("TrulyRandom")
    public static String encrypt(String keyStr, String toEncryptString) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        byte[] input = toEncryptString.getBytes();
        byte[] keyBytes = getKeyBytes(keyStr);

        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] cipherText = new byte[cipher.getOutputSize(input.length)];
        int ctLength = cipher.update(input, 0, input.length, cipherText, 0);
        ctLength += cipher.doFinal(cipherText, ctLength);
        String c = byte2HexStr(cipherText);
        return c;
    }

    public static byte[] encrypt(String keyStr, byte[] data) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        byte[] keyBytes = getKeyBytes(keyStr);

        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] cipherText = new byte[cipher.getOutputSize(data.length)];
        int ctLength = cipher.update(data, 0, data.length, cipherText, 0);
        ctLength += cipher.doFinal(cipherText, ctLength);
        return cipherText;
    }

    public static String decrypt(String keyStr, String toDecryptString) throws Exception {

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        byte[] toDecrypt = hexStr2Bytes(toDecryptString);
        byte[] keyBytes = getKeyBytes(keyStr);
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] cipherText = new byte[cipher.getOutputSize(toDecrypt.length)];
        int ctLength = cipher.update(toDecrypt, 0, toDecrypt.length, cipherText, 0);
        ctLength += cipher.doFinal(cipherText, ctLength);

        String c = new String(cipherText);
        if (ctLength < c.length()) {
            return c.substring(0, ctLength);
        }
        return c;
    }

    public static byte[] decrypt(String keyStr, byte[] data) throws Exception {

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        byte[] keyBytes = getKeyBytes(keyStr);
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] cipherText = new byte[cipher.getOutputSize(data.length)];
        int ctLength = cipher.update(data, 0, data.length, cipherText, 0);
        ctLength += cipher.doFinal(cipherText, ctLength);
        return cipherText;
    }

    /**
     * bytes转换成十六进制字符串
     */
    public static String byte2HexStr(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs.toUpperCase(Locale.US);
    }

    // 从指定字符串生成密钥，AES密钥所需的字节数组长度为16位 不足16位时后面补0，超出8位只取前8位
    private static byte[] getKeyBytes(String keyStr) throws Exception {
        byte[] tmp = keyStr.getBytes("utf-8");
        // 创建一个空的8位字节数组（默认值为0）
        byte[] arrB = new byte[16];

        // 将原始字节数组转换为8位
        for (int i = 0; i < tmp.length && i < arrB.length; i++) {
            arrB[i] = tmp[i];
        }
        return arrB;
    }

    private static byte uniteBytes(String src0, String src1) {
        byte b0 = Byte.decode("0x" + src0).byteValue();
        b0 = (byte) (b0 << 4);
        byte b1 = Byte.decode("0x" + src1).byteValue();
        byte ret = (byte) (b0 | b1);
        return ret;
    }

    /**
     * 十六进制字符串转换成bytes
     */
    public static byte[] hexStr2Bytes(String src) {
        int m = 0, n = 0;
        int l = src.length() / 2;
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            m = i * 2 + 1;
            n = m + 1;
            ret[i] = uniteBytes(src.substring(i * 2, m), src.substring(m, n));
        }
        return ret;
    }

}
