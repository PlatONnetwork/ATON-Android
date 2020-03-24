package com.platon.aton;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.multidex.MultiDex;

import com.platon.aton.app.AppFramework;
import com.platon.aton.component.ui.view.UnlockFigerprintActivity;
import com.platon.aton.config.AppSettings;
import com.platon.aton.engine.DeviceManager;
import com.platon.aton.engine.WalletManager;
import com.platon.framework.base.BaseAppDeletage;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;

import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;

/**
 * @author matrixelement
 */
public class App extends BaseAppDeletage {

    private final static long MAX_TIME = 120000;

    private static Context context;
    private int mActivityAmount = 0;
    private long mBackgroundTimeInMills;

    public App(Application application) {
        super(application);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        });
        context = this;
        AppFramework.getAppFramework().initAppFramework(this);
        //初始化友盟
        initUMConfigure();
        registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
    }

    @Override
    public void onTerminate() {
        unregisterActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
        super.onTerminate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        //非默认值
        if (newConfig.fontScale != 1) {
            getResources();
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        //非默认值
        if (res.getConfiguration().fontScale != 1) {
            Configuration newConfig = new Configuration();
            //设置默认
            newConfig.setToDefaults();
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
        }
        return res;
    }

    @Override
    protected String getConfiguredReleaseType() {
        return BuildConfig.RELEASE_TYPE;
    }

    public static Context getContext() {
        return context;
    }

    private void initUMConfigure() {
        UMConfigure.init(this, BuildConfig.UM_APPKEY, DeviceManager.getInstance().getChannel(), UMConfigure.DEVICE_TYPE_PHONE, null);
        if (BuildConfig.DEBUG) {
            UMConfigure.setLogEnabled(true);
        }
        // 选用LEGACY_AUTO页面采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.LEGACY_MANUAL);
        PlatformConfig.setSinaWeibo(BuildConfig.SINA_APPKEY, BuildConfig.SINA_APP_SECRET, BuildConfig.SINA_APP_REDIRECT_URL);
    }

    private ActivityLifecycleCallbacks mActivityLifecycleCallbacks = new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {
            if (mActivityAmount == 0) {
                long timeInMills = System.currentTimeMillis();
                if (mBackgroundTimeInMills > 0 &&
                        timeInMills - mBackgroundTimeInMills > MAX_TIME &&
                        AppSettings.getInstance().getFaceTouchIdFlag() &&
                        !WalletManager.getInstance().getWalletList().isEmpty()) {
                    UnlockFigerprintActivity.actionStart(activity);
                }
                mBackgroundTimeInMills = timeInMills;
            }
            mActivityAmount++;
        }

        @Override
        public void onActivityResumed(Activity activity) {
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
            mActivityAmount--;
            mBackgroundTimeInMills = System.currentTimeMillis();
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
        }
    };
}
