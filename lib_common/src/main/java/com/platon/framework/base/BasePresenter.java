package com.platon.framework.base;

import android.content.Context;
import android.support.annotation.NonNull;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.lang.ref.WeakReference;

/**
 * @author matrixelement
 * @date 2017/12/25
 */

public class BasePresenter<V extends BaseViewImp> implements IPresenter<V> {

    private WeakReference<V> mViewReference;

    @Override
    public V getView() {
        return mViewReference.get();
    }

    @Override
    public void attachView(V v) {
        this.mViewReference = new WeakReference<V>(v);
    }

    @Override
    public void detachView() {
        if (mViewReference != null) {
            mViewReference.clear();
            mViewReference = null;
        }
    }

    @Override
    public boolean isViewAttached() {
        return mViewReference != null && mViewReference.get() != null;
    }

    public LifecycleProvider<?> getLifecycleProvider() {
        if (getView() instanceof BaseActivity) {
            return (BaseActivity) getView();
        } else {
            return (BaseFragment) getView();
        }
    }

    public final <T> LifecycleTransformer<T> bindToLifecycle() {
        if (getView() instanceof BaseActivity) {
            return ((BaseActivity) getView()).bindToLifecycle();
        } else {
            return ((BaseFragment) getView()).bindToLifecycle();
        }
    }

    public final <T> LifecycleTransformer<T> bindUntilEvent(@NonNull FragmentEvent event) {
        if (getView() instanceof BaseFragment) {
            return ((BaseFragment) getView()).bindUntilEvent(event);
        } else {
            return bindToLifecycle();
        }
    }

    protected Context getContext() {
        return getView().getContext();
    }

    protected BaseActivity currentActivity() {
        return getView().currentActivity();
    }

    protected String string(int resId, Object... formatArgs) {
        return getView().string(resId, formatArgs);
    }

    protected void showShortToast(String text) {
        getView().showShortToast(text);
    }

    protected void showLongToast(String text) {
        getView().showLongToast(text);
    }

    protected void showShortToast(int resId) {
        getView().showShortToast(resId);
    }

    protected void showLongToast(int resId) {
        getView().showLongToast(resId);
    }

    protected void dismissLoadingDialogImmediately() {
        getView().dismissLoadingDialogImmediately();
    }

    protected void showLoadingDialog() {
        getView().showLoadingDialog();
    }

    protected void showLoadingDialog(int resId) {
        getView().showLoadingDialog(resId);
    }

    protected void showLoadingDialog(String text) {
        getView().showLoadingDialog(text, false);
    }

    protected void showLoadingDialogWithCancelable(String text) {
        getView().showLoadingDialogWithCancelable(text);
    }
}
