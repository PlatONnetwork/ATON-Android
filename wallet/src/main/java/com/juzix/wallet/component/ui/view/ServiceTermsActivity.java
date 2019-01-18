package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.juzix.wallet.R;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.config.AppSettings;

public class ServiceTermsActivity extends BaseActivity implements View.OnClickListener {

    private final static String    TAG = ServiceTermsActivity.class.getSimpleName();
    private              ImageView mIvAgree;
    private              Button    mBtnContinue;
    private boolean mContinueEnabled;

    public static void actionStart(Context context){
        context.startActivity(new Intent(context, ServiceTermsActivity.class));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_terms);
        initView();
    }

    @Override
    protected int getStatusBarColor() {
        return R.color.color_1b2137;
    }

    private void initView(){
        mIvAgree = findViewById(R.id.iv_agree);
        mIvAgree.setImageResource(R.drawable.icon_unchecked1);
        findViewById(R.id.ll_agree).setOnClickListener(this);
        mBtnContinue = findViewById(R.id.btn_continue);
        mBtnContinue.setOnClickListener(this);
        ((TextView)findViewById(R.id.tv_content)).setText(getString(R.string.serviceProtocolContent, "<http://fsf.org/>"));
        enableContinue(false);
    }

    private void enableContinue(boolean enabled){
        mBtnContinue.setEnabled(enabled);
        mIvAgree.setImageResource(enabled ? R.drawable.icon_checked1 : R.drawable.icon_unchecked1);
        mBtnContinue.setBackgroundColor(ContextCompat.getColor(getContext(), enabled ? R.color.color_eff0f5 : R.color.color_373e51));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_continue:
                AppSettings.getInstance().setServiceTermsFlag(false);
                OperateMenuActivity.actionStart(this);
                ServiceTermsActivity.this.finish();
                break;

            case R.id.ll_agree:
                enableContinue(mContinueEnabled = !mContinueEnabled);
                break;
        }
    }
}
