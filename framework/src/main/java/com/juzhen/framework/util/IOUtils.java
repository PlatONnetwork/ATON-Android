/*
 * FileName: IOUtils.java
 * Description: 流读写工具类文件。
 */
package com.juzhen.framework.util;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;

/**
 * 输入输出辅助工具类，提供从流中读取整数、长整数、短整数、字符串等
 * 
 * @author devilxie
 * @version 1.0
 */
public class IOUtils {
	/**
	 * 临时缓冲区长度
	 */
	private static final int	MAX_BUFFER_BYTES	= 1024;
	
	/**
	 * 按指定字节序将整数转换为指定长度的字节数组
	 * 
	 * @param s 整数
	 * @param len 整数所占字节数
	 * @param big_endian 是否网络字节序。true表示按网络字节序(Big Endian)，false表示按Little Endian字节序
	 * @return 返回字节数组
	 */
	public static byte[] numberToBytes(long s, int len, boolean big_endian) {
		byte[] buffer = new byte[len];
		int start = big_endian ? (len - 1) : 0;
		int end = big_endian ? -1 : len;
		int inc = big_endian ? -1 : 1;
		
		for (int i = start; i != end; i += inc) {
			buffer[i] = (byte) (s & 0xff);
			s >>>= 8;
		}
		
		return buffer;
	}
	
	/**
	 * 从输入流中读一个短整数，按Big Endian形式读入
	 * 
	 * @param in 输入流
	 * @return 返回获取到的短整形
	 * @throws IOException 如果获读取失败，会抛出此IO异常
	 */
	public static int readShort(InputStream in) throws IOException {
		return (int) readNumber(in, 2, true);
	}
	
	/**
	 * 从输入流中读一个短整数，按指定字节顺序读入
	 * 
	 * @param in 输入流
	 * @param big_endian 是否为Big Endian，true表示是BIG Endian，false表示是Little Endian
	 * @return 返回获取到的短整形
	 * @throws IOException 如果获读取失败，会抛出此IO异常
	 */
	public static int readShort(InputStream in, boolean big_endian) throws IOException {
		return (int) readNumber(in, 2, big_endian);
	}
	
	/**
	 * 从输入流中读一个整数，按Big Endian形式读入
	 * 
	 * @param in 输入流
	 * @return 返回获取到的整数
	 * @throws IOException 如果获读取失败，会抛出此IO异常
	 */
	public static long readInt(InputStream in) throws IOException {
		return readNumber(in, 4, true);
	}
	
	/**
	 * 从输入流中读一个整数，按指定字节顺序读入
	 * 
	 * @param in 输入流
	 * @param big_endian 是否为Big Endian，true表示是BIG Endian，false表示是Little Endian
	 * @return 返回获取到的整形
	 * @throws IOException 如果获读取失败，会抛出此IO异常
	 */
	public static long readInt(InputStream in, boolean big_endian) throws IOException {
		return readNumber(in, 4, big_endian);
	}
	
	public static float readFloat(InputStream in) throws IOException {
		return readFloat(in, true);
	}
	
	public static float readFloat(InputStream in, boolean big_endian) throws IOException {
		int i = (int) readInt(in, big_endian);
		return Float.intBitsToFloat(i);
		
	}
	
	public static double readDouble(InputStream in) throws IOException {
		return readDouble(in, true);
	}
	
	public static double readDouble(InputStream in, boolean big_endian) throws IOException {
		long l = readNumber(in, 8, big_endian);
		return Double.longBitsToDouble(l);
	}
	
