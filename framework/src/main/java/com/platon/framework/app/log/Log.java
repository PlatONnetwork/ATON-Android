package com.platon.framework.app.log;

import java.util.Hashtable;

public class Log {
    private static Logger systemLog = LogManager.systemLog;
    private static Hashtable<String, Logger> loggerMap = LogManager.getInstance().getTagLoggerMap();
    public static boolean enable_console_log = true; // 控制系统log是否可用,该值可由xml来配置
    public static boolean enable_file_log = false;    // 控制文件log是否可用

    public static final String FILE_COMMON_LOG = "YAP";

    public static void debug(String tag, String message) {
        if (enable_file_log) {
            Logger logger = loggerMap.get(tag);
            if (logger != null) {
                logger.debug(tag, message);
            }
        }
        if (enable_console_log) {
            systemLog.debug(tag, message);
        }
    }

    public static void debug(String tag, String message, boolean saveToFile) {
        if (saveToFile) {
            Logger logger = loggerMap.get(FILE_COMMON_LOG);
            if (logger != null) {
                logger.debug(tag, message);
            }
        }
        if (enable_console_log) {
            systemLog.debug(tag, message);
        }
    }

    public static void debug(String tag, String message, Throwable tr) {
        if (enable_file_log) {
            Logger logger = loggerMap.get(tag);
            if (logger != null) {
                logger.debug(tag, message, tr);
            }
        }
        if (enable_console_log) {
            systemLog.debug(tag, message, tr);
        }
    }

    public static void info(String tag, String message) {
        if (enable_file_log) {
            Logger logger = loggerMap.get(tag);
            if (logger != null) {
                logger.info(tag, message);
            }
        }
        if (enable_console_log) {
            systemLog.info(tag, message);
        }
    }

    public static void info(String tag, String message, boolean saveToFile) {
        if (saveToFile) {
            Logger logger = loggerMap.get(FILE_COMMON_LOG);
            if (logger != null) {
                logger.info(tag, message);
            }
        }
        if (enable_console_log) {
            systemLog.info(tag, message);
        }
    }

    public static void info(String tag, String message, Throwable tr) {
        if (enable_file_log) {
            Logger logger = loggerMap.get(tag);
            if (logger != null) {
                logger.info(tag, message, tr);
            }
        }
        if (enable_console_log) {
            systemLog.info(tag, message, tr);
        }
    }

    public static void warn(String tag, String message) {
        if (enable_file_log) {
            Logger logger = loggerMap.get(tag);
            if (logger != null) {
                logger.warn(tag, message);
            }
        }
        if (enable_console_log) {
            systemLog.warn(tag, message);
        }
    }

    public static void warn(String tag, String message, boolean saveToFile) {
        if (saveToFile) {
            Logger logger = loggerMap.get(FILE_COMMON_LOG);
            if (logger != null) {
                logger.warn(tag, message);
            }
        }
        if (enable_console_log) {
            systemLog.warn(tag, message);
        }
    }

    public static void warn(String tag, String message, Throwable tr) {
        systemLog.warn(tag, message, tr);
        if (enable_file_log) {
            Logger logger = loggerMap.get(tag);
            if (logger != null) {
                logger.warn(tag, message, tr);
            }
        }
        if (enable_console_log) {
            systemLog.warn(tag, message, tr);
        }
    }

    public static void error(String tag, String message) {
        if (enable_file_log) {
            Logger logger = loggerMap.get(tag);
            if (logger != null) {
                logger.error(tag, message);
            }
        }
        if (enable_console_log) {
            systemLog.error(tag, message);
        }
    }

    public static void error(String tag, String message, boolean saveToFile) {
        if (saveToFile) {
            Logger logger = loggerMap.get(FILE_COMMON_LOG);
            if (logger != null) {
                logger.error(tag, message);
            }
        }
        if (enable_console_log) {
            systemLog.error(tag, message);
        }
    }

    public static void error(String tag, Throwable throwable) {
        if (enable_file_log) {
            Logger logger = loggerMap.get(tag);
            if (logger != null) {
                logger.error(tag, throwable);
            }
        }
        if (enable_console_log) {
            systemLog.error(tag, throwable);
        }
    }

    public static void error(String tag, String message, Throwable throwable) {
        if (enable_file_log) {
            Logger logger = loggerMap.get(tag);
            if (logger != null) {
                logger.error(tag, message, throwable);
            }
        }
        if (enable_console_log) {
            systemLog.error(tag, message, throwable);
        }
    }

    public static boolean isDebugEnabled() {
        return systemLog.isDebugEnabled();
    }

    public static String getCurrentLogFilePath() {
        Logger logger = loggerMap.get(FILE_COMMON_LOG);
        return logger.getLogFilePath();
    }

}
