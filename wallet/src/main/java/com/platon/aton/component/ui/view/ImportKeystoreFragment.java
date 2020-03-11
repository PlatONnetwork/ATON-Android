package com.platon.aton.component.ui.view;

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
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.platon.aton.R;
import com.platon.aton.app.Constants;
import com.platon.aton.app.CustomObserver;
import com.platon.aton.component.ui.base.MVPBaseFragment;
import com.platon.aton.component.ui.contract.ImportKeystoreContract;
import com.platon.aton.component.ui.presenter.ImportKeystorePresenter;
import com.platon.aton.component.widget.ShadowButton;
import com.platon.aton.utils.CommonUtil;
import com.platon.aton.utils.GZipUtil;
import com.platon.aton.utils.RxUtils;
import com.platon.aton.utils.SoftHideKeyboardUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.functions.Function3;

public class ImportKeystoreFragment extends MVPBaseFragment<ImportKeystorePresenter> implements ImportKeystoreContract.View {
    Unbinder unbinder;
    @BindView(R.id.et_keystore)
    EditText mEtKeystore;
    @BindView(R.id.et_password)
    EditText mEtPassword;
    @BindView(R.id.iv_password_eyes)
    ImageView mIvPasswordEyes;
    @BindView(R.id.et_name)
    EditText mEtWalletName;
    @BindView(R.id.sbtn_import)
    ShadowButton mBtnImport;
    @BindView(R.id.tv_name_error)
    TextView mTvNameError;
    @BindView(R.id.tv_keystore_error)
    TextView mTvKeystoreError;
    @BindView(R.id.tv_password_error)
    TextView mTvPasswordError;
    @BindView(R.id.btn_paste)
    Button mBtnPaste;

    private boolean mShowPassword;

    @Override
    protected ImportKeystorePresenter createPresenter() {
        return new ImportKeystorePresenter(this);
    }

    @Override
    protected void onFragmentPageStart() {
        mPresenter.checkPaste();
    }

    @Override
    protected View onCreateFragmentPage(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_import_keystore, container, false);
        unbinder = ButterKnife.bind(this, view);
        addListeners();
        initDatas();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addListeners();
    }

    private void initDatas() {
        enableImport(false);
        showPassword();
        showKeystoreError("", false);
        showNameError("", false);
        showPasswordError("", false);
        mPresenter.init();
        SoftHideKeyboardUtils.assistActivity(getActivity());
    }

    private void addListeners() {

        RxView.clicks(mIvPasswordEyes)
                .compose(RxUtils.getClickTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object unit) {
                        showPassword();
                    }
                });

        RxView.
                clicks(mBtnImport)
                .compose(RxUtils.getClickTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object unit) {
                        mPresenter.importKeystore(mEtKeystore.getText().toString(),
                                mEtWalletName.getText().toString(),
                                mEtPassword.getText().toString());
                    }
                });

        RxView
                .clicks(mBtnPaste)
                .compose(RxUtils.getClickTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object unit) {
                        mEtKeystore.setText(CommonUtil.getTextFromClipboard(getContext()));
                        mEtKeystore.setSelection(mEtKeystore.getText().toString().length());
                    }
                });

        Observable<CharSequence> keystoreObservable = RxTextView.textChanges(mEtKeystore).skipInitialValue();
        Observable<CharSequence> passwordObservable = RxTextView.textChanges(mEtPassword).skipInitialValue();
        Observable<CharSequence> walletNameObservable = RxTextView.textChanges(mEtWalletName).skipInitialValue();

        Observable
                .combineLatest(keystoreObservable, passwordObservable, walletNameObservable, new Function3<CharSequence, CharSequence, CharSequence, Boolean>() {
                    @Override
                    public Boolean apply(CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3) throws Exception {
                        String keystore = charSequence.toString().trim();
                        String passsword = charSequence2.toString().trim();
                        String walletName = charSequence3.toString().trim();
                        return !TextUtils.isEmpty(keystore) && !TextUtils.isEmpty(passsword) && passsword.length() >= 6 && !TextUtils.isEmpty(walletName) && walletName.length() <= 12;
                    }
                }).compose(RxUtils.bindToLifecycle(this)).subscribe(new CustomObserver<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {
                enableImport(aBoolean);
            }
        });

        RxView
                .focusChanges(mEtKeystore)
                .skipInitialValue()
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Boolean>() {
                    @Override
                    public void accept(Boolean hasFocus) {
                        String keystore = mEtKeystore.getText().toString().trim();
                        if (!hasFocus) {
                            if (TextUtils.isEmpty(keystore)) {
                                showKeystoreError(string(R.string.validKeystoreEmptyTips), true);
                            } else {
                                showKeystoreError("", false);
                            }
                        }
                    }
                });

        RxView.focusChanges(mEtWalletName).skipInitialValue().subscribe(new CustomObserver<Boolean>() {
            @Override
            public void accept(Boolean hasFocus) {
                String walletName = mEtWalletName.getText().toString().trim();
                if (!hasFocus) {
                    if (TextUtils.isEmpty(walletName)) {
                        showNameError(string(R.string.validWalletNameEmptyTips), true);
                    } else if (walletName.length() > 12) {
                        showNameError(string(R.string.validWalletNameTips), true);
                    } else if (mPresenter.isExists(walletName)) {
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
                        if (!hasFocus) {
                            if (TextUtils.isEmpty(password)) {
                                showPasswordError(string(R.string.validPasswordEmptyTips), true);
                            } else if (password.length() < 6) {
                                showPasswordError(string(R.string.validPasswordTips), true);
                            } else {
                                showPasswordError("", false);
                            }
                        }
                    }
                });
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
            mPresenter.parseQRCode(TextUtils.isEmpty(unzip) ? scanResult : unzip);
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
        mEtKeystore.setText(QRCode);
        mEtKeystore.setSelection(QRCode.length());
    }

    private void enableImport(boolean enabled) {
        mBtnImport.setEnabled(enabled);
    }

    @Override
    public void showKeystoreError(String text, boolean isVisible) {
        mTvKeystoreError.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        mTvKeystoreError.setText(text);
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
    }

    @Override
    public void enablePaste(boolean enabled) {
        mBtnPaste.setEnabled(enabled);
        mBtnPaste.setTextColor(ContextCompat.getColor(getContext(), enabled ? R.color.color_105cfe : R.color.color_d8d8d8));
    }

}
