package com.platon.framework.app.log;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class FileLogWriterFormatter extends Formatter {
	@SuppressLint("SimpleDateFormat")
	private static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	public FileLogWriterFormatter() {
		super();
	}

	@Override
	public String format(LogRecord r) {
		StringBuilder sb = new StringBuilder();
		sb.append(df.format(new Date(r.getMillis())));
		sb.append(" <-> ");
		sb.append(formatMessage(r));
		sb.append("\r\n");
		return sb.toString();
	}
}
