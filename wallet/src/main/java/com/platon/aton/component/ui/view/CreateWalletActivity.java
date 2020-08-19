package com.platon.aton.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.platon.aton.R;
import com.platon.aton.component.ui.contract.CreateWalletContract;
import com.platon.aton.component.ui.presenter.CreateWalletPresenter;
import com.platon.aton.component.widget.ShadowButton;
import com.platon.aton.engine.WalletManager;
import com.platon.aton.entity.WalletType;
import com.platon.aton.utils.CheckStrength;
import com.platon.aton.utils.DefParserStrUtil;
import com.platon.aton.utils.SoftHideKeyboardUtils;
import com.platon.framework.app.Constants;
import com.platon.framework.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CreateWalletActivity extends BaseActivity<CreateWalletContract.View, CreateWalletPresenter> implements CreateWalletContract.View, View.OnClickListener {

    Unbinder unbinder;
    @BindView(R.id.et_name)
    EditText mEtName;
    @BindView(R.id.tv_name_error)
    TextView mTvNameError;
    @BindView(R.id.et_password)
    EditText mEtPassword;
    @BindView(R.id.et_repeat_password)
    EditText mEtRepeatPassword;
    @BindView(R.id.iv_password_eyes)
    ImageView mIvPasswordEyes;
    @BindView(R.id.iv_repeat_password_eyes)
    ImageView mIvRepeatPasswordEyes;
    @BindView(R.id.tv_password_desc)
    TextView mTvPasswordDesc;
    @BindView(R.id.tv_password_error)
    TextView mTvPasswordError;
    @BindView(R.id.tv_wallet_type)
    TextView mTvWalletType;
    @BindView(R.id.sbtn_create)
    ShadowButton mSbtnCreate;
    @BindView(R.id.tv_strength)
    TextView mTvStrength;
    @BindView(R.id.v_line1)
    View mVLine1;
    @BindView(R.id.v_line2)
    View mVLine2;
    @BindView(R.id.v_line3)
    View mVLine3;
    @BindView(R.id.v_line4)
    View mVLine4;
    @BindView(R.id.layout_password_strength)
    LinearLayout mPasswordStrengthLayout;
    @BindView(R.id.wallet_num_over_limit)
    TextView tvWalletNumOverLimit;

    private boolean mShowPassword;
    private boolean mShowRepeatPassword;
    public static final int REQ_WALLET_TYPE_QR_CODE = 0x101;
    @WalletType
    int walletType = WalletType.ORDINARY_WALLET;//默认普通钱包类型
    private int walletNum = 0;
    private boolean isEnableCreate = false;
    private boolean isEnableName = true;
    private boolean isEnablePassword = true;


    public static void actionStart(Context context) {
        context.startActivity(new Intent(context, CreateWalletActivity.class));
    }

    @Override
    public CreateWalletPresenter createPresenter() {
        return new CreateWalletPresenter();
    }

    @Override
    public CreateWalletContract.View createView() {
        return this;
    }

    @Override
    public void init() {
        unbinder = ButterKnife.bind(this);
        getPresenter().loadDBWalletNumber();
        initView();
        showPassword();
        showRepeatPassword();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_create_wallet;
    }

    @Override
    protected boolean immersiveBarViewEnabled() {
        return true;
    }

    @Override
    public void showWalletNumber(int walletNum) {
        this.walletNum = walletNum;
        setWalletNumOverLimit();
    }

    private void initView() {

        mSbtnCreate.setOnClickListener(this);
        mEtPassword.setTypeface(Typeface.DEFAULT);
        mEtRepeatPassword.setTypeface(Typeface.DEFAULT);
        mIvPasswordEyes.setOnClickListener(this);
        mIvRepeatPasswordEyes.setOnClickListener(this);
        mTvWalletType.setOnClickListener(this);
       /* showNameError("", false);
        showPasswordError("", false);*/
        SoftHideKeyboardUtils.assistActivity(this);
        enableCreate(false);

        mEtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String name = mEtName.getText().toString().trim();
                    if (TextUtils.isEmpty(name)) {
                        showNameError(string(R.string.validWalletNameEmptyTips), true);
                    } else if (name.length() > 20) {
                        showNameError(string(R.string.validWalletNameTips), true);
                    } else if (WalletManager.getInstance().isWalletNameExists(name)) {
                        showNameError(string(R.string.wallet_name_exists), true);
                    } else {
                        showNameError("", false);
                    }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });


        mEtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = mEtPassword.getText().toString().trim();
                String repeatPassword = mEtRepeatPassword.getText().toString().trim();
                if (TextUtils.isEmpty(password)) {
                    showPasswordError(string(R.string.validPasswordEmptyTips), true);
                } else if (password.length() < 6) {
                    showPasswordError(string(R.string.validPasswordTips), true);
                } else {
                    if (password.equals(repeatPassword)) {
                        showPasswordError("", false);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String password = mEtPassword.getText().toString().trim();
                checkPwdStrength(password);
            }
        });



        mEtRepeatPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = mEtPassword.getText().toString().trim();
                String repeatPassword = mEtRepeatPassword.getText().toString().trim();
                if (TextUtils.isEmpty(repeatPassword)) {
                    showPasswordError(string(R.string.validRepeatPasswordEmptyTips), true);
                } else if (!repeatPassword.equals(password)) {
                    showPasswordError(string(R.string.passwordTips), true);
                } else {
                    if (repeatPassword.equals(password) && password.length() >= 6) {
                        showPasswordError("", false);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });



    }

    private void enableCreate(boolean enabled) {
        mSbtnCreate.setEnabled(enabled);
    }

    private void showPassword() {
        if (mShowPassword) {
            // 显示密码
            mEtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            mEtPassword.setSelection(mEtPassword.getText().toString().length());
            mIvPasswordEyes.setImageResource(R.drawable.icon_open_eyes);
            mShowPassword = !mShowPassword;
        } else {
            // 隐藏密码
            mEtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            mEtPassword.setSelection(mEtPassword.getText().toString().length());
            mIvPasswordEyes.setImageResource(R.drawable.icon_close_eyes);
            mShowPassword = !mShowPassword;
        }
    }

    private void showRepeatPassword() {
        if (mShowRepeatPassword) {
            // 显示密码
            mEtRepeatPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            mEtRepeatPassword.setSelection(mEtRepeatPassword.getText().toString().length());
            mIvRepeatPasswordEyes.setImageResource(R.drawable.icon_open_eyes);
            mShowRepeatPassword = !mShowRepeatPassword;
        } else {
            // 隐藏密码
            mEtRepeatPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            mEtRepeatPassword.setSelection(mEtRepeatPassword.getText().toString().length());
            mIvRepeatPasswordEyes.setImageResource(R.drawable.icon_close_eyes);
            mShowRepeatPassword = !mShowRepeatPassword;
        }
    }

    @Override
    public void showNameError(String text, boolean isVisible) {
        mTvNameError.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        mTvNameError.setText(text);
        this.isEnableName = isVisible;
        enableCreate(!isEnablePassword && !isEnableName && isEnableCreate);
    }

    @Override
    public void showPasswordError(String text, boolean isVisible) {
        mTvPasswordError.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        mTvPasswordError.setText(text);
        mTvPasswordDesc.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        this.isEnablePassword = isVisible;
        enableCreate(!isEnablePassword && !isEnableName && isEnableCreate);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sbtn_create:
                getPresenter().createWallet(mEtName.getText().toString().trim(),
                        mEtPassword.getText().toString().trim(),
                        mEtRepeatPassword.getText().toString().trim(), walletType);
                break;
            case R.id.iv_password_eyes:
                showPassword();
                break;
            case R.id.iv_repeat_password_eyes:
                showRepeatPassword();
                break;
            case R.id.tv_wallet_type:
                String walletType = mTvWalletType.getText().toString();
                int type = DefParserStrUtil.transforInverseWalletType(walletType, this);
                SwitchWalletTypeActivity.actionStartForResult(this, type, CreateWalletActivity.REQ_WALLET_TYPE_QR_CODE);
                break;
            default:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CreateWalletActivity.REQ_WALLET_TYPE_QR_CODE && resultCode == RESULT_OK) {
            walletType = data.getIntExtra(Constants.Extra.EXTRA_WALLET_TYPE, 0);
            mTvWalletType.setText(DefParserStrUtil.transformWalletType(walletType, this));
           //设置钱包上限
            setWalletNumOverLimit();
        }
    }

    private void setWalletNumOverLimit(){

        //检查钱包上限
        isEnableCreate = checkWalletNumLimit(walletNum,walletType);
        if(isEnableCreate){
            tvWalletNumOverLimit.setVisibility(View.GONE);
        }else{
            tvWalletNumOverLimit.setVisibility(View.VISIBLE);
        }
    }


    private boolean checkWalletNumLimit(int currentWalletNum,@WalletType int walletType){
        //控制钱包数量上限
        int sumWalletNum = 0;
        if (walletType == WalletType.ORDINARY_WALLET) {
            sumWalletNum = currentWalletNum + Constants.WalletConstants.WALLET_ADD_ORDINARY;
        } else {
            sumWalletNum = currentWalletNum + Constants.WalletConstants.WALLET_ADD_HD;
        }
        if(sumWalletNum > Constants.WalletConstants.WALLET_LIMIT){
            return false;
        }else{
            return true;
        }
    }

    private void checkPwdStrength(String password) {

        mPasswordStrengthLayout.setVisibility(TextUtils.isEmpty(password) ? View.GONE : View.VISIBLE);

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
                mTvStrength.setTextColor(ContextCompat.getColor(getContext(), R.color.color_f5302c));
                mTvStrength.setText(R.string.weak);
                mVLine1.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_f5302c));
                mVLine2.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_00000000));
                mVLine3.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_00000000));
                mVLine4.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_00000000));
                break;
            case MIDIUM:
                mTvStrength.setTextColor(ContextCompat.getColor(getContext(), R.color.color_ff9000));
                mTvStrength.setText(R.string.so_so);
                mVLine1.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_ff9000));
                mVLine2.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_ff9000));
                mVLine3.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_00000000));
                mVLine4.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_00000000));
                break;
            case STRONG:
                mTvStrength.setTextColor(ContextCompat.getColor(getContext(), R.color.color_58b8ff));
                mTvStrength.setText(R.string.good);
                mVLine1.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_58b8ff));
                mVLine2.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_58b8ff));
                mVLine3.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_58b8ff));
                mVLine4.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_00000000));
                break;
            case VERY_STRONG:
            case EXTREMELY_STRONG:
                mTvStrength.setTextColor(ContextCompat.getColor(getContext(), R.color.color_19a20e));
                mTvStrength.setText(R.string.strong);
                mVLine1.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_19a20e));
                mVLine2.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_19a20e));
                mVLine3.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_19a20e));
                mVLine4.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_19a20e));
                break;
            default:
                break;
        }
    }


}
