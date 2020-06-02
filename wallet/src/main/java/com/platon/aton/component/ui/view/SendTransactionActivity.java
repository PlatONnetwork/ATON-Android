package com.platon.aton.component.ui.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.platon.aton.R;
import com.platon.aton.app.CustomObserver;
import com.platon.aton.component.ui.contract.SendTransationContract;
import com.platon.aton.component.ui.dialog.TransactionSignatureDialogFragment;
import com.platon.aton.component.ui.presenter.SendTransactionPresenter;
import com.platon.aton.component.widget.CommonTitleBar;
import com.platon.aton.component.widget.EmojiExcludeFilter;
import com.platon.aton.component.widget.MyWatcher;
import com.platon.aton.component.widget.RoundedTextView;
import com.platon.aton.component.widget.ShadowButton;
import com.platon.aton.component.widget.bubbleSeekBar.BubbleSeekBar;
import com.platon.aton.db.sqlite.WalletDao;
import com.platon.aton.entity.Address;
import com.platon.aton.entity.AddressMatchingResultType;
import com.platon.aton.entity.Wallet;
import com.platon.aton.event.Event;
import com.platon.aton.event.EventPublisher;
import com.platon.aton.netlistener.NetworkType;
import com.platon.aton.netlistener.NetworkUtil;
import com.platon.aton.utils.BigDecimalUtil;
import com.platon.aton.utils.GZipUtil;
import com.platon.aton.utils.JZWalletUtil;
import com.platon.aton.utils.NumberParserUtils;
import com.platon.aton.utils.RxUtils;
import com.platon.aton.utils.StringUtil;
import com.platon.aton.utils.UMEventUtil;
import com.platon.framework.app.Constants;
import com.platon.framework.base.BaseActivity;
import com.platon.framework.network.NetConnectivity;
import com.platon.framework.utils.LogUtils;
import com.platon.framework.utils.ToastUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.web3j.tx.gas.DefaultGasProvider;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;

public class SendTransactionActivity extends BaseActivity<SendTransationContract.View,SendTransactionPresenter> implements SendTransationContract.View{

    private final static long MAX_GAS_LIMIT = 999999999;

    @BindView(R.id.commonTitleBar)
    CommonTitleBar commonTitleBar;
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
    @BindView(R.id.layout_advanced_function)
    LinearLayout layoutAdvancedFunction;
    @BindView(R.id.layout_view)
    View layoutView;
    @BindView(R.id.iv_advanced_function)
    ImageView ivAdvancedFunction;
    @BindView(R.id.et_gas_limit)
    EditText etGasLimit;
    @BindView(R.id.tv_gas_limit_error)
    TextView tvGasLimitError;
    @BindView(R.id.et_transaction_note)
    EditText etTransactionNote;
    @BindView(R.id.iv_clear)
    ImageView ivClear;
    @BindView(R.id.layout_network_no)
    RelativeLayout layoutNetworkNo;
    @BindView(R.id.tv_network_title)
    TextView tvNetworkTitle;
    @BindView(R.id.tv_refresh_network)
    RoundedTextView tvRefreshNetwork;

    private Unbinder unbinder;
    private boolean mShowAdvancedFunction = false;

    @Override
    protected boolean immersiveBarViewEnabled() {
        return true;
    }

    @Override
    public SendTransactionPresenter createPresenter() {
        return new SendTransactionPresenter();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_send_transaction;
    }

    @Override
    public SendTransationContract.View createView() {
        return this;
    }

    @Override
    public void init() {
        unbinder = ButterKnife.bind(this);
        EventPublisher.getInstance().register(this);
        initViews();
        //getPresenter().init();
        getPresenter().fetchDefaultWalletInfo();
        getPresenter().checkAddressBook(etWalletAddress.getText().toString());
    }

