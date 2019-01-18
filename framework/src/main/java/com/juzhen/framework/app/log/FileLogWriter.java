package com.juzhen.framework.app.log;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;

public class FileLogWriter implements LogWriter {
	
	private final static String INNER_FILEPATH = "data/data/";
	private static String EXTERNAL_FILEPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/yap/log/";

	// 封装sdk里面的java.util.logging日志工具，利用其来写文件
	private java.util.logging.Logger realLogger;

	private String logFilePath;

	private boolean writetag = false;

	FileLogWriter(String name, Map<String, String> params) {
		try {
			realLogger = java.util.logging.Logger.getLogger(name);
			realLogger.setLevel(java.util.logging.Level.ALL);
			realLogger.setUseParentHandlers(false);
			String path = params.get("path");
			String file = params.get("file");
			String encode = params.get("encode");
			String spaceType = params.get("logspace");
			int count = Integer.parseInt(params.get("count"));
			int size = Integer.parseInt(params.get("size"));
			logFilePath = buildLogFilepath(spaceType, path, file);
			FileHandler fileHandler = new FileHandler(logFilePath, size, count, true);
			fileHandler.setEncoding(encode);
			fileHandler.setFormatter(new FileLogWriterFormatter());
			realLogger.addHandler(fileHandler);
			String writetagStr = params.get("writetag");
			if (writetagStr != null) {
				this.writetag = Boolean.getBoolean(writetagStr);
			} else {
				this.writetag = true;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private String buildLogFilepath(String spaceType, String path, String file) {
		String logFilepath = spaceType.equals("inner") ? INNER_FILEPATH : EXTERNAL_FILEPATH;
		if (path.charAt(0) == '/') {
			path = path.substring(1);
		}
		if (!path.endsWith("/")) {
			path = path + "/";
		}
		logFilepath += path;
		// 检查目录是否存在，不存在则需要创建
		File logPath = new File(logFilepath);
		if (!logPath.exists()) {
			logPath.mkdirs();
		}

		logFilepath += file;
		return logFilepath;
	}

	String getLogFilePath() {
		return this.logFilePath;
	}

	public void writeLog(Level level, String tag, String message) {
		// 调用封装的java.util.loggin的Logger写日志
		int levelValue = level.levelValue;
		java.util.logging.Level realLoggerLevel = java.util.logging.Level.ALL;
		switch (levelValue) {
		case Level.DEBUG:
			realLoggerLevel = java.util.logging.Level.FINEST;
			break;
		case Level.INFO:
			realLoggerLevel = java.util.logging.Level.INFO;
			break;
		case Level.WARN:
			realLoggerLevel = java.util.logging.Level.WARNING;
			break;
		case Level.ERROR:
			realLoggerLevel = java.util.logging.Level.SEVERE;
			break;
		}
		LogRecord record = null;
		if (writetag) {
			record = new LogRecord(realLoggerLevel, tag + " <->  : " + message);
		} else {
			record = new LogRecord(realLoggerLevel, message);
		}
		this.realLogger.log(record);
	}

}
