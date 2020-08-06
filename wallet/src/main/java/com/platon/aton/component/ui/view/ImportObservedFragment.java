package com.platon.aton.component.ui.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.platon.aton.R;
import com.platon.aton.app.CustomObserver;
import com.platon.aton.component.ui.contract.ImportObservedContract;
import com.platon.aton.component.ui.presenter.ImportObservedPresenter;
import com.platon.aton.component.widget.ShadowButton;
import com.platon.aton.utils.CommonUtil;
import com.platon.aton.utils.GZipUtil;
import com.platon.aton.utils.RxUtils;
import com.platon.framework.app.Constants;
import com.platon.framework.base.BaseLazyFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ImportObservedFragment extends BaseLazyFragment<ImportObservedContract.View, ImportObservedPresenter> implements ImportObservedContract.View {
    Unbinder unbinder;
    @BindView(R.id.et_observed)
    EditText et_observed;
    @BindView(R.id.sbtn_finish)
    ShadowButton sbtn_finish;
    @BindView(R.id.btn_paste)
    Button mBtnPaste;
    @BindView(R.id.tv_wallet_num_over_limit)
    TextView tvWalletNumOverLimit;
    private boolean isEnableCreate = false;


    @Override
    public int getLayoutId() {
        return R.layout.fragment_import_observed;
    }

    @Override
    public ImportObservedPresenter createPresenter() {
        return new ImportObservedPresenter();
    }

    @Override
    public ImportObservedContract.View createView() {
        return this;
    }

    @Override
    public String getDataFromIntent() {
        Bundle bundle =getArguments();
        if (bundle != null && !bundle.isEmpty()) {
            return bundle.getString(Constants.Extra.EXTRA_SCAN_QRCODE_DATA,"");
        }
        return null;
    }

    @Override
    public void init(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
        addListener();
        getPresenter().init();
        getPresenter().loadDBWalletNumber();
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


    private void addListener() {
        RxView.clicks(sbtn_finish)
                .compose(RxUtils.getClickTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        //点击完成，验证是否正确
                        getPresenter().importWalletAddress(et_observed.getText().toString());
                    }
                });

        RxView.clicks(mBtnPaste)
                .compose(RxUtils.getClickTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        et_observed.setText(CommonUtil.getTextFromClipboard(getContext()));
                        et_observed.setSelection(et_observed.getText().toString().length());
                    }
                });

        RxTextView
                .textChanges(et_observed)
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<CharSequence>() {
                    @Override
                    public void accept(CharSequence charSequence) {
                        getPresenter().IsImportObservedWallet(charSequence.toString(),isEnableCreate);
                    }
                });
    }

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
            String scanResult = bundle.getString(Constants.Extra.EXTRA_SCAN_QRCODE_DATA, "");
            String unzip = GZipUtil.unCompress(scanResult);
            getPresenter().parseQRCode(TextUtils.isEmpty(unzip) ? scanResult : unzip);
            getPresenter().IsImportObservedWallet(TextUtils.isEmpty(unzip) ? scanResult : unzip,isEnableCreate);
        }
    }


    @Override
    public void showQRCode(String QRCode) {
        et_observed.setText(QRCode);
        et_observed.setSelection(QRCode.length());
    }

    @Override
    public void enableImportObservedWallet(boolean isCan) {
        sbtn_finish.setEnabled(isCan);
    }

    @Override
    public void enablePaste(boolean enabled) {
        mBtnPaste.setEnabled(enabled);
        mBtnPaste.setTextColor(ContextCompat.getColor(getContext(), enabled ? R.color.color_105cfe : R.color.color_d8d8d8));
    }

}
