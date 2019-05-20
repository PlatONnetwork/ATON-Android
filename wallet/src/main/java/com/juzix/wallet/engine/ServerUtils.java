package com.juzix.wallet.engine;

import android.content.Context;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.juzhen.framework.network.ApiCallback;
import com.juzhen.framework.network.ApiErrorCode;
import com.juzhen.framework.network.ApiFastjsonConverterFactory;
import com.juzhen.framework.network.ApiResponse;
import com.juzhen.framework.network.LoggingInterceptor;
import com.juzhen.framework.network.RequestInfo;
import com.juzhen.framework.network.RequestInterceptor;
import com.juzix.wallet.BuildConfig;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.config.AppConfig;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author ziv
 */
public class ServerUtils {

    private volatile static BaseApi mBaseApi;

    public static BaseApi getCommonApi() {
        try {
            if (mBaseApi == null) {
                synchronized (ServerUtils.class) {
                    if (mBaseApi == null) {
                        mBaseApi = createService(BaseApi.class, Constants.URL.URL_HTTP_A);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mBaseApi;
    }

    private static <S> S createService(Class<S> serviceClass, String url) throws Exception {
        Retrofit.Builder builder =
                new Retrofit.Builder()
                        .baseUrl(url)
                        .client(httpClient.build())
                        .addConverterFactory(ApiFastjsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create());

        Retrofit retrofit = builder.build();
        return retrofit.create(serviceClass);
    }

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(25, TimeUnit.SECONDS)
            .readTimeout(25, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(new BaseUrlInterceptor())
            .addInterceptor(new RequestInterceptor(buildHeadsMap()))
            .addInterceptor(getLogInterceptor())
            .addInterceptor(new StethoInterceptor())
            .sslSocketFactory(getSSLSocketFactory());

    private static HttpLoggingInterceptor getLogInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }
        return loggingInterceptor;
    }

    private static Map<String, String> buildHeadsMap() {
        Map<String, String> map = new HashMap<>();
        map.put("x-aton-cid", NodeManager.getInstance().getChainId());
        return map;
    }

    /**
     * 不验证证书
     *
     * @return
     * @throws Exception
     */
    private static SSLSocketFactory getSSLSocketFactory() {
        //创建一个不验证证书链的证书信任管理器。
        final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] chain,
                    String authType) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] chain,
                    String authType) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[0];
            }
        }};
        final SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts,
                    new java.security.SecureRandom());
            return sslContext
                    .getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
