package com.platon.framework.app.log;

public class Level {
	public static final int ERROR = 8;

	public static final int WARN = 4;

	public static final int INFO = 2;

	public static final int DEBUG = 1;

	public static final String ERROR_STRING = "ERROR";

	public static final String WARN_STRING = "WARN";

	public static final String INFO_STRING = "INFO";

	public static final String DEBUG_STRING = "DEBUG";

	static final Level ERROR_OBJ = new Level(ERROR, ERROR_STRING);

	static final Level WARN_OBJ = new Level(WARN, WARN_STRING);

	static final Level INFO_OBJ = new Level(INFO, INFO_STRING);

	static final Level DEBUG_OBJ = new Level(DEBUG, DEBUG_STRING);

	int levelValue;

	private String levelString = "";

	Level(int level) {
		this.levelValue = level;
		switch (level) {
		case DEBUG:
			this.levelString = DEBUG_STRING;
			break;
		case INFO:
			this.levelString = INFO_STRING;
			break;
		case WARN:
			this.levelString = WARN_STRING;
			break;
		case ERROR:
			this.levelString = ERROR_STRING;
			break;
		}
	}

	Level(int level, String levelString) {
		this.levelValue = level;
		this.levelString = levelString;
	}

	static Level getLevel(String levelStr) {
		if (DEBUG_STRING.equalsIgnoreCase(levelStr)) {
			return DEBUG_OBJ;
		}
		if (INFO_STRING.equalsIgnoreCase(levelStr)) {
			return INFO_OBJ;
		}
		if (WARN_STRING.equalsIgnoreCase(levelStr)) {
			return WARN_OBJ;
		}
		if (ERROR_STRING.equalsIgnoreCase(levelStr)) {
			return ERROR_OBJ;
		}
		return null;
	}

	public int toInt() {
		return levelValue;
	}

	public String toString() {
		return levelString;
	}

	public boolean equals(Object obj) {
		boolean equals = false;

		if (obj instanceof Level) {
			Level compareLevel = (Level) obj;

			if (levelValue == compareLevel.levelValue) {
				equals = true;
			}
		}

		return equals;
	}

	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + levelValue;
		return hash;
	}

}
