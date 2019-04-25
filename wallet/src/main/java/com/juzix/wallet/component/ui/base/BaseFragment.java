package com.juzix.wallet.component.ui.base;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.juzhen.framework.app.activity.CoreFragment;
import com.juzhen.framework.app.log.Log;
import com.juzix.wallet.component.ui.BaseContextImpl;
import com.juzix.wallet.component.ui.IContext;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.android.RxLifecycleAndroid;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;


/**
 * 因为首页TabHost采用Hide和show的方式,会干扰Fragment原生的生命周期回调,
 * 这里采用onTabShown和onTabHidden替代原生的onStart和onPause
 * <p>
 * 通过Hide和show方式使用fragment，第一次会走正常的生命周期 onAttach onCreate onActivityCreated onStart onResume,然后切换时只会回调
 * onHiddenChanged
 *
 * @author matrixelement
 */
public class BaseFragment extends CoreFragment implements IContext, LifecycleProvider<FragmentEvent> {

    private static final String TAG = BaseFragment.class.getSimpleName();

    private boolean mResumedForFirstTime;

    private final BehaviorSubject<FragmentEvent> lifecycleSubject = BehaviorSubject.create();

    @Override
    @NonNull
    @CheckResult
    public final Observable<FragmentEvent> lifecycle() {
        return lifecycleSubject.hide();
    }

    @Override
    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindUntilEvent(@NonNull FragmentEvent event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event);
    }

    @Override
    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycleAndroid.bindFragment(lifecycleSubject);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        lifecycleSubject.onNext(FragmentEvent.ATTACH);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifecycleSubject.onNext(FragmentEvent.CREATE);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lifecycleSubject.onNext(FragmentEvent.CREATE_VIEW);
    }

    @Override
    public void onStart() {
        super.onStart();
        lifecycleSubject.onNext(FragmentEvent.START);
        if (!mResumedForFirstTime) {
            mResumedForFirstTime = true;
        }
        if (canBeShown()) {
            onTabShown();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        lifecycleSubject.onNext(FragmentEvent.RESUME);
    }

    @Override
    public void onPause() {
        lifecycleSubject.onNext(FragmentEvent.PAUSE);
        super.onPause();
    }

    @Override
    public void onStop() {
        lifecycleSubject.onNext(FragmentEvent.STOP);
        super.onStop();
        onTabHidden();
    }

    @Override
    public void onDestroyView() {
        lifecycleSubject.onNext(FragmentEvent.DESTROY_VIEW);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        lifecycleSubject.onNext(FragmentEvent.DESTROY);
        super.onDestroy();
        mResumedForFirstTime = false;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.debug(TAG, "[" + getClass().getSimpleName() + "]: onHiddenChanged " + hidden);
        if (hidden) {
            lifecycleSubject.onNext(FragmentEvent.STOP);
            onTabHidden();
        } else {
            lifecycleSubject.onNext(FragmentEvent.RESUME);
            onTabShown();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    /**
     * tab页展示时回调
     */
    public void onTabShown() {
        Log.debug(TAG, "[" + getClass().getSimpleName() + "]: onTabShow");
    }

    /**
     * tab页隐藏时回调
     */
    public void onTabHidden() {
        Log.debug(TAG, "[" + getClass().getSimpleName() + "]: onTabHidden");
    }

    // 当前Fragment是否显示，或者需要被显示
    // 当执行onResume方法后，就使用mHidden变量来替代
    private boolean canBeShown() {
        if (mResumedForFirstTime) {
            return !mHidden;
        } else {
            return mCreate;
        }
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
        mContextImpl.showLoadingDialog();
    }

    @Override
    public void showLoadingDialog(int resId) {
        mContextImpl.showLoadingDialog(resId);
    }

    @Override
    public void showLoadingDialog(String text, boolean cancelable) {
        mContextImpl.showLoadingDialog(text, cancelable);
    }

    @Override
    public void showLoadingDialog(String text) {
        mContextImpl.showLoadingDialog(text, false);
    }

    @Override
    public void showLoadingDialogWithCancelable(String text) {
        mContextImpl.showLoadingDialogWithCancelable(text);
    }

    private BaseContextImpl mContextImpl = new BaseContextImpl() {
        @Override
        public Context getContext() {
            return getActivity();
        }

        @Override
        public BaseActivity currentActivity() {
            return (BaseActivity) getActivity();
        }
    };
}
