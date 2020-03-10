/*
 * FileName: CacheChecker.java
 * Description: 缓存检测类文件
 */
package com.platon.framework.fs;

import java.io.File;

/**
 * 缓存检测类，辅助校验文件是否超期
 * 
 * @author devilxie
 * @version 1.0
 */
public final class CacheChecker {
	public static boolean expired(File file, long expiredTimeMs) {
		long current = System.currentTimeMillis() / 1000 * 1000 - file.lastModified();
		return (current < 0 || current >= expiredTimeMs);
	}
	
	/**
	 * 检测缓存是否有效，如果无效，将删除缓存
	 * 
	 * @param path 缓存文件路径
	 * @param expiredtime 有效时间，以毫秒为单位
	 * @return 返回检测结果。true表示有效，false表示无效
	 */
	public static boolean checkCache(String path, long expiredtime) {
		return checkCache(path, expiredtime, true);
	}
	
	/**
	 * 检测缓存是否有效
	 * 
	 * @param path 缓存文件路径
	 * @param expiredtime 有效时间，以毫秒为单位
	 * @param del 指示无效时是否删除标识
	 * @return true表示有效，false表示无效
	 */
	public static boolean checkCache(String path, long expiredtime, boolean del) {
		File file = new File(path);
		// 若文件不存在，则无效
		if (!file.exists()) {
			return false;
		}
		// 有效期检测没超期
		if (!expired(file, expiredtime)) {
			return true;
		}
		
		if (del) {
			file.delete();
		}
		
		return false;
	}
	
	/**
	 * 检测缓存是否有效
	 * 
	 * @param file 缓存文件
	 * @param expiredtime 有效时间，以毫秒为单位
	 * @param del 指示无效时是否删除标识
	 * @return true表示有效，false表示无效
	 */
	public static boolean checkCache(File file, long expiredtime, boolean del) {
		// 若文件不存在，则无效
		if (!file.exists()) {
			return false;
		}
		// 有效期检测没超期
		if (!expired(file, expiredtime)) {
			return true;
		}
		
		if (del) {
			file.delete();
		}
		
		return false;
	}
}
