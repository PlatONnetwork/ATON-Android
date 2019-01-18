package com.juzhen.framework.app.exception;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;

import com.juzhen.framework.app.activity.ActivityManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;

public class CustomerException implements UncaughtExceptionHandler {

	private static final CustomerException EXCEPTION_CONTROL = new CustomerException();

	@SuppressWarnings("unused")
	private Context mContext;

	private UncaughtExceptionHandler defaultExceptionHandler = null;
	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");

	private CustomerException() {
	}

	public static CustomerException getExceptionControl() {
		return EXCEPTION_CONTROL;
	}

	public void init(Context context) {
		this.mContext = context;
		defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && defaultExceptionHandler != null) {

			ex.printStackTrace();
			String lastContentStr = "";
			File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/ZhdUncaughtExceptionLog.txt");

			FileInputStream fis = null;

			FileOutputStream fos = null;
			PrintStream pWriter = null;
			try {
				if (!file.exists()) {
					file.createNewFile();
				}
				fis = new FileInputStream(file);
				byte[] lastContent = new byte[fis.available()];
				fis.read(lastContent, 0, fis.available());
				fis.close();
				lastContentStr = new String(lastContent);

				fos = new FileOutputStream(file);
				pWriter = new PrintStream(fos);
				pWriter.print(sdf.format(System.currentTimeMillis()) + " : ");
				ex.printStackTrace(pWriter);
				pWriter.append("\n\n");
				pWriter.append(lastContentStr);
				fos.flush();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (fos != null)
						fos.close();
					if (pWriter != null)
						pWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		ActivityManager.getInstance().finishAll();
		android.os.Process.killProcess(android.os.Process.myPid());

	}

	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return true;
		} else {
			return false;
		}
	}
}
