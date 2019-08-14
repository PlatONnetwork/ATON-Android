package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.just.agentweb.WebChromeClient;
import com.just.agentweb.WebViewClient;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.ui.base.BaseAgentWebActivity;
import com.juzix.wallet.component.widget.CommonTitleBar;

/**
 * Created by will12190 on 2019/4/27
 * Email:will12190@hotmail.com
 * 通用webview界面
 */
public class CommonHybridActivity extends BaseAgentWebActivity {

    private String mUrl;
    private CommonTitleBar mCtb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitiy_common_hybrid);
        init();
        buildAgentWeb();
    }

    private void init() {
        mUrl = getIntent().getStringExtra(Constants.Extra.EXTRA_URL);
        mCtb = findViewById(R.id.ctb);

        mCtb.setLeftDrawableClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAgentWeb == null || !mAgentWeb.back()){
                    finish();
                }
            }
        });
    }

    @Override
    protected boolean immersiveBarViewEnabled() {
        return true;
    }

    @NonNull
    @Override
    protected ViewGroup getAgentWebParent() {
        return (ViewGroup) this.findViewById(R.id.layout_container);
    }

    @Override
    protected void setTitle(WebView view, String title) {
        super.setTitle(view, title);
        mCtb.setTitle(title);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mAgentWeb != null && mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected int getIndicatorColor() {
        return getResources().getColor(R.color.color_0077ff);
    }

    @Override
    protected int getIndicatorHeight() {
        return 3;
    }

    @Nullable
    @Override
    protected WebViewClient getWebViewClient() {
        return webViewClient;
    }

    @Nullable
    @Override
    protected WebChromeClient getWebChromeClient() {
        return webChromeClient;
    }

    private WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

    };

    @Nullable
    @Override
    protected String getUrl() {
        return mUrl;
    }

    private WebChromeClient webChromeClient = new WebChromeClient() {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);

        }
    };

    public static void actionStart(Context context, String url) {
        Intent intent = new Intent(context, CommonHybridActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_URL, url);
        context.startActivity(intent);
    }
}