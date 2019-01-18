package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.juzix.wallet.R;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.CreateIndividualWalletContract;
import com.juzix.wallet.component.ui.presenter.CreateIndividualWalletPresenter;
import com.juzix.wallet.utils.CheckStrength;

public class CreateIndividualWalletActivity extends MVPBaseActivity<CreateIndividualWalletPresenter> implements CreateIndividualWalletContract.View, View.OnClickListener, TextWatcher, View.OnFocusChangeListener {

    private final static String    TAG = CreateIndividualWalletActivity.class.getSimpleName();
    private              EditText  mEtName;
    private              TextView  mTvNameError;
    private              EditText  mEtPassword;
    private              EditText  mEtRepeatPassword;
    private              TextView  mTvPasswordDesc;
    private              TextView  mTvPasswordError;
    private              ImageView mIvEyes;
    private              ImageView mIvRepeatEyes;
    private              Button    mBtnCreate;
    private TextView mTvStrength;
    private View      mVLine1;
    private View      mVLine2;
    private View      mVLine3;
    private View      mVLine4;
    private boolean      mShowPassword;
    private boolean      mShowRepeatPassword;

    public static void actionStart(Context context) {
        context.startActivity(new Intent(context, CreateIndividualWalletActivity.class));
    }

    @Override
    protected CreateIndividualWalletPresenter createPresenter() {
        return new CreateIndividualWalletPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_individual_wallet);
        findViewById(R.id.btn_create).setOnClickListener(this);
        initView();
        enableCreate(false);
        showPassword();
        showRepeatPassword();
    }

    private void initView() {
        findViewById(R.id.ll_left).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_middle)).setText(R.string.createIndividualWallet);
        mEtName = findViewById(R.id.et_name);
        mTvNameError = findViewById(R.id.tv_name_error);
        mEtPassword = findViewById(R.id.et_password);
        mEtRepeatPassword = findViewById(R.id.et_repeat_password);
        mIvEyes = findViewById(R.id.iv_eyes);
        mIvEyes.setOnClickListener(this);
        mIvRepeatEyes = findViewById(R.id.iv_repeat_eyes);
        mIvRepeatEyes.setOnClickListener(this);
        mTvPasswordError = findViewById(R.id.tv_password_error);
        mTvPasswordDesc = findViewById(R.id.tv_password_desc);
        mBtnCreate = findViewById(R.id.btn_create);
        mBtnCreate.setOnClickListener(this);
        mTvStrength = findViewById(R.id.tv_strength);
        mVLine1 = findViewById(R.id.v_line1);
        mVLine2 = findViewById(R.id.v_line2);
        mVLine3 = findViewById(R.id.v_line3);
        mVLine4 = findViewById(R.id.v_line4);
        mEtName.addTextChangedListener(this);
        mEtPassword.addTextChangedListener(this);
        mEtRepeatPassword.addTextChangedListener(this);
        mEtName.setOnFocusChangeListener(this);
        mEtPassword.setOnFocusChangeListener(this);
        mEtRepeatPassword.setOnFocusChangeListener(this);
        showNameError("", false);
        showPasswordError("", false);
    }

    private void enableCreate(boolean enabled){
        mBtnCreate.setEnabled(enabled);
        mBtnCreate.setBackgroundColor(ContextCompat.getColor(getContext(), enabled ? R.color.color_eff0f5 : R.color.color_373e51));
    }

    private void showPassword(){
        if (mShowPassword) {
            // 显示密码
            mIvEyes.setImageDrawable(getResources().getDrawable(R.drawable.icon_open_eyes));
            mEtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            mEtPassword.setSelection(mEtPassword.getText().toString().length());
//            mEtRepeatPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//            mEtRepeatPassword.setSelection(mEtRepeatPassword.getText().toString().length());
            mShowPassword = !mShowPassword;
        } else {
            // 隐藏密码
            mIvEyes.setImageDrawable(getResources().getDrawable(R.drawable.icon_close_eyes));
            mEtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            mEtPassword.setSelection(mEtPassword.getText().toString().length());
//            mEtRepeatPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
//            mEtRepeatPassword.setSelection(mEtRepeatPassword.getText().toString().length());
            mShowPassword = !mShowPassword;
        }
    }

    private void showRepeatPassword(){
        if (mShowRepeatPassword) {
            // 显示密码
            mIvRepeatEyes.setImageDrawable(getResources().getDrawable(R.drawable.icon_open_eyes));
            mEtRepeatPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            mEtRepeatPassword.setSelection(mEtRepeatPassword.getText().toString().length());
            mShowRepeatPassword = !mShowRepeatPassword;
        } else {
            // 隐藏密码
            mIvRepeatEyes.setImageDrawable(getResources().getDrawable(R.drawable.icon_close_eyes));
            mEtRepeatPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            mEtRepeatPassword.setSelection(mEtRepeatPassword.getText().toString().length());
            mShowRepeatPassword = !mShowRepeatPassword;
        }
    }

    @Override
    public void showNameError(String text, boolean isVisible){
        mTvNameError.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        mTvNameError.setText(text);
    }

    @Override
    public void showPasswordError(String text, boolean isVisible){
        mTvPasswordError.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        mTvPasswordError.setText(text);
        mTvPasswordDesc.setVisibility(isVisible ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_left:
                hideSoftInput();
                finish();
                break;

            case R.id.btn_create:
                mPresenter.createWallet(mEtName.getText().toString().trim(),
                        mEtPassword.getText().toString().trim(),
                        mEtRepeatPassword.getText().toString().trim());
                break;

            case R.id.iv_eyes:
                showPassword();
                break;

            case R.id.iv_repeat_eyes:
                showRepeatPassword();
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        String name = mEtName.getText().toString().trim();
        String password = mEtPassword.getText().toString().trim();
        String repeatPassword = mEtRepeatPassword.getText().toString().trim();
        if (v == mEtName && !hasFocus){
            if (TextUtils.isEmpty(name)){
                showNameError(string(R.string.validWalletNameEmptyTips), true);
            }else if (name.length() > 12){
                showNameError(string(R.string.validWalletNameTips), true);
            }else {
                showNameError("", false);
            }
        }
        if (v == mEtPassword && !hasFocus){
            if (TextUtils.isEmpty(password)){
                showPasswordError(string(R.string.validPasswordEmptyTips), true);
            }else if (password.length() < 6){
                showPasswordError(string(R.string.validPasswordTips), true);
            }else {
                if (password.equals(repeatPassword)){
                    showPasswordError("", false);
                }
            }
        }
        if (v == mEtRepeatPassword && !hasFocus){
            if (TextUtils.isEmpty(repeatPassword)){
                showPasswordError(string(R.string.validRepeatPasswordEmptyTips), true);
            }else if (!repeatPassword.equals(password)){
                showPasswordError(string(R.string.passwordTips), true);
            }else {
                if (repeatPassword.equals(password) && password.length() >= 6){
                    showPasswordError("", false);
                }
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String name = mEtName.getText().toString().trim();
        String password = mEtPassword.getText().toString().trim();
        String repeatPassword = mEtRepeatPassword.getText().toString().trim();
        enableCreate(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(repeatPassword) && password.length() >= 6 && name.length() <= 12);
        checkPwdStreng(password);
    }

    private void checkPwdStreng(String password) {
        if (TextUtils.isEmpty(password)) {
            mTvStrength.setText(R.string.strength);
            mVLine1.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_00000000));
            mVLine2.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_00000000));
            mVLine3.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_00000000));
            mVLine4.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_00000000));
            return;
        }
        switch (CheckStrength.getPasswordLevelNew(password)) {
            case EASY:
                mTvStrength.setText(R.string.weak);
                mVLine1.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_ff4747));
                mVLine2.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_00000000));
                mVLine3.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_00000000));
                mVLine4.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_00000000));
                break;
            case MIDIUM:
                mTvStrength.setText(R.string.so_so);
                mVLine1.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_ff9947));
                mVLine2.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_ff9947));
                mVLine3.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_00000000));
                mVLine4.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_00000000));
                break;
            case STRONG:
                mTvStrength.setText(R.string.good);
                mVLine1.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_ffed54));
                mVLine2.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_ffed54));
                mVLine3.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_ffed54));
                mVLine4.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_00000000));
                break;
            case VERY_STRONG:
            case EXTREMELY_STRONG:
                mTvStrength.setText(R.string.strong);
                mVLine1.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_41d325));
                mVLine2.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_41d325));
                mVLine3.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_41d325));
                mVLine4.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_41d325));
                break;
            default:
                break;
        }
    }
}
