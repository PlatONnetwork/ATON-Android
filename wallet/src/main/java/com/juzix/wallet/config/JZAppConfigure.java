package com.juzix.wallet.config;

import android.Manifest;
import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.juzhen.framework.app.config.AppConfigure;
import com.juzhen.framework.app.log.TUncaughtExceptionHandler;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;

import io.reactivex.functions.Consumer;

/**
 * 全局变量配置
 */
public class JZAppConfigure {

    private JZAppConfigure() {

    }

    public static JZAppConfigure getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * 初始化全局变量配置
     */
    public void init(Context context) {
        AppConfigure.init(context);
    }

    /**
     * 获取全局Context
     *
     * @return
     */
    public Context getAppContext() {
        return AppConfigure.getAppContext();
    }

    /**
     * 获取目录路径
     *
     * @param dirType 定义在{@link JZDirType}或其子类
     * @return
     */
    public void getDir(final FragmentActivity activity, final String dirType, final DirCallback callback) {
        new RxPermissions(activity)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean success) throws Exception {
                        if (success) {
                            setConfigure(activity.getApplication());
                            if (callback != null) {
                                callback.callback(AppConfigure.getDir(dirType));
                            }
                        } else {
                            if (callback != null) {
                                callback.callback(null);
                            }
                        }
                    }
                });
    }

    /**
     * 获取用户目录路径
     *
     * @param userID
     * @param userDirType 定义在{@link JZUserDirType}或其子类
     * @return
     */
    public void getUserDir(final FragmentActivity activity, final String userID, final String userDirType, final DirCallback callback) {
        new RxPermissions(activity)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean success) throws Exception {
                        if (success) {
                            setConfigure(activity.getApplication());
                            if (callback != null) {
                                callback.callback(createUserFile(userID, userDirType));
                            }
                        } else {
                            if (callback != null) {
                                callback.callback(null);
                            }
                        }
                    }
                });
    }


    /**
     * 获取用户图片目录
     *
     * @param userID
     * @param toUserId 定义在{@link JZUserDirType}或其子类
     * @return
     */
    public void getUserImageDir(final FragmentActivity activity, final String userID, final String toUserId, final DirCallback callback) {

        new RxPermissions(activity)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean success) throws Exception {
                        if (success) {
                            setConfigure(activity.getApplication());
                            if (callback != null) {
                                callback.callback(createUserImageFile(userID, toUserId));
                            }
                        } else {
                            if (callback != null) {
                                callback.callback(null);
                            }
                        }
                    }
                });
    }

    private File createUserFile(String userID, String userDirType) {

        File f = null;

        try {

            File file = new File(AppConfigure.getDir(JZDirType.user), userID);

            if (!file.exists()) {
                file.mkdirs();
            }

            f = new File(file, userDirType);
            if (!f.exists()) {
                f.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return f;
    }

    private File createUserImageFile(String userID, String toUserId) {

        File userImageFile = null;
        try {
            userImageFile = new File(createUserFile(userID, JZUserDirType.image), toUserId);

            if (!userImageFile.exists()) {
                userImageFile.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return userImageFile;
    }

    private void setConfigure(Context context) {
        AppConfigure.setConfigure(new JZDirectroyContext(context), new TUncaughtExceptionHandler(context) {
            @Override
            protected boolean handleException(Thread thread, Throwable ex) {
                //捕获全局异常，可以在此将异常文件发送至服务器上
                return super.handleException(thread, ex);
            }
        });
    }

    private static class InstanceHolder {
        private static volatile JZAppConfigure INSTANCE = new JZAppConfigure();
    }

    public interface DirCallback {
        void callback(File dir);
    }
}
