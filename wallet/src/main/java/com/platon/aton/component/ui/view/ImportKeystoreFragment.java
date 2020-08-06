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
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.platon.aton.R;
import com.platon.aton.app.CustomObserver;
import com.platon.aton.component.ui.contract.ImportKeystoreContract;
import com.platon.aton.component.ui.presenter.ImportKeystorePresenter;
import com.platon.aton.component.widget.ShadowButton;
import com.platon.aton.utils.CommonUtil;
import com.platon.aton.utils.GZipUtil;
import com.platon.aton.utils.RxUtils;
import com.platon.aton.utils.SoftHideKeyboardUtils;
import com.platon.framework.app.Constants;
import com.platon.framework.base.BaseLazyFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ImportKeystoreFragment extends BaseLazyFragment<ImportKeystoreContract.View, ImportKeystorePresenter> implements ImportKeystoreContract.View {
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
    @BindView(R.id.tv_wallet_num_over_limit)
    TextView tvWalletNumOverLimit;

    private boolean mShowPassword;
    private boolean isEnableName = true;
    private boolean isEnablePassword = true;
    private boolean isEnableKeystore = true;
    private boolean isEnableCreate = false;


    @Override
    public int getLayoutId() {
        return R.layout.fragment_import_keystore;
    }

    @Override
    public ImportKeystorePresenter createPresenter() {
        return new ImportKeystorePresenter();
    }

    @Override
    public ImportKeystoreContract.View createView() {
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

    private void initDatas() {
        showPassword();
       /* showKeystoreError("", false);
        showNameError("", false);
        showPasswordError("", false);*/
        enableImport(false);
        getPresenter().init();
        getPresenter().loadDBWalletNumber();
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
                        getPresenter().importKeystore(mEtKeystore.getText().toString(),
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

     /*   Observable<CharSequence> keystoreObservable = RxTextView.textChanges(mEtKeystore).skipInitialValue();
        Observable<CharSequence> passwordObservable = RxTextView.textChanges(mEtPassword).skipInitialValue();
        Observable<CharSequence> walletNameObservable = RxTextView.textChanges(mEtWalletName).skipInitialValue();

        Observable
                .combineLatest(keystoreObservable, passwordObservable, walletNameObservable, new Function3<CharSequence, CharSequence, CharSequence, Boolean>() {
                    @Override
                    public Boolean apply(CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3) throws Exception {
                        String keystore = charSequence.toString().trim();
                        String passsword = charSequence2.toString().trim();
                        String walletName = charSequence3.toString().trim();
                        return !TextUtils.isEmpty(keystore) &&
                               !TextUtils.isEmpty(passsword) && passsword.length() >= 6 &&
                               !TextUtils.isEmpty(walletName) && walletName.length() <= 20 && getPresenter().isExists(walletName) &&
                               !isEnableKeystore && !isEnableName && !isEnablePassword;
                    }
                }).compose(RxUtils.bindToLifecycle(this)).subscribe(new CustomObserver<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {
                enableImport(aBoolean);
            }
        });*/


        mEtWalletName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String walletName = mEtWalletName.getText().toString().trim();
                if (TextUtils.isEmpty(walletName)) {
                    showNameError(string(R.string.validWalletNameEmptyTips), true);
                } else if (walletName.length() > 12) {
                    showNameError(string(R.string.validWalletNameTips), true);
                } else if (getPresenter().isExists(walletName)) {
                    showNameError(string(R.string.wallet_name_exists), true);
                } else {
                    showNameError("", false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mEtKeystore.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keystore = mEtKeystore.getText().toString().trim();
                if (TextUtils.isEmpty(keystore)) {
                    showKeystoreError(string(R.string.validKeystoreEmptyTips), true);
                } else {
                    showKeystoreError("", false);
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
                if (TextUtils.isEmpty(password)) {
                    showPasswordError(string(R.string.validPasswordEmptyTips), true);
                } else if (password.length() < 6) {
                    showPasswordError(string(R.string.validPasswordTips), true);
                } else {
                    showPasswordError("", false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });



       /* RxView
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
                });*/

      /*  RxView.focusChanges(mEtWalletName).skipInitialValue().subscribe(new CustomObserver<Boolean>() {
            @Override
            public void accept(Boolean hasFocus) {
                String walletName = mEtWalletName.getText().toString().trim();
                if (!hasFocus) {
                    if (TextUtils.isEmpty(walletName)) {
                        showNameError(string(R.string.validWalletNameEmptyTips), true);
                    } else if (walletName.length() > 12) {
                        showNameError(string(R.string.validWalletNameTips), true);
                    } else if (getPresenter().isExists(walletName)) {
                        showNameError(string(R.string.wallet_name_exists), true);
                    } else {
                        showNameError("", false);
                    }
                }
            }
        });*/

       /* RxView
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
                });*/
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
        this.isEnableKeystore = isVisible;
        enableImport(!isEnableName && !isEnableKeystore && !isEnablePassword && isEnableCreate);
    }

    @Override
    public void showNameError(String text, boolean isVisible) {
        mTvNameError.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        mTvNameError.setText(text);
        this.isEnableName = isVisible;
        enableImport(!isEnableName && !isEnableKeystore && !isEnablePassword && isEnableCreate);
    }

    @Override
    public void showPasswordError(String text, boolean isVisible) {
        mTvPasswordError.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        mTvPasswordError.setText(text);
        this.isEnablePassword = isVisible;
        enableImport(!isEnableName && !isEnableKeystore && !isEnablePassword && isEnableCreate);
    }

    @Override
    public void enablePaste(boolean enabled) {
        mBtnPaste.setEnabled(enabled);
        mBtnPaste.setTextColor(ContextCompat.getColor(getContext(), enabled ? R.color.color_105cfe : R.color.color_d8d8d8));
    }

}
