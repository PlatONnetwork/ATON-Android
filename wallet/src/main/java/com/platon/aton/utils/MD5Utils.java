package com.platon.aton.utils;

import com.facebook.stetho.common.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5工具
 */
public class MD5Utils {

    private static MessageDigest md5 = null;

    private MD5Utils() {
    }

    //静态方法，便于作为工具类
    public static String getMd5(String plainText) {
                try {
                    MessageDigest md = MessageDigest.getInstance("MD5");
                    md.update(plainText.getBytes());
                         byte b[] = md.digest();
                         int i;
                        StringBuffer buf = new StringBuffer("");
                        for (int offset = 0; offset < b.length; offset++) {
                                i = b[offset];
                                 if (i < 0)
                                        i += 256;
                                 if (i < 16)
                                         buf.append("0");
                                 buf.append(Integer.toHexString(i));
                             }
                        //32位加密
                         return buf.toString();
                         // 16位的加密
                         //return buf.toString().substring(8, 24);
                     } catch (NoSuchAlgorithmException e) {
                         LogUtil.e(e.getMessage(),e.fillInStackTrace());
                         return null;
                     }
    }

    private synchronized static MessageDigest getMessageDigest() {
        if (md5 == null) {
            try {
                md5 = MessageDigest.getInstance("md5");
            } catch (NoSuchAlgorithmException e) {
                LogUtil.e(e.getMessage(),e.fillInStackTrace());
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
