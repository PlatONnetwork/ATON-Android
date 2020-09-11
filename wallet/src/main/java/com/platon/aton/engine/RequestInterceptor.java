package com.platon.aton.engine;


import com.platon.framework.utils.LanguageUtil;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author matrixelement
 */
public class RequestInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        //mHeaders为静态请求头，x-aton-cid为动态请求头
        Request.Builder requestBuilder = original.newBuilder()
                .addHeader("x-aton-cid", NodeManager.getInstance().getChainId())
                .addHeader("Accept-Language", Locale.CHINESE.getLanguage().equals(LanguageUtil.getLocale().getLanguage()) ? "zh-cn" : "en-us")
                .method(original.method(), original.body());
        return chain.proceed(requestBuilder.build());
    }

}
