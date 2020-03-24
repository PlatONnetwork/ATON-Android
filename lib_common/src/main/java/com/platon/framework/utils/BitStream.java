/**
 * FileName: BitStream.java
 * 
 * Description: 位流读写操作类文件。
 *
 */
package com.platon.framework.utils;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * 位流读写操作类，提供位流的写入与读取操作，可用于网络流的构造
 * 
 * @author devilxie
 * @version 1.0
 */
public class BitStream {
	private final static int	MIN_CAPACITY_INCREMENT	= 12;
	private static final int[]	MASKS					= { 0x00, 0x01, 0x03, 0x07, 0x0f, 0x1f, 0x3f, 0x7f, 0xff };
	byte[]						bitsBuffer;
	int							iBitsReadOffset;
	int							iBitsWriteOffset;
	int							nCurrentWriteByte;
	int							nCurrentReadByte;
	int							nBufferedBitsLen;
	
	public BitStream() {
		this(MIN_CAPACITY_INCREMENT / 2);
	}
	
	public BitStream(int capacity) {
		bitsBuffer = new byte[capacity];
		iBitsReadOffset = 0;
		iBitsWriteOffset = 0;
		nCurrentWriteByte = 0;
		nBufferedBitsLen = 0;
		nCurrentReadByte = 0;
	}
	
	public BitStream(byte[] b) {
		this(b, 0, b.length);
	}
	
	public BitStream(byte[] b, int offset, int len) {
		this(len + MIN_CAPACITY_INCREMENT / 2);
		if (b == null)
			throw new NullPointerException("byte array b is null");
		
		if (len <= 0 || (offset + len) > b.length)
			throw new ArrayIndexOutOfBoundsException("offset(" + offset + ") or len(" + len + ") is invalid");
		
		System.arraycopy(b, offset, bitsBuffer, 0, len);
		nCurrentWriteByte = len;
		nBufferedBitsLen = len * 8;
	}
	
	public BitStream(BitStream other) {
		bitsBuffer = new byte[other.bitsBuffer.length];
		iBitsReadOffset = other.iBitsReadOffset;
		iBitsWriteOffset = other.iBitsWriteOffset;
		nCurrentWriteByte = other.nCurrentWriteByte;
		nBufferedBitsLen = other.nBufferedBitsLen;
		nCurrentReadByte = other.nCurrentReadByte;
		System.arraycopy(other.bitsBuffer, 0, bitsBuffer, 0, bitsBuffer.length);
	}
	
	/**
	 * 获取缓存的当前最大容量
	 * 
	 * @return 返回缓存的当前最大容量
	 */
	public int capacity() {
		return bitsBuffer.length;
	}
	
	/**
	 * 判断当前有效数据是否以某字节数组收尾
	 * 
	 * @param b 字节数组
	 * @return 返回判断结果
	 */
	public boolean endWiths(byte[] b) {
		if (nBufferedBitsLen < b.length * 8)
			return false;
		
		if (iBitsWriteOffset == 0) {
			for (int i = 1; i < (b.length + 1); i++) {
				if (bitsBuffer[nCurrentWriteByte - i] != b[b.length - i])
					return false;
			}
		}
		
		else {
			
			int temp = 0;
			int current = nCurrentWriteByte;
			
			int leftmask = MASKS[iBitsWriteOffset] << (8 - iBitsWriteOffset);
			int rightmask = MASKS[8 - iBitsWriteOffset];
			int shift = iBitsWriteOffset;
			
			for (int i = b.length - 1; i >= 0; i--) {
				temp = (bitsBuffer[current--] & leftmask) >>> shift;
				temp |= (bitsBuffer[current] & rightmask) << (8 - shift);
				
				if (b[i] != temp)
					return false;
			}
			
		}
		
		return true;
	}
	
	/**
	 * 将字节数组写入位流中
	 * 
	 * @param b 待写入的字节数组
	 * @param align 是否进行字节对齐标识
	 */
	public void write(byte[] b, boolean align) {
		if (b == null)
			throw new NullPointerException("byte array b is null");
		
		write(b, 0, b.length, align);
	}
	
	/**
	 * 将字节数组写入位流中
	 * 
	 * @param bb 待写入的字节数组
	 * @param align 是否进行字节对齐标识
	 */
	public void write(ByteBuffer bb, boolean align) {
		if (!bb.hasRemaining())
			return;
		
		byte[] b = new byte[bb.remaining()];
		bb.get(b);
		write(b, align);
	}
	
