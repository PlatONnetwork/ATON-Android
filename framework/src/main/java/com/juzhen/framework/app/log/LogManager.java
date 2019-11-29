package com.juzhen.framework.app.log;

import android.content.Context;
import android.util.Xml;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class LogManager {
	private static final String TAG = "LogManager";
	private static LogManager instance = new LogManager();
	private static final boolean DEBUG = false;

	// 系统默认日志
	static Logger systemLog;

	private Hashtable<String, Logger> loggers;

	private Hashtable<String, Logger> tagLoggerMap;

	private Hashtable<String, String[]> addFileLogTagMap;

	Level logLevel;

	private HashMap<String, String> nameParams;
	
	private LogManager() {
		loggers = new Hashtable<String, Logger>();
		tagLoggerMap = new Hashtable<String, Logger>();
		addFileLogTagMap = new Hashtable<String, String[]>();
		logLevel = Level.DEBUG_OBJ;

		// 初始化一个系统默认Logger
		Logger logger = new Logger();
		LogWriter logWriter = LogWriterBuilder.getLogWriter("_system_log", "console", null);
		logger.setLogWriter(logWriter);
		systemLog = logger;
	}

	Hashtable<String, Logger> getTagLoggerMap() {
		return tagLoggerMap;
	}

	public static LogManager getInstance() {
		return instance;
	}

	public void setNameParams(HashMap<String, String> params) {
		this.nameParams = params;
	}

	/**
	 * 初始化所有日志 日志定义文件必须放在assets目录下
	 * 
	 * @param context Context，执行的android系统上下文
	 * @param logConfigFile String，日志定义文件的assets路径
	 */
	public void init(Context context, String logConfigFile) {
		logD(TAG, "init log...");

		this.loggers.clear();
		this.tagLoggerMap.clear();
		this.addFileLogTagMap.clear();

		// this.context = context;

		InputStream is = null;
		try {
			is = context.getAssets().open(logConfigFile);
			Xml.parse(is, Xml.Encoding.UTF_8, new LogXMLParseHandler());
		} catch (Exception e) {
			logE(TAG, "Read log config File failed!", e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {

				}
			}
		}
	}

	private Logger addLogger(String name, String type, String level, Map<String, String> params) {
		Logger logger = new Logger();
		LogWriter logWriter = LogWriterBuilder.getLogWriter(name, type, params);
		logger.setLogWriter(logWriter);
		logger.setLogLevel(Level.getLevel(level));
		loggers.put(name, logger);
		logD(TAG, "add a logger:[" + name + "," + logger + "]");
		return logger;
	}

	public Logger getLogger(String name) {
		Logger logger = loggers.get(name);
		logD(TAG, "get a logger:[" + name + "," + logger + "]");
		return logger == null ? systemLog : logger;
	}

	// 动态添加一个文件log
	public Logger addFileLogger(String logName, String level, String[] tags, FileLogParams params) {
		logD(TAG, "addFileLogger(" + logName + ")");
		Logger logger = loggers.get(logName);
		if (logger != null) {
			logD(TAG, "the logger named '" + logName + "' is allreay exist ");
			return logger;
		}

		logger = addLogger(logName, "file", level, params.toStringMapParams());

		// 把这些标签分别映射到这个logger上
		for (String tag : tags) {
			tagLoggerMap.put(tag, logger);
		}
		addFileLogTagMap.put(logName, tags);
		return logger;
	}

	/**
	 * 移除一个appLog
	 * 
	 * @param appId
	 */
	public void removeFileLogger(String logName) {
		logD(TAG, "removeFileLogger(" + logName + ")");
		Logger logger = loggers.remove(logName);
		if (logger == null) {
			logD(TAG, "the logger named '" + logName + "' is not exist ");
			return;
		}
		String[] tags = addFileLogTagMap.remove(logName);
		// 移除tag对应的logger
		for (String temTag : tags) {
			tagLoggerMap.remove(temTag);
		}
	}

	private class LogXMLParseHandler extends DefaultHandler {
		// 各种临时参数
		private HashMap<String, String> params = new HashMap<String, String>();
		private ArrayList<String> tagList = new ArrayList<String>();
		private String name;
		private String type;
		private String level;

		/**
		 * 用于缓存信息
		 */
		private StringBuffer buffer = new StringBuffer();

		@Override
		public void characters(char[] cha, int start, int length) throws SAXException {
			buffer.append(cha, start, length);
			super.characters(cha, start, length);
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			// 根据节点解析XML
			String content = buffer.toString().trim();
			if ("threshold".equals(localName)) {
				// 设置全局的日志等级
				logD(TAG, "threshold log level:[" + content + "]");
				Level logLevel = Level.getLevel(content);
				if (logLevel != null) {
					LogManager.this.logLevel = logLevel;
					systemLog.setLogLevel(logLevel);
				}
			} else if ("logtype".equals(localName)) {
				logD(TAG, "logtype:[" + content + "]");
				// 这里设置日志记录的范围，all 文件和控制台 ， console控制台 ,file 文件
				if (content.equals("all")) {
					Log.enable_console_log = true;
					Log.enable_file_log = true;
				} else if (content.equals("console")) {
					Log.enable_console_log = true;
					Log.enable_file_log = false;
				} else if (content.equals("file")) {
					Log.enable_console_log = false;
					Log.enable_file_log = true;
				} else if (content.equals("none")) {
					Log.enable_console_log = false;
					Log.enable_file_log = false;
				}
			} else if ("logger".equals(localName)) {
				// logger定义结束，添加一个log吧
				Logger logger = addLogger(name, type, level, params);
				name = null;
				type = null;
				level = null;
				params.clear();
				for (String tag : tagList) {
					tagLoggerMap.put(tag, logger);
				}
				tagList.clear();
			} else if ("name".equals(localName)) {
				this.name = content;
			} else if ("type".equals(localName)) {
				this.type = content;
			} else if ("level".equals(localName)) {
				this.level = content;
			} else if ("logspace".equals(localName)) {
				params.put("logspace", content);
			} else if ("path".equals(localName)) {
				params.put("path", content);
			} else if ("file".equals(localName)) {
				int s1 = content.indexOf("[");
				int s2 = content.indexOf("]");
				if (s1 > 0 && s2 > s1) {
					String p = content.substring(s1 + 1, s2);
					String v = "";
					if (nameParams != null) {
						v = nameParams.get(p);
						if (v == null)
							v = "";
					}
					String f = content.replace("[" + p + "]", v);
					params.put("file", f);
				} else {
					params.put("file", content);
				}
			} else if ("encode".equals(localName)) {
				params.put("encode", content);
			} else if ("count".equals(localName)) {
				params.put("count", content);
			} else if ("size".equals(localName)) {
				params.put("size", content);
			} else if ("writetag".equals(localName)) {
				params.put("writetag", content);
			} else if ("tag".equals(localName)) {
				tagList.add(content);
			}
			buffer.setLength(0);
		}
	}

	public static class FileLogParams {
		public String path;
		public String file;
		public String encode;
		public int size;
		public int count;
		public boolean writetag;
		public String logspace; // inner或者external

		Map<String, String> toStringMapParams() {
			Map<String, String> params = new HashMap<String, String>();
			params.put("path", path);
			params.put("file", file);
			params.put("encode", encode);
			params.put("count", String.valueOf(count));
			params.put("size", String.valueOf(size));
			params.put("logspace", logspace);
			params.put("writetag", String.valueOf(writetag));
			return params;
		}

	}
	
	private void logD(String tag, String msg) {
		if (DEBUG) {
			android.util.Log.d(tag, msg);
		}
	}
	
	private void logE(String tag, String msg, Throwable tr) {
		if (DEBUG) {
			android.util.Log.e(tag, msg, tr);
		}
	}

}
