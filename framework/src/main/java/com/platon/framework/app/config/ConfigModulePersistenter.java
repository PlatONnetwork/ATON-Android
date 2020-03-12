package com.platon.framework.app.config;

interface ConfigModulePersistenter {
	void setConfigMoudle(ConfigModule moudle);

	void load(String persistence);

	void removeItem(String name);

	void setStringItem(String name, String value);

	void setBooleanItem(String name, boolean value);

	void setLongItem(String name, long value);

	void setIntItem(String name, int value);

	String getStringItem(String name, String defaultValue);

	int getIntItem(String name, int defaultValue);

	long getLongItem(String name, long defaultValue);

	boolean getBooleanItem(String name, boolean defaultValue);

}
