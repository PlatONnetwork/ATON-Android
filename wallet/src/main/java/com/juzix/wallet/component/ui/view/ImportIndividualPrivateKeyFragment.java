package com.juzix.wallet.component.ui.view;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.TextView;

import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxbinding3.widget.RxTextView;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.ui.base.MVPBaseFragment;
import com.juzix.wallet.component.ui.contract.ImportIndividualPrivateKeyContract;
import com.juzix.wallet.component.ui.presenter.ImportIndividualPrivateKeyPresenter;
import com.juzix.wallet.utils.CheckStrength;

import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import kotlin.Unit;

public class ImportIndividualPrivateKeyFragment extends MVPBaseFragment<ImportIndividualPrivateKeyPresenter> implements ImportIndividualPrivateKeyContract.View {

    private EditText mEtPrivateKey;
    private EditText mEtWalletName;
    private ImageView mIvEyes;
    private ImageView mIvRepeatEyes;
    private EditText mEtPassword;
    private EditText mEtRepeatPassword;
    private TextView mTvPasswordDesc;
    private Button mBtnImport;
    private boolean mShowPassword;
    private boolean mShowRepeatPassword;
    private TextView mTvStrength;
    private View mVLine1;
    private View mVLine2;
    private View mVLine3;
    private View mVLine4;
    private TextView mTvPrivateKeyError;
    private TextView mTvNameError;
    private TextView mTvPasswordError;

    @Override
    protected ImportIndividualPrivateKeyPresenter createPresenter() {
        return new ImportIndividualPrivateKeyPresenter(this);
    }

    @Override
    protected void onFragmentPageStart() {
    }

    @Override
    protected View onCreateFragmentPage(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_import_individual_private_key, container, false);
        initViews(view);
        addListeners();
        initDatas();
        return view;
    }

    private void initViews(View rootView) {
        mEtPrivateKey = rootView.findViewById(R.id.et_private_key);
        mTvPrivateKeyError = rootView.findViewById(R.id.tv_private_key_error);
        mEtWalletName = rootView.findViewById(R.id.et_name);
        mTvNameError = rootView.findViewById(R.id.tv_name_error);
        mIvEyes = rootView.findViewById(R.id.iv_eyes);
        mIvRepeatEyes = rootView.findViewById(R.id.iv_repeat_eyes);
        mEtPassword = rootView.findViewById(R.id.et_password);
        mEtRepeatPassword = rootView.findViewById(R.id.et_repeat_password);
        mTvPasswordDesc = rootView.findViewById(R.id.tv_password_desc);
        mTvPasswordError = rootView.findViewById(R.id.tv_password_error);
        mBtnImport = rootView.findViewById(R.id.btn_import);
        mTvStrength = rootView.findViewById(R.id.tv_strength);
        mVLine1 = rootView.findViewById(R.id.v_line1);
        mVLine2 = rootView.findViewById(R.id.v_line2);
        mVLine3 = rootView.findViewById(R.id.v_line3);
        mVLine4 = rootView.findViewById(R.id.v_line4);
    }

    private void addListeners() {

        RxView.clicks(mIvEyes).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) throws Exception {
                showPassword();
            }
        });
        RxView.clicks(mIvRepeatEyes).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) throws Exception {
                showRepeatPassword();
            }
        });
        RxView.clicks(mBtnImport).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) throws Exception {
                mPresenter.importPrivateKey(mEtPrivateKey.getText().toString(),
                        mEtWalletName.getText().toString(),
                        mEtPassword.getText().toString(),
                        mEtRepeatPassword.getText().toString());
            }
        });


        Observable<Boolean> privateKeyAndWalletNameObservable = Observable
                .combineLatest(RxTextView.textChanges(mEtPrivateKey).skipInitialValue(), RxTextView.textChanges(mEtWalletName).skipInitialValue(), new BiFunction<CharSequence, CharSequence, Boolean>() {
                    @Override
                    public Boolean apply(CharSequence charSequence, CharSequence charSequence2) throws Exception {
                        String privateKey = charSequence.toString().trim();
                        String walletName = charSequence2.toString().trim();
                        return !TextUtils.isEmpty(privateKey) && !TextUtils.isEmpty(walletName) && walletName.length() <= 12;
                    }
                });

        Observable<Boolean> passwordAndRepeatPasswordObservable = Observable.combineLatest(RxTextView.textChanges(mEtPassword).skipInitialValue(), RxTextView.textChanges(mEtRepeatPassword).skipInitialValue(), new BiFunction<CharSequence, CharSequence, Boolean>() {
            @Override
            public Boolean apply(CharSequence charSequence, CharSequence charSequence2) throws Exception {
                checkPwdStreng(charSequence.toString());
                return !TextUtils.isEmpty(charSequence) && !TextUtils.isEmpty(charSequence2) && charSequence.length() >= 6;
            }
        });

        Observable.combineLatest(privateKeyAndWalletNameObservable, passwordAndRepeatPasswordObservable, new BiFunction<Boolean, Boolean, Boolean>() {

            @Override
            public Boolean apply(Boolean aBoolean, Boolean aBoolean2) throws Exception {
                return aBoolean && aBoolean2;
            }
        }).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                enableImport(aBoolean);
            }
        });

        RxView.focusChanges(mEtPrivateKey).skipInitialValue().subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean hasFocus) throws Exception {
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
        RxView.focusChanges(mEtWalletName).skipInitialValue().subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean hasFocus) throws Exception {
                String name = mEtWalletName.getText().toString().trim();
                if (!hasFocus) {
                    if (TextUtils.isEmpty(name)) {
                        showNameError(string(R.string.validWalletNameEmptyTips), true);
                    } else if (name.length() > 12) {
                        showNameError(string(R.string.validWalletNameTips), true);
                    } else {
                        showNameError("", false);
                    }
                }
            }
        });
        RxView.focusChanges(mEtPassword).skipInitialValue().subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean hasFocus) throws Exception {
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
        RxView.focusChanges(mEtRepeatPassword).skipInitialValue().subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean hasFocus) throws Exception {
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
        if (requestCode == ImportIndividualWalletActivity.REQ_QR_CODE) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString(ScanQRCodeActivity.EXTRA_SCAN_QRCODE_DATA);
            mPresenter.parseQRCode(scanResult);
        }
    }

    @Override
    public String getKeystoreFromIntent() {
        Bundle bundle = getArguments();
        if (bundle != null && !bundle.isEmpty()) {
            return bundle.getString(Constants.Extra.EXTRA_SCAN_QRCODE_DATA);
        }
        return "";
    }

    @Override
    public void showQRCode(String QRCode) {
        mEtPrivateKey.setText(QRCode);
    }

    private void enableImport(boolean enabled) {
        mBtnImport.setEnabled(enabled);
        mBtnImport.setBackgroundColor(ContextCompat.getColor(getContext(), enabled ? R.color.color_eff0f5 : R.color.color_373e51));
    }

    private void showPassword() {
        if (mShowPassword) {
            // 显示密码
            mIvEyes.setImageDrawable(getResources().getDrawable(R.drawable.icon_open_eyes));
            mEtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            mEtPassword.setSelection(mEtPassword.getText().toString().length());
            mShowPassword = !mShowPassword;
        } else {
            // 隐藏密码
            mIvEyes.setImageDrawable(getResources().getDrawable(R.drawable.icon_close_eyes));
            mEtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            mEtPassword.setSelection(mEtPassword.getText().toString().length());
            mShowPassword = !mShowPassword;
        }
    }

    private void showRepeatPassword() {
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
}
