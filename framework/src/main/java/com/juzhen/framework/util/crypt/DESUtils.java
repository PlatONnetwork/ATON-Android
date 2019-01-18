/*
 * FileName: DESUtils.java
 * Description: DES加解密工具类文件
 */
package com.juzhen.framework.util.crypt;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * DES加解密工具类, 提供DES加解密功能
 * @author ziv
 */
public final class DESUtils {

    private DESUtils() {

    }

    public static byte[] encrypt(byte[] src, byte[] key) throws Exception {
        // 从原始密匙数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);

        // 创建一个密匙工厂，然后用它把DESKeySpec转换成
        // 一个SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey securekey = keyFactory.generateSecret(dks);
        // Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance("DES");

        // DES算法要求有一个可信任的随机数源
        SecureRandom sr = new SecureRandom();
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);

        return cipher.doFinal(src);
    }

    public static byte[] decrypt(byte[] src, byte[] key) throws Exception {
        // 从原始密匙数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);

        // 创建一个密匙工厂，然后用它把DESKeySpec转换成
        // 一个SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey securekey = keyFactory.generateSecret(dks);
        // Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance("DES");

        // DES算法要求有一个可信任的随机数源
        SecureRandom sr = new SecureRandom();
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, sr);

        return cipher.doFinal(src);
    }
}
