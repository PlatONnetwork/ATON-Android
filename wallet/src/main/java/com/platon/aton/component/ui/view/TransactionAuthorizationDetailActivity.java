package com.platon.aton.component.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.constraint.Group;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.platon.aton.R;
import com.platon.aton.app.CustomObserver;
import com.platon.aton.component.ui.dialog.AuthorizationSignatureDialogFragment;
import com.platon.aton.component.ui.dialog.InputWalletPasswordDialogFragment;
import com.platon.aton.component.widget.ShadowButton;
import com.platon.aton.db.sqlite.AddressDao;
import com.platon.aton.db.sqlite.WalletDao;
import com.platon.aton.engine.WalletManager;
import com.platon.aton.entity.InputWalletPasswordFromType;
import com.platon.aton.entity.TransactionAuthorizationData;
import com.platon.aton.entity.TransactionAuthorizationDetail;
import com.platon.aton.entity.Wallet;
import com.platon.aton.utils.AddressFormatUtil;
import com.platon.aton.utils.AmountUtil;
import com.platon.aton.utils.CommonTextUtils;
import com.platon.aton.utils.DensityUtil;
import com.platon.aton.utils.NumberParserUtils;
import com.platon.aton.utils.RxUtils;
import com.platon.framework.app.Constants;
import com.platon.framework.base.BaseActivity;
import com.platon.framework.base.BasePresenter;
import com.platon.framework.base.BaseViewImp;

import org.web3j.crypto.Credentials;
import org.web3j.platon.FunctionType;

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
    @BindView(R.id.tv_memo)
    TextView tvMemo;
    @BindView(R.id.tv_memo_title)
    TextView tvMemoTitle;
    @BindView(R.id.group_memo)
    Group groupMemo;

    Unbinder unbinder;

    @Override
    public int getLayoutId() {
        return R.layout.activity_transaction_authorization_detail;
    }

    @Override
    public BasePresenter createPresenter() {
        return null;
    }

    @Override
    public BaseViewImp createView() {
        return null;
    }

    @Override
    public void init() {
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
        String amountText = string(R.string.amount_with_unit, AmountUtil.formatAmountText(transactionAuthorizationDetail.getAmount(), 12));
        tvAmount.setText(amountText);
        CommonTextUtils.richText(tvAmount, amountText, "LAT", new AbsoluteSizeSpan(DensityUtil.dp2px(this, 24)) {
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setFakeBoldText(false);
            }
        });
        setTvSenderFormatName(transactionAuthorizationDetail.getSender(), null,tvSender);
        tvRecipient.setText(getReceiverName(transactionAuthorizationDetail));
        tvFee.setText(string(R.string.amount_with_unit, NumberParserUtils.getPrettyBalance(AmountUtil.convertVonToLat(transactionAuthorizationDetail.getFee()))));

        groupMemo.setVisibility(transactionAuthorizationDetail.getFunctionType() == FunctionType.TRANSFER && !TextUtils.isEmpty(transactionAuthorizationDetail.getRemark()) ? View.VISIBLE : View.GONE);
        tvMemoTitle.setText(string(R.string.msg_transaction_memo, ""));
        tvMemo.setText(transactionAuthorizationDetail.getRemark());

        RxView
                .clicks(sbtnNext)
                .compose(bindToLifecycle())
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
                .compose(bindToLifecycle())
                .subscribe(new Consumer<Wallet>() {
                    @Override
                    public void accept(Wallet wallet) throws Exception {
                        if (TextUtils.isEmpty(wallet.getKey())) {
                            showLongToast(R.string.msg_keystore_nor_exist);
                        } else {
                            InputWalletPasswordDialogFragment.newInstance(wallet, InputWalletPasswordFromType.TRANSACTION).setOnWalletPasswordCorrectListener(new InputWalletPasswordDialogFragment.OnWalletPasswordCorrectListener() {
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
                return R.string.msg_transfer_info;
        }
    }

    private @StringRes
    int getRecipientInfoRes(int functionType) {
        switch (functionType) {
            case FunctionType.DELEGATE_FUNC_TYPE:
                return R.string.msg_delegated_to;
            case FunctionType.WITHDREW_DELEGATE_FUNC_TYPE:
                return R.string.msg_undelegated_from;
            case FunctionType.WITHDRAW_DELEGATE_REWARD_FUNC_TYPE:
                return R.string.reward_amount;
            default:
                return R.string.msg_recipient;
        }
    }

    private @StringRes
    int getSenderTitleRes(int functionType) {
        if (functionType == FunctionType.DELEGATE_FUNC_TYPE || functionType == FunctionType.WITHDREW_DELEGATE_FUNC_TYPE) {
            return R.string.msg_operator_address;
        } else if (functionType == FunctionType.WITHDRAW_DELEGATE_REWARD_FUNC_TYPE) {
            return R.string.claim_wallet;
        }

        return R.string.msg_sender;
    }


    private void setTvSenderFormatName(String prefixAddress, String nodeName,TextView tvSenderView) {

        if (!TextUtils.isEmpty(nodeName)) {
            tvSenderView.setText(String.format("%s(%s)", nodeName, AddressFormatUtil.formatTransactionAddress(prefixAddress)));
            return;
        }
        WalletManager.getInstance().getWalletNameFromAddress(prefixAddress)
                .compose(RxUtils.getSingleSchedulerTransformer())
                .compose(bindToLifecycle())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String walletName) throws Exception {
                        String formatName = "";
                        if (!TextUtils.isEmpty(walletName)) {
                            formatName = walletName;
                        }else{
                            formatName = AddressFormatUtil.formatTransactionAddress(prefixAddress);
                        }
                        tvSenderView.setText(formatName);
                    }
                });
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
        } else if (FunctionType.WITHDRAW_DELEGATE_REWARD_FUNC_TYPE == transactionAuthorizationDetail.getFunctionType()) {
            return AmountUtil.formatAmountText(transactionAuthorizationDetail.getAmount(), 12);
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
