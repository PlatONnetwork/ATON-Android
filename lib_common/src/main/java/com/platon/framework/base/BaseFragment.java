package com.platon.framework.base;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gyf.immersionbar.components.ImmersionOwner;
import com.gyf.immersionbar.components.ImmersionProxy;
import com.platon.framework.R;
import com.platon.framework.app.BaseContextImpl;
import com.platon.framework.app.IContext;
import com.trello.rxlifecycle2.components.support.RxFragment;

/**
 * 父类->基类->动态指定类型->泛型设计（通过泛型指定动态类型->由子类指定，父类只需要规定范围即可）
 *
 * @author ziv
 */
public abstract class BaseFragment<V extends BaseViewImp, P extends BasePresenter<V>> extends RxFragment implements ImmersionOwner, IContext {

    /**
     * 引用V层和P层
     */
    private P presenter;
    private V view;
    public Context mContext;
    private View rootView;
    /**
     * ImmersionBar代理类
     */
    private ImmersionProxy mImmersionProxy = new ImmersionProxy(this);

    public P getPresenter() {
        return presenter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImmersionProxy.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        rootView = view;
        mContext = getActivity();
        if (presenter == null) {
            presenter = createPresenter();
        }
        if (this.view == null) {
            this.view = createView();
        }
        if (presenter != null && view != null) {
            presenter.attachView(this.view);
        }

        init(view);

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mImmersionProxy.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mImmersionProxy.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mImmersionProxy.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mImmersionProxy.onPause();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mImmersionProxy.onDestroy();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        mImmersionProxy.onHiddenChanged(hidden);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mImmersionProxy.onConfigurationChanged(newConfig);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null) {
            presenter.detachView();
        }
    }

    /**
     * 懒加载，在view初始化完成之前执行
     * On lazy after view.
     */
    @Override
    public void onLazyBeforeView() {

    }

    /**
     * 懒加载，在view初始化完成之后执行
     * On lazy before view.
     */
    @Override
    public void onLazyAfterView() {

    }

    /**
     * Fragment用户可见时候调用
     * On visible.
     */
    @Override
    public void onVisible() {

    }

    /**
     * Fragment用户不可见时候调用
     * On invisible.
     */
    @Override
    public void onInvisible() {
    }

    /**
     * 是否可以实现沉浸式，当为true的时候才可以执行initImmersionBar方法
     * Immersion bar enabled boolean.
     *
     * @return the boolean
     */
    @Override
    public boolean immersionBarEnabled() {
        return true;
    }

    @Override
    public void initImmersionBar() {

    }

    //由子类指定具体类型
    public abstract int getLayoutId();

    public abstract P createPresenter();

    public abstract V createView();

    public abstract void init(View rootView);

    public View getRootView() {
        return rootView;
    }


    @Override
    public Context getContext() {
        return mContextImpl.getContext();
    }

    @Override
    public BaseActivity currentActivity() {
        return mContextImpl.currentActivity();
    }

    @Override
    public String string(int resId, Object... formatArgs) {
        return mContextImpl.string(resId, formatArgs);
    }

    @Override
    public void showShortToast(String text) {
        mContextImpl.showShortToast(text);
    }

    @Override
    public void showLongToast(String text) {
        mContextImpl.showLongToast(text);
    }

    @Override
    public void showShortToast(int resId) {
        mContextImpl.showShortToast(resId);
    }

    @Override
    public void showLongToast(int resId) {
        mContextImpl.showLongToast(resId);
    }


    @Override
    public void dismissLoadingDialogImmediately() {
        mContextImpl.dismissLoadingDialogImmediately();
    }

    @Override
    public void showLoadingDialog() {
        showLoadingDialog(string(R.string.loading));
    }

    @Override
    public void showLoadingDialog(int resId) {
        showLoadingDialog(string(resId));
    }

    @Override
    public void showLoadingDialog(String text) {
        showLoadingDialog(text, false);
    }

    @Override
    public void showLoadingDialogWithCancelable(String text) {
        showLoadingDialog(text, true);
    }

    @Override
    public void showLoadingDialog(String text, boolean cancelable) {
        mContextImpl.showLoadingDialog(text, cancelable);
    }

    private BaseContextImpl mContextImpl = new BaseContextImpl() {
        @Override
        public Context getContext() {
            return getContext();
        }

        @Override
        public BaseActivity currentActivity() {
            return (BaseActivity) getActivity();
        }
    };


}