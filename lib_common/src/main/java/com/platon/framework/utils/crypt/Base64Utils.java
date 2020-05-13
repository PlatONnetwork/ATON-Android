/*
 * FileName: Base64.java
 * Description: Base64编解码类文件
 */
package com.platon.framework.utils.crypt;

import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Base64编解码类， 提供base64编解码类功能
 * @author ziv
 */
public final class Base64Utils {
    /**
     * 空字节数组
     */
    private final static byte[] EMPTY_BYTE_ARRAY = new byte[0];
    /**
     * base64 基础字符集
     */
    private static final byte[] map = new byte[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+',
            '/'};

    private Base64Utils() {
    }

    public static byte[] decode(byte[] in) {
        if (in == null) {
            return EMPTY_BYTE_ARRAY;
        }
        return decode(in, 0, in.length);
    }

    public static String decodeToString(byte[] in, String charset) throws UnsupportedEncodingException {
        if (in == null) {
            return "";
        }
        return decodeToString(in, 0, in.length, charset);
    }

    public static String decodeToString(byte[] in, int offset, int len, String charset) throws UnsupportedEncodingException {
        byte[] bytes = decode(in, offset, len);
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        return new String(bytes, 0, bytes.length, charset);
    }

    public static byte[] decode(byte[] in, int offset, int len) {
        // approximate output length
        int length = len / 4 * 3;
        // return an empty array on empty or short input without padding
        if (length == 0) {
            return EMPTY_BYTE_ARRAY;
        }
        // temporary array
        byte[] out = new byte[length];
        // number of padding characters ('=')
        int pad = 0;
        byte chr;
        // compute the number of the padding characters
        // and adjust the length of the input

        int end = len + offset;
        for (; ; end--) {
            chr = in[end - 1];
            // skip the neutral characters
            if ((chr == '\n') || (chr == '\r') || (chr == ' ') || (chr == '\t')) {
                continue;
            }
            if (chr == '=') {
                pad++;
            } else {
                break;
            }
        }
        // index in the output array
        int outIndex = 0;
        // index in the input array
        int inIndex = 0;
        // holds the value of the input character
        int bits = 0;
        // holds the value of the input quantum
        int quantum = 0;
        for (int i = offset; i < end; i++) {
            chr = in[i];
            // skip the neutral characters
            if ((chr == '\n') || (chr == '\r') || (chr == ' ') || (chr == '\t')) {
                continue;
            }
            if ((chr >= 'A') && (chr <= 'Z')) {
                // char ASCII value
                // A 65 0
                // Z 90 25 (ASCII - 65)
                bits = chr - 65;
            } else if ((chr >= 'a') && (chr <= 'z')) {
                // char ASCII value
                // a 97 26
                // z 122 51 (ASCII - 71)
                bits = chr - 71;
            } else if ((chr >= '0') && (chr <= '9')) {
                // char ASCII value
                // 0 48 52
                // 9 57 61 (ASCII + 4)
                bits = chr + 4;
            } else if (chr == '+') {
                bits = 62;
            } else if (chr == '/') {
                bits = 63;
            } else {
                return null;
            }
            // append the value to the quantum
            quantum = (quantum << 6) | (byte) bits;
            if (inIndex % 4 == 3) {
                // 4 characters were read, so make the output:
                out[outIndex++] = (byte) (quantum >> 16);
                out[outIndex++] = (byte) (quantum >> 8);
                out[outIndex++] = (byte) quantum;
            }
            inIndex++;
        }
        if (pad > 0) {
            // adjust the quantum value according to the padding
            quantum = quantum << (6 * pad);
            // make output
            out[outIndex++] = (byte) (quantum >> 16);
            if (pad == 1) {
                out[outIndex++] = (byte) (quantum >> 8);
            }
        }
        // create the resulting array
        byte[] result = new byte[outIndex];
        System.arraycopy(out, 0, result, 0, outIndex);
        return result;
    }

    public static byte[] encode(byte[] in, int offset, int len) {
        int length = (len + 2) * 4 / 3;
        byte[] out = new byte[length];
        int index = 0, end = offset + len - len % 3;
        for (int i = offset; i < end; i += 3) {
            out[index++] = map[(in[i] & 0xff) >> 2];
            out[index++] = map[((in[i] & 0x03) << 4) | ((in[i + 1] & 0xff) >> 4)];
            out[index++] = map[((in[i + 1] & 0x0f) << 2) | ((in[i + 2] & 0xff) >> 6)];
            out[index++] = map[(in[i + 2] & 0x3f)];
        }
        switch (len % 3) {
            case 1:
                out[index++] = map[(in[end] & 0xff) >> 2];
                out[index++] = map[(in[end] & 0x03) << 4];
                out[index++] = '=';
                out[index++] = '=';
                break;
            case 2:
                out[index++] = map[(in[end] & 0xff) >> 2];
                out[index++] = map[((in[end] & 0x03) << 4) | ((in[end + 1] & 0xff) >> 4)];
                out[index++] = map[((in[end + 1] & 0x0f) << 2)];
                out[index++] = '=';
                break;
        }

        return out;
    }

    public static byte[] encode(byte[] in) {
        return encode(in, 0, in.length);
    }

    public static String encodeToString(byte[] in, int offset, int len) {
        int length = (len + 2) * 4 / 3;
        byte[] out = new byte[length];
        int index = 0, end = offset + len - len % 3;
        for (int i = offset; i < end; i += 3) {
            out[index++] = map[(in[i] & 0xff) >> 2];
            out[index++] = map[((in[i] & 0x03) << 4) | ((in[i + 1] & 0xff) >> 4)];
            out[index++] = map[((in[i + 1] & 0x0f) << 2) | ((in[i + 2] & 0xff) >> 6)];
            out[index++] = map[(in[i + 2] & 0x3f)];
        }
        switch (len % 3) {
            case 1:
                out[index++] = map[(in[end] & 0xff) >> 2];
                out[index++] = map[(in[end] & 0x03) << 4];
                out[index++] = '=';
                out[index++] = '=';
                break;
            case 2:
                out[index++] = map[(in[end] & 0xff) >> 2];
                out[index++] = map[((in[end] & 0x03) << 4) | ((in[end + 1] & 0xff) >> 4)];
                out[index++] = map[((in[end + 1] & 0x0f) << 2)];
                out[index++] = '=';
                break;
        }

        try {
            return new String(out, 0, index, "US-ASCII");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
        // return new String(out, 0, index, Charset.forName("US-ASCII"));
    }

    public static String encodeToString(byte[] in) {
        if (in == null) {
            return null;
        }
        return encodeToString(in, 0, in.length);
    }

    public static String encodeToString(String s, String charset) throws UnsupportedEncodingException {
        byte[] bytes = s.getBytes(charset);
        return encodeToString(bytes);
    }

    public static byte[] encode(String s, String charset) throws UnsupportedEncodingException {
        byte[] bytes = s.getBytes(charset);
        return encode(bytes);
    }

    public static String fileToBase64(File file) {

        FileInputStream inputFile = null;
        byte[] bytes = null;
        try {
            inputFile = new FileInputStream(file);
            bytes = new byte[(int) file.length()];
            byte[] buffer = new byte[(int) file.length()];
            inputFile.read(buffer);
            inputFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputFile != null) {
                try {
                    inputFile.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return Base64.encodeToString(bytes, Base64.DEFAULT);

    }
}
