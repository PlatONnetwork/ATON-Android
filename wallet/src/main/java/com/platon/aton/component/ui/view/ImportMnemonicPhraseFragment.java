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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.platon.aton.R;
import com.platon.aton.component.ui.contract.ImportMnemonicPhraseContract;
import com.platon.aton.component.ui.presenter.ImportMnemonicPhrasePresenter;
import com.platon.aton.component.widget.ShadowButton;
import com.platon.aton.entity.WalletType;
import com.platon.aton.utils.CheckStrength;
import com.platon.aton.utils.DefParserStrUtil;
import com.platon.framework.app.Constants;
import com.platon.framework.base.BaseLazyFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;

public class ImportMnemonicPhraseFragment extends BaseLazyFragment<ImportMnemonicPhraseContract.View, ImportMnemonicPhrasePresenter> implements ImportMnemonicPhraseContract.View {
    Unbinder unbinder;
    @BindView(R.id.et_mnemonic1)
    EditText mEtMnemonicPhrase1;
    @BindView(R.id.et_mnemonic2)
    EditText mEtMnemonicPhrase2;
    @BindView(R.id.et_mnemonic3)
    EditText mEtMnemonicPhrase3;
    @BindView(R.id.et_mnemonic4)
    EditText mEtMnemonicPhrase4;
    @BindView(R.id.et_mnemonic5)
    EditText mEtMnemonicPhrase5;
    @BindView(R.id.et_mnemonic6)
    EditText mEtMnemonicPhrase6;
    @BindView(R.id.et_mnemonic7)
    EditText mEtMnemonicPhrase7;
    @BindView(R.id.et_mnemonic8)
    EditText mEtMnemonicPhrase8;
    @BindView(R.id.et_mnemonic9)
    EditText mEtMnemonicPhrase9;
    @BindView(R.id.et_mnemonic10)
    EditText mEtMnemonicPhrase10;
    @BindView(R.id.et_mnemonic11)
    EditText mEtMnemonicPhrase11;
    @BindView(R.id.et_mnemonic12)
    EditText mEtMnemonicPhrase12;
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
    @BindView(R.id.tv_name_error)
    TextView mTvNameError;
    @BindView(R.id.tv_mnemonic_phrase_error)
    TextView mTvMnemonicError;
    @BindView(R.id.tv_password_error)
    TextView mTvPasswordError;
    @BindView(R.id.layout_password_strength)
    LinearLayout mPasswordStrengthLayout;
    @BindView(R.id.tv_wallet_type)
    TextView mTvWalletType;
    @BindView(R.id.tv_wallet_num_over_limit)
    TextView tvWalletNumOverLimit;

    private boolean mShowPassword;
    private boolean mShowRepeatPassword;
    @WalletType
    int walletType = WalletType.ORDINARY_WALLET;//默认普通钱包类型
    private int walletNum = 0;
    private boolean isEnableCreate = false;
    private boolean isEnableName = true;
    private boolean isEnablePassword = true;
    private boolean isMnemonicPhrase = true;


    @Override
    public int getLayoutId() {
        return R.layout.fragment_import_mnemonic_phrase;
    }

    @Override
    public ImportMnemonicPhrasePresenter createPresenter() {
        return new ImportMnemonicPhrasePresenter();
    }

    @Override
    public ImportMnemonicPhraseContract.View createView() {
        return this;
    }

    @Override
    public void init(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
        addListeners();
        addTextWatcher();
        initDatas();
    }

    @Override
    public void showWalletNumber(int walletNum) {
        this.walletNum = walletNum;
        checkWalletLimit();
    }

