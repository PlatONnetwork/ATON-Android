package com.juzhen.framework.app.log;

import java.util.Map;

public class LogWriterBuilder {
	public static LogWriter getLogWriter(String name, String type, Map<String, String> params) {
		if ("console".equals(type)) {
			return ConsoleLogWriter.getInstance();
		} else if ("file".equals(type)) {
			return new FileLogWriter(name, params);
		} else {
			throw new RuntimeException("not supported log type:[" + type + "]");
		}
	}

}
