package com.juzix.wallet.component.ui.view;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding3.view.RxView;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.ClickTransformer;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.ui.base.MVPBaseFragment;
import com.juzix.wallet.component.ui.contract.SendTransationContract;
import com.juzix.wallet.component.ui.dialog.CommonEditDialogFragment;
import com.juzix.wallet.component.ui.dialog.CommonTipsDialogFragment;
import com.juzix.wallet.component.ui.dialog.OnDialogViewClickListener;
import com.juzix.wallet.component.ui.presenter.SendTransationPresenter;
import com.juzix.wallet.component.widget.PointLengthFilter;
import com.juzix.wallet.component.widget.ShadowButton;
import com.juzix.wallet.component.widget.bubbleSeekBar.BubbleSeekBar;
import com.juzix.wallet.config.PermissionConfigure;
import com.juzix.wallet.entity.AddressEntity;
import com.juzix.wallet.entity.WalletEntity;
import com.juzix.wallet.event.Event;
import com.juzix.wallet.event.EventPublisher;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.JZWalletUtil;
import com.juzix.wallet.utils.ToastUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;
import kotlin.Unit;

/**
 * @author matrixelement
 */
public class SendTransactionFragment extends MVPBaseFragment<SendTransationPresenter> implements SendTransationContract.View {

    @BindView(R.id.iv_address_book)
    ImageView ivAddressBook;
    @BindView(R.id.et_wallet_address)
    EditText etWalletAddress;
    @BindView(R.id.tv_transation_amount)
    TextView tvTransationAmount;
    @BindView(R.id.et_wallet_amount)
    EditText etWalletAmount;
    @BindView(R.id.tv_all_amount)
    TextView tvAllAmount;
    @BindView(R.id.tv_fee_amount)
    TextView tvFeeAmount;
    @BindView(R.id.bubbleSeekBar)
    BubbleSeekBar bubbleSeekBar;
    @BindView(R.id.sbtn_send_transation)
    ShadowButton btnSendTransation;
    @BindView(R.id.tv_wallet_balance)
    TextView tvWalletBalance;
    @BindView(R.id.layout_transation_amount)
    FrameLayout layoutTransationAmount;
    @BindView(R.id.tv_to_address_error)
    TextView tvToAddressError;
    @BindView(R.id.tv_amount_error)
    TextView tvAmountError;
    @BindView(R.id.tv_save_address)
    TextView tvSaveAddress;
    @BindString(R.string.cheaper)
    String cheaper;
    @BindString(R.string.faster)
    String faster;

    private Unbinder unbinder;

    @Override
    protected void onFragmentPageStart() {
        mPresenter.checkAddressBook(etWalletAddress.getText().toString());
    }

