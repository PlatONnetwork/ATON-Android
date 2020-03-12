package com.platon.framework.app.config;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * XML配置模块的持久化处理类，配置项保存在XML文件中。 XML方式的配置模块是只读的，只支持配置项的读取，不支持配置项的更新和移除。
 */
class XMLPersistenter implements ConfigModulePersistenter {
	private static final String TAG = "MIBRIDGE.CONFIG";
	private static final String SPLIT = ".";
	private static final boolean DEBUG = false;

	private HashMap<String, String> map = new HashMap<String, String>();
	private ConfigModule module;

	public XMLPersistenter() {

	}

	@Override
	public void setConfigMoudle(ConfigModule module) {
		this.module = module;
	}

	@Override
	public void load(String persistence) {
		if (DEBUG) {
			Log.d(TAG, "load a XML Config Moudle:" + persistence);
		}
		InputStream is = null;
		String filename = null;
		try {
			if (persistence.startsWith("file:")) {
				// file
				filename = persistence.substring(5);
				is = new FileInputStream(filename);
			} else if (persistence.startsWith("jar:")) {
				// jar
				filename = persistence.substring(4);
				is = this.getClass().getResourceAsStream(filename);
			} else if (persistence.startsWith("raw:")) {
				// raw
				Context context = Config.getInstance().getContext();
				filename = persistence.substring(4);
				is = context.getAssets().open(filename);
			} else {
				// 没有指定，就认为是raw
				Context context = Config.getInstance().getContext();
				filename = persistence;
				is = context.getAssets().open(filename);
			}
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(is, null);

			String nodeName = "";// 当前节点名称
			String nodePath = "";// 当前节点路径
			String[] nodeTree = new String[6];// 当前节点树结构
			int nodeTreeIdx = -1;// 当前在节点树的第几层

			int attrCount;
			String attrName, attrValue;

			while (xpp.getEventType() != XmlResourceParser.END_DOCUMENT) {
				if (xpp.getEventType() == XmlResourceParser.START_TAG) {
					nodeName = xpp.getName();
					if (DEBUG) {
						Log.d(TAG, "Tag start:" + nodeName);
					}
					nodeTreeIdx++;
					nodeTree[nodeTreeIdx] = nodeName;
					nodePath = this.getNodePath(nodeTree, nodeTreeIdx);
					if (nodeTreeIdx < 1) {
						// 需要检查root元素必须是ConfigMoudle，并且name属性和模块名称必须一致
						if (!"ConfigModule".equals(nodeName)) {
							throw new RuntimeException("Config XML file root element must be [ConfigMoudle],but now is:" + nodeName);
						} else {
							String moudleName = xpp.getAttributeValue(null, "name");
							if (!module.getName().equals(moudleName)) {
								throw new RuntimeException("Config Module name mismatch!Must be [" + module.name + "],but now is:" + moudleName);
							}
						}
					} else {
						// 非root节点
						// 读取元素的属性
						attrCount = xpp.getAttributeCount();
						for (int i = 0; i < attrCount; i++) {
							attrName = xpp.getAttributeName(i);
							attrValue = xpp.getAttributeValue(i);
							map.put(nodePath + SPLIT + attrName, attrValue);
							if (DEBUG) {
								Log.d(TAG, "put config item:[" + nodePath + SPLIT + attrName + "," + attrValue + "]");
							}
						}
					}
				} else if (xpp.getEventType() == XmlResourceParser.END_TAG) {
					nodeName = xpp.getName();
					if (DEBUG) {
						Log.d(TAG, "Tag end:" + nodeName);
					}
					nodeTreeIdx--;
					nodePath = this.getNodePath(nodeTree, nodeTreeIdx);
				} else if (xpp.getEventType() == XmlResourceParser.TEXT) {
					// 配置文件的root节点和其属性，全部忽略。只处理root之下的元素和其属性
					// white space，也忽略。
					if (!xpp.isWhitespace() && nodeTreeIdx >= 1) {
						map.put(nodePath, xpp.getText());
						if (DEBUG) {
							Log.d(TAG, "put config item:[" + nodePath + "," + xpp.getText() + "]");
						}
					}
				}
				xpp.next();
			}
		} catch (XmlPullParserException e) {
			Log.e(TAG, "Parse Module Config File failed!", e);
		} catch (IOException e) {
			Log.e(TAG, "Read Module Config File failed!", e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {

				}
			}
		}
	}

	private String getNodePath(String[] nodeTree, int nodeTreeIdx) {
		String nodePath = "";
		for (int i = 1; i <= nodeTreeIdx; i++) {
			if (i > 1) {
				nodePath += SPLIT;
			}
			nodePath += nodeTree[i];
		}
		return nodePath;
	}

	@Override
	public void removeItem(String name) {
		throw new RuntimeException("xml config do not support this operation!");
	}

	@Override
	public void setStringItem(String name, String value) {
		throw new RuntimeException("xml config do not support this operation!");
	}

	@Override
	public void setBooleanItem(String name, boolean value) {
		throw new RuntimeException("xml config do not support this operation!");
	}

	@Override
	public void setLongItem(String name, long value) {
		throw new RuntimeException("xml config do not support this operation!");
	}

	@Override
	public void setIntItem(String name, int value) {
		throw new RuntimeException("xml config do not support this operation!");
	}

	@Override
	public String getStringItem(String name, String defaultValue) {
		if (this.map.containsKey(name)) {
			return this.map.get(name);
		}
		return defaultValue;
	}

	@Override
	public int getIntItem(String name, int defaultValue) {
		if (this.map.containsKey(name)) {
			return Integer.parseInt(this.map.get(name));
		}
		return defaultValue;
	}

	@Override
	public long getLongItem(String name, long defaultValue) {
		if (this.map.containsKey(name)) {
			return Long.parseLong(this.map.get(name));
		}
		return defaultValue;
	}

	@Override
	public boolean getBooleanItem(String name, boolean defaultValue) {
		if (this.map.containsKey(name)) {
			return Boolean.parseBoolean(this.map.get(name));
		}
		return defaultValue;
	}

}
