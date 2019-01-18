package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.juzhen.framework.util.AndroidUtil;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.VerificationMnemonicContract;
import com.juzix.wallet.component.ui.dialog.CustomDialog;
import com.juzix.wallet.component.ui.presenter.VerificationMnemonicPresenter;
import com.juzix.wallet.component.widget.RoundedTextView;
import com.juzix.wallet.component.widget.ShadowDrawable;

import java.util.ArrayList;

public class VerificationMnemonicActivity extends MVPBaseActivity<VerificationMnemonicPresenter> implements VerificationMnemonicContract.View, View.OnClickListener {

    private final static String TAG = VerificationMnemonicActivity.class.getSimpleName();
    private CustomDialog mDisclaimerDialog;
    private CustomDialog mFailedDialog;
    private RoundedTextView rtvSubmit;
    private RoundedTextView rtvEmpty;
    private LinearLayout leftLayout;
    private TextView tvMiddle;
    private FlexboxLayout flexboxLayout;

    public static void actionStart(Context context, String mnemonic) {
        Intent intent = new Intent(context, VerificationMnemonicActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_MNEMONIC, mnemonic);
        context.startActivity(intent);
    }

    @Override
    protected VerificationMnemonicPresenter createPresenter() {
        return new VerificationMnemonicPresenter(this);
    }

    @Override
    public String getMnemonicFromIntent() {
        return getIntent().getStringExtra(Constants.Extra.EXTRA_MNEMONIC);
    }

    @Override
    public void setCompletedBtnEnable(boolean enable) {
        rtvSubmit.setEnabled(enable);
    }

    @Override
    public void setClearBtnEnable(boolean enable) {
        rtvEmpty.setEnabled(enable);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_mnemonic);
        initView();