    private void addListeners() {

        mEtPassword.setTypeface(Typeface.DEFAULT);
        mEtRepeatPassword.setTypeface(Typeface.DEFAULT);

        RxView.clicks(mTvWalletType).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                String walletType = mTvWalletType.getText().toString();
                int type = DefParserStrUtil.transforInverseWalletType(walletType, getActivity());
                SwitchWalletTypeActivity.actionStartForResult(getActivity(), type, CreateWalletActivity.REQ_WALLET_TYPE_QR_CODE);
            }
        });
        RxView.clicks(mIvPasswordEyes).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object object) throws Exception {
                showPassword();
            }
        });
        RxView.clicks(mIvRepeatPasswordEyes).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object object) throws Exception {
                showRepeatPassword();
            }
        });
        RxView.clicks(mBtnImport).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object unit) throws Exception {
                String mnemonic1 = mEtMnemonicPhrase1.getText().toString().trim();
                String mnemonic2 = mEtMnemonicPhrase2.getText().toString().trim();
                String mnemonic3 = mEtMnemonicPhrase3.getText().toString().trim();
                String mnemonic4 = mEtMnemonicPhrase4.getText().toString().trim();
                String mnemonic5 = mEtMnemonicPhrase5.getText().toString().trim();
                String mnemonic6 = mEtMnemonicPhrase6.getText().toString().trim();
                String mnemonic7 = mEtMnemonicPhrase7.getText().toString().trim();
                String mnemonic8 = mEtMnemonicPhrase8.getText().toString().trim();
                String mnemonic9 = mEtMnemonicPhrase9.getText().toString().trim();
                String mnemonic10 = mEtMnemonicPhrase10.getText().toString().trim();
                String mnemonic11 = mEtMnemonicPhrase11.getText().toString().trim();
                String mnemonic12 = mEtMnemonicPhrase12.getText().toString().trim();
                if (TextUtils.isEmpty(mnemonic1) || TextUtils.isEmpty(mnemonic2) || TextUtils.isEmpty(mnemonic3) || TextUtils.isEmpty(mnemonic4)
                        || TextUtils.isEmpty(mnemonic5) || TextUtils.isEmpty(mnemonic6) || TextUtils.isEmpty(mnemonic7) || TextUtils.isEmpty(mnemonic8)
                        || TextUtils.isEmpty(mnemonic9) || TextUtils.isEmpty(mnemonic10) || TextUtils.isEmpty(mnemonic11) || TextUtils.isEmpty(mnemonic12)) {
                    showLongToast(string(R.string.validMnenonicEmptyTips));
                    return;
                }
                if (TextUtils.isEmpty(mnemonic1) && TextUtils.isEmpty(mnemonic2) && TextUtils.isEmpty(mnemonic3) && TextUtils.isEmpty(mnemonic4)
                        && TextUtils.isEmpty(mnemonic5) && TextUtils.isEmpty(mnemonic6) && TextUtils.isEmpty(mnemonic7) && TextUtils.isEmpty(mnemonic8)
                        && TextUtils.isEmpty(mnemonic9) && TextUtils.isEmpty(mnemonic10) && TextUtils.isEmpty(mnemonic11) && TextUtils.isEmpty(mnemonic12)) {
                    showLongToast(string(R.string.validMnenonicEmptyTips));
                    return;
                }
                StringBuilder builder = new StringBuilder();
                builder.append(mnemonic1).append(" ");
                builder.append(mnemonic2).append(" ");
                builder.append(mnemonic3).append(" ");
                builder.append(mnemonic4).append(" ");
                builder.append(mnemonic5).append(" ");
                builder.append(mnemonic6).append(" ");
                builder.append(mnemonic7).append(" ");
                builder.append(mnemonic8).append(" ");
                builder.append(mnemonic9).append(" ");
                builder.append(mnemonic10).append(" ");
                builder.append(mnemonic11).append(" ");
                builder.append(mnemonic12);
                getPresenter().importMnemonic(builder.toString(),
                        mEtWalletName.getText().toString(),
                        mEtPassword.getText().toString(),
                        mEtRepeatPassword.getText().toString(),
                        walletType);
            }
        });

        mEtWalletName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
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
                checkPwdStreng(password);
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

    private void addTextWatcher() {
        setTextWatcher(mEtMnemonicPhrase1, mEtMnemonicPhrase2);
        setTextWatcher(mEtMnemonicPhrase2, mEtMnemonicPhrase3);
        setTextWatcher(mEtMnemonicPhrase3, mEtMnemonicPhrase4);
        setTextWatcher(mEtMnemonicPhrase4, mEtMnemonicPhrase5);
        setTextWatcher(mEtMnemonicPhrase5, mEtMnemonicPhrase6);
        setTextWatcher(mEtMnemonicPhrase6, mEtMnemonicPhrase7);
        setTextWatcher(mEtMnemonicPhrase7, mEtMnemonicPhrase8);
        setTextWatcher(mEtMnemonicPhrase8, mEtMnemonicPhrase9);
        setTextWatcher(mEtMnemonicPhrase9, mEtMnemonicPhrase10);
        setTextWatcher(mEtMnemonicPhrase10, mEtMnemonicPhrase11);
        setTextWatcher(mEtMnemonicPhrase11, mEtMnemonicPhrase12);
        setTextWatcher(mEtMnemonicPhrase12, null);
    }

    private void setTextWatcher(EditText src, EditText dst) {
        src.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String mnemonic1 = mEtMnemonicPhrase1.getText().toString().trim();
                String mnemonic2 = mEtMnemonicPhrase2.getText().toString().trim();
                String mnemonic3 = mEtMnemonicPhrase3.getText().toString().trim();
                String mnemonic4 = mEtMnemonicPhrase4.getText().toString().trim();
                String mnemonic5 = mEtMnemonicPhrase5.getText().toString().trim();
                String mnemonic6 = mEtMnemonicPhrase6.getText().toString().trim();
                String mnemonic7 = mEtMnemonicPhrase7.getText().toString().trim();
                String mnemonic8 = mEtMnemonicPhrase8.getText().toString().trim();
                String mnemonic9 = mEtMnemonicPhrase9.getText().toString().trim();
                String mnemonic10 = mEtMnemonicPhrase10.getText().toString().trim();
                String mnemonic11 = mEtMnemonicPhrase11.getText().toString().trim();
                String mnemonic12 = mEtMnemonicPhrase12.getText().toString().trim();
                if (TextUtils.isEmpty(mnemonic1) || TextUtils.isEmpty(mnemonic2) || TextUtils.isEmpty(mnemonic3) || TextUtils.isEmpty(mnemonic4)
                        || TextUtils.isEmpty(mnemonic5) || TextUtils.isEmpty(mnemonic6) || TextUtils.isEmpty(mnemonic7) || TextUtils.isEmpty(mnemonic8)
                        || TextUtils.isEmpty(mnemonic9) || TextUtils.isEmpty(mnemonic10) || TextUtils.isEmpty(mnemonic11) || TextUtils.isEmpty(mnemonic12)) {

                    showMnemonicPhraseError(string(R.string.validMnenonicEmptyTips),true);
                    return;
                }
                if (TextUtils.isEmpty(mnemonic1) && TextUtils.isEmpty(mnemonic2) && TextUtils.isEmpty(mnemonic3) && TextUtils.isEmpty(mnemonic4)
                        && TextUtils.isEmpty(mnemonic5) && TextUtils.isEmpty(mnemonic6) && TextUtils.isEmpty(mnemonic7) && TextUtils.isEmpty(mnemonic8)
                        && TextUtils.isEmpty(mnemonic9) && TextUtils.isEmpty(mnemonic10) && TextUtils.isEmpty(mnemonic11) && TextUtils.isEmpty(mnemonic12)) {
                    showMnemonicPhraseError(string(R.string.validMnenonicEmptyTips),true);
                    return;
                }
                if (!TextUtils.isEmpty(mnemonic1) && !TextUtils.isEmpty(mnemonic2) && !TextUtils.isEmpty(mnemonic3) && !TextUtils.isEmpty(mnemonic4)
                        && !TextUtils.isEmpty(mnemonic5) && !TextUtils.isEmpty(mnemonic6) && !TextUtils.isEmpty(mnemonic7) && !TextUtils.isEmpty(mnemonic8)
                        && !TextUtils.isEmpty(mnemonic9) && !TextUtils.isEmpty(mnemonic10) && !TextUtils.isEmpty(mnemonic11) && !TextUtils.isEmpty(mnemonic12)) {
                    showMnemonicPhraseError(string(R.string.validMnenonicEmptyTips),false);
                    return;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (text.contains(" ")) {
                    src.setText(text.replace(" ", ""));
                    if (dst != null) {
                        dst.requestFocus();
                        dst.setSelection(dst.getText().length());
                    }

                }
            }
        });
    }

    private void initDatas() {

        showPassword();
        showRepeatPassword();
  /*      showMnemonicPhraseError("", false);
        showNameError("", false);
        showPasswordError("", false);*/
        enableImport(false);
        getPresenter().init();
        getPresenter().loadDBWalletNumber();
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
            getPresenter().parseQRCode(scanResult);
        } else if (requestCode == CreateWalletActivity.REQ_WALLET_TYPE_QR_CODE) {
            walletType = data.getIntExtra(Constants.Extra.EXTRA_WALLET_TYPE, 0);
            mTvWalletType.setText(DefParserStrUtil.transformWalletType(walletType, getActivity()));
            checkWalletLimit();
        }
    }

    //控制钱包数量上限
    private void checkWalletLimit(){

        int sumWalletNum = 0;
        if (walletType == WalletType.ORDINARY_WALLET) {
            sumWalletNum = walletNum + Constants.WalletConstants.WALLET_ADD_ORDINARY;
        } else {
            sumWalletNum = walletNum + Constants.WalletConstants.WALLET_ADD_HD;
        }
        if(sumWalletNum > Constants.WalletConstants.WALLET_LIMIT){
            tvWalletNumOverLimit.setVisibility(View.VISIBLE);
            isEnableCreate = false;
        }else{
            tvWalletNumOverLimit.setVisibility(View.GONE);
            isEnableCreate = true;
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
    public void showMnemonicWords(List<String> words) {
        mEtMnemonicPhrase1.setText(words.get(0));
        mEtMnemonicPhrase2.setText(words.get(1));
        mEtMnemonicPhrase3.setText(words.get(2));
        mEtMnemonicPhrase4.setText(words.get(3));
        mEtMnemonicPhrase5.setText(words.get(4));
        mEtMnemonicPhrase6.setText(words.get(5));
        mEtMnemonicPhrase7.setText(words.get(6));
        mEtMnemonicPhrase8.setText(words.get(7));
        mEtMnemonicPhrase9.setText(words.get(8));
        mEtMnemonicPhrase10.setText(words.get(9));
        mEtMnemonicPhrase11.setText(words.get(10));
        mEtMnemonicPhrase12.setText(words.get(11));
    }

    private void enableImport(boolean enabled) {
        mBtnImport.setEnabled(enabled);
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

    private void checkPwdStreng(String password) {

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
    public void showMnemonicPhraseError(String text, boolean isVisible) {
        mTvMnemonicError.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        mTvMnemonicError.setText(text);
        this.isMnemonicPhrase = isVisible;
        enableImport(!isEnableName && !isEnablePassword && !isMnemonicPhrase  && isEnableCreate);
    }

    @Override
    public void showNameError(String text, boolean isVisible) {
        mTvNameError.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        mTvNameError.setText(text);
        this.isEnableName = isVisible;
        enableImport(!isEnableName && !isEnablePassword && !isMnemonicPhrase  && isEnableCreate);
    }

    @Override
    public void showPasswordError(String text, boolean isVisible) {
        mTvPasswordError.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        mTvPasswordError.setText(text);
        mTvPasswordDesc.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        this.isEnablePassword = isVisible;
        enableImport(!isEnableName && !isEnablePassword && !isMnemonicPhrase  && isEnableCreate);
    }

}
