package com.platon.wallet.component.ui.base;

import android.content.Context;
import android.support.annotation.NonNull;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * @author matrixelement
 * @date 2017/12/25
 */

public class BasePresenter<T extends IView> implements IPresenter<T> {

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
