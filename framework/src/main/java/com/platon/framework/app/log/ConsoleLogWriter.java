package com.platon.framework.app.log;

public class ConsoleLogWriter implements LogWriter {

	private static final int MAX_LOG_LENGTH = 3500;

	private static ConsoleLogWriter instance = new ConsoleLogWriter();

	private ConsoleLogWriter() {
	}

	static ConsoleLogWriter getInstance() {
		return instance;
	}

	public void writeLog(Level level, String tag, String message) {
		fixedLog(level, tag, message);
	}

	// 当log过长时，会分多次打印,避免出现显示日志不全的现象
	private void fixedLog(Level level, String tag, String message) {
		if(message == null) {
			doWriteLog(level, tag, "null");
			return;
		}
		final int len = message.length();
		if (len > MAX_LOG_LENGTH) {
			int partsSize = len / MAX_LOG_LENGTH + 1;
			for (int i = 0, part = 1; i < len; i += MAX_LOG_LENGTH, part ++) {
				if (i + MAX_LOG_LENGTH < len) {
					doWriteLog(level, tag, "[" + part + "/" + partsSize + "] " + message.substring(i, i + MAX_LOG_LENGTH));
				} else {
					doWriteLog(level, tag, "[" + part + "/" + partsSize + "] " + message.substring(i, len));
				}
			}
		} else {
			doWriteLog(level, tag, message);
		}
	}

	private void doWriteLog(Level level, String tag, String message) {
		// 调用android系统的自带Log写日志
		int levelValue = level.levelValue;
		switch (levelValue) {
			case Level.DEBUG:
				android.util.Log.d(tag, message);
				break;
			case Level.INFO:
				android.util.Log.i(tag, message);
				break;
			case Level.WARN:
				android.util.Log.w(tag, message);
				break;
			case Level.ERROR:
				android.util.Log.e(tag, message);
				break;
		}
	}

}
