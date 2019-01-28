package com.juzix.wallet.protocol;

import android.content.Context;

import com.juzix.wallet.protocol.base.IHttpLoader;
import com.juzix.wallet.protocol.bridge.NoHttpManager;


public class HttpConfigure {

    private HttpConfigure() {

    }

    /**
     * 初始化网络全局配置
     *
     * @param context
     */
    public static void init(Context context) {
        NoHttpManager.getInstance().init(context);
    }

    public static <T> IHttpLoader<T> getLoader() {
        return NoHttpManager.getInstance().createLoader();
    }
}
