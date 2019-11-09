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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.app.CustomObserver;
import com.juzix.wallet.component.ui.base.MVPBaseFragment;
import com.juzix.wallet.component.ui.contract.SendTransationContract;
import com.juzix.wallet.component.ui.dialog.CommonEditDialogFragment;
import com.juzix.wallet.component.ui.dialog.CommonTipsDialogFragment;
import com.juzix.wallet.component.ui.dialog.OnDialogViewClickListener;
import com.juzix.wallet.component.ui.presenter.SendTransactionPresenter;
import com.juzix.wallet.component.widget.PointLengthFilter;
import com.juzix.wallet.component.widget.ShadowButton;
import com.juzix.wallet.component.widget.bubbleSeekBar.BubbleSeekBar;
import com.juzix.wallet.db.sqlite.WalletDao;
import com.juzix.wallet.entity.Address;
import com.juzix.wallet.entity.Wallet;
import com.juzix.wallet.event.Event;
import com.juzix.wallet.event.EventPublisher;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.GZipUtil;
import com.juzix.wallet.utils.JZWalletUtil;
import com.juzix.wallet.utils.RxUtils;
import com.juzix.wallet.utils.StringUtil;
import com.juzix.wallet.utils.ToastUtil;
import com.juzix.wallet.utils.UMEventUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;

/**
 * @author matrixelement
 */
public class SendTransactionFragment extends MVPBaseFragment<SendTransactionPresenter> implements SendTransationContract.View {

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
    @BindView(R.id.tv_amount_magnitudes)
    TextView tvAmountMagnitudes;
    @BindView(R.id.layout_amount_magnitudes)
    LinearLayout layoutAmountMagnitudes;
    @BindString(R.string.cheaper)
    String cheaper;
    @BindString(R.string.faster)
    String faster;

    private Unbinder unbinder;

