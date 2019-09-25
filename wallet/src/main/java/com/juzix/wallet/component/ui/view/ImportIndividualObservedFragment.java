package com.juzix.wallet.component.ui.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.jakewharton.rxbinding2.view.RxView;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.app.CustomObserver;
import com.juzix.wallet.component.ui.base.MVPBaseFragment;
import com.juzix.wallet.component.ui.contract.ImportIndividualObservedContract;
import com.juzix.wallet.component.ui.presenter.ImportObservedPresenter;
import com.juzix.wallet.component.widget.ShadowButton;
import com.juzix.wallet.utils.RxUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ImportIndividualObservedFragment extends MVPBaseFragment<ImportObservedPresenter> implements ImportIndividualObservedContract.View {
    Unbinder unbinder;
    @BindView(R.id.et_observed)
    EditText et_observed;
    @BindView(R.id.sbtn_finish)
    ShadowButton sbtn_finish;

    @Override
    protected ImportObservedPresenter createPresenter() {
        return new ImportObservedPresenter(this);
    }

    @Override
    protected void onFragmentPageStart() {
    }

    @Override
    protected View onCreateFragmentPage(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_import_individual_observed, container, false);
        unbinder = ButterKnife.bind(this, view);
        addListener();
        return view;
    }

    private void addListener() {
        et_observed.addTextChangedListener(textWatcher);
        RxView.clicks(sbtn_finish)
                .compose(RxUtils.getClickTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        //点击完成，验证是否正确
                        mPresenter.importWalletAddress(et_observed.getText().toString());
                    }
                });

    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mPresenter.IsImportObservedWallet(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };


    @Override
    public String getWalletAddress() {
        return et_observed.getText().toString().trim();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == ImportWalletActivity.REQ_QR_CODE) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString(Constants.Extra.EXTRA_SCAN_QRCODE_DATA);
            mPresenter.parseQRCode(scanResult);
            mPresenter.IsImportObservedWallet(scanResult);
        }
    }

    @Override
    public void showQRCode(String QRCode) {
        et_observed.setText(QRCode);

    }

    @Override
    public void enableImportObservedWallet(boolean isCan) {
        sbtn_finish.setEnabled(isCan);
    }
}