	/**
	 * 从输入流中读一个整数，按指定字节顺序读入
	 * 
	 * @param in 输入流
	 * @param len 读入特定数量的字节数
	 * @param big_endian 是否为Big Endian，true表示是BIG Endian，false表示是Little Endian
	 * @return 返回获取到的整形
	 * @throws IOException 如果获读取失败，会抛出此IO异常
	 */
	public static long readNumber(InputStream in, int len, boolean big_endian) throws IOException {
		if (len <= 0 || len > 8)
			throw new IllegalArgumentException("length must between 1 and 8.");
		
		byte[] buffer = new byte[len];
		if (in.markSupported())
			in.mark(len);
		
		int count = in.read(buffer, 0, len);
		// 没有读取到数据，或者数据不充分
		if (count <= 0) {
			buffer = null;
			return -1L;
		}
		
		int start = big_endian ? 0 : (count - 1);
		int end = big_endian ? count : -1;
		int inc = big_endian ? 1 : -1;
		long ret = 0;
		
		for (int i = start; i != end; i += inc) {
			ret <<= 8;
			ret |= (buffer[i] & 0xff);
		}
		
		return ret;
	}
	
	/**
	 * 从输入流中读入指定长度的字节数组
	 * 
	 * @param in 输入流
	 * @param len 指定长度， 必须大于0
	 * @return 返回字节数组
	 * @throws IOException 如果获读取失败，会抛出此IO异常
	 */
	public static byte[] readBytes(InputStream in, int len) throws IOException {
		if (len <= 0) {
			return null;
		}
		
		int pos = 0, recvBytes = 0;
		byte[] ret = null;
		byte[] buffer = new byte[len];
		try {
			
			while (pos < len && (recvBytes = in.read(buffer, pos, len - pos)) > 0) {
				pos += recvBytes;
			}
			
			ret = buffer;
		} finally {
			buffer = null;
		}
		
		return ret;
	}
	
	/**
	 * 从输入出读出包含指定数量的字节的字符串
	 * 
	 * @param in 输入流
	 * @param len 指定字节数量，不是字符串长度
	 * @return 返回获取到的字符串
	 * @throws IOException 如果获读取失败，会抛出此IO异常
	 */
	public static String readString(InputStream in, int len) throws IOException {
		int leftBytes = len, recvBytes = 0;
		int bufLen = Math.min(leftBytes, MAX_BUFFER_BYTES);
		
		byte[] buffer = new byte[bufLen];
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(MAX_BUFFER_BYTES);
		String result = null;
		try {
			while (leftBytes > 0 && (recvBytes = in.read(buffer, 0, bufLen)) != -1) {
				outputStream.write(buffer, 0, recvBytes);
				leftBytes -= recvBytes;
			}
			
			result = outputStream.toString();
		} finally {
			buffer = null;
			outputStream.close();
		}
		
		return result;
	}
	
	/**
	 * 从输入出读出包含指定数量的字节的字符串, 按指定字符集的形式构造字符串
	 * 
	 * @param in 输入流
	 * @param len 指定字节数量，不是字符串长度
	 * @param characterSet 指定字符集，如"utf-8","gbk"
	 * @return 返回获取到的字符串
	 * @throws IOException 如果获读取失败，会抛出此IO异常
	 */
	public static String readString(InputStream in, int len, String characterSet) throws IOException {
		int leftBytes = len, recvBytes = 0;
		int bufLen = Math.min(leftBytes, MAX_BUFFER_BYTES);
		
		byte[] buffer = new byte[bufLen];
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(MAX_BUFFER_BYTES);
		while (leftBytes > 0 && (recvBytes = in.read(buffer, 0, bufLen)) != -1) {
			outputStream.write(buffer, 0, recvBytes);
			leftBytes -= recvBytes;
		}
		
		buffer = null;
		String result = outputStream.toString(characterSet);
		outputStream.close();
		return result;
	}
	
	/**
	 * 按Big Endian字节序将短整形写入输出流
	 * 
	 * @param out 待写入的输出流
	 * @param s 整数
	 * @throws IOException 如果写出发生错误，将抛出IO异常
	 */
	public static void writeShort(OutputStream out, int s) throws IOException {
		byte[] buffer = new byte[] { (byte) ((s >> 8) & 0xff), (byte) (s & 0xff) };
		
		out.write(buffer);
		out.flush();
		buffer = null;
		
	}
	
