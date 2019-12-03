package com.juzix.wallet.component.ui.dialog;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.juzhen.framework.util.LogUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.app.CustomObserver;
import com.juzix.wallet.component.ui.view.ScanQRCodeActivity;
import com.juzix.wallet.component.widget.ShadowButton;
import com.juzix.wallet.db.sqlite.AddressDao;
import com.juzix.wallet.db.sqlite.TransactionDao;
import com.juzix.wallet.db.sqlite.WalletDao;
import com.juzix.wallet.engine.NodeManager;
import com.juzix.wallet.engine.TransactionManager;
import com.juzix.wallet.entity.Transaction;
import com.juzix.wallet.entity.TransactionAuthorizationBaseData;
import com.juzix.wallet.entity.TransactionAuthorizationData;
import com.juzix.wallet.entity.TransactionAuthorizationDetail;
import com.juzix.wallet.entity.TransactionSignatureData;
import com.juzix.wallet.entity.TransactionStatus;
import com.juzix.wallet.entity.TransactionType;
import com.juzix.wallet.event.EventPublisher;
import com.juzix.wallet.utils.AddressFormatUtil;
import com.juzix.wallet.utils.DensityUtil;
import com.juzix.wallet.utils.GZipUtil;
import com.juzix.wallet.utils.JSONUtil;
import com.juzix.wallet.utils.RxUtils;
import com.juzix.wallet.utils.ToastUtil;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionDecoder;
import org.web3j.platon.FunctionType;
import org.web3j.protocol.core.methods.response.PlatonSendTransaction;

import java.util.List;
import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

public class TransactionSignatureDialogFragment extends BaseDialogFragment {

    public final static String TAG = TransactionSignatureDialogFragment.class.getSimpleName();
    /**
     * 有效签名
     */
    private final static int CODE_VALID_SIGNATURE = 0;
    /**
     * 钱包不匹配
     */
    private final static int CODE_WALLET_MISMATCH = 1;
    /**
     * 无效签名
     */
    private final static int CODE_INVALID_SIGNATURE = 2;

    @BindView(R.id.iv_scan)
    ImageView ivScan;
    @BindView(R.id.tv_transaction_signature)
    TextView tvTransactionSignature;
    @BindView(R.id.sbtn_send_transaction)
    ShadowButton sbtnSendTransaction;

    private Unbinder unbinder;
    private TransactionSignatureData transactionSignatureData;
    private OnSendTransactionSucceedListener sendTransactionSucceedListener;

    public static TransactionSignatureDialogFragment newInstance(TransactionSignatureData transactionSignatureData) {
        TransactionSignatureDialogFragment dialogFragment = new TransactionSignatureDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.Extra.EXTRA_TRANSACTION_SIGNATURE_DATA, transactionSignatureData);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    public static TransactionSignatureDialogFragment newInstance(TransactionAuthorizationData transactionAuthorizationData) {
        TransactionSignatureDialogFragment dialogFragment = new TransactionSignatureDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.Extra.EXTRA_TRANSACTION_AUTHORIZATION_DATA, transactionAuthorizationData);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    public TransactionSignatureDialogFragment setOnSendTransactionSucceedListener(OnSendTransactionSucceedListener sendTransactionSucceedListener) {
        this.sendTransactionSucceedListener = sendTransactionSucceedListener;
        return this;
    }