    @Nullable
    @Override
    public View onCreateFragmentPage(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_send_transaction, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        EventPublisher.getInstance().register(this);
        initViews();
        mPresenter.init();
        mPresenter.fetchDefaultWalletInfo();
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventPublisher.getInstance().unRegister(this);
        etWalletAddress.removeTextChangedListener(mEtWalletAddressWatcher);
        etWalletAmount.removeTextChangedListener(mEtWalletAmountWatcher);
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    protected SendTransationPresenter createPresenter() {
        return new SendTransationPresenter(this);
    }

    private void initViews() {
        setSendTransactionButtonEnable(false);
        etWalletAmount.setFilters(new InputFilter[]{new PointLengthFilter()});
        etWalletAddress.addTextChangedListener(mEtWalletAddressWatcher);
        etWalletAmount.addTextChangedListener(mEtWalletAmountWatcher);
        etWalletAddress.setOnFocusChangeListener(mEtWalletAddressFocusChangeListener);
        etWalletAmount.setOnFocusChangeListener(mEtWalletAmountFocusChangeListener);
        bubbleSeekBar.setCustomSectionTextArray(new BubbleSeekBar.CustomSectionTextArray() {
            @androidx.annotation.NonNull
            @Override
            public SparseArray<String> onCustomize(int sectionCount, @androidx.annotation.NonNull SparseArray<String> array) {
                array.clear();
                array.put(0, cheaper);
                array.put(3, faster);
                return array;
            }
        });
        bubbleSeekBar.setOnProgressChangedListener(mProgressListener);

        RxView.clicks(btnSendTransation)
                .compose(new ClickTransformer())
                .compose(bindToLifecycle())
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
                        mPresenter.submit();
                    }
                });

    }

    @OnClick({R.id.iv_address_scan, R.id.iv_address_book, R.id.tv_save_address, R.id.tv_all_amount})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_address_book:
                SelectAddressActivity.actionStartForResult(getContext(), Constants.Action.ACTION_GET_ADDRESS, MainActivity.REQ_ASSETS_SELECT_ADDRESS_BOOK);
                break;
            case R.id.iv_address_scan:
                requestPermission(currentActivity(), 100, new PermissionConfigure.PermissionCallback() {
                    @Override
                    public void onSuccess(int what, @NonNull List<String> grantPermissions) {
                        ScanQRCodeActivity.startActivityForResult(currentActivity(), MainActivity.REQ_ASSETS_ADDRESS_QR_CODE);
                    }

                    @Override
                    public void onHasPermission(int what) {
                        ScanQRCodeActivity.startActivityForResult(currentActivity(), MainActivity.REQ_ASSETS_ADDRESS_QR_CODE);
                    }

                    @Override
                    public void onFail(int what, @NonNull List<String> deniedPermissions) {

                    }
                }, Manifest.permission.CAMERA);
                break;
            case R.id.tv_save_address:
                showSaveAddressDialog();
                break;
            case R.id.tv_all_amount:
                mPresenter.transferAllBalance();
                break;
            default:
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            switch (requestCode) {
                case MainActivity.REQ_ASSETS_SELECT_ADDRESS_BOOK:
                    AddressEntity addressEntity = data.getParcelableExtra(Constants.Extra.EXTRA_ADDRESS);
                    if (addressEntity != null) {
                        setToAddress(addressEntity.getAddress());
                        mPresenter.calculateFee();
                    }
                    break;
                case MainActivity.REQ_ASSETS_ADDRESS_QR_CODE:
                    String address = data.getStringExtra(Constants.Extra.EXTRA_SCAN_QRCODE_DATA);
                    if (JZWalletUtil.isValidAddress(address)) {
                        setToAddress(address);
                        mPresenter.calculateFee();
                    } else {
                        ToastUtil.showLongToast(getContext(), string(R.string.unrecognized));
                    }
                    break;
            }

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateSelectedWalletEvent(Event.UpdateSelectedWalletEvent event) {
        mPresenter.fetchDefaultWalletInfo();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateAssetsTabEvent(Event.UpdateAssetsTabEvent event) {
        mPresenter.updateAssetsTab(event.tabIndex);
    }

    @Override
    public void updateWalletInfo(WalletEntity walletEntity) {
        tvWalletBalance.setText(string(R.string.balance_text, NumberParserUtils.getPrettyBalance(walletEntity.getBalance())));
    }

    @Override
    public void setToAddress(String address) {
        if (etWalletAddress != null){
            etWalletAddress.setText(address);
        }
    }

    @Override
    public void setTransferAmount(double amount) {
        etWalletAmount.setText(NumberParserUtils.getPrettyBalance(amount));
    }

    @Override
    public void setTransferFeeAmount(String feeAmount) {
        tvFeeAmount.setText(string(R.string.amount_with_unit, feeAmount));
    }

    @Override
    public String getTransferAmount() {
        return etWalletAmount.getText().toString();
    }

    @Override
    public String getToAddress() {
        return etWalletAddress.getText().toString();
    }

    @Override
    public WalletEntity getWalletEntityFromIntent() {
        return getArguments().getParcelable(Constants.Extra.EXTRA_WALLET);
    }

    @Override
    public void showToAddressError(String errMsg) {
        tvToAddressError.setVisibility(TextUtils.isEmpty(errMsg) ? View.GONE : View.VISIBLE);
        tvToAddressError.setText(errMsg);
    }

    @Override
    public void showAmountError(String errMsg) {
        tvAmountError.setVisibility(TextUtils.isEmpty(errMsg) ? View.GONE : View.VISIBLE);
        tvAmountError.setText(errMsg);
    }

    @Override
    public void setSendTransactionButtonEnable(boolean enabled) {
        btnSendTransation.setEnabled(enabled);
    }

    @Override
    public void setSendTransactionButtonVisible(boolean isVisible) {
        btnSendTransation.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setSaveAddressButtonEnable(boolean enable) {
        tvSaveAddress.setEnabled(enable);
        tvSaveAddress.setTextColor(ContextCompat.getColor(getContext(), enable ? R.color.color_105cfe : R.color.color_b6bbd0));
    }

    @Override
    public void resetView(double feeAmount) {
        etWalletAddress.removeTextChangedListener(mEtWalletAddressWatcher);
        etWalletAmount.removeTextChangedListener(mEtWalletAmountWatcher);
        etWalletAddress.setOnFocusChangeListener(null);
        etWalletAmount.setOnFocusChangeListener(null);
        currentActivity().hideSoftInput();
        etWalletAddress.setFocusable(false);
        etWalletAddress.setFocusableInTouchMode(false);
        etWalletAmount.setFocusable(false);
        etWalletAmount.setFocusableInTouchMode(false);
        etWalletAddress.setText("");
        etWalletAmount.setText("");
        setTransferFeeAmount(String.valueOf(feeAmount));
        bubbleSeekBar.setProgress(0);
        setSendTransactionButtonEnable(false);
        setSaveAddressButtonEnable(false);
        showToAddressError("");
        showAmountError("");
        etWalletAddress.setFocusable(true);
        etWalletAddress.setFocusableInTouchMode(true);
        etWalletAmount.setFocusable(true);
        etWalletAmount.setFocusableInTouchMode(true);
        etWalletAddress.addTextChangedListener(mEtWalletAddressWatcher);
        etWalletAmount.addTextChangedListener(mEtWalletAmountWatcher);
        etWalletAddress.setOnFocusChangeListener(mEtWalletAddressFocusChangeListener);
        etWalletAmount.setOnFocusChangeListener(mEtWalletAmountFocusChangeListener);
    }

    @Override
    public void showSaveAddressDialog() {
        CommonEditDialogFragment.createCommonEditDialogFragment(string(R.string.nameOfWallet), "", InputType.TYPE_CLASS_TEXT, string(R.string.confirm), string(R.string.cancel), new OnDialogViewClickListener() {
            @Override
            public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                String text = extra.getString(Constants.Bundle.BUNDLE_TEXT);
                if (text.length() > 12) {
                    CommonTipsDialogFragment.createDialogWithTitleAndOneButton(ContextCompat.getDrawable(getContext(), R.drawable.icon_dialog_tips),
                            string(R.string.formatError), string(R.string.validWalletNameTips), string(R.string.understood), new OnDialogViewClickListener() {
                                @Override
                                public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                                    showSaveAddressDialog();
                                }
                            }).show(getChildFragmentManager(), "showTips");
                } else {
                    mPresenter.saveWallet(text, etWalletAddress.getText().toString());
                }
            }
        }).show(getChildFragmentManager(), "showTips");

    }

    private View.OnFocusChangeListener mEtWalletAddressFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            String address = etWalletAddress.getText().toString().trim();
            if (!hasFocus) {
                mPresenter.checkToAddress(address);
            } else {
                showToAddressError("");
            }
        }
    };

    private TextWatcher mEtWalletAddressWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String text = s.toString();
            mPresenter.updateSendTransactionButtonStatus();
            mPresenter.checkAddressBook(text);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private View.OnFocusChangeListener mEtWalletAmountFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            String amount = etWalletAmount.getText().toString().trim();
            if (!hasFocus) {
                mPresenter.checkTransferAmount(amount);
            } else {
                showAmountError("");
            }
        }
    };

    private TextWatcher mEtWalletAmountWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mPresenter.updateSendTransactionButtonStatus();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private BubbleSeekBar.OnProgressChangedListener mProgressListener = new BubbleSeekBar.OnProgressChangedListener() {
        @Override
        public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
            if (fromUser) {
                mPresenter.calculateFeeAndTime(BigDecimalUtil.div(progress, bubbleSeekBar.getMax()));
                mPresenter.updateSendTransactionButtonStatus();
                if (etWalletAmount.isFocused()) {
                    etWalletAmount.clearFocus();
                }
                if (etWalletAddress.isFocused()) {
                    etWalletAddress.clearFocus();
                }
                String amount = etWalletAmount.getText().toString().trim();
                if (!TextUtils.isEmpty(amount)) {
                    mPresenter.checkTransferAmount(amount);
                }
            }
        }

        @Override
        public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

        }

        @Override
        public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
        }
    };
}
