/*
 * FileName: Directory.java
 * Description: 目录元素类文件
 */
package com.juzhen.framework.fs;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 目录元素类，代表一个可操作的目录实体
 *
 * @version 1.0
 */
public final class Directory {
	private String					path;
	private Directory				parent		= null;
	private Collection<Directory>	children;
	private String					type		= "";
	private boolean					forCache	= false;
	private long					expiredTime	= -1L;
	
	public Directory(String path, Directory parent) {
		this.path = path;
		this.parent = parent;
	}
	
	public String getPath() {
		return path;
	}
	
	public Directory getParent() {
		return parent;
	}
	
	public Collection<Directory> getChildren() {
		return children;
	}
	
	public void addChild(Directory directory) {
		if (children == null) {
			children = new ArrayList<Directory>();
		}
		directory.parent = this;
		children.add(directory);
	}
	
	public void addChildren(Collection<Directory> dirs) {
		if (dirs == null || dirs.size() == 0)
			return;
		
		for (Directory d : dirs) {
			addChild(d);
		}
	}
	
	public void addChild(String type, String path, long expired) {
		Directory child = new Directory(path, this);
		child.type = type;
		
		if (expired > 0) {
			child.expiredTime = expired;
			child.forCache = true;
		}
		addChild(child);
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public boolean isForCache() {
		return forCache;
	}
	
	public void setForCache(boolean forCache) {
		this.forCache = forCache;
	}
	
	public long getExpiredTime() {
		return expiredTime;
	}
	
	public void setExpiredTime(long expiredTime) {
		this.expiredTime = expiredTime;
	}
}