	/**
	 * 将字节数组中指定区域写入位流中
	 * 
	 * @param b 原字节数组
	 * @param offset 起始位置
	 * @param len 区域长度
	 * @param align 是否进行字节对齐标识
	 */
	public void write(byte[] b, int offset, int len, boolean align) {
		if (b == null)
			throw new NullPointerException("byte array b is null");
		
		if (len <= 0 || (offset + len) > b.length)
			throw new ArrayIndexOutOfBoundsException("offset(" + offset + ") or len(" + len + ") is invalid");
		
		int needBits = len * 8;
		// 扩大缓存，保证不溢出
		if ((needBits + 16) > (bitsBuffer.length * 8 - nBufferedBitsLen)) {
			expand(len);
		}
		
		if (align) {
			align();
		}
		
		if (iBitsWriteOffset == 0) {
			System.arraycopy(b, offset, bitsBuffer, nCurrentWriteByte, len);
			nCurrentWriteByte += len;
			nBufferedBitsLen = nCurrentWriteByte * 8;
		} else {
			int leftmask = MASKS[8 - iBitsWriteOffset] << iBitsWriteOffset;
			int rightmask = MASKS[iBitsWriteOffset];
			int shift = iBitsWriteOffset;
			for (int i = 0; i < len; i++) {
				bitsBuffer[nCurrentWriteByte++] |= (byte) ((b[offset + i] & leftmask) >> shift);
				bitsBuffer[nCurrentWriteByte] = (byte) ((b[offset + i] & rightmask) << (8 - shift));
			}
			
			nBufferedBitsLen += len * 8;
		}
	}
	
	/**
	 * 将整数以二进制的形式写入指定长度到位流中
	 * 
	 * @param bits 需要写入的位数
	 * @param s 指定整数
	 */
	public void writeBits(int bits, long s) {
		// 扩大缓存，保证不溢出
		if ((bits + 16) > (bitsBuffer.length * 8 - nBufferedBitsLen)) {
			expand((bits + 7) % 8);
		}
		
		int currentBits = 0;
		int left = bits;
		int mask = 0;
		int shift = 0;
		byte cb = 0;
		
		do {
			
			currentBits = Math.min(left, 8 - iBitsWriteOffset);
			left -= currentBits;
			mask = MASKS[currentBits];
			shift = 8 - iBitsWriteOffset - currentBits;
			cb = (byte) (((s >> left) & mask) << shift);
			bitsBuffer[nCurrentWriteByte] |= cb;
			
			if (shift == 0) {
				iBitsWriteOffset = 0;
				nCurrentWriteByte++;
			} else {
				iBitsWriteOffset += currentBits;
				break;
			}
			
		} while (left > 0);
		
		nBufferedBitsLen += bits;
	}
	
	/**
	 * 从位流中预读指定位数的整数，不影响位流的下次读取
	 * 
	 * @param bits 待预读的位数
	 * @return 返回预读到的整数
	 */
	public long preReadBits(int bits) {
		int oldCurrentReadByte = nCurrentReadByte;
		int olditsReadOffset = iBitsReadOffset;
		long ret = readBits(bits);
		nCurrentReadByte = oldCurrentReadByte;
		iBitsReadOffset = olditsReadOffset;
		return ret;
	}
	
	/**
	 * 跳过位流中指定位数，会影响位流的下次读取
	 * 
	 * @param bits 待跳过的位数
	 */
	public void skip(int bits) {
		readBits(bits);
	}
	
	/**
	 * 将位流中的数据读入到指定缓存区中，返回已读入的数据字节长度
	 * 
	 * @param buffer 缓存区
	 * @param start 缓存区起始存放位置
	 * @return 返回读入的数据长度，以字节为单位
	 */
	public int read(byte[] buffer, int start) {
		if (buffer == null)
			throw new NullPointerException("buffer is null");
		
		if (start < 0 || start >= buffer.length)
			throw new ArrayIndexOutOfBoundsException(start);
		
		int ncopy = Math.min((available() + 7) / 8, buffer.length - start);
		if (ncopy == 0)
			return 0;
		
		if (iBitsReadOffset == 0) {
			System.arraycopy(bitsBuffer, nCurrentReadByte, buffer, start, ncopy);
			nCurrentReadByte += ncopy;
		} else {
			int leftmask = MASKS[iBitsReadOffset] << (8 - iBitsReadOffset);
			int rightmask = MASKS[8 - iBitsReadOffset];
			int shift = iBitsReadOffset;
			for (int i = 0; i < ncopy; i++) {
				buffer[i + start] = (byte) ((bitsBuffer[nCurrentReadByte++] & rightmask) << (8 - shift));
				buffer[i + start] |= (byte) ((bitsBuffer[nCurrentReadByte] & leftmask) >> shift);
			}
		}
		
		return ncopy;
	}
	
	/**
	 * 将位流中的数据读入到指定缓存区中，返回已读入的数据字节长度
	 * 
	 * @param buffer 缓存区
	 * @return 返回读入的数据长度，以字节为单位
	 */
	public int read(byte[] buffer) {
		return read(buffer, 0);
	}
	
