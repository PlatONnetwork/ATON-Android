package com.juzix.wallet.component.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juzix.wallet.R;

public class CustomDialog extends BaseDialog {
    private LinearLayout mLlTitle;
    private LinearLayout mLlBtom;
    private LinearLayout mLlBtomLeft;
    private LinearLayout mLlBtomRight;
    private TextView     mTvTitle;
    private TextView     mTvContent;
    private Button       mBtnLeft;
    private Button       mBtnRight;

    public CustomDialog(@NonNull Context context) {
        super(context, R.style.Dialog_FullScreen);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_custom);
        initView();
    }

    private void initView() {
        mLlTitle = (LinearLayout) findViewById(R.id.ll_title);
        mLlBtom = (LinearLayout) findViewById(R.id.ll_btom);
        mLlBtomLeft = (LinearLayout) findViewById(R.id.ll_btom_left);
        mLlBtomRight = (LinearLayout) findViewById(R.id.ll_btom_right);
        mTvContent = (TextView) findViewById(R.id.tv_content);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mBtnLeft = (Button) findViewById(R.id.btn_left);
        mBtnRight = (Button) findViewById(R.id.btn_right);
    }

    public void show(String title, String content, String btnText, View.OnClickListener btnListener) {
        show(title, content, "", btnText, null, btnListener);
    }

    public void show(String title, String content,
                     String btnLeft, String btnRight, View.OnClickListener leftListener, View.OnClickListener rightListener) {
        this.setCancelable(false);
        super.show();
        if (TextUtils.isEmpty(title)) {
            mLlTitle.setVisibility(View.GONE);
        } else {
            mLlTitle.setVisibility(View.VISIBLE);
            mTvTitle.setText(title);
        }
        if (TextUtils.isEmpty(content)) {
            mTvContent.setVisibility(View.GONE);
        } else {
            mTvContent.setVisibility(View.VISIBLE);
            mTvContent.setText(content);
        }
        if (TextUtils.isEmpty(btnLeft)) {
            mLlBtomLeft.setVisibility(View.GONE);
        } else {
            mLlBtomLeft.setVisibility(View.VISIBLE);
            mBtnLeft.setText(btnLeft);
        }
        if (TextUtils.isEmpty(btnRight)) {
            mLlBtomRight.setVisibility(View.GONE);
        } else {
            mLlBtomRight.setVisibility(View.VISIBLE);
            mBtnRight.setText(btnRight);
        }
        if (leftListener != null) {
            mBtnLeft.setOnClickListener(leftListener);
        }
        if (rightListener != null) {
            mBtnRight.setOnClickListener(rightListener);
        }
    }
}
