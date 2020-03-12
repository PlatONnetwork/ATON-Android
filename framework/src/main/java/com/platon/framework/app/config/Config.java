package com.platon.framework.app.config;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class Config {
	protected static final String TAG = "MIBRIDGE.CONFIG";
	private static final String ENCODE = "utf-8";
	private static final boolean DEBUG = false;

	public static final int MOUDLE_TYPE_XML = 1;// XML类型配置模块
	public static final int MOUDLE_TYPE_PREF = 2;// SharedPreferences类型配置模块

	private static final Config instance = new Config();

	private Context context;
	private HashMap<String, ConfigModule> moduleMap;

	private Config() {
		moduleMap = new HashMap<String, ConfigModule>();
	}

	public static Config getInstance() {
		return instance;
	}

	Context getContext() {
		return this.context;
	}

	public void addMoudle(String name, int type, String persistence) {
		ConfigModulePersistenter persistenter = PersistenerFactory.getPersistenter(type);

		ConfigModule moudle = new ConfigModule(name, type, persistence);
		moudle.setPersistenter(persistenter);
		persistenter.setConfigMoudle(moudle);
		persistenter.load(persistence);
		moduleMap.put(name, moudle);
	}

	public void removeMoudle(String name) {
		moduleMap.remove(name);
	}

	public ConfigModule getMoudle(String name) {
		return moduleMap.get(name);
	}

	/**
	 * 从配置模块定义文件载入里面定义的所有配置模块 这个配置文件必须放在项目的assets目录
	 * 
	 * @param context
	 *            Context,运行上下文
	 * @param moudlesDefineFilePath
	 *            String,配置模块定义文件在assets下的路径
	 */
	public void init(Context context, String moudlesDefineFilePath) {
		if (DEBUG) {
			Log.i(TAG, "loading config moudles...");
		}

		this.context = context;
		this.moduleMap.clear();// 初始化的时候，清空下吧

		InputStream is = null;
		try {
			is = context.getAssets().open(moudlesDefineFilePath);
			this.parseConfigFromStream(is);
		} catch (XmlPullParserException e) {
			Log.w(TAG, "Parse Config File failed!", e);
		} catch (IOException e) {
			Log.w(TAG, "Read Config File failed!", e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {

				}
			}
		}

	}

	private void parseConfigFromStream(InputStream is) throws XmlPullParserException, IOException {
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();
		xpp.setInput(is, ENCODE);
		String nodeName = "";
		while (xpp.getEventType() != XmlResourceParser.END_DOCUMENT) {
			if (xpp.getEventType() == XmlResourceParser.START_TAG) {
				nodeName = xpp.getName();
				if (DEBUG) {
					Log.d(TAG, "find a Tag:" + nodeName);
				}
				if (nodeName.equals("module")) {
					String name = xpp.getAttributeValue(null, "name");
					String typeStr = xpp.getAttributeValue(null, "type");
					String persistence = xpp.getAttributeValue(null, "persistence");
					if (DEBUG) {
						Log.d(TAG, "find a module:[" + name + "," + typeStr + "," + persistence + "]");
					}
					try {
						// load moudle
						int type = Integer.parseInt(typeStr);
						this.addMoudle(name, type, persistence);
					} catch (Exception e) {
						Log.d(TAG, "load config module:[" + name + "] failed!");
						Log.e(TAG, "parseConfigFromStream", e);
					}
				}
			}
			xpp.next();
		}
	}

}
