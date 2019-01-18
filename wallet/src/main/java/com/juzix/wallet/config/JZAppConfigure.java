package com.juzix.wallet.config;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.juzhen.framework.app.config.AppConfigure;
import com.juzhen.framework.app.log.TUncaughtExceptionHandler;

import java.io.File;
import java.util.List;

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
    public void getDir(final Activity activity, final String dirType, final DirCallback callback) {
        String[] params = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,};
        PermissionConfigure.request(activity, 100, new PermissionConfigure.PermissionCallback() {
            @Override
            public void onSuccess(int what, @NonNull List<String> grantPermissions) {
                setConfigure(activity.getApplication());
                if (callback != null) {
                    callback.callback(AppConfigure.getDir(dirType));
                }
            }

            @Override
            public void onHasPermission(int what) {
                setConfigure(activity.getApplication());
                if (callback != null) {
                    callback.callback(AppConfigure.getDir(dirType));
                }
            }

            @Override
            public void onFail(int what, @NonNull List<String> deniedPermissions) {
//                showLongToast(deniedPermissions.toString());
                if (callback != null) {
                    callback.callback(null);
                }
            }
        }, params);
    }

    /**
     * 获取用户目录路径
     *
     * @param userID
     * @param userDirType 定义在{@link JZUserDirType}或其子类
     * @return
     */
    public void getUserDir(final Activity activity, final String userID, final String userDirType, final DirCallback callback) {
        String[] params = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,};
        PermissionConfigure.request(activity, 100, new PermissionConfigure.PermissionCallback() {
            @Override
            public void onSuccess(int what, @NonNull List<String> grantPermissions) {
                setConfigure(activity.getApplication());
                if (callback != null) {
                    callback.callback(createUserFile(userID, userDirType));
                }
            }

            @Override
            public void onHasPermission(int what) {
                setConfigure(activity.getApplication());
                if (callback != null) {
                    callback.callback(createUserFile(userID, userDirType));
                }
            }

            @Override
            public void onFail(int what, @NonNull List<String> deniedPermissions) {
//                showLongToast(deniedPermissions.toString());
                if (callback != null) {
                    callback.callback(null);
                }
            }
        }, params);
    }


    /**
     * 获取用户图片目录
     *
     * @param userID
     * @param toUserId 定义在{@link JZUserDirType}或其子类
     * @return
     */
    public void getUserImageDir(final Activity activity, final String userID, final String toUserId, final DirCallback callback) {
        String[] params = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,};
        PermissionConfigure.request(activity, 100, new PermissionConfigure.PermissionCallback() {
            @Override
            public void onSuccess(int what, @NonNull List<String> grantPermissions) {
                setConfigure(activity.getApplication());
                if (callback != null) {
                    callback.callback(createUserImageFile(userID, toUserId));
                }
            }

            @Override
            public void onHasPermission(int what) {
                setConfigure(activity.getApplication());
                if (callback != null) {
                    callback.callback(createUserImageFile(userID, toUserId));
                }
            }

            @Override
            public void onFail(int what, @NonNull List<String> deniedPermissions) {
//                showLongToast(deniedPermissions.toString());
                if (callback != null) {
                    callback.callback(null);
                }
            }
        }, params);
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
