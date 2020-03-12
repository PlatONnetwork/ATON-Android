package com.platon.framework.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class ZlibUtil {
	
	public static byte[] uncompress(byte[] compressBytes, int offset, int len) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int size = uncompress(compressBytes, offset, len, bos);
		if (size <= 0)
			return null;
		
		return bos.toByteArray();
	}
	
	public static byte[] uncompress(byte[] compressBytes) throws IOException {
		if (compressBytes == null || compressBytes.length == 0)
			return null;
		
		return uncompress(compressBytes, 0, compressBytes.length);
	}
	
	public static byte[] compress(byte[] uncompressBytes, int offset, int len) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		compress(uncompressBytes, offset, len, bos);
		return bos.toByteArray();
	}
	
	public static int uncompress(byte[] compressBytes, int offset, int len, OutputStream os) throws IOException {
		if (compressBytes == null || compressBytes.length < (offset + len))
			return 0;
		InflaterInputStream iis = new InflaterInputStream(new ByteArrayInputStream(compressBytes, offset, len));
		
		byte[] chunk = new byte[2048];
		int count = 0;
		int totalLen = 0;
		
		try {
			while ((count = iis.read(chunk)) >= 0) {
				os.write(chunk, 0, count);
				totalLen += count;
			}
		} finally {
			iis.close();
		}
		
		return totalLen;
	}
	
	public static int uncompress(byte[] compressBytes, byte[] out) {
		
		if (out == null || out.length == 0)
			return 0;
		
		InflaterInputStream iis = new InflaterInputStream(new ByteArrayInputStream(compressBytes));
		byte[] chunk = new byte[2048];
		int count = 0;
		int totalLen = 0;
		
		try {
			while (totalLen < out.length && (count = iis.read(chunk)) >= 0) {
				count = Math.min(count, out.length - totalLen);
				System.arraycopy(chunk, 0, out, totalLen, count);
				totalLen += count;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				iis.close();
			} catch (IOException e) {
			}
		}
		
		return totalLen;
	}
	
	public static void compress(byte[] srcBytes, int offset, int len, OutputStream os) throws IOException {
		DeflaterOutputStream dos = new DeflaterOutputStream(os);
		
		try {
			dos.write(srcBytes, offset, len);
		} finally {
			dos.close();
		}
		
	}
	
	public static byte[] compress(InputStream is) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		DeflaterOutputStream dos = new DeflaterOutputStream(os);
		
		byte[] buf = new byte[4098];
		int read = 0;
		try {
			
			while ((read = is.read(buf)) >= 0) {
				if (read > 0)
					dos.write(buf, 0, read);
			}
			
		} finally {
			dos.close();
		}
		
		byte[] data = os.toByteArray();
		return data;
	}
	
}
