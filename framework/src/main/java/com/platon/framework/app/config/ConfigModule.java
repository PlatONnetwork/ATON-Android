package com.platon.framework.app.config;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class ConfigModule {
	protected String name;
	protected int type;
	protected String persistence;
	protected ConfigModulePersistenter persistener;

	/**
	 * 优先缓存区，如果一个配置项在优先缓存区有值，则应该直接从这个区域获取配置值。 这时候持久化存储中的配置值将被忽略。
	 */
	protected Map<String, String> priorCache = new HashMap<String, String>();

	protected ConfigModule(String name, int type, String persistence) {
		this.name = name;
		this.type = type;
		this.persistence = persistence;
	}

	public void addPriorConfigItem(String key, String value) {
		this.priorCache.put(key, value);
	}

	public void addManyPriorConfigItem(Map<String, String> items) {
		this.priorCache.putAll(items);
	}

	public void clearPriorCache() {
		this.priorCache.clear();
	}

	void setPersistenter(ConfigModulePersistenter persistener) {
		this.persistener = persistener;
	}

	public String getName() {
		return name;
	}

	public int getType() {
		return type;
	}

	public String getPersistence() {
		return persistence;
	}

	public void removeItem(String name) {
		persistener.removeItem(name);
	}

	public void setStringItem(String name, String value) {
		persistener.setStringItem(name, value);
	}

	public void setBooleanItem(String name, boolean value) {
		persistener.setBooleanItem(name, value);
	}

	public void setLongItem(String name, long value) {
		persistener.setLongItem(name, value);
	}

	public void setIntItem(String name, int value) {
		persistener.setIntItem(name, value);
	}

	public String getStringItem(String name, String defaultValue) {
		if (this.priorCache.containsKey(name)) {
			return this.priorCache.get(name);
		}
		return persistener.getStringItem(name, defaultValue);
	}

	public boolean getBooleanItem(String name, boolean defaultValue) {
		if (this.priorCache.containsKey(name)) {
			return Boolean.parseBoolean(this.priorCache.get(name));
		}
		return persistener.getBooleanItem(name, defaultValue);
	}

	public int getIntItem(String name, int defaultValue) {
		if (this.priorCache.containsKey(name)) {
			try {
				return Integer.parseInt(this.priorCache.get(name));
			} catch (Exception e) {
				Log.e(Config.TAG, "", e);
			}
		}
		return persistener.getIntItem(name, defaultValue);
	}

	public long getLongItem(String name, long defaultValue) {
		if (this.priorCache.containsKey(name)) {
			try {
				return Long.parseLong(this.priorCache.get(name));
			} catch (Exception e) {
				Log.e(Config.TAG, "", e);
			}
		}
		return persistener.getLongItem(name, defaultValue);
	}

}
