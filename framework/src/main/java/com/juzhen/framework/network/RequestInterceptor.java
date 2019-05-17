package com.juzhen.framework.network;


import android.os.UserManager;

import com.juzhen.framework.util.DeviceManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author matrixelement
 */
public class RequestInterceptor implements Interceptor {

    private Map<String, String> mHeaders = new HashMap<>();

    public RequestInterceptor(Map<String, String> heads) {
        this.mHeaders = heads;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder requestBuilder = original.newBuilder()
                .headers(Headers.of(mHeaders))
                .method(original.method(), original.body());
        return chain.proceed(requestBuilder.build());
    }

}