	/**
	 * 按指定字节序将短整形写入输出流
	 * 
	 * @param out 待写入的输出流
	 * @param s 整数
	 * @param big_endian 是否网络字节序。true表示按网络字节序(Big Endian)，false表示按Little Endian字节序
	 * @throws IOException 如果写出发生错误，将抛出IO异常
	 */
	public static void writeShort(OutputStream out, int s, boolean big_endian) throws IOException {
		writeNumber(out, s, 2, big_endian);
	}
	
	/**
	 * 按Big Endian字节序将整形写入输出流
	 * 
	 * @param out 待写入的输出流
	 * @param s 整数
	 * @throws IOException 如果写出发生错误，将抛出IO异常
	 */
	public static void writeInt(OutputStream out, long s) throws IOException {
		byte[] buffer = new byte[] { (byte) ((s >> 24) & 0xff), (byte) ((s >> 16) & 0xff), (byte) ((s >> 8) & 0xff), (byte) (s & 0xff) };
		
		out.write(buffer);
		out.flush();
		buffer = null;
	}
	
	/**
	 * 按指定字节序将整形写入输出流
	 * 
	 * @param out 待写入的输出流
	 * @param s 整数
	 * @param big_endian 是否网络字节序。true表示按网络字节序(Big Endian)，false表示按Little Endian字节序
	 * @throws IOException 如果写出发生错误，将抛出IO异常
	 */
	public static void writeInt(OutputStream out, long s, boolean big_endian) throws IOException {
		writeNumber(out, s, 4, big_endian);
	}
	
	public static void writeFloat(OutputStream out, float f) throws IOException {
		writeFloat(out, f, true);
	}
	
	public static void writeFloat(OutputStream out, float f, boolean big_endian) throws IOException {
		int bits = Float.floatToIntBits(f);
		writeInt(out, bits, big_endian);
	}
	
	public static void writeDouble(OutputStream out, double d) throws IOException {
		writeDouble(out, d, true);
	}
	
	public static void writeDouble(OutputStream out, double d, boolean big_endian) throws IOException {
		long bits = Double.doubleToLongBits(d);
		writeNumber(out, bits, 8, big_endian);
	}
	
	/**
	 * 按指定字节序将整数写入输出流
	 * 
	 * @param out 待写入的输出流
	 * @param s 整数
	 * @param len 整数所占字节数
	 * @param big_endian 是否网络字节序。true表示按网络字节序(Big Endian)，false表示按Little Endian字节序
	 * @throws IOException 如果写出发生错误，将抛出IO异常
	 */
	public static void writeNumber(OutputStream out, long s, int len, boolean big_endian) throws IOException {
		if (len <= 0 || len > 8)
			throw new IllegalArgumentException("length must between 1 and 8.");
		
		byte[] buffer = numberToBytes(s, len, big_endian);
		out.write(buffer);
		out.flush();
	}
	
	/**
	 * 将字符串按默认编码格式写入输出流，字符串长度以一个字节表示
	 * 
	 * @param out 输出流
	 * @param s 字符串
	 * @throws IOException 出现写入错误时，抛出该异常
	 */
	public static void writeCString(OutputStream out, String s) throws IOException {
		writeCString(out, s, "utf-8");
	}
	
	/**
	 * 将字符串按默认编码格式写入输出流，字符串长度以两个字节表示
	 * 
	 * @param out 输出流
	 * @param s 字符串
	 * @throws IOException 出现写入错误时，抛出该异常
	 */
	public static void writeWString(OutputStream out, String s) throws IOException {
		byte[] bytes = s.getBytes();
		writeShort(out, bytes.length);
		out.write(bytes);
		out.flush();
	}
	
	/**
	 * 将字符串按指定编码格式写入输出流，字符串长度以一个字节表示
	 * 
	 * @param os 输出流
	 * @param s 字符串
	 * @param characterSet 编码格式，如"utf-8", "gbk"等
	 * @throws IOException 出现写入错误时，抛出该异常
	 */
	public static void writeCString(OutputStream os, String s, String characterSet) throws IOException {
		if (s == null || s.length() == 0) {
			os.write(0);
			return;
		}
		if (characterSet == null) {
			characterSet = "utf-8";
		}
		byte[] bytes = s.getBytes(characterSet);
		writeCLenData(os, bytes);
	}
	
