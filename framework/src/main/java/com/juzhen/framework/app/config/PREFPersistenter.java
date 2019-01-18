package com.juzhen.framework.app.config;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * PREFL配置模块的持久化处理类，配置项保存在Android提供的SharedPreferences中。
 * XML方式的配置模块支持配置项的读取/更新和移除。
 */
class PREFPersistenter implements ConfigModulePersistenter {

	@SuppressWarnings("unused")
	private ConfigModule moudle;

	private SharedPreferences pref;

	public PREFPersistenter() {

	}

	@Override
	public void setConfigMoudle(ConfigModule moudle) {
		this.moudle = moudle;
	}

	@SuppressLint({ "WorldReadableFiles", "WorldWriteableFiles" })
	@Override
	public void load(String persistence) {
		Context context = Config.getInstance().getContext();
		pref = context.getSharedPreferences(persistence, Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
	}

	@Override
	public void removeItem(String name) {
		pref.edit().remove(name).commit();
	}

	@Override
	public void setStringItem(String name, String value) {
		pref.edit().putString(name, value).commit();
	}

	@Override
	public void setBooleanItem(String name, boolean value) {
		pref.edit().putBoolean(name, value).commit();
	}

	@Override
	public void setLongItem(String name, long value) {
		pref.edit().putLong(name, value).commit();
	}

	@Override
	public void setIntItem(String name, int value) {
		pref.edit().putInt(name, value).commit();
	}

	@Override
	public String getStringItem(String name, String defaultValue) {
		return pref.getString(name, defaultValue);
	}

	@Override
	public int getIntItem(String name, int defaultValue) {
		return pref.getInt(name, defaultValue);
	}

	@Override
	public long getLongItem(String name, long defaultValue) {
		return pref.getLong(name, defaultValue);
	}

	@Override
	public boolean getBooleanItem(String name, boolean defaultValue) {
		return pref.getBoolean(name, defaultValue);
	}

}
