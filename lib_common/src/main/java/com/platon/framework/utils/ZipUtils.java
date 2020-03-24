/*
 * FileName: ZipUtils.java
 * Description: 压缩与解压缩辅助类文件
 */
package com.platon.framework.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 压缩与解压缩工具类，提供对目录、文件进行压缩与解压缩功能
 * 
 * @author devilxie
 * @version 1.0
 */
public class ZipUtils {
	
	/**
	 * 解压文件
	 * 
	 * @param zipPath 压缩包路径
	 * @param toPath 解压目录
	 * @throws IOException 过程如果出现错误，将抛出IO异常
	 */
	public static void unCompress(String zipPath, String toPath) throws IOException {
		File zipfile = new File(zipPath);
		if (!zipfile.exists())
			return;
		
		if (!toPath.endsWith("/"))
			toPath += "/";
		
		File destFile = new File(toPath);
		if (!destFile.exists())
			destFile.mkdirs();
		
		ZipInputStream zis = new ZipInputStream(new FileInputStream(zipfile));
		ZipEntry entry = null;
		
		try {
			
			while ((entry = zis.getNextEntry()) != null) {
				if (entry.isDirectory()) {
					File file = new File(toPath + entry.getName() + "/");
					file.mkdirs();
				} else {
					File file = new File(toPath + entry.getName());
					if (!file.getParentFile().exists())
						file.getParentFile().mkdirs();
					
					FileOutputStream fos = null;
					
					try {
						fos = new FileOutputStream(file);
						byte buf[] = new byte[1024];
						int len = -1;
						while ((len = zis.read(buf, 0, 1024)) != -1) {
							fos.write(buf, 0, len);
						}
					} finally {
						
						if (fos != null) {
							try {
								fos.close();
							} catch (Exception e) {
							}
						}
					}
				}
			}
			
		} finally {
			zis.close();
		}
		
	}
	
	/**
	 * 压缩单个文件,并将压缩输出文件置同层目录，以文件名+ ".zip"的形式命名
	 * 
	 * @param file 被压缩文件
	 */
	public static boolean compress(File file) {
		try {
			String fileName = file.getName();
			if (fileName.indexOf(".") != -1)
				fileName = fileName.substring(0, fileName.indexOf("."));
			FileOutputStream f = new FileOutputStream(file.getParent() + "/" + fileName + ".zip");
			CheckedOutputStream cs = new CheckedOutputStream(f, new Adler32());
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(cs));
			InputStream in = new FileInputStream(file);
			out.putNextEntry(new ZipEntry(file.getName()));
			int len = -1;
			byte buf[] = new byte[1024];
			while ((len = in.read(buf, 0, 1024)) != -1)
				out.write(buf, 0, len);
			out.closeEntry();
			
			in.close();
			out.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 压缩文件夹，并将压缩输出文件置同层目录，以目录名 + ".zip"的形式命名
	 * 
	 * @param file 待压缩的目录文件
	 * @throws IOException
	 */
	public static void compressDir(File file) throws IOException {
		FileOutputStream f = new FileOutputStream(file.getParent() + file.getName() + ".zip");
		CheckedOutputStream cs = new CheckedOutputStream(f, new Adler32());
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(cs));
		
		compressDir(file, out, file.getAbsolutePath());
		
		out.flush();
		out.close();
	}
	
	/**
	 * 压缩文件夹递归调用方法
	 * 
	 * @param file
	 * @param out
	 * @throws IOException
	 */
	private static void compressDir(File srcFile, ZipOutputStream out, String destPath) throws IOException {
		if (srcFile.isDirectory()) {
			File subfile[] = srcFile.listFiles();
			for (int i = 0; i < subfile.length; i++) {
				compressDir(subfile[i], out, destPath);
			}
		} else {
			InputStream in = new FileInputStream(srcFile);
			String name = srcFile.getAbsolutePath().replace(destPath, "");
			if (name.startsWith("\\"))
				name = name.substring(1);
			ZipEntry entry = new ZipEntry(name);
			entry.setSize(srcFile.length());
			entry.setTime(srcFile.lastModified());
			out.putNextEntry(entry);
			int len = -1;
			byte buf[] = new byte[1024];
			while ((len = in.read(buf, 0, 1024)) != -1)
				out.write(buf, 0, len);
			
			out.closeEntry();
			in.close();
		}
	}
}