	/**
	 * 将字符串按指定编码格式写入输出流，字符串长度以两个字节表示
	 * 
	 * @param out 输出流
	 * @param s 字符串
	 * @param characterSet 编码格式，如"utf-8", "gbk"等
	 * @throws IOException 出现写入错误时，抛出该异常
	 */
	public static void writeWString(OutputStream out, String s, String characterSet, boolean big_endian) throws IOException {
		if (s == null || s.length() == 0) {
			writeShort(out, 0);
			return;
		}
		if (characterSet == null) {
			characterSet = "utf-8";
		}
		byte[] bytes = s.getBytes(characterSet);
		writeWLenData(out, bytes, big_endian);
	}
	
	/**
	 * 将字符串按默认编码格式写入输出流，字符串长度以一个字节表示，写入固定长度。
	 * 
	 * @param out 输出流
	 * @param s 字符串
	 * @param fixedLen 待写入的字节长度， 如果字符串所占字节长度超出fixedLen，将截短；如果不足，将以0补充；
	 * @throws IOException 出现写入错误时，抛出该异常
	 */
	public static void writeCString(OutputStream out, String s, int fixedLen) throws IOException {
		byte[] bytes = s.getBytes();
		out.write(bytes.length);
		fixedLen -= 1;
		
		if (fixedLen <= 0)
			return;
		
		if (fixedLen <= bytes.length) {
			out.write(bytes, 0, fixedLen);
		} else {
			out.write(bytes);
			byte[] fillBytes = new byte[fixedLen - bytes.length];
			Arrays.fill(fillBytes, (byte) 0);
			out.write(fillBytes);
		}
		
		out.flush();
	}
	
	/**
	 * 将字符串按默认编码格式写入输出流，字符串长度以两个字节表示，写入固定长度。
	 * 
	 * @param out 输出流
	 * @param s 字符串
	 * @param fixedLen 待写入的字节长度， 如果字符串所占字节长度超出fixedLen，将截短；如果不足，将以0补充；
	 * @throws IOException 出现写入错误时，抛出该异常
	 */
	public static void writeWString(OutputStream out, String s, int fixedLen) throws IOException {
		byte[] bytes = s.getBytes();
		writeShort(out, bytes.length);
		fixedLen -= 2;
		
		if (fixedLen <= 0)
			return;
		
		if (fixedLen <= bytes.length) {
			out.write(bytes, 0, fixedLen);
		} else {
			out.write(bytes);
			byte[] fillBytes = new byte[fixedLen - bytes.length];
			Arrays.fill(fillBytes, (byte) 0);
			out.write(fillBytes);
		}
		
		out.flush();
	}
	
	/**
	 * 将字符串按指定编码格式写入输出流，字符串长度以一个字节表示，写入固定长度。
	 * 
	 * @param out 输出流
	 * @param s 字符串
	 * @param characterSet 编码格式，如"utf-8", "gbk"等
	 * @param fixedLen 待写入的字节长度， 如果字符串所占字节长度超出fixedLen，将截短；如果不足，将以0补充；
	 * @throws IOException 出现写入错误时，抛出该异常
	 */
	public static void writeCString(OutputStream out, String s, String characterSet, int fixedLen) throws IOException {
		byte[] bytes = s.getBytes(characterSet);
		out.write(bytes.length);
		fixedLen -= 1;
		
		if (fixedLen <= 0)
			return;
		
		if (fixedLen <= bytes.length) {
			out.write(bytes, 0, fixedLen);
		} else {
			out.write(bytes);
			byte[] fillBytes = new byte[fixedLen - bytes.length];
			Arrays.fill(fillBytes, (byte) 0);
			out.write(fillBytes);
		}
		
		out.flush();
	}
	
