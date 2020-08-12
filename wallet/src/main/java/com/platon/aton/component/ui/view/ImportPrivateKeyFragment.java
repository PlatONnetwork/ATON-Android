package com.platon.aton.component.ui.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.platon.aton.R;
import com.platon.aton.app.CustomObserver;
import com.platon.aton.component.ui.contract.ImportPrivateKeyContract;
import com.platon.aton.component.ui.presenter.ImportPrivateKeyPresenter;
import com.platon.aton.component.widget.ShadowButton;
import com.platon.aton.utils.CheckStrength;
import com.platon.aton.utils.CommonUtil;
import com.platon.aton.utils.GZipUtil;
import com.platon.aton.utils.RxUtils;
import com.platon.framework.app.Constants;
import com.platon.framework.base.BaseLazyFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ImportPrivateKeyFragment extends BaseLazyFragment<ImportPrivateKeyContract.View, ImportPrivateKeyPresenter> implements ImportPrivateKeyContract.View {
    Unbinder unbinder;
    @BindView(R.id.et_private_key)
    EditText mEtPrivateKey;
    @BindView(R.id.et_name)
    EditText mEtWalletName;
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
    @BindView(R.id.sbtn_import)
    ShadowButton mBtnImport;
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
    @BindView(R.id.tv_private_key_error)
    TextView mTvPrivateKeyError;
    @BindView(R.id.tv_name_error)
    TextView mTvNameError;
    @BindView(R.id.tv_password_error)
    TextView mTvPasswordError;
    @BindView(R.id.btn_paste)
    Button mBtnPaste;
    @BindView(R.id.layout_password_strength)
    LinearLayout mPasswordStrengthLayout;
    @BindView(R.id.tv_wallet_num_over_limit)
    TextView tvWalletNumOverLimit;

    private boolean mShowPassword;
    private boolean mShowRepeatPassword;
    private boolean isEnablePrivateKey = true;
    private boolean isEnableName = true;
    private boolean isEnablePassword = true;
    private boolean isEnableCreate = false;


    @Override
    public int getLayoutId() {
        return R.layout.fragment_import_private_key;
    }

    @Override
    public ImportPrivateKeyPresenter createPresenter() {
        return new ImportPrivateKeyPresenter();
    }

    @Override
    public ImportPrivateKeyContract.View createView() {
        return this;
    }

    @Override
    public void init(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
        addListeners();
        initDatas();
    }

    @Override
    public void onFragmentFirst() {
        super.onFragmentFirst();
        getPresenter().checkPaste();
    }

    private void addListeners() {

        RxView
                .clicks(mIvPasswordEyes)
                .compose(RxUtils.getClickTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object object) {
                        showPassword();
                    }
                });
        RxView
                .clicks(mIvRepeatPasswordEyes)
                .compose(RxUtils.getClickTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object object) {
                        showRepeatPassword();
                    }
                });
        RxView
                .clicks(mBtnImport)
                .compose(RxUtils.getClickTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object unit) {
                        getPresenter().importPrivateKey(mEtPrivateKey.getText().toString(),
                                mEtWalletName.getText().toString(),
                                mEtPassword.getText().toString(),
                                mEtRepeatPassword.getText().toString());
                    }
                });
        RxView
                .clicks(mBtnPaste)
                .compose(RxUtils.getClickTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object unit) {
                        mEtPrivateKey.setText(CommonUtil.getTextFromClipboard(getContext()));
                        mEtPrivateKey.setSelection(mEtPrivateKey.getText().toString().length());
                    }
                });


        mEtPrivateKey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String privateKey = mEtPrivateKey.getText().toString().trim();
                if (TextUtils.isEmpty(privateKey)) {
                    showPrivateKeyError(string(R.string.validPrivateKeyEmptyTips), true);
                } else {
                    showPrivateKeyError("", false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mEtWalletName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String name = mEtWalletName.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    showNameError(string(R.string.validWalletNameEmptyTips), true);
                } else if (name.length() > 20) {
                    showNameError(string(R.string.validWalletNameTips), true);
                } else if (getPresenter().isExists(name)) {
                    showNameError(string(R.string.wallet_name_exists), true);
                } else {
                    showNameError("", false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mEtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

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
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void initDatas() {

        showPassword();
        showRepeatPassword();
        /*showPrivateKeyError("", false);
        showNameError("", false);
        showPasswordError("", false);*/
        enableImport(false);
        getPresenter().init();
        getPresenter().loadDBWalletNumber();
    }


    @Override
    public void showWalletNumber(int walletNum) {
        int sumWalletNum = walletNum + Constants.WalletConstants.WALLET_ADD_ORDINARY;
        if(sumWalletNum > Constants.WalletConstants.WALLET_LIMIT){
            tvWalletNumOverLimit.setVisibility(View.VISIBLE);
            isEnableCreate = false;
        }else{
            tvWalletNumOverLimit.setVisibility(View.GONE);
            isEnableCreate = true;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == ImportWalletActivity.REQ_QR_CODE) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString(Constants.Extra.EXTRA_SCAN_QRCODE_DATA, "");
            String unzip = GZipUtil.unCompress(scanResult);
            getPresenter().parseQRCode(TextUtils.isEmpty(unzip) ? scanResult : unzip);
        }
    }

    @Override
    public String getKeystoreFromIntent() {
        Bundle bundle = getArguments();
        if (bundle != null && !bundle.isEmpty()) {
            return bundle.getString(Constants.Extra.EXTRA_SCAN_QRCODE_DATA, "");
        }
        return "";
    }

    @Override
    public void showQRCode(String QRCode) {
        mEtPrivateKey.setText(QRCode);
        mEtPrivateKey.setSelection(QRCode.length());
    }

    private void enableImport(boolean enabled) {
        mBtnImport.setEnabled(enabled);
    }

    private void showPassword() {
        mEtPassword.setTypeface(Typeface.DEFAULT);
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
        mEtRepeatPassword.setTypeface(Typeface.DEFAULT);
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


    @Override
    public void showPrivateKeyError(String text, boolean isVisible) {
        mTvPrivateKeyError.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        mTvPrivateKeyError.setText(text);
        this.isEnablePrivateKey = isVisible;
        enableImport(!isEnablePrivateKey && !isEnableName && !isEnablePassword &&isEnableCreate);
    }

    @Override
    public void showNameError(String text, boolean isVisible) {
        mTvNameError.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        mTvNameError.setText(text);
        this.isEnableName = isVisible;
        enableImport(!isEnablePrivateKey && !isEnableName && !isEnablePassword &&isEnableCreate);
    }

    @Override
    public void showPasswordError(String text, boolean isVisible) {
        mTvPasswordError.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        mTvPasswordError.setText(text);
        mTvPasswordDesc.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        this.isEnablePassword = isVisible;
        enableImport(!isEnablePrivateKey && !isEnableName && !isEnablePassword &&isEnableCreate);
    }

    @Override
    public void enablePaste(boolean enabled) {
        mBtnPaste.setEnabled(enabled);
        mBtnPaste.setTextColor(ContextCompat.getColor(getContext(), enabled ? R.color.color_105cfe : R.color.color_d8d8d8));
    }

}
