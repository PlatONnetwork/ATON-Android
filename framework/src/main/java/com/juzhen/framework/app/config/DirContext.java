package com.juzhen.framework.app.config;

import android.content.Context;

import com.juzhen.framework.fs.DirectoryManager;
import com.juzhen.framework.fs.DirectroyContext;

public class DirContext extends SysContext {
	
	DirectoryManager	mDirectoryManager	= null;
	
	public DirContext(Context context) {
		super(context);
	}
	
	public void init(DirectroyContext dir) {
		if (dir == null) {
			dir = new DirectroyContext(getApplicationContext());
		}
		DirectoryManager dm = new DirectoryManager(dir);
		boolean ret = dm.buildAndClean();
		if (!ret) {
			return;
		}
		mDirectoryManager = dm;
	}
	
	public void init() {
		init(null);
	}
	
	public DirectoryManager getDirManager() {
		return mDirectoryManager;
	}
	
}
