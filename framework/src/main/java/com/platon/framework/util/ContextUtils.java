package com.platon.framework.util;

/**
 * @Description:
 * @author hui.zhu
 */
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class ContextUtils {
	
	public static boolean	exit_to_desktop					= false;
	
	public static long		last_check_client_update_time	= 0;
	public static boolean	checking_client_update			= false;
	
	public static String getMetaData(Context context, String name) {
		PackageManager packageManager = context.getPackageManager();
		ApplicationInfo applicationInfo;
		Object value = null;
		try {
			
			applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 128);
			if (applicationInfo != null && applicationInfo.metaData != null) {
				value = applicationInfo.metaData.get(name);
			}
			
		} catch (NameNotFoundException e) {
			// NLog.printStackTrace(e);
			// NLog.w("ContextUtils", "Could not read the name(%s) in the manifest file.", name);
			return null;
		}
		
		return value == null ? null : value.toString();
	}
	
	public static String getNetworkInfoName(Context mContext) {
		ConnectivityManager connectionManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
		/** 无网络 */
		if (networkInfo == null) {
			return "";
		}
		return networkInfo.getTypeName();
	}
}
