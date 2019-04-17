package com.juzhen.framework.network;

import android.content.Context;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * @author ziv
 */
public class HttpClient {

    private Retrofit retrofit;

    private HttpClient() {

    }

    public static HttpClient getInstance() {
        return SingletonHolder.httpClient;
    }

    private static class SingletonHolder {
        private static HttpClient httpClient = new HttpClient();
    }

    public void init(Context context, String baseUrl, Map<String, Object> urlMap) {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(25, TimeUnit.SECONDS)
                .readTimeout(25, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(new MultipleUrlInterceptor())
                .addInterceptor(new LoggingInterceptor())
                .addInterceptor(new StethoInterceptor())
                .build();

        retrofit = new Retrofit.Builder()
                .addConverterFactory(ApiFastjsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .build();

        ApiResponse.init(context);

        RequestInfo.init(baseUrl, urlMap);
    }

    private static SSLContext createSSLContext(TrustManager trustManager) {
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{trustManager}, new SecureRandom());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return sslContext;
    }

    public <T> T createService(Class<T> service) {
        if (service == null) {
            throw new RuntimeException("Api service is null!");
        }
        return retrofit.create(service);
    }


    /**
     * 异步请求
     *
     * @param requestCall
     * @param callback
     * @param <T>
     */
    private <T> void asyncNetWork(Call<ApiResponse<T>> requestCall, final ApiCallback<T> callback) {
        if (callback == null) {
            throw new InvalidParameterException("ApiCallback cannot be NULL!");
        }
        Call<ApiResponse<T>> call;
        if (requestCall.isExecuted()) {
            call = requestCall.clone();
        } else {
            call = requestCall;
        }
        call.enqueue(new Callback<ApiResponse<T>>() {
            @Override
            public void onResponse(Call<ApiResponse<T>> call, Response<ApiResponse<T>> response) {
                System.out.println("enqueue " + call.request().toString());
                if (response.isSuccessful()) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse != null && apiResponse.getResult() == ApiErrorCode.SUCCESS) {
                        Object data = apiResponse.getData();
                        callback.onSuccess(data == null ? null : (T) data);
                        return;
                    } else if (apiResponse != null && apiResponse.getResult() != ApiErrorCode.SUCCESS) {
                        callback.onFailure(apiResponse);
                        return;
                    }
                }
                callback.onFailure(new ApiResponse(ApiErrorCode.SYSTEM_ERROR, response));
            }

            @Override
            public void onFailure(Call<ApiResponse<T>> call, Throwable t) {
                System.out.println("enqueue " + call.request().toString());
                callback.onFailure(new ApiResponse(ApiErrorCode.SYSTEM_ERROR, t));
            }
        });
    }

    /**
     * 同步请求
     *
     * @param requestCall
     * @param <T>
     * @return
     */
    public <T> T syncNetWork(final Call<T> requestCall) {
        Call<T> call;
        T result = null;
        try {
            if (requestCall.isExecuted()) {
                call = requestCall.clone();
            } else {
                call = requestCall;
            }
            Response<T> response = call.execute();
            if (response.isSuccessful()) {
                result = response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
