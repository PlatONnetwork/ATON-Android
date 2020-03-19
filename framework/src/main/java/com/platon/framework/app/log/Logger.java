package com.platon.framework.app.log;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class Logger {
    private Level logLevel = Level.DEBUG_OBJ;
    private Level checkLevel = Level.DEBUG_OBJ;
    private LogWriter logWriter;

    public static Logger getLogger(String name) {
        return LogManager.getInstance().getLogger(name);
    }

    Logger() {

    }

    public String getLogFilePath() {
        if (this.logWriter instanceof FileLogWriter) {
            return ((FileLogWriter) logWriter).getLogFilePath();
        }
        return null;
    }

    public Logger getLogger(Class<?> clazz) {
        return getLogger(clazz.getName());
    }

    void setLogLevel(Level logLevel) {
        if (logLevel != null) {
            this.logLevel = logLevel;
        }
        // 写日志时检查用的Level，应该是LogManager的Level和Logger自身Level中大的那个
        if (this.logLevel.levelValue > LogManager.getInstance().logLevel.levelValue) {
            this.checkLevel = this.logLevel;
        } else {
            this.checkLevel = LogManager.getInstance().logLevel;
        }
    }

    void setLogWriter(LogWriter logWriter) {
        this.logWriter = logWriter;
    }

    private void log(Level level, String tag, String message) {
        if (level == null) {
            throw new IllegalArgumentException("The level must not be null.");
        }

        if (level.levelValue >= checkLevel.levelValue) {
            logWriter.writeLog(level, tag, message);
        }
    }

    public void debug(String tag, String message) {
        log(Level.DEBUG_OBJ, tag, message);
    }

    public void debug(String tag, String message, Throwable tr) {
        log(Level.DEBUG_OBJ, tag, message + '\n' + getStackTraceString(tr));
    }

    public void info(String tag, String message) {
        log(Level.INFO_OBJ, tag, message);
    }

    public void info(String tag, String message, Throwable tr) {
        log(Level.INFO_OBJ, tag, message + '\n' + getStackTraceString(tr));
    }

    public void warn(String tag, String message) {
        log(Level.WARN_OBJ, tag, message);
    }

    public void warn(String tag, String message, Throwable tr) {
        log(Level.WARN_OBJ, tag, message + '\n' + getStackTraceString(tr));
    }

    public void error(String tag, String message) {
        log(Level.ERROR_OBJ, tag, message);
    }

    public void error(String tag, String message, Throwable tr) {
        log(Level.ERROR_OBJ, tag, message + '\n' + getStackTraceString(tr));
    }

    public void error(String TAG, Throwable throwable) {
        log(Level.ERROR_OBJ, TAG, throwable.getMessage() + '\n' + getStackTraceString(throwable));
    }

    public boolean isDebugEnabled() {
        return checkLevel.levelValue == Level.DEBUG;
    }

    private static String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "";
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        return sw.toString();
    }

}
