package com.juzix.wallet;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.multidex.MultiDex;

import com.juzhen.framework.app.CoreApp;
import com.juzix.wallet.app.AppFramework;
import com.juzix.wallet.component.ui.view.UnlockFigerprintActivity;
import com.juzix.wallet.config.AppSettings;
import com.juzix.wallet.engine.IndividualWalletManager;

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
        AppFramework.getAppFramework().initAppFramework(this);
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
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected String getConfiguredReleaseType() {
        return null;
    }

    public static Context getContext() {
        return context;
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
