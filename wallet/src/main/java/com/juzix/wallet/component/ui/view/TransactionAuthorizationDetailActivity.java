package com.juzix.wallet.component.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.app.CustomObserver;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.component.ui.dialog.AuthorizationSignatureDialogFragment;
import com.juzix.wallet.component.ui.dialog.InputWalletPasswordDialogFragment;
import com.juzix.wallet.component.widget.ShadowButton;
import com.juzix.wallet.db.sqlite.AddressDao;
import com.juzix.wallet.db.sqlite.WalletDao;
import com.juzix.wallet.engine.WalletManager;
import com.juzix.wallet.entity.TransactionAuthorizationData;
import com.juzix.wallet.entity.TransactionAuthorizationDetail;
import com.juzix.wallet.entity.TransactionSignatureData;
import com.juzix.wallet.entity.Wallet;
import com.juzix.wallet.utils.AddressFormatUtil;
import com.juzix.wallet.utils.AmountUtil;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.RxUtils;
import com.juzix.wallet.utils.StringUtil;
import com.juzix.wallet.utils.ToastUtil;

import org.web3j.crypto.Credentials;
import org.web3j.platon.FunctionType;

import java.math.BigDecimal;
import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;

public class TransactionAuthorizationDetailActivity extends BaseActivity {

