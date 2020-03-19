package com.platon.framework.utils.thread;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

/**
 * 获取一个非UI线程的Looper用于创建Handler
 * 
 * @author rexzou
 */
public class HandlerUtils {
	
	private static HandlerThread	handerThread	= null;
	private static Handler			uiHandler		= new Handler(Looper.getMainLooper());
	
	/**
	 * 获取一个非UI线程的Looper
	 * 
	 * @return
	 */
	public static Looper getNUILooper() {
		
		if (handerThread == null) {
			synchronized (HandlerUtils.class) {
				if (handerThread == null) {
					handerThread = new HandlerThread("TheadUtils.handerThread");
					handerThread.start();
				}
			}
		}
		return handerThread.getLooper();
	}
	
	/**
	 * 在UI线程运行
	 * 
	 * @param runnable
	 */
	public static void runUITask(Runnable runnable) {
		uiHandler.post(runnable);
	}
	
	/**
	 * 在UI线程延时运行
	 * 
	 * @param runnable
	 */
	public static void runUITask(Runnable runnable, long delayTime) {
		uiHandler.postDelayed(runnable, delayTime);
	}
}
