package com.juzhen.framework.app.config;

import android.content.Context;

import com.juzhen.framework.app.log.OOMHelper;
import com.juzhen.framework.app.log.TUncaughtExceptionHandler;
import com.juzhen.framework.fs.DirectoryManager;
import com.juzhen.framework.fs.DirectroyContext;

import java.io.File;

/**
 * @Description: 对本应用程序的配置，包括DEBUG模式，创建文件路径，
 */

public class AppConfigure {
	
	private static DirContext	dirContext;
	private AppConfigure() {
	};

	public static void init(Context appContext) {
		if (appContext != null) {
			dirContext = new DirContext(appContext);
		}
	}

	public static void setConfigure(DirectroyContext dir, TUncaughtExceptionHandler handler) {
		if (dirContext == null){
			return;
		}
		DirectoryManager directoryManager = dirContext.getDirManager();
		if (directoryManager != null){
			return;
		}
		dirContext.init(dir);
		if (handler == null) {
			handler = new TUncaughtExceptionHandler(dirContext.getApplicationContext());
		} 	
		handler.setCrashPath(getDirPath(DirType.crash));
		handler.registerForExceptionHandler();
	}


	public static void setOOMDumpEnable() {
		OOMHelper.enable = true;
	}
	
	/**
	 * 获取全局Context
	 * 
	 * @return
	 */
	public static Context getAppContext() {
		return dirContext.getApplicationContext();
	}
	
	/**
	 * 获取目录路径
	 * 
	 * @param dirType 定义在{@link DirType}或其子类
	 * @return
	 */
	public static String getDirPath(String dirType) {
		return dirContext.mDirectoryManager.getDirPath(dirType);
	}
	
	/**
	 * 获取目录路径
	 * 
	 * @param dirType 定义在{@link DirType}或其子类
	 * @return
	 */
	public static File getDir(String dirType) {
		return dirContext.mDirectoryManager.getDir(dirType);
	}

	public static void registerSystemObject(String key, Object obj) {
		dirContext.registerSystemObject(key, obj);
	}
	
	public static Object getSystemObject(String name) {
		return dirContext.getSystemObject(name);
	}
}
