package com.platon.framework.base;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.gyf.immersionbar.ImmersionBar;
import com.platon.framework.BuildConfig;
import com.platon.framework.R;
import com.platon.framework.app.BaseContextImpl;
import com.platon.framework.app.IContext;
import com.platon.framework.utils.LogUtils;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.umeng.analytics.MobclickAgent;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 父类->基类->动态指定类型->泛型设计（通过泛型指定动态类型->由子类指定，父类只需要规定范围即可）
 */
public abstract class BaseActivity<V extends IView, P extends BasePresenter<V>> extends RxAppCompatActivity implements IContext {

    //引用V层和P层
    private P presenter;
    private V view;
    private InputMethodManager mInputMethodManager;
    protected View mDecorView;
    protected View mRootView;
    private int mDefaultStatusBarColor = android.R.color.white;

    public P getPresenter() {
        return presenter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getLayoutId());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 禁止所有的activity横屏
        ActivityManager.getInstance().addManagedActivity(this);
//        ARouter.getInstance().inject(this);
//        PushAgent.getInstance(this).onAppStart();
        if (presenter == null) {
            presenter = createPresenter();
        }
        if (view == null) {
            view = createView();
        }
        if (presenter != null && view != null) {
            presenter.attachView(view);
        }
        mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mDecorView = getWindow().getDecorView();
        mRootView = ((ViewGroup) (mDecorView.findViewById(android.R.id.content))).getChildAt(0);
        init();
        if (immersiveBarInitEnabled()) {
            if (immersiveBarViewEnabled()) {
                setStatusBarView();
            } else {
                setStatusBarColor(getStatusBarColor());
            }
        }
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

    protected int getStatusBarColor() {
        return mDefaultStatusBarColor;
    }

    protected View getStatusBarView() {
        View view = new View(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        view.setLayoutParams(layoutParams);
        return view;
    }

    protected void setStatusBarView() {
        ImmersionBar.with(this)
                .statusBarDarkFont(true, 0.2f)
                .statusBarView(getStatusBarView())
                .fitsSystemWindows(true)
                .init();
    }

    protected void setStatusBarColor(int colorRes) {
        ImmersionBar.with(this)
                .statusBarColor(colorRes)
                .statusBarDarkFont(true, 0.2f)
                .init();
    }

    //由子类指定具体类型
    public abstract int getLayoutId();

    public abstract P createPresenter();

    public abstract V createView();

    public abstract void init();

    protected boolean immersiveBarInitEnabled() {
        return true;
    }

    protected boolean immersiveBarViewEnabled() {
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        ActivityManager.getInstance().setIsForeGround(true);
        ActivityManager.getInstance().setCurrActivity(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        ActivityManager.getInstance().setIsForeGround(false);
        // 及时置空，避免内存溢出
        ActivityManager.getInstance().setCurrActivity(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.getInstance().removeManagedActivity(this);
        if (presenter != null) {
            presenter.detachView();
        }
        if (BuildConfig.DEBUG) {
            generateCoverageFile();
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
            return BaseActivity.this;
        }
    };

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
     * 隐藏软键盘
     */
    public void hideSoftInput() {
        if (mInputMethodManager != null && mDecorView != null) {
            mInputMethodManager.hideSoftInputFromWindow(mDecorView.getWindowToken(), 0);
        }
    }

    /**
     * 显示软键盘
     */
    public void showSoftInput(final EditText editText) {
        if (mInputMethodManager != null && mDecorView != null) {
            editText.requestFocus();
            //延迟弹出软键盘防止view还没未初始化完毕
            mDecorView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mInputMethodManager.showSoftInput(editText, 0);
                }
            }, 100);
        }
    }


    public void showSoftInput(final WebView webView) {
        if (mInputMethodManager != null && mDecorView != null) {
            webView.requestFocus();
            //延迟弹出软键盘防止view还没未初始化完毕
            mDecorView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mInputMethodManager.showSoftInput(webView, 0);
                }
            }, 100);
        }
    }

    /**
     * 生成executionData
     */
    public void generateCoverageFile() {

        String filePath = getFilesDir().getAbsolutePath();

        OutputStream out = null;

        try {
            //在SDcard根目录下生产检测报告，文件名自定义
            out = new FileOutputStream(filePath + "/coverage.ec", false);
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