    @Override
    protected Dialog onCreateDialog(Dialog baseDialog) {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_transaction_signature, null, false);
        baseDialog.setContentView(contentView);
        setFullWidthEnable(true);
        setHorizontalMargin(DensityUtil.dp2px(getContext(), 14f));
        setyOffset(DensityUtil.dp2px(getContext(), 16f));
        setAnimation(R.style.Animation_slide_in_bottom);
        unbinder = ButterKnife.bind(this, contentView);
        initViews();
        return baseDialog;
    }

    private void initViews() {

        transactionSignatureData = getArguments().getParcelable(Constants.Extra.EXTRA_TRANSACTION_SIGNATURE_DATA);

        TransactionAuthorizationData transactionAuthorizationData = getArguments().getParcelable(Constants.Extra.EXTRA_TRANSACTION_AUTHORIZATION_DATA);

        tvTransactionSignature.setMovementMethod(ScrollingMovementMethod.getInstance());

        tvTransactionSignature.setText(getSignedMessage(transactionSignatureData));

        RxTextView
                .textChanges(tvTransactionSignature)
                .compose(bindToLifecycle())
                .subscribe(new CustomObserver<CharSequence>() {
                    @Override
                    public void accept(CharSequence charSequence) {
                        sbtnSendTransaction.setEnabled(!TextUtils.isEmpty(charSequence));
                    }
                });

        RxView
                .clicks(ivScan)
                .compose(RxUtils.getClickTransformer())
                .compose(bindToLifecycle())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        new RxPermissions(getActivity())
                                .requestEach(Manifest.permission.CAMERA)
                                .subscribe(new CustomObserver<Permission>() {
                                    @Override
                                    public void accept(Permission permission) {
                                        if (permission.granted && Manifest.permission.CAMERA.equals(permission.name)) {
                                            ScanQRCodeActivity.startActivityForResult(getActivity(), Constants.RequestCode.REQUEST_CODE_TRANSACTION_SIGNATURE);
                                        }
                                    }
                                });
                    }
                });

        RxView
                .clicks(sbtnSendTransaction)
                .compose(RxUtils.getClickTransformer())
                .compose(bindToLifecycle())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {

                        int result = checkSignature(transactionAuthorizationData);

                        if (result == CODE_INVALID_SIGNATURE) {
                            ToastUtil.showLongToast(getContext(), R.string.msg_invalid_signature);
                        } else if (result == CODE_WALLET_MISMATCH) {
                            ToastUtil.showLongToast(getContext(), R.string.msg_wallet_mismatch);
                        } else {
                            sendTransaction(transactionSignatureData, transactionAuthorizationData.getTransactionAuthorizationDetail());
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String signature = data.getStringExtra(Constants.Extra.EXTRA_SCAN_QRCODE_DATA);
        transactionSignatureData = JSONUtil.parseObject(GZipUtil.unCompress(signature), TransactionSignatureData.class);
        if (transactionSignatureData != null && transactionSignatureData.getSignedDatas() != null && !transactionSignatureData.getSignedDatas().isEmpty() && NodeManager.getInstance().getChainId().equals(transactionSignatureData.getChainId())) {
            tvTransactionSignature.setText(getSignedMessage(transactionSignatureData));
        } else {
            ToastUtil.showLongToast(getContext(), R.string.msg_invalid_signature);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    private void sendTransaction(TransactionSignatureData transactionSignatureData, TransactionAuthorizationDetail transactionAuthorizationDetail) {

        Flowable
                .fromIterable(transactionSignatureData.getSignedDatas())
                .map(new Function<String, PlatonSendTransaction>() {
                    @Override
                    public PlatonSendTransaction apply(String signedMessage) throws Exception {
                        return TransactionManager.getInstance().sendTransactionReturnPlatonSendTransaction(signedMessage);
                    }
                })
                .takeLast(1)
                .compose(RxUtils.getFlowableSchedulerTransformer())
                .compose(bindToLifecycle())
                .subscribe(new Consumer<PlatonSendTransaction>() {
                    @Override
                    public void accept(PlatonSendTransaction platonSendTransaction) throws Exception {
                        if (transactionSignatureData.getFunctionType() == FunctionType.TRANSFER) {
                            ToastUtil.showLongToast(getActivity(), getContext().getString(R.string.transfer_succeed));
                        } else if (transactionSignatureData.getFunctionType() == FunctionType.DELEGATE_FUNC_TYPE) {
                            ToastUtil.showLongToast(getActivity(), getContext().getString(R.string.delegate_success));
                        } else if (transactionSignatureData.getFunctionType() == FunctionType.WITHDREW_DELEGATE_FUNC_TYPE) {
                            ToastUtil.showLongToast(getActivity(), getContext().getString(R.string.withdraw_success));
                        }
                        afterSendTransactionSucceed(platonSendTransaction, transactionAuthorizationDetail);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (transactionSignatureData.getFunctionType() == FunctionType.TRANSFER) {
                            ToastUtil.showLongToast(getActivity(), getContext().getString(R.string.transfer_failed));
                        } else if (transactionSignatureData.getFunctionType() == FunctionType.DELEGATE_FUNC_TYPE) {
                            ToastUtil.showLongToast(getActivity(), getContext().getString(R.string.delegate_failed));
                        } else if (transactionSignatureData.getFunctionType() == FunctionType.WITHDREW_DELEGATE_FUNC_TYPE) {
                            ToastUtil.showLongToast(getActivity(), getContext().getString(R.string.withdraw_failed));
                        }
                    }
                });
    }

    private int checkSignature(TransactionAuthorizationData transactionAuthorizationData) {

        if (transactionAuthorizationData == null) {
            return CODE_INVALID_SIGNATURE;
        }

        TransactionAuthorizationBaseData transactionAuthorizationBaseData = transactionAuthorizationData.getBaseDataList().get(0);

        boolean fromNotEquals = !TextUtils.equals(transactionAuthorizationBaseData.getFrom(), transactionSignatureData.getFrom());

        if (fromNotEquals) {
            return CODE_WALLET_MISMATCH;
        }

        boolean chainIdNotEquals = !TextUtils.equals(transactionAuthorizationBaseData.getChainId(), transactionSignatureData.getChainId());
        boolean timestampNotEquals = transactionAuthorizationData.getTimestamp() != transactionSignatureData.getTimestamp();
        boolean functionTypeNotEquals = transactionAuthorizationBaseData.getFunctionType() != transactionSignatureData.getFunctionType();

        if (chainIdNotEquals || timestampNotEquals || functionTypeNotEquals) {
            return CODE_INVALID_SIGNATURE;
        }

        return CODE_VALID_SIGNATURE;
    }

    private void afterSendTransactionSucceed(PlatonSendTransaction platonSendTransaction, TransactionAuthorizationDetail transactionAuthorizationDetail) {
        dismiss();
        List<String> signedDatas = transactionSignatureData.getSignedDatas();
        Transaction transaction = buildTransaction(transactionAuthorizationDetail, platonSendTransaction.getTransactionHash(), signedDatas.get(signedDatas.size() - 1));
        if (transactionSignatureData.getFunctionType() == FunctionType.TRANSFER) {
            //跳转至交易记录页签
            transaction.setTxType(String.valueOf(TransactionType.TRANSFER.getTxTypeValue()));
            afterTransferSucceed(transaction);
        } else if (transactionSignatureData.getFunctionType() == FunctionType.DELEGATE_FUNC_TYPE) {
            transaction.setTxType(String.valueOf(TransactionType.DELEGATE.getTxTypeValue()));
            TransactionManager.getInstance().getTransactionByLoop(transaction);
            if (sendTransactionSucceedListener != null) {
                sendTransactionSucceedListener.onSendTransactionSucceed(transaction);
            }
        } else if (transactionSignatureData.getFunctionType() == FunctionType.WITHDREW_DELEGATE_FUNC_TYPE) {
            transaction.setTxType(String.valueOf(TransactionType.UNDELEGATE.getTxTypeValue()));
            TransactionManager.getInstance().getTransactionByLoop(transaction);
            if (sendTransactionSucceedListener != null) {
                sendTransactionSucceedListener.onSendTransactionSucceed(transaction);
            }
        }
    }

    private void afterTransferSucceed(Transaction transaction) {

        Single
                .fromCallable(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return TransactionDao.insertTransaction(transaction.toTransactionEntity());
                    }
                })
                .filter(new Predicate<Boolean>() {
                    @Override
                    public boolean test(Boolean aBoolean) throws Exception {
                        return aBoolean;
                    }
                })
                .toSingle()
                .doOnSuccess(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        EventPublisher.getInstance().sendUpdateTransactionEvent(transaction);
                        TransactionManager.getInstance().getTransactionByLoop(transaction);
                    }
                })
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (sendTransactionSucceedListener != null) {
                            sendTransactionSucceedListener.onSendTransactionSucceed(transaction);
                        }
                    }
                });

    }


    private Transaction buildTransaction(TransactionAuthorizationDetail transactionAuthorizationDetail, String hash, String signedMessage) {
        RawTransaction rawTransaction = TransactionDecoder.decode(signedMessage);
        return new Transaction.Builder()
                .hash(hash)
                .from(transactionSignatureData.getFrom())
                .to(rawTransaction.getTo())
                .senderWalletName(getSenderName(transactionSignatureData.getFrom()))
                .value(transactionAuthorizationDetail.getAmount())
                .chainId(transactionSignatureData.getChainId())
                .timestamp(System.currentTimeMillis())
                .txReceiptStatus(TransactionStatus.PENDING.ordinal())
                .actualTxCost(transactionAuthorizationDetail.getFee())
                .unDelegation(transactionAuthorizationDetail.getAmount())
                .nodeName(transactionAuthorizationDetail.getNodeName())
                .nodeId(transactionAuthorizationDetail.getNodeId())
                .build();
    }

    private String getSenderName(String prefixAddress) {
        String walletName = WalletDao.getWalletNameByAddress(prefixAddress);
        if (!TextUtils.isEmpty(walletName)) {
            return walletName;
        }
        String remark = AddressDao.getAddressNameByAddress(prefixAddress);
        if (!TextUtils.isEmpty(remark)) {
            return remark;
        }
        return AddressFormatUtil.formatTransactionAddress(prefixAddress);
    }

    private String getSignedMessage(TransactionSignatureData transactionSignatureData) {
        if (isEmpty(transactionSignatureData)) {
            return "";
        }

        return TextUtils.join(",", transactionSignatureData.getSignedDatas());

    }

    private boolean isEmpty(TransactionSignatureData transactionSignatureData) {
        return transactionSignatureData == null || transactionSignatureData.getSignedDatas() == null || transactionSignatureData.getSignedDatas().isEmpty();
    }

    public interface OnSendTransactionSucceedListener {

        void onSendTransactionSucceed(Transaction transaction);
    }
}