    @Override
    protected void onFragmentPageStart() {
        mPresenter.checkAddressBook(etWalletAddress.getText().toString());
        mPresenter.fetchDefaultWalletInfo();
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
    protected SendTransactionPresenter createPresenter() {
        return new SendTransactionPresenter(this);
    }

    private void initViews() {
        setSendTransactionButtonEnable(false);
        etWalletAmount.setFilters(new InputFilter[]{new PointLengthFilter()});
        etWalletAddress.addTextChangedListener(mEtWalletAddressWatcher);
        etWalletAmount.addTextChangedListener(mEtWalletAmountWatcher);
        etWalletAddress.setOnFocusChangeListener(mEtWalletAddressFocusChangeListener);
        etWalletAmount.setOnFocusChangeListener(mEtWalletAmountFocusChangeListener);
        bubbleSeekBar.setCustomSectionTextArray(new BubbleSeekBar.CustomSectionTextArray() {
            @NonNull
            @Override
            public SparseArray<String> onCustomize(int sectionCount, @NonNull SparseArray<String> array) {
                array.clear();
                array.put(0, cheaper);
                array.put(1, faster);
                return array;
            }
        });
        bubbleSeekBar.setOnProgressChangedListener(mProgressListener);

        RxView.clicks(btnSendTransation)
                .compose(RxUtils.getClickTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object object) {
                        UMEventUtil.onEventCount(getActivity(), Constants.UMEventID.SEND_TRANSACTION);
                        mPresenter.submit();
                    }
                });

    }

    @OnClick({R.id.iv_address_scan, R.id.iv_address_book, R.id.tv_save_address, R.id.tv_all_amount})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_address_book:
                SelectAddressActivity.actionStartForResult(getContext(), Constants.Action.ACTION_GET_ADDRESS, MainActivity.REQ_ASSETS_SELECT_ADDRESS_BOOK, mPresenter.getSenderAddress());
                break;
            case R.id.iv_address_scan:
                new RxPermissions(currentActivity())
                        .request(Manifest.permission.CAMERA)
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean success) throws Exception {
                                if (success) {
                                    ScanQRCodeActivity.startActivityForResult(currentActivity(), MainActivity.REQ_ASSETS_ADDRESS_QR_CODE);
                                }
                            }
                        });
                break;
            case R.id.tv_save_address:
                saveToAddressBook();
                break;
            case R.id.tv_all_amount:
                mPresenter.transferAllBalance();
                break;
            default:
                break;
        }

    }

    private void saveToAddressBook() {
        String walletAddress = etWalletAddress.getText().toString().trim();
        String walletName = WalletDao.getWalletNameByAddress(walletAddress);
        if (TextUtils.isEmpty(walletName)) {
            showSaveAddressDialog();
        } else {
            mPresenter.saveWallet(walletName, walletAddress);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            switch (requestCode) {
                case MainActivity.REQ_ASSETS_SELECT_ADDRESS_BOOK:
                    Address addressEntity = data.getParcelableExtra(Constants.Extra.EXTRA_ADDRESS);
                    if (addressEntity != null) {
                        setToAddress(addressEntity.getAddress());
                        if (mPresenter != null) {
                            mPresenter.calculateFee();
                        }
                    }
                    break;
                case MainActivity.REQ_ASSETS_ADDRESS_QR_CODE:
                    String address = data.getStringExtra(Constants.Extra.EXTRA_SCAN_QRCODE_DATA);
                    String unzip = GZipUtil.unCompress(address);
                    if (JZWalletUtil.isValidAddress(unzip)) {
                        setToAddress(unzip);
                        if (mPresenter != null) {
                            mPresenter.calculateFee();
                        }
                    } else {
                        ToastUtil.showLongToast(getContext(), string(R.string.unrecognized));
                    }
                    break;
                default:
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
    public void updateWalletBalance(String balance) {
        if (isAdded()) {
            tvWalletBalance.setText(getString(R.string.balance_text, NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(balance, "1E18"))));
        }
    }

    @Override
    public void setToAddress(String address) {
        if (etWalletAddress != null) {
            etWalletAddress.setText(address);
        }
    }

    @Override
    public void setTransferAmount(double amount) {
        etWalletAmount.setText(NumberParserUtils.getPrettyBalance(amount));
        etWalletAmount.setSelection(NumberParserUtils.getPrettyBalance(amount).length());
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
    public Wallet getWalletEntityFromIntent() {
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
    public void resetView(String feeAmount) {
        layoutAmountMagnitudes.setVisibility(View.GONE);
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
        setTransferFeeAmount(feeAmount);
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
        CommonEditDialogFragment.createCommonEditDialogFragment(string(R.string.nameOfAddress), "", InputType.TYPE_CLASS_TEXT, string(R.string.confirm), string(R.string.cancel), new OnDialogViewClickListener() {
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

    @Override
    public void setProgress(float progress) {
        bubbleSeekBar.setProgress(progress);
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

    private View.OnFocusChangeListener mEtWalletAddressFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (etWalletAddress != null) {
                String address = etWalletAddress.getText().toString().trim();
                if (!hasFocus) {
                    mPresenter.checkToAddress(address);
                } else {
                    showToAddressError("");
                }
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
            if (etWalletAmount != null) {
                String amount = etWalletAmount.getText().toString().trim();
                if (!hasFocus) {
                    mPresenter.checkTransferAmount(amount);
                } else {
                    showAmountError("");
                }
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
            mPresenter.checkTransferAmount(s.toString().trim());
            String amountMagnitudes = StringUtil.getAmountMagnitudes(getContext(), s.toString().trim());
            tvAmountMagnitudes.setText(amountMagnitudes);
            layoutAmountMagnitudes.setVisibility(TextUtils.isEmpty(amountMagnitudes) ? View.GONE : View.VISIBLE);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private BubbleSeekBar.OnProgressChangedListener mProgressListener = new BubbleSeekBar.OnProgressChangedListener() {
        @Override
        public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
            if (fromUser) {
                mPresenter.calculateFeeAndTime(NumberParserUtils.parseFloat(BigDecimalUtil.div(String.valueOf(progressFloat), "100")));
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
