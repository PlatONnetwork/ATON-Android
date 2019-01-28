package com.juzix.wallet.protocol.bridge;

import android.content.Context;

import com.juzhen.framework.network.InitializationConfig;
import com.juzhen.framework.network.NoHttp;
import com.juzhen.framework.network.OkHttpNetworkExecutor;
import com.juzhen.framework.network.cache.DBCacheStore;
import com.juzhen.framework.network.cookie.DBCookieStore;
import com.juzhen.framework.network.download.DownloadListener;
import com.juzhen.framework.network.download.DownloadQueue;
import com.juzhen.framework.network.download.DownloadRequest;
import com.juzhen.framework.network.rest.OnResponseListener;
import com.juzhen.framework.network.rest.Request;
import com.juzhen.framework.network.rest.RequestQueue;
import com.juzix.wallet.protocol.base.IHttpLoader;


public class NoHttpManager {

    //请求队列
    private RequestQueue  mRequestQueue;
    //下载队列
    private DownloadQueue mDownloadQueue;

    private NoHttpManager() {
//        mRequestQueue  = NoHttp.newRequestQueue(3);
//        mDownloadQueue = NoHttp.newDownloadQueue(3);
    }

    public static NoHttpManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * 初始化网络全局配置
     *
     * @param context
     */
    public void init(Context context) {
        InitializationConfig.Builder builder = InitializationConfig.newBuilder(context);
        // 设置全局连接超时时间，单位毫秒，默认10s。
        builder.connectionTimeout(20 * 1000);
        // 设置全局服务器响应超时时间，单位毫秒，默认10s。
        builder.readTimeout(20 * 1000);
        // 配置缓存，默认保存数据库DBCacheStore，保存到SD卡使用DiskCacheStore。
        // 如果不使用缓存，设置setEnable(false)禁用。
        builder.cacheStore(new DBCacheStore(context).setEnable(false));
        // 配置Cookie，默认保存数据库DBCookieStore，开发者可以自己实现。
        // 如果不维护cookie，设置false禁用。
        builder.cookieStore(new DBCookieStore(context).setEnable(false));
        // 配置网络层，URLConnectionNetworkExecutor，如果想用OkHttp：OkHttpNetworkExecutor。
        builder.networkExecutor(new OkHttpNetworkExecutor());
        // 如果你需要自定义配置：
        NoHttp.initialize(builder.build());
    }

    /**
     * 获取当前使用的网络加载库
     *
     * @param <T>
     * @return
     */
    public <T> IHttpLoader<T> createLoader() {
        return new NoHttpLoader<>();
    }

    /**
     * 添加一个请求到请求队列。
     *
     * @param what     用来标志请求, 当多个请求使用同一个Listener时, 在回调方法中会返回这个what。
     * @param request  请求对象。
     * @param listener 结果回调对象。
     */
    public <T> void addRequest(int what, Request<T> request, OnResponseListener listener) {
        if (mRequestQueue == null) {
            mRequestQueue = NoHttp.newRequestQueue(3);
        }
        mRequestQueue.add(what, request, listener);
    }

    /**
     * 添加一个下载请求到请求队列。
     *
     * @param what     用来标志请求, 当多个请求使用同一个Listener时, 在回调方法中会返回这个what。
     * @param request  请求对象。
     * @param listener 结果回调对象。
     */
    public void addDownload(int what, DownloadRequest request, DownloadListener listener) {
        if (mDownloadQueue == null) {
            mDownloadQueue = NoHttp.newDownloadQueue(3);
        }
        mDownloadQueue.add(what, request, listener);
    }

    /**
     * 取消这个sign标记的所有请求。
     *
     * @param sign 请求的取消标志。
     */
    public void cancelBySign(Object sign) {
        mRequestQueue.cancelBySign(sign);
    }

    /**
     * 取消队列中所有请求。
     */
    public void cancelAll() {
        mRequestQueue.cancelAll();
    }

    private static class InstanceHolder {
        private static volatile NoHttpManager INSTANCE = new NoHttpManager();
    }
}
