package com.platon.framework.app;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.platon.framework.app.config.Config;
import com.platon.framework.app.config.ConfigModule;
import com.platon.framework.app.exception.CustomerException;
import com.platon.framework.app.log.LogManager;
import com.platon.framework.network.ApiResponse;
import com.platon.framework.network.NetConnectivity;
import com.platon.framework.util.AndroidUtil;

/**
 * @author ziv
 */
public abstract class CoreApp extends Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();

        if (!AndroidUtil.isMainProcess(this)) {
            Log.d("CoreApp", "start CoreApp not called by main process, so skip!!");
            return;
        }
        // 初始化config
        Config.getInstance().init(context, "config/config.xml");
        //初始化日志
        LogManager.getInstance().init(context, getLogPath());
        //初始化异常组件
        CustomerException.getExceptionControl().init(context);
        // 初始化网络组件
        NetConnectivity.getConnectivityManager().init(this);
        //初始化请求响应类
        ApiResponse.init(context);
    }

    private String getLogPath() {
        // 通过config的配置，实现动态初始化LogManager
        String logPath = "config/log.xml";
        ConfigModule module = Config.getInstance().getMoudle("Engine");
        if (module != null) {
            // String type = module.getStringItem("releaseType", "");
            String type = getConfiguredReleaseType();
            String logCfgPath = module.getStringItem(type + ".logConfig", "");
            if (!"".equals(type) && !"".equals(logCfgPath)) {
                logPath = logCfgPath;
                android.util.Log.d("CoreApp", "logPath: " + logPath);
            } else {
                android.util.Log.w("CoreApp", "config/engine.xml文件缺少相关字段!");
            }
        }
//        // 使用adb shell getprop debug.aton.log.enable 来查看该系统属性，如果为1，则强制开启log
//        // 若需要在app运行时，强制开启log，只需要设置debug.aton.log.enable为1,然后重启app即可
//        // (执行 adb shell setprop debug.aton.log.enable 1)
//        if ("1".equals(AndroidUtil.getProperties("debug.aton.log.enable"))) {
//            logPath = "config/log_debug.xml";
//        }

        return logPath;
    }

    protected abstract String getConfiguredReleaseType();

}
