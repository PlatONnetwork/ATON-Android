package com.platon.framework.app.log;

public interface LogWriter {
	void writeLog(Level level, String tag, String message);
}
