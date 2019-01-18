/*
 * FileName: DeviceManager.java
 * Description: 设备信息管理类
 */

package com.juzhen.framework.util;

import java.util.Locale;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.WindowManager;

/**
 * 设备信息管理类，提供设备信息相关获取、操作等工具
 * 
 * @author devilxie
 * @version 1.0
 * 
 */
public final class DeviceManager {
	/**
	 * 判断当前设备是否为中文
	 *
	 */
	public static boolean isChinese(Context context) {
		Locale locale = context.getResources().getConfiguration().locale;
		return locale.toString().equals("zh_CN");
	}
	
	/**
	 * 判断横盘
	 */
	public static boolean isScreenPortrait(Context context) {
		return context.getResources().getConfiguration().orientation == 1;
	}

	public static float px2sp(Context context, float size) {
		final float scale = context.getResources().getDisplayMetrics().density;
		if (size <= 0) {
			size = 15;
		}
		float realSize = (float) (size * (scale - 0.1));
		return realSize;
	}

	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static float sp2px(Context context, float size) {
		final float scale = context.getResources().getDisplayMetrics().density;
		if (size <= 0) {
			size = 15;
		}
		float realSize = (float) (size / (scale - 0.1));
		return realSize;
	}

	public static int getDimens(Context context, int resid) {
		return context.getResources().getDimensionPixelSize(resid);
	}
	
	public static String getString(Context context, int resid) {
		return context.getResources().getString(resid);
	}
	
	public static Drawable getDrawable(Context context, int resid) {
		return context.getResources().getDrawable(resid);
	}
	
	public static int getColor(Context context, int resid) {
		return context.getResources().getColor(resid);
	}
	
	public static ColorStateList getColorStateList(Context context, int resid) {
		return context.getResources().getColorStateList(resid);
	}
	
	/**
	 * 获取显示密度
	 */
	public static float getScreenDensity(Context context) {
		return context.getResources().getDisplayMetrics().density;
	}
	
	/**
	 * 获取显示宽度
	 */
	public static float getScreenWidth(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}
	
	/**
	 * 获取显示高度
	 */
	public static float getScreenHeight(Context context) {
		return context.getResources().getDisplayMetrics().heightPixels;
	}
	
	/**
	 * 获取屏幕宽度
	 * 
	 * @param context
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static int getDisplayMetricsWith(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		
		if (AndroidNewApi.IsSDKLevelAbove(13)) {
			Point sizePoint = new Point();
			wm.getDefaultDisplay().getSize(sizePoint);
			return sizePoint.x;
		} else {
			return wm.getDefaultDisplay().getWidth();
			
		}
	}
	
	/**
	 * 获取屏幕高度
	 * 
	 * @param mContext
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static int getDisplayMetricsHeight(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		
		if (AndroidNewApi.IsSDKLevelAbove(13)) {
			Point sizePoint = new Point();
			wm.getDefaultDisplay().getSize(sizePoint);
			return sizePoint.y;
		} else {
			return wm.getDefaultDisplay().getHeight();
			
		}
		
	}
}
