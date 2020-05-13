package com.platon.aton.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.facebook.stetho.common.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author matrixelement
 */
public class JSONUtil {

    private static SerializeConfig serializeConfig;

    private static final SerializerFeature[] serializerFeature = {
            //打开循环引用检测，JSONField(serialize = false)不循环
            SerializerFeature.DisableCircularReferenceDetect,
            //默认使用系统默认 格式日期格式化
            SerializerFeature.WriteDateUseDateFormat,
            //输出空置字段
            SerializerFeature.WriteMapNullValue,
            //list字段如果为null，输出为[]，而不是null
            SerializerFeature.WriteNullListAsEmpty,
            // 数值字段如果为null，输出为0，而不是null
            SerializerFeature.WriteNullNumberAsZero,
            //Boolean字段如果为null，输出为false，而不是null
            SerializerFeature.WriteNullBooleanAsFalse,
            //字符类型字段如果为null，输出为""，而不是null
            SerializerFeature.WriteNullStringAsEmpty
    };

    static {
        serializeConfig = new SerializeConfig();
    }

    private JSONUtil() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 将json字符串反序列化成javabean
     *
     * @param text
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T parseObject(String text, Class<T> clazz) {
        T t = null;
        try {
            t = JSON.parseObject(text, clazz);
        } catch (Exception e) {
            LogUtil.e(e.getMessage(),e.fillInStackTrace());
        }
        return t;
    }

    /**
     * 把JSON文本parse成JavaBean集合
     *
     * @param text
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> parseArray(String text, Class<T> clazz) {
        List<T> list = new ArrayList<>();
        try {
            list = JSON.parseArray(text, clazz);
        } catch (Exception e) {
            LogUtil.e(e.getMessage(),e.fillInStackTrace());
        }
        return list;
    }

    /**
     * 解析二维数组
     * 把JSON文本parse成JavaBean集合
     *
     * @param text
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> parseTwoDimensionArray(String text, Class<T> clazz) {
        List<T> list = new ArrayList<>();
        try {
            JSONArray array = JSONArray.parseArray(text);
            for (int i = 0; i < array.size(); i++) {
                list.addAll(parseArray(array.getJSONArray(i).toJSONString(), clazz));
            }
        } catch (Exception e) {
            LogUtil.e(e.getMessage(),e.fillInStackTrace());
        }
        return list;
    }

    /**
     * 将对象序列化程json字符串
     *
     * @param object
     * @return
     */
    public static String toJSONString(Object object) {
        String json = "";
        try {
            json = JSON.toJSONString(object, serializeConfig, serializerFeature);
        } catch (Exception e) {
            LogUtil.e(e.getMessage(),e.fillInStackTrace());
        }
        return json;
    }

    /**
     * map转json字符串
     *
     * @param params
     * @return
     */
    public static String toJSONString(Map<String, String> params) {

        JSONObject jsonObject = new JSONObject();

        if (params == null || params.isEmpty()) {
            return jsonObject.toString();
        }

        for (Map.Entry<String, String> entry : params.entrySet()) {
            try {
                jsonObject.put(entry.getKey(), entry.getValue());
            } catch (Exception e) {
                LogUtil.e(e.getMessage(),e.fillInStackTrace());
            }
        }

        return jsonObject.toString();
    }
}
