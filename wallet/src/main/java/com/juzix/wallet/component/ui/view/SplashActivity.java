package com.juzix.wallet.component.ui.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;

import com.juzix.wallet.R;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.config.AppSettings;
import com.juzix.wallet.engine.NodeManager;
import com.juzix.wallet.engine.WalletManager;
import com.juzix.wallet.entity.WebType;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FIX: 以下代码是为了解决Android自level 1以来的[安装完成点击“Open”后导致的应用被重复启动]的Bug
        if (!isTaskRoot()) {
            final Intent intent = getIntent();
            final String intentAction = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) &&
                    intentAction != null && intentAction.equals(Intent.ACTION_MAIN)) {
                finish();
            }
        }

        setContentView(R.layout.activity_splash);

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
                if (AppSettings.getInstance().isFirstEnter()) {
                    CommonHybridActivity.actionStart(SplashActivity.this, getResources().getString(R.string.web_url_agreement, NodeManager.getInstance().getCurNodeAddress()), WebType.WEB_TYPE_AGREEMENT, true);
                    SplashActivity.this.finish();
                    return;
                }

                if (AppSettings.getInstance().getOperateMenuFlag()) {
                    OperateMenuActivity.actionStart(SplashActivity.this);
                    SplashActivity.this.finish();
                    return;
                }
                if (AppSettings.getInstance().getFaceTouchIdFlag() &&
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
