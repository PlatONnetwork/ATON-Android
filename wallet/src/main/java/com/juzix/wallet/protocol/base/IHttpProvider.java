package com.juzix.wallet.protocol.base;

import java.util.Map;

public interface IHttpProvider {
    int GET  = 1101;
    int POST = 1102;

    /**
     * 获取服务网络主地址
     */
    String getURL();

    /**
     * 请求方式
     */
    int requestMethod();

    /**
     * 获取在HTTP头部的参数
     */
    Map<String, String> getHeaders();

    /**
     * 是否支持cache
     */
    boolean supportCache();

    /**
     * 是否有cookie
     */
    boolean supportCookie();
}
