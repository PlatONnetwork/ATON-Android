package com.platon.aton.component.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.platon.framework.app.log.Log;

/**
 * viewPager+Fragment使用
 *
 * @author matrixelement
 */
public abstract class BaseViewPageFragment<T extends BasePresenter> extends BaseFragment {

    private static final String TAG = BaseViewPageFragment.class.getSimpleName();
    private View mRootView;
    private boolean mVisibleToUser;

    /**
     * presenter对象
     */
    protected T mPresenter;

    protected abstract T createPresenter();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mPresenter = createPresenter();
        if (mRootView == null) {
            Log.debug(TAG, "onCreateView root is null !! [" + getClass().getSimpleName() + "]");
            mRootView = onCreatePage(inflater, container, savedInstanceState);
        } else {
            Log.debug(TAG, "onCreateView root reused !! [" + getClass().getSimpleName() + "]");
            ViewParent parent = mRootView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(mRootView);
            }
        }
        return mRootView;
    }

    /**
     * 每次在视图可见的时候，用于加载数据，比如启动网络数据的加载.
     * 调用场景：
     * 1.切换到对应当前页签时
     * 2.从其他Activity返回到当前Activity时
     */
    public abstract void onPageStart();

    /**
     * 创建视图
     */
    protected abstract View onCreatePage(LayoutInflater inflater, @Nullable ViewGroup container,
                                         @Nullable Bundle savedInstanceState);

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mVisibleToUser = isVisibleToUser;
        Log.debug(TAG, "setUserVisibleHint isVisibleToUser=" + isVisibleToUser + ", [" + getClass().getSimpleName() + "]");
        if (isVisibleToUser && mResumed) {
            Log.debug(TAG, "onPageStart !! [" + getClass().getSimpleName() + "]");
            onPageStart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.debug(TAG, "onResume !! mVisibleToUser =" + mVisibleToUser + ", [" + getClass().getSimpleName() + "]");
        if (mVisibleToUser) {
            Log.debug(TAG, "onPageStart !! [" + getClass().getSimpleName() + "]");
            onPageStart();
        }
    }

}
