package com.juzhen.framework.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.List;
import java.util.regex.Pattern;

public class AndroidUtil {
	
	/**
	 * 获取状态栏高度
	 * 
	 * @param activity
	 * @return
	 */
	public static int getWindowTitleHeight(Context context) {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, sbar = 0;
		try {
			c = Class.forName("com.android.internal.RUtils$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			sbar = context.getResources().getDimensionPixelSize(x);
			return sbar;
		} catch (Exception e1) {
			System.out.println("get status bar height fail");
			e1.printStackTrace();
		}
		return 0;
	}
	
	/** 计算目标View的高度 */
	public static int getViewHeight(View target) {
		int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		target.measure(w, h);
		int height = target.getMeasuredHeight();
		return height;
	}
	
	/** 改变键盘状态：开--->关，关--->开 */
	public static void changedKeyBoard(Context context) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		// 得到InputMethodManager的实例
		if (imm != null && imm.isActive()) {
			// 如果开启
			imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);// 关闭软键盘，开启方法相同，这个方法是切换开启与关闭状态的
		}
	}
	
	/** 打开键盘 */
	public static void openKeyBoard(Activity activity) {
		View view = activity.getWindow().peekDecorView();
		if (view != null) {
			InputMethodManager inputmanger = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputmanger.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.SHOW_IMPLICIT);
		}
	}
	
	/** 关闭键盘 */
	public static void closeKeyBoard(Activity activity) {
		View view = activity.getWindow().peekDecorView();
		if (view != null) {
			InputMethodManager inputmanger = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputmanger.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
	
	/**
	 * 获取屏幕宽度
	 * 
	 * @param mContext
	 * @return
	 */
	public static int getWindowWith(Context mContext) {
		WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		
		return wm.getDefaultDisplay().getWidth();
	}
	
	/**
	 * 获取屏幕高度
	 * 
	 * @param mContext
	 * @return
	 */
	public static int getWindowHeight(Context mContext) {
		WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		return wm.getDefaultDisplay().getHeight();
	}
	
	public static int getDisplayMetricsWith(Context mContext) {
		WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		
		return wm.getDefaultDisplay().getWidth();
	}
	
	public static int getDisplayMetricsHeight(Context mContext) {
		WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		return wm.getDefaultDisplay().getHeight();
	}
	
	/**
	 * 获取版本code
	 * 
	 * @param context
	 * @return
	 */
	public static int getVersionCode(Context context) {
		int verCode = -1;
		try {
			verCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			
		}
		return verCode;
	}
	
	/**
	 * 获取版本name
	 * 
	 * @param context
	 * @return
	 */
	public static String getVersionName(Context context) {
		String verCode = "0.0.0.0";
		try {
			verCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			
		}
		return verCode;
	}
	
	// 字符串为空
	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0 || str.equals("null");
	}
	
	/**
	 * 获取图片资源
	 * 
	 * @param mContext
	 * @param name
	 * @return
	 */
	public static int getResource(Context mContext, String name) {
		ApplicationInfo appInfo = mContext.getApplicationInfo();
		return mContext.getResources().getIdentifier(name, "drawable", appInfo.packageName);
	}
	
	/**
	 * 指定位置颜色
	 * 
	 * @param text
	 * @param start
	 * @param end
	 * @return
	 */
	public static SpannableStringBuilder setStringPartColor(String text) {
		SpannableStringBuilder style = new SpannableStringBuilder(text);
		style.setSpan(new ForegroundColorSpan(0xFF5db224), text.lastIndexOf("->"), text.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		return style;
	}
	
	/**
	 * convert dip to px
	 * 
	 * @param dipValue
	 * @param scale
	 * @return
	 */
	public static int convertDIP2PX(Context context, int dip) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
	}
	
	// 获取系统版本
	public static String getVersionRelease() {
		return android.os.Build.VERSION.RELEASE;
	}
	
	/**
	 * 安装应用包
	 * 
	 * @param context
	 * @param installPath
	 * @author shuangshuang.li
	 */
	
	public static void install(Context context, String installPath) {
		if (TextUtils.isEmpty(installPath)) {
			return;
		}
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(new File(installPath)), "application/vnd.android.package-archive");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 大于 1024*1024 取 M 大于 1024 取K 否则去 1K
	 * 
	 * @param fileSize
	 * @return
	 * @author shuangshuang.li
	 */
	public static String getFileSize(double fileSize) {
		String fileSizeStr = "";
		DecimalFormat df = new DecimalFormat("#0.00"); // double
		// 格式double保留一位小数
		double msize = fileSize / (1024 * 1024);
		double ksize = fileSize / 1024;
		if (msize > 1) {
			fileSizeStr = df.format(msize) + " M";
		} else if (ksize > 1) {
			fileSizeStr = df.format(ksize) + " Kb";
		} else {
			fileSizeStr = "1 k";
		}
		return fileSizeStr;
	}
	
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
			Log.d("ContextUtils", "NameNotFoundException:" + e.toString());
			Log.d("ContextUtils", "Could not read the name:" + name);
			return null;
		}
		
		return value == null ? null : value.toString();
	}
	
	public static File uri2File(Activity activity, String uriStr) {
		Uri uri = Uri.parse(uriStr);
		File file = null;
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor actualimagecursor = activity.managedQuery(uri, proj, null, null, null);
		int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		actualimagecursor.moveToFirst();
		String img_path = actualimagecursor.getString(actual_image_column_index);
		file = new File(img_path);
		return file;
	}
	
	/**
	 * 将px值转换为dip或dp值，保证尺寸大小不变
	 * 
	 * @param pxValue
	 * @param scale （DisplayMetrics类中属性density）
	 * @return
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	
	/**
	 * 将dip或dp值转换为px值，保证尺寸大小不变
	 * 
	 * @param dipValue
	 * @param scale （DisplayMetrics类中属性density）
	 * @return
	 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}
	
	/**
	 * 将px值转换为sp值，保证文字大小不变
	 * 
	 * @param pxValue
	 * @param fontScale （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}
	
	/**
	 * 将sp值转换为px值，保证文字大小不变
	 * 
	 * @param spValue
	 * @param fontScale （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}


	static final String TAG = "Android";

	public static boolean isMainProcess(Context context) {
		ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
		List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
		String mainProcessName = context.getPackageName();
		int myPid = android.os.Process.myPid();
		for (ActivityManager.RunningAppProcessInfo info : processInfos) {
			if (info.pid == myPid && mainProcessName.equals(info.processName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 检查context对应的Activity的状态
	 *
	 */
	public static boolean isValidContext(Activity activity) {
		if(activity == null ){
			return false;
		}
		if (isDestroyed(activity) || activity.isFinishing()) {
			return false;
		} else {
			return true;
		}
	}

	@TargetApi(17)
	private static boolean isDestroyed(Activity activity) {
		// TODO Auto-generated method stub
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			return activity.isDestroyed();
		}
		return false;
	}

	@TargetApi(11)
	// @TargetApi(VERSION_CODES.HONEYCOMB)
	public static void enableStrictMode() {
		if (hasGingerbread()) {
			StrictMode.ThreadPolicy.Builder threadPolicyBuilder = new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog();
			StrictMode.VmPolicy.Builder vmPolicyBuilder = new StrictMode.VmPolicy.Builder().detectAll().penaltyLog();

			if (hasHoneycomb()) {
				threadPolicyBuilder.penaltyFlashScreen();
				// vmPolicyBuilder.setClassInstanceLimit(ImageGridActivity.class,
				// 1).setClassInstanceLimit(ImageDetailActivity.class, 1);
			}
			StrictMode.setThreadPolicy(threadPolicyBuilder.build());
			StrictMode.setVmPolicy(vmPolicyBuilder.build());
		}
	}

	/**
	 * API level is or higher than 8
	 */
	public static boolean hasFroyo() {
		// Can use static final constants like FROYO, declared in later versions
		// of the OS since they are inlined at compile time. This is guaranteed
		// behavior.
		return Build.VERSION.SDK_INT >= 8; // VERSION_CODES.FROYO;
	}

	/**
	 * API level is or higher than 9
	 */
	public static boolean hasGingerbread() {
		return Build.VERSION.SDK_INT >= 9; // VERSION_CODES.GINGERBREAD;
	}

	/**
	 * API level is or higher than 11
	 */
	public static boolean hasHoneycomb() {
		return Build.VERSION.SDK_INT >= 11; // VERSION_CODES.HONEYCOMB;
	}

	/**
	 * API level is or higher than 12
	 */
	public static boolean hasHoneycombMR1() {
		return Build.VERSION.SDK_INT >= 12; // VERSION_CODES.HONEYCOMB_MR1;
	}

	/**
	 * API level is or higher than 16
	 */
	public static boolean hasJellyBean() {
		return Build.VERSION.SDK_INT >= 16; // VERSION_CODES.JELLY_BEAN;
	}

	/**
	 * API level is higher than 19
	 */
	public static boolean hasKitKat() {
		return Build.VERSION.SDK_INT >= 19; // VERSION_CODES.KITKAT;
	}

	public static boolean checkDeviceIsRoot() {

		// get from build info
		String buildTags = android.os.Build.TAGS;
		if (buildTags != null && buildTags.contains("test-keys")) {
			return true;
		}

		// check if /system/app/Superuser.apk is present
		try {
			File file = new File("/system/app/Superuser.apk");
			if (file.exists()) {
				Log.d("command", "/system/app/Superuser.apk file existed!");
				return true;
			} else {
				Log.e("command", file.getName() + " Not present!");
			}
		} catch (Exception e1) {
			// ignore
		}
		if (findBinary("su")) {
			return true;
		}
		return false;
	}

	public static boolean findBinary(String binaryName) {
		boolean found = false;
		if (!found) {
			String[] places = { "/sbin/", "/system/bin/", "/system/xbin/", "/data/local/xbin/", "/data/local/bin/", "/system/sd/xbin/",
					"/system/bin/failsafe/", "/data/local/" };
			for (String where : places) {
				if (new File(where + binaryName).exists()) {
					found = true;
					break;
				}
			}
		}
		return found;
	}

	/**
	 * 获取内存大小
	 *
	 * @return
	 */
	public static String getTotalMemory() {
		String str1 = "/proc/meminfo";
		String str2 = "";
		BufferedReader localBufferedReader = null;
		FileReader fr = null;
		try {
			fr = new FileReader(str1);
			localBufferedReader = new BufferedReader(fr, 8192);
			while ((str2 = localBufferedReader.readLine()) != null) {
				// Log.i(TAG, "---" + str2);
				if (str2.startsWith("MemTotal")) {
					str2 = str2.trim().substring(9);
					return str2.trim();
				}
			}
		} catch (IOException e) {
			return "0";
		} finally {
			try {
				if (fr != null)
					fr.close();
				if (localBufferedReader != null)
					localBufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "0";
	}

	/**
	 * 最小频率
	 *
	 * @return
	 */
	public static String getMinCpuFreq() {
		String result = "";
		ProcessBuilder cmd;
		try {
			String[] args = { "/system/bin/cat", "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq" };
			cmd = new ProcessBuilder(args);
			Process process = cmd.start();
			InputStream in = process.getInputStream();
			byte[] re = new byte[24];
			while (in.read(re) != -1) {
				result = result + new String(re);
			}
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
			result = "N/A";
		}
		return result.trim();
	}

	/**
	 * 获取cpu个数
	 *
	 * @return
	 */
	public static int getNumCores() {
		// Private Class to display only CPU devices in the directory listing
		class CpuFilter implements FileFilter {
			@Override
			public boolean accept(File pathname) {
				// Check if filename is "cpu", followed by a single digit number
				if (Pattern.matches("cpu[0-9]", pathname.getName())) {
					return true;
				}
				return false;
			}
		}
		try {
			// Get directory containing CPU info
			File dir = new File("/sys/devices/system/cpu/");
			// Filter to only list the devices we care about
			File[] files = dir.listFiles(new CpuFilter());
			// Log.debug(TAG, "CPU Count: " + files.length);
			// Return the number of cores (virtual CPU devices)
			return files.length;
		} catch (Exception e) {
			// Print exception
			Log.e(TAG, "CPU Count: Failed.");
			e.printStackTrace();
			// Default to return 1 core
			return 1;
		}
	}

	/**
	 * 最大频率（单位:兆赫）
	 *
	 * @return
	 */
	public static String getCpuFrequence() {
		ProcessBuilder cmd;
		try {
			String[] args = { "/system/bin/cat", "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq" };
			cmd = new ProcessBuilder(args);

			Process process = cmd.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = reader.readLine();
			// Log.debug(TAG, line);
			long z = Long.parseLong(line);
			z = z / 1000; // Mhz 兆赫
			return z + "";
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return "0";
	}

	// 检测SDcard是否存在
	public static boolean checkSDcard() {
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}

	// 检查app是否已安装
	public static boolean chechAppInstallState(Context ctx, String packageName) {
		// 获取所有应用的名称，包名，以及权限 有了包名就可以判断是否有某个应用了
		List<PackageInfo> list = ctx.getPackageManager().getInstalledPackages(PackageManager.GET_PERMISSIONS);

		for (PackageInfo packageInfo : list) {
			if (packageInfo.packageName.equals(packageName)) {
				return true;
			}
		}
		return false;
	}

	public static void installApp(Context ctx, String packageName) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + packageName), "application/vnd.android.package-archive");
		ctx.startActivity(i);
	}

	public static void startAnotherApp(Context ctx, String packageName, String userToken) {
		PackageInfo packageInfo = null;
		try {
			packageInfo = ctx.getPackageManager().getPackageInfo(packageName, 0);
			if (packageInfo == null) {
				System.out.println("packageInfo==null");
			} else {
				System.out.println("packageInfo!=null");
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
		resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resolveIntent.setPackage(packageInfo.packageName);

		List<ResolveInfo> resolveInfoList = ctx.getPackageManager().queryIntentActivities(resolveIntent, 0);

		ResolveInfo resolveInfo = resolveInfoList.iterator().next();
		if (resolveInfo != null) {
			String activityPackageName = resolveInfo.activityInfo.packageName;
			String className = resolveInfo.activityInfo.name;

			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			ComponentName componentName = new ComponentName(activityPackageName, className);

			intent.setComponent(componentName);
			intent.putExtra("token", userToken);
			ctx.startActivity(intent);
		}
	}

	public static String getProperties(String key) {
		String value;
		try {
			Class<?> clazz = Class.forName("android.os.SystemProperties");
			Method methodGet = clazz.getDeclaredMethod("get", String.class);
			//Method methodSet = clazz.getDeclaredMethod("set", String.class, String.class);
			value = (String) methodGet.invoke(clazz.newInstance(), key);
		} catch (Exception e) {
			e.printStackTrace();
			value = null;
		}
		Log.d(TAG, "getProperties: [key, value] = [" + key + ", " + value + "]");
		return value;
	}

	/**
	 * 判断是否在v之外区域(目前适用EditText之外点击隐藏键盘)
	 * @param v
	 * @param event
	 * @return
	 */
	public static boolean isOutSizeView(View v, MotionEvent event) {
		if (v != null && event != null) {
			int[] leftTop = { 0, 0 };
			v.getLocationInWindow(leftTop);
			int left = leftTop[0];
			int top = leftTop[1];
			int bottom = top + v.getHeight();
			int right = left + v.getWidth();
			return !(event.getX() > left && event.getX() < right
					&& event.getY() > top && event.getY() < bottom);
		}
		return false;
	}

	public static int getViewXLocationOnScreen(View view) {
		int[] location = new int[2];
		view.getLocationOnScreen(location);
		return location[0];
	}

	public static int getViewYLocationOnScreen(View view) {
		int[] location = new int[2];
		view.getLocationOnScreen(location);
		return location[1];
	}
}