	/**
	 * 将字符串按指定编码格式写入输出流，字符串长度以两个字节表示，写入固定长度。
	 * 
	 * @param out 输出流
	 * @param s 字符串
	 * @param characterSet 编码格式，如"utf-8", "gbk"等
	 * @param fixedLen 待写入的字节长度， 如果字符串所占字节长度超出fixedLen，将截短；如果不足，将以0补充；
	 * @throws IOException 出现写入错误时，抛出该异常
	 */
	public static void writeWString(OutputStream out, String s, String characterSet, int fixedLen) throws IOException {
		byte[] bytes = s.getBytes(characterSet);
		writeShort(out, bytes.length);
		fixedLen -= 2;
		
		if (fixedLen <= 0)
			return;
		
		if (fixedLen <= bytes.length) {
			out.write(bytes, 0, fixedLen);
		} else {
			out.write(bytes);
			byte[] fillBytes = new byte[fixedLen - bytes.length];
			Arrays.fill(fillBytes, (byte) 0);
			out.write(fillBytes);
		}
		
		out.flush();
	}
	
	/**
	 * 获取输入流的可读字节通道
	 * 
	 * @param inputStream 输入流
	 * @return 返回输入流的可读字节通道对象
	 */
	public static ReadableByteChannel getChannel(InputStream inputStream) {
		return (inputStream != null) ? Channels.newChannel(inputStream) : null;
	}
	
	/**
	 * 获取输出流的可写字节通道
	 * 
	 * @param outputStream 输出流
	 * @return 返回输出流的可写字节通道
	 */
	public static WritableByteChannel getChannel(OutputStream outputStream) {
		return (outputStream != null) ? Channels.newChannel(outputStream) : null;
	}
	
	/**
	 * 消耗掉输入流，使流中无可读数据
	 * 
	 * @param input 输入流
	 * @return 返回消耗的字节总数
	 * @throws IOException 出现读写错误时，抛出该异常
	 */
	public static long exhaust(InputStream input) throws IOException {
		long result = 0L;
		
		if (input != null) {
			byte[] buf = new byte[MAX_BUFFER_BYTES / 2];
			try {
				int read = input.read(buf);
				result = (read == -1) ? -1 : 0;
				
				while (read != -1) {
					result += read;
					read = input.read(buf);
				}
				
			} finally {
				buf = null;
			}
		}
		
		return result;
	}
	
	/**
	 * 忽略流中指定长度的字节数
	 * 
	 * @param in 输入流
	 * @param len 字节数
	 * @throws IOException 读取错误将抛出此异常
	 */
	public static void skip(InputStream in, int len) throws IOException {
		if (in == null || len <= 0) {
			return;
		}
		
		int recvBytes = 0;
		byte[] buffer = new byte[MAX_BUFFER_BYTES / 2];
		
		do {
			int need = Math.min(buffer.length, len);
			recvBytes = in.read(buffer, 0, need);
			if (recvBytes < 0)
				break;
			len -= recvBytes;
		} while (len > 0);
		buffer = null;
	}
	
	/**
	 * 消耗掉输入流，使流中无可读数据，并将消耗的数据构造成字符串
	 * 
	 * @param input 输入流
	 * @return 返回字符串
	 * @throws IOException 出现读写错误时，抛出该异常
	 */
	public static String readLeft(InputStream in) throws IOException {
		
		String result = null;
		if (in == null) {
			return null;
		}
		
		int recvBytes = 0;
		byte[] buffer = new byte[MAX_BUFFER_BYTES / 2];
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(MAX_BUFFER_BYTES);
		try {
			while ((recvBytes = in.read(buffer, 0, MAX_BUFFER_BYTES / 2)) >= 0) {
				outputStream.write(buffer, 0, recvBytes);
				
			}
			
			buffer = null;
			result = outputStream.toString();
		} finally {
			outputStream.close();
			buffer = null;
		}
		
		return result;
	}
	
