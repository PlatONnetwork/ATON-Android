package com.juzhen.framework.app.config;

public class PersistenerFactory {
	public static ConfigModulePersistenter getPersistenter(int type) {
		if (type == Config.MOUDLE_TYPE_XML) {
			return new XMLPersistenter();
		} else if (type == Config.MOUDLE_TYPE_PREF) {
			return new PREFPersistenter();
		}
		throw new RuntimeException("Not supported ConfigMoudle type[" + type + "]!");
	}

}