    private void initViews() {
        setSendTransactionButtonEnable(false);
        updateNetWorkState();
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
                array.put(3, faster);

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
                        UMEventUtil.onEventCount(getContext(), Constants.UMEventID.SEND_TRANSACTION);
                        getPresenter().submit();
                    }
                });

        etGasLimit.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (NumberParserUtils.parseLong(dest.toString() + source.toString()) > MAX_GAS_LIMIT) {
                    return "";
                }
                return null;
            }
        }});

        etTransactionNote.setFilters(new InputFilter[]{new EmojiExcludeFilter(), new InputFilter.LengthFilter(30)});

        RxTextView
                .textChanges(etGasLimit)
                .compose(bindToLifecycle())
                .subscribe(new CustomObserver<CharSequence>() {
                    @Override
                    public void accept(CharSequence gasLimit) {
                        getPresenter().setGasLimit(gasLimit.toString().trim());
                    }
                });
        RxTextView.textChanges(etTransactionNote)
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<CharSequence>() {
                    @Override
                    public void accept(CharSequence charSequence) {
                        ivClear.setVisibility(TextUtils.isEmpty(charSequence) ? View.GONE : View.VISIBLE);
                    }
                });

        RxView.clicks(ivClear)
                .compose(RxUtils.getClickTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Object>() {

                    @Override
                    public void accept(Object o) {
                        etTransactionNote.setText("");
                    }
                });

        commonTitleBar.setRightDrawableClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });
    }

    private void showAdvancedFunctionView(boolean showAdvancedFunction) {
        layoutAdvancedFunction.setVisibility(showAdvancedFunction ? View.VISIBLE : View.GONE);
    }

    @OnClick({R.id.iv_address_book, R.id.tv_save_address, R.id.tv_all_amount, R.id.tv_fee_amount_title, R.id.iv_advanced_function, R.id.tv_refresh_network})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_address_book:
                SelectAddressActivity.actionStartForResult(getContext(), Constants.Action.ACTION_GET_ADDRESS, MainActivity.REQ_ASSETS_SELECT_ADDRESS_BOOK, getPresenter().getSenderAddress());
                break;
            case R.id.tv_save_address:
                saveToAddressBook();
                break;
            case R.id.tv_all_amount:
                getPresenter().transferAllBalance();
                break;
            case R.id.tv_fee_amount_title:
            case R.id.iv_advanced_function:
                mShowAdvancedFunction = !mShowAdvancedFunction;
                layoutAdvancedFunction.setVisibility(mShowAdvancedFunction ? View.VISIBLE : View.GONE);
                if(NetworkUtil.getNetWorkType(getContext()) != NetworkType.NETWORK_NO){
                    layoutNetworkNo.setVisibility(mShowAdvancedFunction ? View.GONE : View.INVISIBLE);
                }
               // layoutView.setVisibility(mShowAdvancedFunction ? View.GONE : View.VISIBLE);

                showAnimation(mShowAdvancedFunction);
                break;
            case R.id.tv_refresh_network:
                if (!NetConnectivity.getConnectivityManager().isConnected()) {
                    showLongToast(R.string.network_error);
                    return;
                }
                getPresenter().fetchDefaultWalletInfo();
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
                    Address addressEntity = data.getParcelableExtra(Constants.Extra.EXTRA_ADDRESS);
                    if (addressEntity != null) {
                        setToAddress(addressEntity.getAddress());
                    }
                    break;
                case MainActivity.REQ_ASSETS_ADDRESS_QR_CODE:
                    String address = data.getStringExtra(Constants.Extra.EXTRA_SCAN_QRCODE_DATA);
                    String unzip = GZipUtil.unCompress(address);
                    String newStr = TextUtils.isEmpty(unzip) ? address : unzip;

                    if (JZWalletUtil.isValidAddress(newStr)) {
                        int result = JZWalletUtil.isValidAddressMatchingNet(newStr);
                        if(result == AddressMatchingResultType.ADDRESS_MAINNET_MATCHING){
                            setToAddress(newStr);
                        }else if(result == AddressMatchingResultType.ADDRESS_MAINNET_MISMATCHING){
                            ToastUtil.showLongToast(getContext(), string(R.string.receive_address_match_mainnet_error));
                        }else if(result == AddressMatchingResultType.ADDRESS_TESTNET_MATCHING){
                            setToAddress(newStr);
                        }else if(result == AddressMatchingResultType.ADDRESS_TESTNET_MISMATCHING){
                            ToastUtil.showLongToast(getContext(), string(R.string.receive_address_match_testnet_error));
                        }
                    } else {
                        ToastUtil.showLongToast(getContext(), string(R.string.unrecognized_content));
                    }
                    break;
                case Constants.RequestCode.REQUEST_CODE_TRANSACTION_SIGNATURE:
                    getSupportFragmentManager().findFragmentByTag(TransactionSignatureDialogFragment.TAG).onActivityResult(Constants.RequestCode.REQUEST_CODE_TRANSACTION_SIGNATURE, resultCode, data);
                    break;
                default:
                    break;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateSelectedWalletEvent(Event.UpdateSelectedWalletEvent event) {
        getPresenter().fetchDefaultWalletInfo();
    }

  /*  @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateAssetsTabEvent(Event.UpdateAssetsTabEvent event) {
        mPresenter.updateAssetsTab(event.tabIndex);
        resetDefaultGasLimit();
    }*/

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetWorkStateChangedEvent(Event.NetWorkStateChangedEvent event) {
        updateNetWorkState();
    }

    public void updateNetWorkState(){
        if(NetworkUtil.getNetWorkType(getContext()) == NetworkType.NETWORK_NO){
            layoutNetworkNo.setVisibility(View.VISIBLE);
        }else{
            layoutNetworkNo.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void updateWalletBalance(String balance) {
        LogUtils.e("----balance:" + balance);
        LogUtils.e("----formatBalance:" + StringUtil.formatBalance(BigDecimalUtil.div(balance, "1E18")));
        tvWalletBalance.setText(getString(R.string.balance_text, StringUtil.formatBalance(BigDecimalUtil.div(balance, "1E18"))));
    }

    @Override
    public void setToAddress(String address) {
        if (etWalletAddress != null) {
            etWalletAddress.setText(address);
            etWalletAddress.setSelection(address.length());
        }
    }

    @Override
    public void setTransferAmount(double amount) {
        etWalletAmount.setText(NumberParserUtils.getPrettyBalance(amount));
        etWalletAmount.setSelection(NumberParserUtils.getPrettyBalance(amount).length());
    }

    @Override
    public void setTransferFeeAmount(String feeAmount) {
        //tvFeeAmount.setText(string(R.string.amount_with_unit, feeAmount));
        tvFeeAmount.setText(feeAmount);
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
       // return getArguments().getParcelable(Constants.Extra.EXTRA_WALLET);
        return null;
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
        etTransactionNote.setText("");
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

    //保存钱包地址至地址簿
    @Override
    public void showSaveAddressDialog() {
       /* CommonEditDialogFragment.createCommonEditDialogFragment(string(R.string.nameOfAddress), "", InputType.TYPE_CLASS_TEXT, string(R.string.confirm), string(R.string.cancel), new OnDialogViewClickListener() {
            @Override
            public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                String text = extra.getString(Constants.Bundle.BUNDLE_TEXT);
                if (text.length() > 20) {
                    CommonTipsDialogFragment.createDialogWithTitleAndOneButton(ContextCompat.getDrawable(getContext(), R.drawable.icon_dialog_tips),
                            string(R.string.formatError), string(R.string.validWalletNameTips), string(R.string.understood), new OnDialogViewClickListener() {
                                @Override
                                public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                                    showSaveAddressDialog();
                                }
                            }).show(getSupportFragmentManager(), "showTips");
                } else {
                    mPresenter.saveWallet(text, etWalletAddress.getText().toString());
                }
            }
        }).show(getSupportFragmentManager(), "showTips");*/

    }

    @Override
    public void setProgress(float progress) {
        bubbleSeekBar.setProgress(progress);
     /*   LogUtils.e("-------progress:" + progress);
        LogUtils.e("-------getMax:" + bubbleSeekBar.getMax());
        LogUtils.e("-------getMin:" + bubbleSeekBar.getMin());*/
    }

    @Override
    public String getGasLimit() {
        return etGasLimit.getText().toString().trim();
    }

    @Override
    public void setGasLimit(String gasLimit) {
        etGasLimit.setText(gasLimit);
        etGasLimit.setSelection(gasLimit.length());
    }

    @Override
    public void showGasLimitError(boolean isShow) {
        tvGasLimitError.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public boolean isShowAdvancedFunction() {
        return mShowAdvancedFunction;
    }

    @Override
    public String getTransactionRemark() {
        return etTransactionNote.getText().toString().trim();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventPublisher.getInstance().unRegister(this);
        etWalletAddress.removeTextChangedListener(mEtWalletAddressWatcher);
        etWalletAmount.removeTextChangedListener(mEtWalletAmountWatcher);
        if (unbinder != null) {
            unbinder.unbind();
        }
    }


    private void resetDefaultGasLimit() {
        showAdvancedFunctionView(mShowAdvancedFunction = false);
        etGasLimit.setText(DefaultGasProvider.GAS_LIMIT.toString(10));
        etGasLimit.setSelection(DefaultGasProvider.GAS_LIMIT.toString(10).length());
    }


    private void showAnimation(boolean showAdvancedFunction) {

        int startDegree = showAdvancedFunction ? 0 : 180;
        int toDegree = showAdvancedFunction ? 180 : 360;

        RotateAnimation rotateAnimation = new RotateAnimation(startDegree, toDegree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setDuration(500);
        ivAdvancedFunction.startAnimation(rotateAnimation);
    }


    private void saveToAddressBook() {
        String walletAddress = etWalletAddress.getText().toString().trim();
        String walletName = WalletDao.getWalletNameByAddress(walletAddress);
        if (TextUtils.isEmpty(walletName)) {
            showSaveAddressDialog();
        } else {
            getPresenter().saveWallet(walletName, walletAddress);
        }
    }

    private View.OnFocusChangeListener mEtWalletAddressFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (etWalletAddress != null) {
                String address = etWalletAddress.getText().toString().trim();
                if (!hasFocus) {
                    getPresenter().checkToAddress(address);
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
            getPresenter().updateSendTransactionButtonStatus();
            getPresenter().checkAddressBook(text);
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
                    getPresenter().checkTransferAmount(amount);
                } else {
                    showAmountError("");
                }
            }
        }
    };

    private TextWatcher mEtWalletAmountWatcher = new MyWatcher(-1, 8) {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            getPresenter().updateSendTransactionButtonStatus();
            getPresenter().checkTransferAmount(s.toString().trim());
            String amountMagnitudes = StringUtil.getAmountMagnitudes(getContext(), s.toString().trim());
            tvAmountMagnitudes.setText(amountMagnitudes);
            layoutAmountMagnitudes.setVisibility(TextUtils.isEmpty(amountMagnitudes) ? View.GONE : View.VISIBLE);
        }

        @Override
        public void afterTextChanged(Editable s) {
            super.afterTextChanged(s);
        }
    };

    private BubbleSeekBar.OnProgressChangedListener mProgressListener = new BubbleSeekBar.OnProgressChangedListener() {
        @Override
        public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
            if (fromUser) {
                LogUtils.e("----onProgressChanged progress:" + progress +",progressFloat:"+progressFloat);
                getPresenter().calculateFeeAndTime(progressFloat);
                getPresenter().updateSendTransactionButtonStatus();
                if (etWalletAmount.isFocused()) {
                    etWalletAmount.clearFocus();
                }
                if (etWalletAddress.isFocused()) {
                    etWalletAddress.clearFocus();
                }
                String amount = etWalletAmount.getText().toString().trim();
                if (!TextUtils.isEmpty(amount)) {
                    getPresenter().checkTransferAmount(amount);
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

    public static void actionStart(Context context) {
        context.startActivity(new Intent(context, SendTransactionActivity.class));
    }



}
