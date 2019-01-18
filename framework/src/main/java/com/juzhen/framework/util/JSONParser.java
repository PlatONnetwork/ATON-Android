package com.juzhen.framework.util;

import android.annotation.SuppressLint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


@SuppressLint("SimpleDateFormat")
public class JSONParser {

    public static Map<String, Object> parse(String text) throws JSONException {
        Map<String, Object> r = parseInternal(text);
        return r;
    }

    private static Map<String, Object> parseInternal(String text) throws JSONException {
        //拿到最外层的那个对象
        JSONObject object = (JSONObject) new JSONTokener(text).nextValue();
        return parseJSONObject(object);
    }

    private static Map<String, Object> parseJSONObject(JSONObject obj) throws JSONException {
        //遍历所有属性
        HashMap<String, Object> map = new HashMap<String, Object>();
        Iterator<?> iter = obj.keys();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            Object value = obj.get(key);

            //解析value，转换成javascript可以识别的类型
            Object v = parseValue(value);
            //添加属性
            map.put(key, v);
        }
        return map;
    }

    private static Object parseValue(Object value) throws JSONException {
        if (value == null) {
            return null;
        } else if (value instanceof JSONObject) {
            //如果value是JSON对象，调用parseJSONObject
            return parseJSONObject((JSONObject) value);
        } else if (value instanceof JSONArray) {
            //如果value是数组，拿到里面的元素，逐个再调用parseValue，然后包装成JavaScript的Array
            JSONArray jsonArray = (JSONArray) value;
            int len = jsonArray.length();
            Object[] jsArrayValues = new Object[len];
            for (int i = 0; i < len; i++) {
                Object jsonArrayValue = jsonArray.get(i);
                jsArrayValues[i] = parseValue(jsonArrayValue);
            }
            return jsArrayValues;
        } else {
            //到这里，value是普通类型（boolean/String/number），搞定。
            return value;
        }
    }

    public static String toJSONString(Object obj) {
        if (obj instanceof Date) {
            //日期
            Date date = (Date) obj;
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            return df.format(date);
        } else if (obj instanceof Object[]) {
            //数组
            Object[] array = (Object[]) obj;
            StringBuffer sb = new StringBuffer();
            sb.append('[');
            boolean hasDealFirstItem = false;
            for (Object arrayElement : array) {
                if (hasDealFirstItem) {
                    sb.append(',');
                }
                sb.append(toJSONString(arrayElement));
                hasDealFirstItem = true;
            }
            sb.append(']');

            return sb.toString();
        } else if (obj instanceof Map) {
            //对象
            Map<?, ?> map = (Map<?, ?>) obj;
            StringBuffer sb = new StringBuffer();
            sb.append('{');
            boolean hasDealFirstItem = false;
            for (Object propName : map.keySet()) {
                if (hasDealFirstItem) {
                    sb.append(',');
                }
                sb.append('\"');
                sb.append(propName);
                sb.append("\":");

                sb.append(toJSONString(map.get(propName)));
                hasDealFirstItem = true;
            }
            sb.append('}');
            return sb.toString();
        }
        if (obj instanceof String) {
            String origin = (String) obj;
            String convert = fitJSON(origin);
            return "\"" + convert + "\"";
        } else {
            return obj == null ? "null" : obj.toString();
        }
    }

    public static String fitJSON(String s) {
        //把单引号/回车/换行都转义了
        String r = s.replace("\\", "\\\\");
        r = r.replace("\"", "\\\"");
        r = r.replace("'", "\\'");
        r = r.replace("\r", "\\r");
        r = r.replace("\n", "\\n");
        return r;
    }


}
