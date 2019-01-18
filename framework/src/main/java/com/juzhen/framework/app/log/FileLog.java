package com.juzhen.framework.app.log;

import android.util.Log;

import java.util.Hashtable;

class FileLog {

	public static final String TAG = "FileLog";

	private static Hashtable<String, Logger> loggerMap = LogManager.getInstance().getTagLoggerMap();

	public static void debug(String tag, String msg) {
		Logger logger = loggerMap.get(tag);
		if (logger == null) {
			Log.i(TAG, msg);
			return;
		}
		logger.debug(tag, msg);
	}

	public static void debug(String tag, String msg, Throwable errorMsg) {
		Logger logger = loggerMap.get(tag);
		if (logger == null) {
			Log.i(TAG, msg);
			return;
		}
		logger.debug(tag, msg, errorMsg);
	}

	public static void info(String tag, String msg) {
		Logger logger = loggerMap.get(tag);
		if (logger == null) {
			Log.i(TAG, msg);
			return;
		}
		logger.info(tag, msg);
	}

	public static void info(String tag, String msg, Throwable errorMsg) {
		Logger logger = loggerMap.get(tag);
		if (logger == null) {
			Log.i(TAG, msg);
			return;
		}
		logger.info(tag, msg, errorMsg);
	}

	public static void warn(String tag, String msg) {
		Logger logger = loggerMap.get(tag);
		if (logger == null) {
			Log.i(TAG, msg);
			return;
		}
		logger.warn(tag, msg);
	}

	public static void warn(String tag, String msg, Throwable errorMsg) {
		Logger logger = loggerMap.get(tag);
		if (logger == null) {
			Log.i(TAG, msg);
			return;
		}
		logger.warn(tag, msg, errorMsg);
	}

	public static void error(String tag, String msg) {
		Logger logger = loggerMap.get(tag);
		if (logger == null) {
			Log.i(TAG, msg);
			return;
		}
		logger.error(tag, msg);
	}

	public static void error(String tag, String msg, Throwable errorMsg) {
		Logger logger = loggerMap.get(tag);
		if (logger == null) {
			Log.i(TAG, msg);
			return;
		}
		logger.error(tag, msg, errorMsg);
	}

}
