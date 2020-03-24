package com.platon.framework.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class MVPBaseFragment<T extends BasePresenter> extends BaseFragment {

    /**
     * presenter对象
     */
    protected T mPresenter;

    @Override
    protected abstract T createPresenter();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.mPresenter = createPresenter();
        return onCreateFragmentPage(inflater, container, savedInstanceState);
    }

    @Override
    public void onTabShown() {
        super.onTabShown();
        onFragmentPageStart();
    }

    @Override
    public void onTabHidden() {
        super.onTabHidden();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

    /**
     * 每次在视图可见的时候，用于加载数据，比如启动网络数据的加载.
     * 调用场景：
     * 1.切换到对应当前页签时
     * 2.从其他Activity返回到当前Activity时
     */
    protected abstract void onFragmentPageStart();

    /**
     * 创建视图
     */
    protected abstract View onCreateFragmentPage(LayoutInflater inflater, @Nullable ViewGroup container,
                                                 @Nullable Bundle savedInstanceState);
}
