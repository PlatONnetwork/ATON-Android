package com.platon.wallet.engine;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.platon.framework.network.ApiFastjsonConverterFactory;
import com.platon.wallet.BuildConfig;
import com.platon.wallet.app.Constants;

import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * @author ziv
 */
public class ServerUtils {

    public final static String HEADER_UPDATE_VERSION = "updateVersion";

    private volatile static BaseApi mBaseApi;

    private ServerUtils() {
    }

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
            .addInterceptor(new RequestInterceptor())
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
