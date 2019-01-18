/*
 * Copyright (c) 1998-2016 TENCENT Inc. All Rights Reserved.
 * FileName: MD5.java
 * Description: MD5加密工具类文件
 */
package com.juzhen.framework.util.crypt;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密工具类
 *
 * @author devilxie
 * @version 1.0
 */
public final class MD5Utils {

    private static MessageDigest md5 = null;

    private MD5Utils() {

    }

    private synchronized static MessageDigest getMessageDigest() {
        if (md5 == null) {
            try {
                md5 = MessageDigest.getInstance("md5");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return null;
            }
        }

        return md5;
    }

    public static byte[] encode(InputStream is) {
        MessageDigest md = getMessageDigest();
        if (md == null) {
            throw new IllegalAccessError("no md5 algorithm");
        }

        DigestInputStream dis = null;
        byte[] buf = new byte[1024];
        int reads = 0;
        try {
            dis = new DigestInputStream(is, md);
            do {
                reads = dis.read(buf);
                if (reads < 0) {
                    break;
                }
            } while (true);
        } catch (IOException e) {

        } finally {
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                }
            }
        }

        return md.digest();
    }

    public static byte[] encode(String origin) {
        if (origin == null || origin.length() == 0) {
            return null;
        }

        MessageDigest md = getMessageDigest();
        if (md == null) {
            throw new IllegalAccessError("no md5 algorithm");
        }

        byte[] bytes = md.digest(origin.getBytes());
        return bytes;
    }

    public static byte[] encode16(String origin) {
        if (origin == null || origin.length() == 0) {
            return null;
        }

        MessageDigest md = getMessageDigest();
        if (md == null) {
            throw new IllegalAccessError("no md5 algorithm");
        }
        byte[] bytes = md.digest(origin.getBytes());
        byte[] dstBytes = new byte[8];
        System.arraycopy(bytes, 4, dstBytes, 0, 8);
        bytes = null;
        return dstBytes;
    }

    public static byte[] encode(String origin, String enc) throws UnsupportedEncodingException {
        if (origin == null || origin.length() == 0) {
            return null;
        }

        MessageDigest md = getMessageDigest();
        if (md == null) {
            throw new IllegalAccessError("no md5 algorithm");
        }

        byte[] bytes = md.digest(origin.getBytes(enc));
        return bytes;
    }

    public static byte[] encode16(String origin, String enc) throws UnsupportedEncodingException {
        if (origin == null || origin.length() == 0) {
            return null;
        }

        MessageDigest md = getMessageDigest();
        if (md == null) {
            throw new IllegalAccessError("no md5 algorithm");
        }

        byte[] bytes = md.digest(origin.getBytes(enc));
        byte[] dstBytes = new byte[8];
        System.arraycopy(bytes, 4, dstBytes, 0, 8);
        bytes = null;
        return dstBytes;
    }

    public static byte[] encode(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        MessageDigest md = getMessageDigest();
        if (md == null) {
            throw new IllegalAccessError("no md5 algorithm");
        }

        byte[] resultbytes = md.digest(bytes);
        return resultbytes;
    }

    public static byte[] encode16(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        MessageDigest md = getMessageDigest();
        if (md == null) {
            throw new IllegalAccessError("no md5 algorithm");
        }

        byte[] resultbytes = md.digest(bytes);
        byte[] dstBytes = new byte[8];
        System.arraycopy(resultbytes, 4, dstBytes, 0, 8);
        bytes = null;
        return dstBytes;
    }
}
