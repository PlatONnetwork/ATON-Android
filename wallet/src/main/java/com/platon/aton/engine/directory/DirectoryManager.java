/*
 * FileName: DirectoryManager.java
 * Description: 目录管理类文件
 */
package com.platon.aton.engine.directory;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * 目录管理，负责检索每 一个目录，同时负责校验与创建。对需要清理的目录进行相关清理
 * 
 * @author devilxie
 * @version 1.0
 */
public final class DirectoryManager {
	
	/**
	 * 目录上下文
	 */
	private DirectroyContext context;
	/**
	 * 目录管理集合
	 */
	private LinkedHashMap<String, File>	dirs;
	
	public DirectoryManager(DirectroyContext context) {
		this.context = context;
		this.dirs = new LinkedHashMap<String, File>();
	}
	
	/**
	 * 校验并创建文件系统所有相关的目录
	 * 
	 * @return 返回操作结果。
	 */
	public boolean buildAndClean() {
		Directory directory = context.getBaseDirectory();
		return createDirectory(directory);
	}
	
	/**
	 * 获取指定类型的目录
	 * 
	 * @param type 目录类型，可自由定义。必须是大于0的整数
	 * @return 返回代表指定目录的文件实例，null表示没有找到
	 */
	public File getDir(String type) {
		File dir = dirs.get(type);
		return dir;
	}
	
	/**
	 * 获取指定类型的目录的完整路径
	 * 
	 * @param type 目录类型，可自由定义。必须是大于0的整数
	 * @return 返回代表指定目录的绝对路径。找不到时返回空字符串"".
	 */
	public String getDirPath(String type) {
		File file = getDir(type);
		if (file == null)
			return "";
		
		return file.getAbsolutePath();
	}
	

	public boolean createDirectory(Directory directory) {
		boolean ret = true;
		String path = null;
		Directory parent = directory.getParent();
		// 这是一个根目录
		if (parent == null) {
			path = directory.getPath();
		} else {
			File file = getDir(parent.getType());
			path = file.getAbsolutePath() + File.separator + directory.getPath();
		}
		
		// 先检测当前目前是否存在
		File file = new File(path);
		if (!file.exists()) {
			ret = file.mkdirs();
		}
		
		if (!ret) {
			return false;
		}
		
		dirs.put(directory.getType(), file);
		// 再检测各子目录是否存在
		Collection<Directory> children = directory.getChildren();
		if (children != null) {
			for (Directory dir : children) {
				if (!createDirectory(dir)) {
					return false;
				}
			}
		}
		
		return ret;
	}
	
}
