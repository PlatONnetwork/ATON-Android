package com.juzix.wallet.component.ui;

import android.content.Context;

import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.config.PermissionConfigure;


public interface IContext {

    Context getContext();

    BaseActivity currentActivity();

    String string(int resId, Object... formatArgs);

    void showShortToast(String text);

    void showLongToast(String text);

    void showShortToast(int resId);

    void showLongToast(int resId);

    void dismissLoadingDialogImmediately();

    void showLoadingDialog();

    void showLoadingDialog(int resId);

    void showLoadingDialog(String text, boolean cancelable);

    void showLoadingDialog(String text);

    /**
     * 允许用户取消
     *
     * @param text
     */
    void showLoadingDialogWithCancelable(String text);

    /**
     * 获取应用权限
     *
     * @param activity
     * @param what
     * @param callback
     * @param permissions
     */
    void requestPermission(BaseActivity activity, int what, PermissionConfigure.PermissionCallback callback,
                           String... permissions);
}