	/**
	 * 消耗掉输入流，使流中无可读数据，并将消耗的数据按照指定编码方式构造成字符串
	 * 
	 * @param input 输入流
	 * @param charaterSet 编码格式，如"utf-8", "gbk"等
	 * @return 返回消耗的字节总数
	 * @throws IOException 出现读写错误时，抛出该异常
	 */
	public static String readLeft(InputStream in, String characterSet) throws IOException {
		String result = null;
		if (in == null) {
			return null;
		}
		
		int recvBytes = 0;
		byte[] buffer = new byte[MAX_BUFFER_BYTES / 2];
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(MAX_BUFFER_BYTES);
		try {
			while ((recvBytes = in.read(buffer, 0, MAX_BUFFER_BYTES / 2)) >= 0) {
				outputStream.write(buffer, 0, recvBytes);
				
			}
			
			buffer = null;
			result = outputStream.toString(characterSet);
		} finally {
			outputStream.close();
			buffer = null;
		}
		
		return result;
	}
	
	/**
	 * 从输入流中消耗掉剩余的数据，并以字节数组的形式返回, 该方法为IO阻塞方法
	 * 
	 * @param in 输入流
	 * @return 返回流中剩余的数据
	 * @throws IOException 读写出错时，将抛出IO异常
	 */
	public static byte[] readLeftBytes(InputStream in) throws IOException {
		
		if (in == null) {
			return null;
		}
		
		byte[] result = null;
		int recvBytes = 0;
		byte[] buffer = new byte[MAX_BUFFER_BYTES];
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(MAX_BUFFER_BYTES);
		try {
			while ((recvBytes = in.read(buffer, 0, MAX_BUFFER_BYTES)) >= 0) {
				outputStream.write(buffer, 0, recvBytes);
			}
			
			buffer = null;
			result = outputStream.toByteArray();
		} finally {
			outputStream.close();
			buffer = null;
		}
		
		return result;
	}
	
	/**
	 * 创建指定长度的字节数组，并初始为0
	 * 
	 * @param length 指定的字节数组长度
	 * @return 返回创建的数组
	 */
	public static byte[] createZeroBytes(int length) {
		if (length <= 0)
			throw new IllegalArgumentException("length must be gt 0");
		
		byte[] bytes = new byte[length];
		Arrays.fill(bytes, (byte) 0);
		return bytes;
	}
	
	/**
	 * 从指定字节数组中查找某子字节数组的第一次出现的位置
	 * 
	 * @param datas 指定数组
	 * @param start 起始查询位置
	 * @param t 待查询数组
	 * @return 如果没找到，返回-1，否则返回索引
	 */
	public static int indexOf(byte[] datas, int start, byte[] t) {
		
		if (datas == null || t == null) {
			throw new NullPointerException("source or target array is null!");
		}
		
		int index = -1;
		int len = datas.length;
		int tlen = t.length;
		
		if (start >= len || len - start < tlen) {
			return -1;
		}
		
		while (start <= len - tlen) {
			int i = 0;
			for (; i < tlen; i++) {
				if (datas[start + i] != t[i]) {
					break;
				}
			}
			
			if (i == tlen) {
				index = start;
				break;
			}
			
			start++;
		}
		
		return index;
	}
	
	/**
	 * 从一个字节数组中解析一个整数
	 * 
	 * @param buf 字节数组
	 * @param bigEndian 是否大字节序解析
	 * @return 相应的整数
	 */
	public static int parseInteger(byte[] buf, boolean bigEndian) {
		return (int) parseNumber(buf, 4, bigEndian);
	}
	
	/**
	 * 从一个字节数组中解析一个短整数
	 * 
	 * @param buf 字节数组
	 * @param bigEndian 是否大字节序解析
	 * @return 相应的短整数
	 */
	public static int parseShort(byte[] buf, boolean bigEndian) {
		return (int) parseNumber(buf, 2, bigEndian);
	}
	
