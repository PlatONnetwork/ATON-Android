package com.juzix.wallet;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.juzhen.framework.app.CoreApp;
import com.juzix.wallet.app.AppFramework;
import com.juzix.wallet.component.ui.view.UnlockFigerprintActivity;
import com.juzix.wallet.config.AppSettings;
import com.juzix.wallet.engine.IndividualWalletManager;
import com.juzix.wallet.entity.CountryEntity;
import com.juzix.wallet.utils.CountryUtil;
import com.juzix.wallet.utils.FileUtil;
import com.juzix.wallet.utils.JSONUtil;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import java.util.List;

import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;

/**
 * @author matrixelement
 */
public class App extends CoreApp {

    private final static String TAG = App.class.getSimpleName();
    private final static long MAX_TIMEINMILLS = 2 * 60 * 1000;
    private static Context context;
    private int mActivityAmount = 0;
    private long mBackgroundTimeInMills;

    @Override
    public void onCreate() {
        super.onCreate();
        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        });
        context = this;
        //初始化友盟
        initUMConfigure();
        initBugly();
        AppFramework.getAppFramework().initAppFramework(this);
        registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
    }

    private void initBugly() {
        CrashReport.initCrashReport(getApplicationContext(), BuildConfig.BUGLY_APPID, true);
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
        if (newConfig.fontScale != 1)
            getResources();
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
        UMConfigure.init(this, BuildConfig.UM_APPKEY, "Umeng", UMConfigure.DEVICE_TYPE_PHONE, null);
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.MANUAL);
        if (BuildConfig.DEBUG) {
            UMConfigure.setLogEnabled(true);
        }
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
                        timeInMills - mBackgroundTimeInMills > MAX_TIMEINMILLS &&
                        AppSettings.getInstance().getFaceTouchIdFlag() &&
                        !IndividualWalletManager.getInstance().getWalletList().isEmpty()) {
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