	/**
	 * 从位流中读入指定位数的整数，影响位流的下次读取
	 * 
	 * @param bits 指定位数
	 * @return 返回读取到的整数
	 */
	public long readBits(int bits) {
		long ret = 0;
		if (bits <= 0)
			throw new IllegalArgumentException("bits must > 0");
		
		int readyReadBits = nCurrentReadByte * 8 + iBitsReadOffset + bits;
		
		if (readyReadBits > nBufferedBitsLen) {
			bits = nBufferedBitsLen - (nCurrentReadByte * 8 + iBitsReadOffset);
		}
		
		int left = bits;
		int currentBits = 0;
		int shift = 0;
		int mask = 0;
		
		do {
			currentBits = Math.min(8 - iBitsReadOffset, left);
			left -= currentBits;
			shift = 8 - iBitsReadOffset - currentBits;
			mask = MASKS[currentBits];
			ret |= ((bitsBuffer[nCurrentReadByte] >> shift) & mask) << left;
			
			if (shift == 0) {
				iBitsReadOffset = 0;
				nCurrentReadByte++;
			} else {
				iBitsReadOffset += currentBits;
				break;
			}
			
		} while ((left > 0));
		
		return ret;
	}
	
	/**
	 * 将位流中的有效数据转化为字节数组
	 * 
	 * @return 返回转换后的字节数组
	 */
	public byte[] toByteArray() {
		if (nBufferedBitsLen == 0)
			return new byte[0];
		
		int bits = nBufferedBitsLen - (nCurrentReadByte * 8 + iBitsReadOffset);
		int bytes = (bits + 7) / 8;
		byte[] ret = new byte[bytes];
		int oldCurrentReadByte = nCurrentReadByte;
		int olditsReadOffset = iBitsReadOffset;
		read(ret);
		nCurrentReadByte = oldCurrentReadByte;
		iBitsReadOffset = olditsReadOffset;
		return ret;
	}
	
	/**
	 * 将位流中的数据转化为指定长度的字节数组，不够的补0
	 * 
	 * @param size 指定的长度
	 * @return 返回转换后的字节数组
	 */
	public byte[] toByteArray(int size) {
		if (size <= 0)
			throw new IllegalArgumentException("size must be > 0");
		
		byte[] ret = new byte[size];
		int oldCurrentReadByte = nCurrentReadByte;
		int olditsReadOffset = iBitsReadOffset;
		read(ret);
		nCurrentReadByte = oldCurrentReadByte;
		iBitsReadOffset = olditsReadOffset;
		return ret;
	}
	
	/**
	 * 紧缩位缓存，将无用的数据去除，同时减少缓存空间，一般不建议使用
	 */
	public void compact() {
		if (nCurrentReadByte == 0 || nCurrentWriteByte == 0)
			return;
		
		System.arraycopy(bitsBuffer, nCurrentReadByte, bitsBuffer, 0, nCurrentWriteByte - nCurrentReadByte);
		
		int last = nCurrentWriteByte;
		int start = nCurrentWriteByte - nCurrentReadByte;
		nBufferedBitsLen -= nCurrentReadByte * 8;
		nCurrentWriteByte -= nCurrentReadByte;
		nCurrentReadByte = 0;
		Arrays.fill(bitsBuffer, start, last, (byte) 0);
	}
	
	/**
	 * 获取位流中可读取的位数
	 * 
	 * @return 返回位流中可读取的位数
	 */
	public int available() {
		return (nBufferedBitsLen - nCurrentReadByte * 8 - iBitsReadOffset);
	}
	
	/**
	 * 获取位流中有效的字节数量
	 * 
	 * @return 返回位流中有效的字节数量
	 */
	public int size() {
		return (nBufferedBitsLen + 7) / 8;
	}
	
	/**
	 * 清空位流缓存区
	 */
	public void reset() {
		nBufferedBitsLen = 0;
		iBitsReadOffset = 0;
		iBitsWriteOffset = 0;
		nCurrentWriteByte = 0;
		nCurrentReadByte = 0;
		Arrays.fill(bitsBuffer, (byte) 0);
	}
	
	private static int newCapacity(int need) {
		return need + MIN_CAPACITY_INCREMENT;
	}
	
	/**
	 * 扩大缓存容量
	 */
	private void expand(int need) {
		if (need == 0)
			return;
		
		int needLen = need - (bitsBuffer.length - nCurrentWriteByte) + bitsBuffer.length;
		
		int capacity = newCapacity(needLen);
		byte[] temp = new byte[capacity];
		System.arraycopy(bitsBuffer, 0, temp, 0, bitsBuffer.length);
		bitsBuffer = temp;
	}
	
	/**
	 * 进行字节对齐
	 */
	private void align() {
		if (iBitsWriteOffset == 0)
			return;
		
		iBitsWriteOffset = 0;
		nCurrentWriteByte++;
		nBufferedBitsLen = nCurrentWriteByte * 8;
	}
}
