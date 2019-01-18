package com.juzix.wallet.component.ui.view;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.LinearLayout;

import com.juzix.wallet.R;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.config.AppSettings;
import com.juzix.wallet.engine.IndividualWalletManager;
import com.juzix.wallet.utils.CommonUtil;

public class SplashActivity extends BaseActivity {

    private final static String TAG = SplashActivity.class.getSimpleName();

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

        initViews();

        mDecorView.postDelayed(new Runnable() {
            @Override
            public void run() {
//                if (AppSettings.getInstance().getServiceTermsFlag()) {
//                    ServiceTermsActivity.actionStart(SplashActivity.this);
//                    SplashActivity.this.finish();
//                    return;
//                }
                if (AppSettings.getInstance().getOperateMenuFlag()) {
                    OperateMenuActivity.actionStart(SplashActivity.this);
                    SplashActivity.this.finish();
                    return;
                }
                if (AppSettings.getInstance().getFaceTouchIdFlag() &&
//                        BiometricPromptCompat.supportBiometricPromptCompat(SplashActivity.this) &&
                        !IndividualWalletManager.getInstance().getWalletList().isEmpty()){
                    UnlockFigerprintActivity.actionStartMainActivity(SplashActivity.this);
                    SplashActivity.this.finish();
                    return;
                }
                MainActivity.actionStart(SplashActivity.this);
                SplashActivity.this.finish();
            }
        }, 1000);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    private void initViews() {

        LinearLayout layout = findViewById(R.id.layout_logo);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) layout.getLayoutParams();

        int marginTop = 0;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            marginTop = CommonUtil.getStatusBarHeight(this);
        }

        layoutParams.topMargin = marginTop;

        layout.setLayoutParams(layoutParams);

    }

    @Override
    protected boolean immersiveBarViewEnabled() {
        return true;
    }
}
