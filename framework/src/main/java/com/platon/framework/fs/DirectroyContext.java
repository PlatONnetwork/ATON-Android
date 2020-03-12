/*
 * FileName: DirectroyContext.java
 * Description: 目录上下文类文件
 */
package com.platon.framework.fs;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.platon.framework.app.config.DirType;
import com.platon.framework.util.TimeConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 目录上下文类，主要提供上层进行具体目录管理集合的定义的扩展
 *
 * @version 1.0
 */
public class DirectroyContext {
	protected Directory	baseDirectory;
	protected Context	mContext;

	public DirectroyContext(Context context) {
		this.mContext = context;
		initContext();
	}

	public void initContext() {
		String root = getRootPath();
		Directory dir = new Directory(root, null);
		this.baseDirectory = dir;
		dir.setType(root);
		Collection<Directory> children = initDirectories();
		if (children != null && children.size() > 0)
			dir.addChildren(children);
	}
	
	public Directory getBaseDirectory() {
		return baseDirectory;
	}

	/**
	 * 获取存储的根路径，对于内部存储：
	 * 
	 * @return
	 */
	protected String getRootPath() {
		String rootPath = "";
		if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
			File file = mContext.getExternalFilesDir(null);
			if (file != null) {
				rootPath = mContext.getExternalFilesDir(null).getAbsolutePath();
			}
		}
		if (TextUtils.isEmpty(rootPath)) {
			File fileDir = mContext.getFilesDir();
			rootPath = fileDir.getAbsolutePath() + File.separator;
		}
		return rootPath;
	}
	
	protected Collection<Directory> initDirectories() {
		List<Directory> children = new ArrayList<Directory>();
		Directory dir = newDirectory(DirType.log);
		children.add(dir);
		dir = newDirectory(DirType.image);
		children.add(dir);
		dir = newDirectory(DirType.crash);
		children.add(dir);
		dir = newDirectory(DirType.cache);
		children.add(dir);
		return children;
	}
	
	private Directory newDirectory(String type) {
		Directory child = new Directory(type, null);
		child.setType(type);
		if (type.equals(DirType.cache)) {
			child.setForCache(true);
			child.setExpiredTime(TimeConstants.ONE_DAY_MS);
		}
		return child;
	}
}
