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
import com.platon.aton.engine.WalletManager;
import com.platon.framework.app.Constants;
import com.platon.framework.base.BaseApplication;
import com.platon.framework.utils.PreferenceTool;

import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;

/**
 * @author matrixelement
 */
public class App extends BaseApplication {

    private final static long MAX_TIME = 120000;

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
        AppFramework.getAppFramework().initAppFramework(context);
        //初始化友盟
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

    public static Context getContext() {
        return context;
    }

    private Application.ActivityLifecycleCallbacks mActivityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {
            if (mActivityAmount == 0) {
                long timeInMills = System.currentTimeMillis();
                if (mBackgroundTimeInMills > 0 &&
                        timeInMills - mBackgroundTimeInMills > MAX_TIME &&
                        PreferenceTool.getBoolean(Constants.Preference.KEY_FACE_TOUCH_ID_FLAG, false) &&
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
