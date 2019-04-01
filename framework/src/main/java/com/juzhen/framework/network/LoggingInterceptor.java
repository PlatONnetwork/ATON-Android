package com.juzhen.framework.network;

import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * 日志拦截器
 * Created by villey on 2017/8/7.
 */

public class LoggingInterceptor implements Interceptor {

    private static final String TAG = "ApiService/Logging";
    private static final Gson GSON = new Gson();
    private static final Charset UTF8 = Charset.forName("UTF-8");

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        String reqInfo = "--> " + request.method() + ' ' + request.url() + ' ' + request.headers();
        
        if (request.body() instanceof ApiRequestBody) {
            ApiRequestBody apiRequestBody = (ApiRequestBody) request.body();
            reqInfo += ("\n bodyEncrypt " + GSON.toJson(apiRequestBody.getBodyMap()));
            ApiRequestBody rawBody = apiRequestBody.getRawRequestBody();
            if (rawBody != null) {
                reqInfo += ("\n bodyRAW " + GSON.toJson(rawBody.getBodyMap()));
            }
        }

        Log.e(TAG, reqInfo);

        long startNs = System.nanoTime();
        Response response = chain.proceed(request);
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        ResponseBody responseBody = response.body();
        BufferedSource source = responseBody.source();
        // Buffer the entire body.
        source.request(Long.MAX_VALUE);
        Buffer buffer = source.buffer();
        Charset charset = UTF8;
        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            try {
                charset = contentType.charset(UTF8);
            } catch (UnsupportedCharsetException e) {
                return response;
            }
        }
        String ms = buffer.clone().readString(charset);

        Log.e(TAG, ms);

        return response;
    }

}
