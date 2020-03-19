package com.platon.aton.engine;

import com.platon.framework.util.LogUtils;
import com.platon.aton.entity.AppConfig;
import com.platon.aton.utils.JSONUtil;
import com.platon.aton.utils.RxUtils;

import io.reactivex.functions.Consumer;


public class AppConfigManager {

    private final static String DEFAULT_MIN_GASPRICE = "10000000000";
    private final static String DEFAULT_MIN_DELEGATION = "10000000000000000000";
    private final static String DEFAULT_TIMEOUT = String.valueOf(24 * 60 * 60 * 1000);

    private AppConfig mAppConfig;

    private AppConfigManager() {
        mAppConfig = new AppConfig(DEFAULT_MIN_GASPRICE, DEFAULT_MIN_DELEGATION, DEFAULT_TIMEOUT);
    }

    private static class InstanceHolder {
        private static volatile AppConfigManager INSTANCE = new AppConfigManager();
    }

    public static AppConfigManager getInstance() {
        return AppConfigManager.InstanceHolder.INSTANCE;
    }

    public String getMinGasPrice() {
        return mAppConfig.getMinGasPrice();
    }

    public String getMinDelegation() {
        return mAppConfig.getMinDelegation();
    }

    public String getTimeout() {
        return mAppConfig.getTimeout();
    }

    @Override
    public String toString() {
        return "AppConfigManager{" +
                "mAppConfig=" + mAppConfig +
                '}';
    }

    public void init() {

        ServerUtils
                .getCommonApi()
                .getAppConfig()
                .compose(RxUtils.getSingleSchedulerTransformer())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String text) throws Exception {
                        LogUtils.e(text);
                        mAppConfig = JSONUtil.parseObject(text, AppConfig.class);
                        LogUtils.e(mAppConfig.toString());
                    }
                });

    }


}