	/**
	 * 从一个字节数组中解析一个长整数
	 * 
	 * @param buf 字节数组
	 * @param len 整数组成的字节数
	 * @param bigEndian 是否大字节序解析
	 * @return 相应的长整数
	 */
	public static long parseNumber(byte[] buf, int len, boolean bigEndian) {
		if (buf == null || buf.length == 0) {
			throw new IllegalArgumentException("byte array is null or empty!");
		}
		
		int mlen = Math.min(len, buf.length);
		long r = 0;
		if (bigEndian)
			for (int i = 0; i < mlen; i++) {
				r <<= 8;
				r |= (buf[i] & 0xff);
			}
		else
			for (int i = mlen - 1; i >= 0; i--) {
				r <<= 8;
				r |= (buf[i] & 0xff);
			}
		return r;
	}
	
	/**
	 * 判断字节数组是否以另一字节数组开头
	 * 
	 * @param all 原字节数组
	 * @param sub 子字节数组
	 * @return 返回判断结果
	 */
	public static boolean startWiths(byte[] all, byte[] sub) {
		if (all == null || sub == null || all.length < sub.length)
			return false;
		
		for (int i = 0; i < sub.length; i++) {
			if (all[i] != sub[i])
				return false;
		}
		
		return true;
	}
	
	/**
	 * 判断字节数组是否以另一字节数组结尾
	 * 
	 * @param all 原字节数组
	 * @param sub 子字节数组
	 * @return 返回判断结果
	 */
	public static boolean endWiths(byte[] all, byte[] sub) {
		if (all == null || sub == null || all.length < sub.length)
			return false;
		int allLen = all.length;
		int subLen = sub.length;
		
		for (int i = 1; i < (subLen + 1); i++) {
			if (all[allLen - i] != sub[subLen - i])
				return false;
		}
		
		return true;
	}
	
	/**
	 * 判断字节数组是否以另一字节数组结尾
	 * 
	 * @param all 原字节数组
	 * @param length 原字节数组最大长度
	 * @param sub 子字节数组
	 * @return 返回判断结果
	 */
	public static boolean endWiths(byte[] all, int length, byte[] sub) {
		if (all == null || sub == null || length < sub.length)
			return false;
		
		int allLen = Math.min(all.length, length);
		int subLen = sub.length;
		
		for (int i = 1; i < (subLen + 1); i++) {
			if (all[allLen - i] != sub[subLen - i])
				return false;
		}
		
		return true;
	}
	
	public static byte[] readCLenData(InputStream is) throws IOException {
		int len = is.read();
		if (len <= 0) {
			return null;
		}
		
		return readBytes(is, len);
	}
	
	public static void writeCLenData(OutputStream os, byte[] bytes) throws IOException {
		if (bytes == null) {
			os.write(0);
		} else {
			os.write(bytes.length);
			os.write(bytes);
		}
	}
	
	public static byte[] readWLenData(InputStream is, boolean big_endian) throws IOException {
		int len = readShort(is, big_endian);
		if (len <= 0) {
			return null;
		}
		
		return readBytes(is, len);
	}
	
	public static void writeWLenData(OutputStream os, byte[] bytes, boolean big_endian) throws IOException {
		if (bytes == null) {
			writeShort(os, 0, big_endian);
		} else {
			writeShort(os, bytes.length, big_endian);
			os.write(bytes);
		}
	}
	
	public static String readCString(InputStream is, String characterSet) throws IOException {
		int len = is.read();
		if (len <= 0)
			return null;
		
		return readString(is, len, characterSet);
	}
	
	public static String readWString(InputStream is, boolean big_endian, String characterSet) throws IOException {
		int len = readShort(is, big_endian);
		if (len <= 0)
			return null;
		
		return readString(is, len, characterSet);
	}
	
	/**
	 * 关闭一个Closeable对象，比如输入/输出流。
	 * 
	 * @param conn
	 */
	public static void close(Closeable conn) {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (IOException ex) {
			// 该异常可以忽略。
		}
	}
	
}
