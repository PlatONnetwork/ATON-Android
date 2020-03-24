package com.platon.framework.base;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.platon.framework.BuildConfig;
import com.platon.framework.app.CustomerException;
import com.platon.framework.entity.DeviceManager;
import com.platon.framework.network.ApiResponse;
import com.platon.framework.network.NetConnectivity;
import com.platon.framework.utils.AndroidUtil;
import com.platon.framework.utils.LogUtils;
import com.platon.framework.utils.PreferenceTool;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;


public class BaseAppDeletage {

    private Application mApplication;

    //static 代码段可以防止内存泄露
    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                // 指定为经典Header，默认是 贝塞尔雷达Header
                return new ClassicsHeader(context);
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setDrawableSize(20);
            }
        });
    }

    public BaseAppDeletage(Application application) {
        mApplication = application;
    }

    public void onCreate() {

        if (!AndroidUtil.isMainProcess(mApplication)) {
            Log.d("CoreApp", "start CoreApp not called by main process, so skip!!");
            return;
        }
        //Log日志
        LogUtils.setLogEnable(BuildConfig.DEBUG);
        //Preference参数
        PreferenceTool.init(mApplication);
        //数据库香相关的初始化
        //网络状态变化监听
        NetConnectivity.getConnectivityManager().init(mApplication);
        //初始化异常组件
        CustomerException.getExceptionControl().init(context);
        // 初始化网络组件
        NetConnectivity.getConnectivityManager().init(this);
        //初始化请求响应类
        ApiResponse.init(context);
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
}
