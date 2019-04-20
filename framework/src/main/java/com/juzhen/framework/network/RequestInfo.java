package com.juzhen.framework.network;

import android.text.TextUtils;

import com.juzhen.framework.util.MapUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author ziv
 */
public class RequestInfo {

    public static final String DEFAULT_URL_KEY = "restful";
    public static final String URL_IP = "url_ip";

    public enum EncryptionType {
        TYPE_PLATFORM,
        TYPE_BUSINESS,
        TYPE_NONE;

        public static EncryptionType ofValue(String value) {
            if (!TextUtils.isEmpty(value)) {
                if ("biz".equalsIgnoreCase(value)) {
                    return TYPE_BUSINESS;
                } else if ("plt".equalsIgnoreCase(value)) {
                    return TYPE_PLATFORM;
                }
            }
            return TYPE_NONE;
        }
    }

    public static String getUrl(String key) {
        return URL_MAP.get(key);
    }

    private String mRealUrl;

    private EncryptionType mEncryptType;

    private static final Map<String, String> URL_MAP = new HashMap<>();

    public static void init(String defaultBaseUrl, Map<String, Object> addressMap) {
        if (addressMap != null) {
            Iterator<String> iterator = addressMap.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                URL_MAP.put(key, MapUtils.getString(addressMap, key));
            }
        }
        URL_MAP.put(DEFAULT_URL_KEY, defaultBaseUrl);
    }

    public static RequestInfo create(String url) {
        if (url == null) {
            new NullPointerException("url is null");
        }
        RequestInfo info = new RequestInfo();

        Pattern pattern = Pattern.compile("(wallet_api)(/.*)+");

        Pattern encryptPattern = Pattern.compile("url-http.encrypt-(.+?)(/.*)+");

        Matcher matcher = pattern.matcher(url);
        Matcher encryptMatcher = encryptPattern.matcher(url);

//        //加密
//        if (encryptMatcher.find()) {
//            String urlKey = encryptMatcher.group(1).toUpperCase();
//            String path = encryptMatcher.group(1) + encryptMatcher.group(2);
//            String baseUrl = URL_MAP.containsKey(urlKey) ? URL_MAP.get(urlKey) : URL_MAP.get(DEFAULT_URL_KEY);
//            info.mRealUrl = baseUrl + path;
//            info.mEncryptType = EncryptionType.TYPE_BUSINESS;
//        } else {
//            if (matcher.find()) {
//                String urlKey = matcher.group(1).toUpperCase();
//                String path = matcher.group(0);
//                String baseUrl = URL_MAP.containsKey(urlKey) ? URL_MAP.get(urlKey) : URL_MAP.get(DEFAULT_URL_KEY);
//                info.mRealUrl = baseUrl + path;
//                info.mEncryptType = EncryptionType.TYPE_NONE;
//            } else {
//                info.mRealUrl = url;
//                info.mEncryptType = EncryptionType.TYPE_NONE;
//            }
//        }

        return info;
    }

    public String getRealUrl() {
        return mRealUrl;
    }

    public EncryptionType getEncryptType() {
        return mEncryptType;
    }

}
