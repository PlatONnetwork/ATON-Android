package com.juzhen.framework.util;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class MapUtils {
	private static final String TAG = "MapUtils";

	/**
	 * 读取map中key对应的键值，<br>
	 * 1.若无该键值则返回默认值""<br>
	 * 2.若存在该key，则将该键值强转成String类型（整型，浮点型均可）<br>
	 * @param map
	 * @param key
	 * @return
	 */
	public static String getString(Map<String, Object> map, String key) {
		return getString(map, key, "");
	}
	
	/**
	 * 读取map中key对应的键值，<br>
	 * 1.若无该键值则返回默认值defaultValue<br>
	 * 2.若存在该key，则将该键值强转成String类型（整型，浮点型均可）<br>
	 * @param map
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static String getString(Map<String, Object> map, String key, String defaultValue) {
		if(map == null) {
			return defaultValue;
		}
		Object o = map.get(key);
		if (o == null) {
			Log.w(TAG, "KeyNotFound, please check key <" + key + ">");
            return defaultValue;
        }
		try {
            return (String) o;
        } catch (ClassCastException e) {
            Log.w(TAG, "ClassCastException in getString(): at key=<" + key + ">, " + e.getMessage());
            return String.valueOf(o);
        }
	}
	
	/**
	 * 读取map中key对应的键值，<br>
	 * 1.若无该键值则返回默认值0<br>
	 * 2.若存在该key，则将该键值强转成int类型, 强转失败则返回默认值0<br>
	 * @param map
	 * @param key
	 * @return
	 */
	public static int getInt(Map<String, Object> map, String key) {
		return getInt(map, key, 0);
	}
	
	/**
	 * 读取map中key对应的键值，<br>
	 * 1.若无该键值则返回默认值defaultValue<br>
	 * 2.若存在该key，则将该键值强转成int类型，强转失败则返回默认值<br>
	 * @param map
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static int getInt(Map<String, Object> map, String key, int defaultValue) {
		if(map == null) {
			return defaultValue;
		}
		Object o = map.get(key);
		if (o == null) {
			Log.w(TAG, "KeyNotFound, please check key <" + key + ">");
            return defaultValue;
        }
		try {
            return (Integer) o;
        } catch (ClassCastException e) {
            Log.w(TAG, "ClassCastException in getInt(): at key=<" + key + ">, " + e.getMessage());
			try {
				return Integer.parseInt((String) o);
			} catch (RuntimeException e1) {
				return defaultValue;
			}
        }
	}
	
	/**
     * 读取map中key对应的键值，<br>
	 * 1.若无该键值则返回0.0<br>
	 * 2.若存在该key，则将该键值强转成double类型，强转失败则返回0.0<br>
     *
     * @param key a String
     * @return a double value
     */
    public static double getDouble(Map<String, Object> map, String key) {
        return getDouble(map, key, 0.0);
    }

    /**
     * 读取map中key对应的键值，<br>
	 * 1.若无该键值则返回默认值defaultValue<br>
	 * 2.若存在该key，则将该键值强转成double类型，强转失败则返回默认值<br>
     *
     * @param key a String
     * @return a double value
     */
    public static double getDouble(Map<String, Object> map, String key, double defaultValue) {
    	if(map == null) {
			return defaultValue;
		}
        Object o = map.get(key);
        if (o == null) {
        	Log.w(TAG, "KeyNotFound, please check key <" + key + ">");
            return defaultValue;
        }
        try {
            return (Double) o;
        } catch (ClassCastException e) {
        	Log.w(TAG, "ClassCastException in getDouble() at key=<" + key + ">, " + e.getMessage());
			try {
				return Double.parseDouble((String) o);
			} catch (RuntimeException e1) {
				return defaultValue;
			}
        }
    }
    
    /**
     * 读取map中key对应的键值，<br>
	 * 1.若无该键值则返回false<br>
	 * 2.若存在该key，则将该键值强转成double类型，强转失败则返回false<br>
     *
     * @param key a String
     * @return
     */
    public static boolean getBoolean(Map<String, Object> map, String key) {
        return getBoolean(map, key, false);
    }

    /**
     * 读取map中key对应的键值，<br>
	 * 1.若无该键值则返回默认值defaultValue<br>
	 * 2.若存在该key，则将该键值强转成boolean类型，强转失败则返回默认值<br>
     *
     * @param key a String
     * @return 
     */
    public static boolean getBoolean(Map<String, Object> map, String key, boolean defaultValue) {
    	if(map == null) {
			return defaultValue;
		}
        Object o = map.get(key);
        if (o == null) {
        	Log.w(TAG, "KeyNotFound, please check key <" + key + ">");
            return defaultValue;
        }
        try {
            return (Boolean) o;
        } catch (ClassCastException e) {
        	Log.w(TAG, "ClassCastException in getBoolean() at key=<" + key + ">, " + e.getMessage());
			try {
				return Boolean.parseBoolean((String) o);
			} catch (RuntimeException e1) {
				return defaultValue;
			}
        }
    }
    
    /**
     * 读取map中key对应的键值，<br>
	 * 1.若无该键值则返回false<br>
	 * 2.若存在该key，则将该键值强转成float类型，强转失败则返回0.0F<br>
     *
     * @param key a String
     * @return
     */
    public static float getFloat(Map<String, Object> map, String key) {
        return getFloat(map, key, 0.0F);
    }

    /**
     * 读取map中key对应的键值，<br>
	 * 1.若无该键值则返回默认值defaultValue<br>
	 * 2.若存在该key，则将该键值强转成float类型，强转失败则返回默认值<br>
     *
     * @param key a String
     * @return 
     */
    public static float getFloat(Map<String, Object> map, String key, float defaultValue) {
    	if(map == null) {
			return defaultValue;
		}
        Object o = map.get(key);
        if (o == null) {
        	Log.w(TAG, "KeyNotFound, please check key <" + key + ">");
            return defaultValue;
        }
        try {
            return (Float) o;
        } catch (ClassCastException e) {
        	Log.w(TAG, "ClassCastException in getFloat() at key=<" + key + ">, " + e.getMessage());
			try {
				return Float.parseFloat((String) o);
			} catch (RuntimeException e1) {
				return defaultValue;
			}
        }
    }
    
    /**
     * 读取map中key对应的键值，<br>
	 * 1.若无该键值则返回false<br>
	 * 2.若存在该key，则将该键值强转成long类型，强转失败则返回0L<br>
     *
     * @param key a String
     * @return
     */
    public static long getLong(Map<String, Object> map, String key) {
        return getLong(map, key, 0L);
    }

    /**
     * 读取map中key对应的键值，<br>
	 * 1.若无该键值则返回默认值defaultValue<br>
	 * 2.若存在该key，则将该键值强转成long类型，强转失败则返回默认值<br>
     *
     * @param key a String
     * @return 
     */
    public static long getLong(Map<String, Object> map, String key, long defaultValue) {
    	if(map == null) {
			return defaultValue;
		}
        Object o = map.get(key);
        if (o == null) {
        	Log.w(TAG, "KeyNotFound, please check key <" + key + ">");
            return defaultValue;
        }
        try {
            return (Long) o;
        } catch (ClassCastException e) {
        	Log.w(TAG, "ClassCastException in getLong() at key=<" + key + ">, " + e.getMessage());
			try {
				return Long.parseLong((String) o);
			} catch (RuntimeException e1) {
				return defaultValue;
			}
        }
    }
    
    /**
     * 读取map中key对应的键值，<br>
	 * 1.若无该键值则返回false<br>
	 * 2.若存在该key，则将该键值强转成char类型，强转失败则返回(char) 0<br>
     *
     * @param key a String
     * @return
     */
    public static char getChar(Map<String, Object> map, String key) {
        return getChar(map, key, (char) 0);
    }

    /**
     * 读取map中key对应的键值，<br>
	 * 1.若无该键值则返回默认值defaultValue<br>
	 * 2.若存在该key，则将该键值强转成char类型，强转失败则返回默认值<br>
     *
     * @param key a String
     * @return 
     */
    public static char getChar(Map<String, Object> map, String key, char defaultValue) {
    	if(map == null) {
			return defaultValue;
		}
        Object o = map.get(key);
        if (o == null) {
        	Log.w(TAG, "KeyNotFound, please check key <" + key + ">");
            return defaultValue;
        }
        try {
            return (Character) o;
        } catch (ClassCastException e) {
        	Log.w(TAG, "ClassCastException in getChar() at key=<" + key + ">, " + e.getMessage());
            return defaultValue;
        }
    }
	
	/**
	 * 读取map中key对应的键值，<br>
	 * 1.若无该键值则返回空数组<br>
	 * 2.若存在该key，且对应键值是Object[]类型，则正常返回，否则返回空数组<br>
	 * @param map
	 * @param key
	 * @return
	 */
	public static Object[] getObjectArray(Map<String, Object> map, String key){
		return getObjectArray(map, key, new Object[0]);
	}

	/**
	 * 读取map中key对应的键值，<br>
	 * 1.若无该键值则返回默认值defaultValue<br>
	 * 2.若存在该key，则将该键值是Object[]类型，则正常返回，否则返回defaultValue<br>
	 * @param map
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static Object[] getObjectArray(Map<String, Object> map, String key, Object[] defaultValue){
		if(map == null) {
			return defaultValue;
		}
		Object o = map.get(key);
		if (o == null) {
			Log.w(TAG, "KeyNotFound, please check key <" + key + ">");
            return defaultValue;
        }
		try {
			return (Object[]) o;
		} catch (ClassCastException e) {
			// TODO Auto-generated catch block
			Log.w(TAG, "ClassCastException in getObjectArray() at key=<" + key + ">, " + e.getMessage());
			return defaultValue;
		}
	}
	
	/**
	 * 读取map中key对应的键值，<br>
	 * 1.若无该键值则返回空Map<br>
	 * 2.若存在该key，则将该键值强转成Map<String, Object>类型，若强转失败则返回空Map<br>
	 * @param map
	 * @param key
	 * @return
	 */
	public static Map<String, Object> getMap(Map<String, Object> map, String key){
		return getMap(map, key, new HashMap<String, Object>(0));
	}

	/**
	 * 读取map中key对应的键值，<br>
	 * 1.若无该键值则返回默认值defaultValue<br>
	 * 2.若存在该key，则将该键值强转成Map<String, Object>类型，若强转失败则返回defaultValue<br>
	 * @param map
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getMap(Map<String, Object> map, String key, Map<String, Object> defaultValue){
		if(map == null) {
			return defaultValue;
		}
		Object o = map.get(key);
		if (o == null) {
			Log.w(TAG, "KeyNotFound, please check key <" + key + ">");
            return defaultValue;
        }
		try {
			return (Map<String, Object>) o;
		} catch (ClassCastException e) {
			// TODO Auto-generated catch block
			Log.w(TAG, "ClassCastException in getMap() at key=<" + key + ">, " + e.getMessage());
			return defaultValue;
		}
	}
}
