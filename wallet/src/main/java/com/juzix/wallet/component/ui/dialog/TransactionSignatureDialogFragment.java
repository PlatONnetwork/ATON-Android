package com.juzix.wallet.component.ui.dialog;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
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
import com.juzix.wallet.component.ui.view.AssetsFragment;
import com.juzix.wallet.component.ui.view.MainActivity;
import com.juzix.wallet.component.ui.view.ScanQRCodeActivity;
import com.juzix.wallet.component.widget.ShadowButton;
import com.juzix.wallet.db.sqlite.AddressDao;
import com.juzix.wallet.db.sqlite.TransactionDao;
import com.juzix.wallet.db.sqlite.WalletDao;
import com.juzix.wallet.engine.TransactionManager;
import com.juzix.wallet.entity.Transaction;
import com.juzix.wallet.entity.TransactionSignatureData;
import com.juzix.wallet.entity.TransactionStatus;
import com.juzix.wallet.entity.TransactionType;
import com.juzix.wallet.event.EventPublisher;
import com.juzix.wallet.utils.AddressFormatUtil;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.DensityUtil;
import com.juzix.wallet.utils.JSONUtil;
import com.juzix.wallet.utils.RxUtils;
import com.juzix.wallet.utils.ToastUtil;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionDecoder;
import org.web3j.platon.FunctionType;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

public class TransactionSignatureDialogFragment extends BaseDialogFragment {

    public final static String TAG = TransactionSignatureDialogFragment.class.getSimpleName();

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

    public static TransactionSignatureDialogFragment newInstance(long timeStamp) {
        TransactionSignatureDialogFragment dialogFragment = new TransactionSignatureDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(Constants.Bundle.BUNDLE_TIME_STAMP, timeStamp);
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

        long timeStamp = getArguments().getLong(Constants.Bundle.BUNDLE_TIME_STAMP);

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
                        if (transactionSignatureData != null && transactionSignatureData.getTimeStamp() == timeStamp) {

                            Flowable
                                    .fromIterable(transactionSignatureData.getSignedDatas())
                                    .map(new Function<String, String>() {
                                        @Override
                                        public String apply(String signedMessage) throws Exception {
                                            return TransactionManager.getInstance().sendTransaction(signedMessage);
                                        }
                                    })
                                    .takeLast(1)
                                    .compose(RxUtils.getFlowableSchedulerTransformer())
                                    .compose(bindToLifecycle())
                                    .subscribe(new Consumer<String>() {
                                        @Override
                                        public void accept(String hash) throws Exception {
                                            if (transactionSignatureData.getFunctionType() == FunctionType.TRANSFER) {
                                                ToastUtil.showLongToast(getActivity(), getContext().getString(R.string.transfer_succeed));
                                            } else if (transactionSignatureData.getFunctionType() == FunctionType.DELEGATE_FUNC_TYPE) {
                                                ToastUtil.showLongToast(getActivity(), getContext().getString(R.string.delegate_success));
                                            }else if(transactionSignatureData.getFunctionType() == FunctionType.WITHDREW_DELEGATE_FUNC_TYPE){
                                                ToastUtil.showLongToast(getActivity(), getContext().getString(R.string.withdraw_success));
                                            }
                                            afterSendTransactionSucceed(hash);
                                        }
                                    }, new Consumer<Throwable>() {
                                        @Override
                                        public void accept(Throwable throwable) throws Exception {
                                            if (transactionSignatureData.getFunctionType() == FunctionType.TRANSFER) {
                                                ToastUtil.showLongToast(getActivity(), getContext().getString(R.string.transfer_failed));
                                            } else if (transactionSignatureData.getFunctionType() == FunctionType.DELEGATE_FUNC_TYPE) {
                                                ToastUtil.showLongToast(getActivity(), getContext().getString(R.string.delegate_failed));
                                            }else if(transactionSignatureData.getFunctionType() == FunctionType.WITHDREW_DELEGATE_FUNC_TYPE){
                                                ToastUtil.showLongToast(getActivity(), getContext().getString(R.string.withdraw_failed));
                                            }
                                        }
                                    });
                        } else {
                            ToastUtil.showLongToast(getActivity(), R.string.msg_invalid_signature);
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String signature = data.getStringExtra(Constants.Extra.EXTRA_SCAN_QRCODE_DATA);
        transactionSignatureData = JSONUtil.parseObject(signature, TransactionSignatureData.class);
        tvTransactionSignature.setText(getSignedMessage(transactionSignatureData));

        RawTransaction rawTransaction = TransactionDecoder.decode(transactionSignatureData.getSignedDatas().get(0));

        LogUtils.e(rawTransaction.toString());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    private void afterSendTransactionSucceed(String hash) {
        dismiss();
        if (transactionSignatureData.getFunctionType() == FunctionType.TRANSFER) {
            //跳转至交易记录页签
            List<String> signedDatas = transactionSignatureData.getSignedDatas();
            afterTransferSucceed(hash, signedDatas.get(signedDatas.size() - 1));
        } else {
            //进入交易详情
            if (sendTransactionSucceedListener != null) {
                sendTransactionSucceedListener.onSendTransactionSucceed(hash);
            }
        }
    }

    private void afterTransferSucceed(String hash, String signedMessage) {

        Single
                .fromCallable(new Callable<Transaction>() {
                    @Override
                    public Transaction call() throws Exception {
                        return buildTransaction(hash, signedMessage);
                    }
                })
                .filter(new Predicate<Transaction>() {
                    @Override
                    public boolean test(Transaction transaction) throws Exception {
                        return TransactionDao.insertTransaction(transaction.toTransactionEntity());
                    }
                })
                .toSingle()
                .doOnSuccess(new Consumer<Transaction>() {
                    @Override
                    public void accept(Transaction transaction) throws Exception {
                        EventPublisher.getInstance().sendUpdateTransactionEvent(transaction);
                        TransactionManager.getInstance().getTransactionByLoop(transaction);
                    }
                })
                .subscribe(new Consumer<Transaction>() {
                    @Override
                    public void accept(Transaction transaction) throws Exception {
                        if (sendTransactionSucceedListener != null) {
                            sendTransactionSucceedListener.onSendTransactionSucceed(hash);
                        }
                    }
                });

    }


    private Transaction buildTransaction(String hash, String signedMessage) {
        RawTransaction rawTransaction = TransactionDecoder.decode(signedMessage);
        return new Transaction.Builder()
                .hash(hash)
                .from(transactionSignatureData.getFrom())
                .to(rawTransaction.getTo())
                .senderWalletName(getSenderName(transactionSignatureData.getFrom()))
                .value(rawTransaction.getValue().toString(10))
                .chainId(transactionSignatureData.getChainId())
                .txType(String.valueOf(TransactionType.TRANSFER.getTxTypeValue()))
                .timestamp(System.currentTimeMillis())
                .txReceiptStatus(TransactionStatus.PENDING.ordinal())
                .actualTxCost(rawTransaction.getGasLimit().multiply(rawTransaction.getGasLimit()).toString(10))
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

        void onSendTransactionSucceed(String hash);
    }
}
