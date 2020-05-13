package com.platon.aton.component.ui.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;

import com.platon.aton.BuildConfig;
import com.platon.aton.R;
import com.platon.aton.engine.NodeManager;
import com.platon.aton.engine.WalletManager;
import com.platon.aton.entity.WebType;
import com.platon.framework.app.Constants;
import com.platon.framework.base.BaseActivity;
import com.platon.framework.base.BasePresenter;
import com.platon.framework.base.BaseViewImp;
import com.platon.framework.utils.LogUtils;
import com.platon.framework.utils.PreferenceTool;

public class SplashActivity extends BaseActivity {

    @Override
    public int getLayoutId() {
        //FIX: 以下代码是为了解决Android自level 1以来的[安装完成点击“Open”后导致的应用被重复启动]的Bug
        if (!isTaskRoot()) {
            final Intent intent = getIntent();
            final String intentAction = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) &&
                    intentAction != null && intentAction.equals(Intent.ACTION_MAIN)) {
                finish();
            }
        }
        return R.layout.activity_splash;
    }

    @Override
    public BasePresenter createPresenter() {
        return null;
    }

    @Override
    public BaseViewImp createView() {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void init() {
        //适配刘海屏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            View decorView = window.getDecorView();
            decorView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
                @Override
                public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                    WindowInsets defaultInsets = v.onApplyWindowInsets(insets);
                    return defaultInsets.replaceSystemWindowInsets(
                            defaultInsets.getSystemWindowInsetLeft(),
                            0,
                            defaultInsets.getSystemWindowInsetRight(),
                            defaultInsets.getSystemWindowInsetBottom());
                }
            });
            ViewCompat.requestApplyInsets(decorView);
            //将状态栏设成透明，如不想透明可设置其他颜色
            window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent));
        }

        mDecorView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (PreferenceTool.getBoolean(Constants.Preference.KEY_FIRST_ENTER, true)) {
                    CommonHybridActivity.actionStart(SplashActivity.this, getResources().getString(R.string.web_url_agreement, NodeManager.getInstance().getCurNodeAddress()), WebType.WEB_TYPE_AGREEMENT, true);
                    SplashActivity.this.finish();
                    return;
                }

                if (WalletManager.getInstance().getWalletList().isEmpty()) {
                    OperateMenuActivity.actionStart(SplashActivity.this);
                    SplashActivity.this.finish();
                    return;
                }
                if (PreferenceTool.getBoolean(Constants.Preference.KEY_FACE_TOUCH_ID_FLAG, false) &&
                        !WalletManager.getInstance().getWalletList().isEmpty()) {
                    UnlockFigerprintActivity.actionStartMainActivity(SplashActivity.this);
                    SplashActivity.this.finish();
                    return;
                }
                MainActivity.actionStart(SplashActivity.this);
                SplashActivity.this.finish();
            }
        }, 1000);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}
