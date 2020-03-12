package com.platon.aton.component.ui.base;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.gyf.immersionbar.ImmersionBar;
import com.platon.framework.app.activity.CoreFragmentActivity;
import com.platon.framework.util.LogUtils;
import com.platon.aton.BuildConfig;
import com.platon.aton.R;
import com.platon.aton.component.ui.BaseContextImpl;
import com.platon.aton.component.ui.CustomContextWrapper;
import com.platon.aton.component.ui.IContext;
import com.platon.aton.utils.LanguageUtil;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.RxLifecycleAndroid;
import com.umeng.analytics.MobclickAgent;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.BehaviorSubject;


/**
 * @author matrixelement
 */
public abstract class BaseActivity extends CoreFragmentActivity implements IContext, LifecycleProvider<ActivityEvent> {

    public static String DEFAULT_COVERAGE_FILE_PATH = Environment.getExternalStorageDirectory() + "/";

    private final BehaviorSubject<ActivityEvent> lifecycleSubject = BehaviorSubject.create();
    private InputMethodManager mInputMethodManager;
    protected View mDecorView;
    protected ViewGroup mRootView;
    protected CompositeDisposable mCompositeDisposable;
    private int mDefaultStatusBarColor = R.color.color_ffffff;

    @Override
    @NonNull
    @CheckResult
    public final Observable<ActivityEvent> lifecycle() {
        return lifecycleSubject.hide();
    }

    @Override
    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindUntilEvent(@NonNull ActivityEvent event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event);
    }

    @Override
    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycleAndroid.bindActivity(lifecycleSubject);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        Context context = CustomContextWrapper.wrap(newBase, LanguageUtil.getLocale(newBase));
        super.attachBaseContext(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifecycleSubject.onNext(ActivityEvent.CREATE);
        mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mDecorView = getWindow().getDecorView();
        mRootView = mDecorView.findViewById(android.R.id.content);
        mCompositeDisposable = new CompositeDisposable();
        if (immersiveBarInitEnabled()) {
            if (immersiveBarViewEnabled()) {
                setStatusBarView();
            } else {
                setStatusBarColor(getStatusBarColor());
            }
        }
    }

    protected View getStatusBarView() {
        View view = new View(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        view.setLayoutParams(layoutParams);
        return view;
    }

    protected void setStatusBarView() {
        ImmersionBar.with(this).statusBarDarkFont(true, 0.2f).statusBarView(getStatusBarView()).init();
    }

    protected void setStatusBarColor(int colorRes) {
        ImmersionBar.with(this)
                .statusBarColor(colorRes)
                .statusBarDarkFont(true, 0.2f)
                .init();
    }

    protected int getStatusBarColor() {
        return mDefaultStatusBarColor;
    }

    public ViewGroup getContentView() {
        return (ViewGroup) mRootView.getChildAt(0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        lifecycleSubject.onNext(ActivityEvent.START);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (immersiveBarInitEnabled()) {
            if (immersiveBarViewEnabled()) {
                setStatusBarView();
            } else {
                setStatusBarColor(getStatusBarColor());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        lifecycleSubject.onNext(ActivityEvent.RESUME);
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        lifecycleSubject.onNext(ActivityEvent.PAUSE);
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        lifecycleSubject.onNext(ActivityEvent.STOP);
        super.onStop();
    }

    @Override
    protected void onDestroy() {

        lifecycleSubject.onNext(ActivityEvent.DESTROY);

        if (mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
        }

        super.onDestroy();

        if (BuildConfig.DEBUG) {
            generateCoverageFile();
        }
    }

    protected boolean immersiveBarInitEnabled() {
        return true;
    }

    protected boolean immersiveBarViewEnabled() {
        return false;
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
            return BaseActivity.this;
        }

        @Override
        public BaseActivity currentActivity() {
            return BaseActivity.this;
        }
    };

    /**
     * 隐藏软键盘
     */
    public void hideSoftInput() {
        if (mInputMethodManager != null && mDecorView != null) {
            mInputMethodManager.hideSoftInputFromWindow(mDecorView.getWindowToken(), 0);
        }
    }


    /**
     * 隐藏输入软键盘
     *
     * @param context
     * @param view
     */
    public void hideSoftInput(Context context, View view) {
        if (mInputMethodManager != null && view != null) {
            mInputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 显示软键盘
     */
    public void showSoftInput(final View view) {
        if (mInputMethodManager != null && view != null) {
            view.requestFocus();
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mInputMethodManager.showSoftInput(view, 0);
                }
            }, 100);
        }
    }

    public void toggleSoftInput(View view) {
        if (mInputMethodManager != null && mDecorView != null) {
            mInputMethodManager.toggleSoftInput(0, 0);
        }
    }

    /**
     * 生成executionData
     */
    public void generateCoverageFile() {

        OutputStream out = null;

        try {
            //在SDcard根目录下生产检测报告，文件名自定义
            out = new FileOutputStream(DEFAULT_COVERAGE_FILE_PATH + "/coverage.ec", false);
            Object agent = Class.forName("org.jacoco.agent.rt.RT").getMethod("getAgent").invoke(null);
            // 这里之下就统计不到了
            out.write((byte[]) agent.getClass().getMethod("getExecutionData", boolean.class).invoke(agent, false));

            LogUtils.d("BaseActivity.java BaseActivity generateCoverageFile write success");
        } catch (Exception e) {
            LogUtils.d("BaseActivity.java BaseActivity generateCoverageFile Exception:" + e.toString());

        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
