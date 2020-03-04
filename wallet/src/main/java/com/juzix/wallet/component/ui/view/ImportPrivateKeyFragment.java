package com.juzix.wallet.component.ui.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.app.CustomObserver;
import com.juzix.wallet.component.ui.base.MVPBaseFragment;
import com.juzix.wallet.component.ui.contract.ImportPrivateKeyContract;
import com.juzix.wallet.component.ui.presenter.ImportPrivateKeyPresenter;
import com.juzix.wallet.component.widget.ShadowButton;
import com.juzix.wallet.utils.CheckStrength;
import com.juzix.wallet.utils.CommonUtil;
import com.juzix.wallet.utils.GZipUtil;
import com.juzix.wallet.utils.RxUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;

public class ImportPrivateKeyFragment extends MVPBaseFragment<ImportPrivateKeyPresenter> implements ImportPrivateKeyContract.View {
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

    private boolean mShowPassword;
    private boolean mShowRepeatPassword;

    @Override
    protected ImportPrivateKeyPresenter createPresenter() {
        return new ImportPrivateKeyPresenter(this);
    }

    @Override
    protected void onFragmentPageStart() {
        mPresenter.checkPaste();
    }

    @Override
    protected View onCreateFragmentPage(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_import_private_key, container, false);
        unbinder = ButterKnife.bind(this, view);
        addListeners();
        initDatas();
        return view;
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
                        mPresenter.importPrivateKey(mEtPrivateKey.getText().toString(),
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
        Observable<Boolean> privateKeyAndWalletNameObservable = Observable
                .combineLatest(RxTextView.textChanges(mEtPrivateKey).skipInitialValue(), RxTextView.textChanges(mEtWalletName).skipInitialValue(), new BiFunction<CharSequence, CharSequence, Boolean>() {
                    @Override
                    public Boolean apply(CharSequence charSequence, CharSequence charSequence2) throws Exception {
                        String privateKey = charSequence.toString().trim();
                        String walletName = charSequence2.toString().trim();
                        return !TextUtils.isEmpty(privateKey) && !TextUtils.isEmpty(walletName) && walletName.length() <= 20;
                    }
                });

        Observable<Boolean> passwordAndRepeatPasswordObservable = Observable.combineLatest(RxTextView.textChanges(mEtPassword).skipInitialValue(), RxTextView.textChanges(mEtRepeatPassword).skipInitialValue(), new BiFunction<CharSequence, CharSequence, Boolean>() {
            @Override
            public Boolean apply(CharSequence charSequence, CharSequence charSequence2) throws Exception {
                checkPwdStrength(charSequence.toString());
                return !TextUtils.isEmpty(charSequence) && !TextUtils.isEmpty(charSequence2) && charSequence.length() >= 6;
            }
        });

        Observable
                .combineLatest(privateKeyAndWalletNameObservable, passwordAndRepeatPasswordObservable, new BiFunction<Boolean, Boolean, Boolean>() {

                    @Override
                    public Boolean apply(Boolean aBoolean, Boolean aBoolean2) throws Exception {
                        return aBoolean && aBoolean2;
                    }
                })
                .compose(RxUtils.bindToLifecycle(this)).subscribe(new CustomObserver<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {
                enableImport(aBoolean);
            }
        });

        RxView
                .focusChanges(mEtPrivateKey)
                .skipInitialValue()
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Boolean>() {
                    @Override
                    public void accept(Boolean hasFocus) {
                        String privateKey = mEtPrivateKey.getText().toString().trim();
                        if (!hasFocus) {
                            if (TextUtils.isEmpty(privateKey)) {
                                showPrivateKeyError(string(R.string.validPrivateKeyEmptyTips), true);
                            } else {
                                showPrivateKeyError("", false);
                            }
                        }
                    }
                });
        RxView
                .focusChanges(mEtWalletName)
                .skipInitialValue()
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Boolean>() {
                    @Override
                    public void accept(Boolean hasFocus) {
                        String name = mEtWalletName.getText().toString().trim();
                        if (!hasFocus) {
                            if (TextUtils.isEmpty(name)) {
                                showNameError(string(R.string.validWalletNameEmptyTips), true);
                            } else if (name.length() > 20) {
                                showNameError(string(R.string.validWalletNameTips), true);
                            } else if (mPresenter.isExists(name)) {
                                showNameError(string(R.string.wallet_name_exists), true);
                            } else {
                                showNameError("", false);
                            }
                        }
                    }
                });
        RxView
                .focusChanges(mEtPassword)
                .skipInitialValue()
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Boolean>() {
                    @Override
                    public void accept(Boolean hasFocus) {
                        String password = mEtPassword.getText().toString().trim();
                        String repeatPassword = mEtRepeatPassword.getText().toString().trim();
                        if (!hasFocus) {
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
                    }
                });

        RxView
                .focusChanges(mEtRepeatPassword)
                .skipInitialValue()
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Boolean>() {
                    @Override
                    public void accept(Boolean hasFocus) {
                        String password = mEtPassword.getText().toString().trim();
                        String repeatPassword = mEtRepeatPassword.getText().toString().trim();
                        if (!hasFocus) {
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
                    }
                });

    }

    private void initDatas() {
        enableImport(false);
        showPassword();
        showRepeatPassword();
        showPrivateKeyError("", false);
        showNameError("", false);
        showPasswordError("", false);
        mPresenter.init();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == ImportWalletActivity.REQ_QR_CODE) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString(Constants.Extra.EXTRA_SCAN_QRCODE_DATA,"");
            String unzip = GZipUtil.unCompress(scanResult);
            mPresenter.parseQRCode(TextUtils.isEmpty(unzip)? scanResult : unzip);
        }
    }

    @Override
    public String getKeystoreFromIntent() {
        Bundle bundle = getArguments();
        if (bundle != null && !bundle.isEmpty()) {
            return bundle.getString(Constants.Extra.EXTRA_SCAN_QRCODE_DATA,"");
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
    }

    @Override
    public void showNameError(String text, boolean isVisible) {
        mTvNameError.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        mTvNameError.setText(text);
    }

    @Override
    public void showPasswordError(String text, boolean isVisible) {
        mTvPasswordError.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        mTvPasswordError.setText(text);
        mTvPasswordDesc.setVisibility(isVisible ? View.GONE : View.VISIBLE);
    }

    @Override
    public void enablePaste(boolean enabled) {
        mBtnPaste.setEnabled(enabled);
        mBtnPaste.setTextColor(ContextCompat.getColor(getContext(), enabled ? R.color.color_105cfe : R.color.color_d8d8d8));
    }
}
