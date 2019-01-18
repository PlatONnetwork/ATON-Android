package com.juzhen.framework.app.config;

import android.content.Context;
import android.content.SharedPreferences;


public class Settings {
	
	private static Settings sInstance;
	private SharedPreferences mPreferences; // 文件存储
	
	public static final String KEY_USERTOKEN_FOR_DECRYPTION = "usertoken_for_decryption";

	private Settings() {
	}

	public static Settings getInstance() {
		if (sInstance == null) {
			sInstance = new Settings();
		}
		return sInstance;
	}

	public void init(Context ctx) {
		mPreferences = ctx.getSharedPreferences("com.pagoda.appcore", Context.MODE_PRIVATE);
	}
	
	public void put(String key, String value) {
		mPreferences.edit().putString(key, value).commit();
	}
	public String get(String key, String defValue) {
		return mPreferences.getString(key, defValue);
	}
}
