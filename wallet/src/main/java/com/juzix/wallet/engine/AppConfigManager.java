package com.juzix.wallet.engine;

import com.juzhen.framework.network.ApiResponse;
import com.juzhen.framework.network.ApiSingleObserver;
import com.juzhen.framework.util.LogUtils;
import com.juzix.wallet.entity.AppConfig;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.RxUtils;


public class AppConfigManager {

    private final static String DEFAULT_MIN_GASPRICE = "1000000000";
    private final static String DEFAULT_MIN_DELEGATION = "10000000000000000000";
    private final static String DEFAULT_TIMEOUT = String.valueOf(24 * 60 * 60);

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
        return BigDecimalUtil.mul(mAppConfig.getTimeout(), "1000").toPlainString();
    }

    public void init() {

        ServerUtils
                .getCommonApi()
                .getAppConfig()
                .compose(RxUtils.getSingleSchedulerTransformer())
                .subscribe(new ApiSingleObserver<AppConfig>() {
                    @Override
                    public void onApiSuccess(AppConfig appConfig) {
                        LogUtils.e("onApiSuccess");
                        mAppConfig = appConfig;
                        LogUtils.e(appConfig.toString());
                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {
                        LogUtils.e("onApiFailure");
                    }
                });

    }


}