        mPresenter.init();
    }


    private void initView() {

        leftLayout = findViewById(R.id.ll_left);
        tvMiddle = findViewById(R.id.tv_middle);
        rtvSubmit = findViewById(R.id.rtv_submit);
        rtvEmpty = findViewById(R.id.rtv_empty);
        flexboxLayout = findViewById(R.id.fl_checked);

        leftLayout.setOnClickListener(this);
        tvMiddle.setText(R.string.verificationOfMnemonic);
        rtvSubmit.setOnClickListener(this);
        rtvEmpty.setOnClickListener(this);

        int           shapeRadius  = AndroidUtil.dip2px(getContext(), 4);
        int           shadowRadius = AndroidUtil.dip2px(getContext(), 1);
        ShadowDrawable.setShadowDrawable(flexboxLayout,
                ContextCompat.getColor(this, R.color.color_1f2841),
                shapeRadius,
                ContextCompat.getColor(this, R.color.color_020527),
                shadowRadius, 0, 0);
    }

    @Override
    public void showAllList(ArrayList<VerificationMnemonicContract.DataEntity> list) {
        FlexboxLayout flAll = findViewById(R.id.fl_all);
        flAll.removeAllViews();
        for (int i = 0;i < list.size();i++){
            VerificationMnemonicContract.DataEntity dataEntity = list.get(i);
            flAll.addView(createAllItemView(i, dataEntity));
        }
    }

    @Override
    public void showCheckedList(ArrayList<VerificationMnemonicContract.DataEntity> list) {
        FlexboxLayout flChecked = findViewById(R.id.fl_checked);
        flChecked.removeAllViews();
        for (int i = 0;i < list.size();i++){
            VerificationMnemonicContract.DataEntity dataEntity = list.get(i);
            FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    AndroidUtil.dip2px(this, 24));
            int marginLeft = AndroidUtil.dip2px(this, 10);
            int marginTop = AndroidUtil.dip2px(this, 12);
            layoutParams.setMargins(marginLeft, marginTop, 0, 0);
//            flChecked.addView(createCheckedItemView(i, dataEntity), layoutParams);
            flChecked.addView(createCheckedItemView(i, dataEntity));
        }
    }

    private TextView createAllItemView(int position, VerificationMnemonicContract.DataEntity dataEntity){
        TextView textView = new TextView(this);
        textView.setText(dataEntity.getMnemonic());
        textView.setGravity(Gravity.CENTER);
        textView.setAllCaps(false);
        textView.setTextSize(13);
        if (!dataEntity.isChecked()){
            textView.setBackgroundResource(R.drawable.bg_shape_edittext3);
            textView.setTextColor(ContextCompat.getColor(this, R.color.color_ffffff));
        }else {
            textView.setBackgroundResource(R.drawable.bg_shape_edittext4);
            textView.setTextColor(ContextCompat.getColor(this, R.color.color_7a8092));
        }
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.checkAllListItem(position);
            }
        });
        int paddingLeftAndRight = AndroidUtil.dip2px(this, 7.5f);
        int paddingTopAndBottom = 0;
        ViewCompat.setPaddingRelative(textView, paddingLeftAndRight, paddingTopAndBottom, paddingLeftAndRight, paddingTopAndBottom);
        FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                AndroidUtil.dip2px(this, 24));
        int marginRight = AndroidUtil.dip2px(this, 10);
        int marginTop = AndroidUtil.dip2px(this, 12);
        layoutParams.setMargins(0, marginTop, marginRight, 0);
        textView.setLayoutParams(layoutParams);
        return textView;
    }

    private TextView createCheckedItemView(int position, VerificationMnemonicContract.DataEntity dataEntity){
        TextView textView = new TextView(this);
        textView.setGravity(Gravity.CENTER);
        textView.setAllCaps(false);
        textView.setTextSize(13);
        textView.setBackgroundResource(R.drawable.bg_shape_edittext2);
        textView.setTextColor(ContextCompat.getColor(this, R.color.color_24272b));
        if (dataEntity.isChecked()){
            textView.setVisibility(View.VISIBLE);
            textView.setText(dataEntity.getMnemonic());
        }else {
            textView.setVisibility(View.GONE);
            textView.setText("");
        }
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.uncheckItem(position);
            }
        });
        int paddingLeftAndRight = AndroidUtil.dip2px(this, 7.5f);
        int paddingTopAndBottom = 0;
        ViewCompat.setPaddingRelative(textView, paddingLeftAndRight, paddingTopAndBottom, paddingLeftAndRight, paddingTopAndBottom);
        FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                AndroidUtil.dip2px(this, 24));
        int marginLeft = AndroidUtil.dip2px(this, 10);
        int marginTop = AndroidUtil.dip2px(this, 10);
        layoutParams.setMargins(marginLeft, marginTop, 0, 0);
        textView.setLayoutParams(layoutParams);
        return textView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_left:
                MainActivity.actionStart(this);
                VerificationMnemonicActivity.this.finish();
                break;
            case R.id.rtv_submit:
                mPresenter.submit();
                break;
            case R.id.rtv_empty:
                mPresenter.emptyChecked();
                break;
        }
    }

    @Override
    public void showDisclaimerDialog(){
        dimissDisclaimerDialog();
        mDisclaimerDialog = new CustomDialog(getContext());
        mDisclaimerDialog.show(string(R.string.disclaimer), string(R.string.disclaimerResume), string(R.string.understood), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dimissDisclaimerDialog();
                MainActivity.actionStart(VerificationMnemonicActivity.this);
                VerificationMnemonicActivity.this.finish();
            }
        });
    }

    @Override
    public void showBackupFailedDialog(){
        dimissFailedDialog();
        mFailedDialog = new CustomDialog(getContext());
        mFailedDialog.show(string(R.string.backupFailed), string(R.string.backupMnemonicFailedResume), string(R.string.understood1), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dimissFailedDialog();
                //mPresenter.emptyChecked();
            }
        });
    }

    private void dimissDisclaimerDialog(){
        if (mDisclaimerDialog != null && mDisclaimerDialog.isShowing()){
            mDisclaimerDialog.dismiss();
            mDisclaimerDialog = null;
        }
    }

    private void dimissFailedDialog(){
        if (mFailedDialog != null && mFailedDialog.isShowing()){
            mFailedDialog.dismiss();
            mFailedDialog = null;
        }
    }
}
