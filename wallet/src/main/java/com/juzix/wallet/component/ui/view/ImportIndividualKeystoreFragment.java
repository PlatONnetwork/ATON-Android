package com.juzix.wallet.component.ui.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.ui.base.MVPBaseFragment;
import com.juzix.wallet.component.ui.contract.ImportIndividualKeystoreContract;
import com.juzix.wallet.component.ui.presenter.ImportIndividualKeystorePresenter;
import com.juzix.wallet.component.widget.ShadowButton;
import com.juzix.wallet.utils.CommonUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function3;

public class ImportIndividualKeystoreFragment extends MVPBaseFragment<ImportIndividualKeystorePresenter> implements ImportIndividualKeystoreContract.View {
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
    protected ImportIndividualKeystorePresenter createPresenter() {
        return new ImportIndividualKeystorePresenter(this);
    }

    @Override
    protected void onFragmentPageStart() {
        mPresenter.checkPaste();
    }

    @Override
    protected View onCreateFragmentPage(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_import_individual_keystore, container, false);
        unbinder = ButterKnife.bind(this, view);
//        initViews(view);

        addListeners();
        initDatas();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addListeners();
    }

    //    private void initViews(View rootView) {
//        mEtKeystore = rootView.findViewById(R.id.et_keystore);
//        mTvKeystoreError = rootView.findViewById(R.id.tv_keystore_error);
//        mEtPassword = rootView.findViewById(R.id.et_password);
//        mIvPasswordEyes = rootView.findViewById(R.id.iv_password_eyes);
//        mTvPasswordError = rootView.findViewById(R.id.tv_password_error);
//        mEtWalletName = rootView.findViewById(R.id.et_name);
//        mTvNameError = rootView.findViewById(R.id.tv_name_error);
//        mBtnImport = rootView.findViewById(R.id.sbtn_import);
//        mBtnPaste = rootView.findViewById(R.id.btn_paste);
//    }

    private void initDatas() {
        enableImport(false);
        showPassword();
        showKeystoreError("", false);
        showNameError("", false);
        showPasswordError("", false);
        mPresenter.init();
    }

    private void addListeners() {

        RxView.clicks(mIvPasswordEyes).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object unit) throws Exception {
                showPassword();
            }
        });

        RxView.clicks(mBtnImport).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object unit) throws Exception {
                mPresenter.importKeystore(mEtKeystore.getText().toString(),
                        mEtWalletName.getText().toString(),
                        mEtPassword.getText().toString());
            }
        });

        RxView.clicks(mBtnPaste).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object unit) throws Exception {
                mEtKeystore.setText(CommonUtil.getTextFromClipboard(getContext()));
                mEtKeystore.setSelection(mEtKeystore.getText().toString().length());
            }
        });

        Observable<CharSequence> keystoreObservable = RxTextView.textChanges(mEtKeystore).skipInitialValue();
        Observable<CharSequence> passwordObservable = RxTextView.textChanges(mEtPassword).skipInitialValue();
        Observable<CharSequence> walletNameObservable = RxTextView.textChanges(mEtWalletName).skipInitialValue();

        Observable.combineLatest(keystoreObservable, passwordObservable, walletNameObservable, new Function3<CharSequence, CharSequence, CharSequence, Boolean>() {
            @Override
            public Boolean apply(CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3) throws Exception {
                String keystore = charSequence.toString().trim();
                String passsword = charSequence2.toString().trim();
                String walletName = charSequence3.toString().trim();
                return !TextUtils.isEmpty(keystore) && !TextUtils.isEmpty(passsword) && passsword.length() >= 6 && !TextUtils.isEmpty(walletName) && walletName.length() <= 12;
            }
        }).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                enableImport(aBoolean);
            }
        });

        RxView.focusChanges(mEtKeystore).skipInitialValue().subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean hasFocus) throws Exception {
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
        RxView.focusChanges(mEtWalletName).skipInitialValue().subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean hasFocus) throws Exception {
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
        RxView.focusChanges(mEtPassword).skipInitialValue().subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean hasFocus) throws Exception {
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
        mEtKeystore.setText(QRCode);
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
