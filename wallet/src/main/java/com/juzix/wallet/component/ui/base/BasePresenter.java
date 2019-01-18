package com.juzix.wallet.component.ui.base;

import android.content.Context;

import com.juzix.wallet.component.ui.IContext;
import com.juzix.wallet.config.PermissionConfigure;
import com.trello.rxlifecycle2.LifecycleTransformer;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * @author matrixelement
 * @date 2017/12/25
 */

public class BasePresenter<T extends IView> implements IPresenter<T>, IContext {

    private Reference<T> mViewReference;

    public BasePresenter(T view) {
        attachView(view);
    }

    @Override
    public void attachView(T t) {
        this.mViewReference = new WeakReference<T>(t);
    }

    /**
     * 在调用该方法之前，应该调用isViewAttached进行判断
     *
     * @return
     */
    @Override
    public T getView() {
        return mViewReference.get();
    }

    @Override
    public Boolean isViewAttached() {
        return mViewReference != null && mViewReference.get() != null;
    }

    @Override
    public void detachView() {
        if (mViewReference != null) {
            mViewReference.clear();
            mViewReference = null;
        }
    }

    @Override
    public Context getContext() {
        return getView().getContext();
    }

    @Override
    public BaseActivity currentActivity() {
        return getView().currentActivity();
    }

    @Override
    public String string(int resId, Object... formatArgs) {
        return getView().string(resId, formatArgs);
    }

    @Override
    public void showShortToast(String text) {
        getView().showShortToast(text);
    }

    @Override
    public void showLongToast(String text) {
        getView().showLongToast(text);
    }

    @Override
    public void showShortToast(int resId) {
        getView().showShortToast(resId);
    }

    @Override
    public void showLongToast(int resId) {
        getView().showLongToast(resId);
    }

    @Override
    public void dismissLoadingDialogImmediately() {
        getView().dismissLoadingDialogImmediately();
    }

    @Override
    public void showLoadingDialog() {
        getView().showLoadingDialog();
    }

    @Override
    public void showLoadingDialog(int resId) {
        getView().showLoadingDialog(resId);
    }

    @Override
    public void showLoadingDialog(String text) {
        getView().showLoadingDialog(text);
    }

    @Override
    public void showLoadingDialogWithCancelable(String text) {
        getView().showLoadingDialogWithCancelable(text);
    }

    @Override
    public void requestPermission(BaseActivity activity, int what, PermissionConfigure.PermissionCallback callback, String... permissions) {
        getView().requestPermission(activity, what, callback, permissions);
    }
}