    @BindView(R.id.tv_amount)
    TextView tvAmount;
    @BindView(R.id.tv_txn_info)
    TextView tvTxnInfo;
    @BindView(R.id.tv_sender)
    TextView tvSender;
    @BindView(R.id.tv_recipient)
    TextView tvRecipient;
    @BindView(R.id.tv_fee)
    TextView tvFee;
    @BindView(R.id.sbtn_next)
    ShadowButton sbtnNext;
    @BindView(R.id.tv_sender_title)
    TextView tvSenderTitle;
    @BindView(R.id.tv_recipient_title)
    TextView tvRecipientTitle;

    Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_authorization_detail);
        unbinder = ButterKnife.bind(this);
        initViews();
    }

    @Override
    protected boolean immersiveBarViewEnabled() {
        return true;
    }

    private void initViews() {

        TransactionAuthorizationData transactionAuthorizationData = getIntent().getParcelableExtra(Constants.Extra.EXTRA_TRANSACTION_AUTHORIZATION_DATA);

        if (transactionAuthorizationData == null || transactionAuthorizationData.getTransactionAuthorizationDetail() == null) {
            return;
        }

        TransactionAuthorizationDetail transactionAuthorizationDetail = transactionAuthorizationData.getTransactionAuthorizationDetail();

        tvSenderTitle.setText(getSenderTitleRes(transactionAuthorizationDetail.getFunctionType()));
        tvRecipientTitle.setText(getRecipientInfoRes(transactionAuthorizationDetail.getFunctionType()));
        tvTxnInfo.setText(string(getTxnInfoRes(transactionAuthorizationDetail.getFunctionType())));
        tvAmount.setText(StringUtil.formatBalance(AmountUtil.convertVonToLat(transactionAuthorizationDetail.getAmount())));
        tvSender.setText(getFormatName(transactionAuthorizationDetail.getSender(), null));
        tvRecipient.setText(getReceiverName(transactionAuthorizationDetail));
        tvFee.setText(string(R.string.amount_with_unit, NumberParserUtils.getPrettyBalance(AmountUtil.convertVonToLat(transactionAuthorizationDetail.getFee()))));

        RxView
                .clicks(sbtnNext)
                .compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        checkAndShowAuthorizationSignatureDialog(transactionAuthorizationData);
                    }
                });
    }

    @SuppressLint("CheckResult")
    private void checkAndShowAuthorizationSignatureDialog(TransactionAuthorizationData transactionAuthorizationData) {
        Single
                .fromCallable(new Callable<Wallet>() {
                    @Override
                    public Wallet call() throws Exception {
                        return WalletManager.getInstance().getWalletByAddress(transactionAuthorizationData.getTransactionAuthorizationDetail().getSender());
                    }
                })
                .compose(RxUtils.getSingleSchedulerTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new Consumer<Wallet>() {
                    @Override
                    public void accept(Wallet wallet) throws Exception {
                        if (TextUtils.isEmpty(wallet.getKey())) {
                            showLongToast(R.string.msg_keystore_nor_exist);
                        } else {
                            InputWalletPasswordDialogFragment.newInstance(wallet).setOnWalletPasswordCorrectListener(new InputWalletPasswordDialogFragment.OnWalletPasswordCorrectListener() {
                                @Override
                                public void onWalletPasswordCorrect(Credentials credentials) {
                                    AuthorizationSignatureDialogFragment.newInstance(transactionAuthorizationData.toTransactionSignatureData(credentials).toJSONString()).show(getSupportFragmentManager(), "showAuthorizationSignatureDialog");
                                }
                            }).show(currentActivity().getSupportFragmentManager(), "inputPassword");
                        }
                    }

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        showLongToast(R.string.msg_wallet_mismatch);
                    }
                });
    }

    private int getTxnInfoRes(int functionType) {
        switch (functionType) {
            case FunctionType.DELEGATE_FUNC_TYPE:
                return R.string.delegate;
            case FunctionType.WITHDREW_DELEGATE_FUNC_TYPE:
                return R.string.undelegate;
            case FunctionType.WITHDRAW_DELEGATE_REWARD_FUNC_TYPE:
                return R.string.msg_claim_rewards;
            default:
                return R.string.transfer;
        }
    }

    private @StringRes
    int getRecipientInfoRes(int functionType) {
        switch (functionType) {
            case FunctionType.DELEGATE_FUNC_TYPE:
                return R.string.msg_delegated_to;
            case FunctionType.WITHDREW_DELEGATE_FUNC_TYPE:
                return R.string.msg_undelegated_from;
            default:
                return R.string.msg_recipient;
        }
    }

    private @StringRes
    int getSenderTitleRes(int functionType) {
        if (functionType == FunctionType.DELEGATE_FUNC_TYPE || functionType == FunctionType.WITHDREW_DELEGATE_FUNC_TYPE) {
            return R.string.msg_operator_address;
        }

        return R.string.msg_sender;
    }

    private String getFormatName(String prefixAddress, String nodeName) {
        if (!TextUtils.isEmpty(nodeName)) {
            return String.format("%s(%s)", nodeName, AddressFormatUtil.formatTransactionAddress(prefixAddress));
        }
        String walletName = WalletDao.getWalletNameByAddress(prefixAddress);
        if (!TextUtils.isEmpty(walletName)) {
            return String.format("%s(%s)", walletName, AddressFormatUtil.formatTransactionAddress(prefixAddress));
        }
        String remark = AddressDao.getAddressNameByAddress(prefixAddress);
        if (!TextUtils.isEmpty(remark)) {
            return String.format("%s(%s)", remark, AddressFormatUtil.formatTransactionAddress(prefixAddress));
        }
        return AddressFormatUtil.formatTransactionAddress(prefixAddress);
    }

    private String getTransferFormatName(String prefixAddress) {
        String walletName = WalletDao.getWalletNameByAddress(prefixAddress);
        if (!TextUtils.isEmpty(walletName)) {
            return String.format("%s(%s)", walletName, prefixAddress);
        }
        String remark = AddressDao.getAddressNameByAddress(prefixAddress);
        if (!TextUtils.isEmpty(remark)) {
            return String.format("%s(%s)", remark, prefixAddress);
        }
        return prefixAddress;
    }

    private String getReceiverName(TransactionAuthorizationDetail transactionAuthorizationDetail) {
        if (FunctionType.TRANSFER == transactionAuthorizationDetail.getFunctionType()) {
            return getTransferFormatName(transactionAuthorizationDetail.getReceiver());
        } else {
            return String.format("%s(%s)", transactionAuthorizationDetail.getNodeName(), transactionAuthorizationDetail.getNodeId());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public static void actionStart(Context context, TransactionAuthorizationData transactionAuthorizationData) {
        Intent intent = new Intent(context, TransactionAuthorizationDetailActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_TRANSACTION_AUTHORIZATION_DATA, transactionAuthorizationData);
        context.startActivity(intent);
    }
}
