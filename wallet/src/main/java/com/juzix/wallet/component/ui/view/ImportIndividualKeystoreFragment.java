package com.juzix.wallet.component.ui.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxbinding3.widget.RxTextView;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.ui.base.MVPBaseFragment;
import com.juzix.wallet.component.ui.contract.ImportIndividualKeystoreContract;
import com.juzix.wallet.component.ui.presenter.ImportIndividualKeystorePresenter;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function3;
import kotlin.Unit;

public class ImportIndividualKeystoreFragment extends MVPBaseFragment<ImportIndividualKeystorePresenter> implements ImportIndividualKeystoreContract.View {

    private EditText mEtKeystore;
    private EditText mEtPassword;
    private EditText mEtWalletName;
    private Button mBtnImport;
    private TextView mTvNameError;
    private TextView mTvKeystoreError;
    private TextView mTvPasswordError;

    @Override
    protected ImportIndividualKeystorePresenter createPresenter() {
        return new ImportIndividualKeystorePresenter(this);
    }

    @Override
    protected void onFragmentPageStart() {
    }

    @Override
    protected View onCreateFragmentPage(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_import_individual_keystore, container, false);
        initViews(view);
        addListeners();
        initDatas();
        return view;
    }

    private void initViews(View rootView) {
        mEtKeystore = rootView.findViewById(R.id.et_keystore);
        mTvKeystoreError = rootView.findViewById(R.id.tv_keystore_error);
        mEtPassword = rootView.findViewById(R.id.et_password);
        mTvPasswordError = rootView.findViewById(R.id.tv_password_error);
        mEtWalletName = rootView.findViewById(R.id.et_name);
        mTvNameError = rootView.findViewById(R.id.tv_name_error);
        mBtnImport = rootView.findViewById(R.id.btn_import);
    }

    private void initDatas() {
        enableImport(false);
        showKeystoreError("", false);
        showNameError("", false);
        showPasswordError("", false);
        mPresenter.init();
    }

    private void addListeners() {

        RxView.clicks(mBtnImport).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) throws Exception {
                mPresenter.importKeystore(mEtKeystore.getText().toString(),
                        mEtWalletName.getText().toString(),
                        mEtPassword.getText().toString());
            }
        });

        Observable<CharSequence> keystoreObservable = RxTextView.textChanges(mEtKeystore).skipInitialValue();
        Observable<CharSequence> passwordbservable = RxTextView.textChanges(mEtPassword).skipInitialValue();
        Observable<CharSequence> walletNameObservable = RxTextView.textChanges(mEtWalletName).skipInitialValue();

        Observable.combineLatest(keystoreObservable, passwordbservable, walletNameObservable, new Function3<CharSequence, CharSequence, CharSequence, Boolean>() {
            @Override
            public Boolean apply(CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3) throws Exception {
                return !TextUtils.isEmpty(charSequence) && !TextUtils.isEmpty(charSequence2) && charSequence2.length() >= 6 && !TextUtils.isEmpty(charSequence3) && charSequence3.length() <= 12;
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
                    if (TextUtils.isEmpty(keystore)){
                        showKeystoreError(string(R.string.validKeystoreEmptyTips), true);
                    }else {
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
        mBtnImport.setBackgroundColor(ContextCompat.getColor(getContext(), enabled ? R.color.color_eff0f5 : R.color.color_373e51));
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

}
