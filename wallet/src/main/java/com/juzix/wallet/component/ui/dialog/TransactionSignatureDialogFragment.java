package com.juzix.wallet.component.ui.dialog;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.app.CustomObserver;
import com.juzix.wallet.app.LoadingTransformer;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.component.ui.view.ScanQRCodeActivity;
import com.juzix.wallet.component.widget.ShadowButton;
import com.juzix.wallet.db.sqlite.AddressDao;
import com.juzix.wallet.db.sqlite.TransactionDao;
import com.juzix.wallet.db.sqlite.WalletDao;
import com.juzix.wallet.engine.NodeManager;
import com.juzix.wallet.engine.TransactionManager;
import com.juzix.wallet.engine.WalletManager;
import com.juzix.wallet.entity.Transaction;
import com.juzix.wallet.entity.TransactionAuthorizationBaseData;
import com.juzix.wallet.entity.TransactionAuthorizationData;
import com.juzix.wallet.entity.TransactionSignatureData;
import com.juzix.wallet.entity.TransactionStatus;
import com.juzix.wallet.entity.TransactionType;
import com.juzix.wallet.entity.Wallet;
import com.juzix.wallet.event.EventPublisher;
import com.juzix.wallet.utils.AddressFormatUtil;
import com.juzix.wallet.utils.BigIntegerUtil;
import com.juzix.wallet.utils.DensityUtil;
import com.juzix.wallet.utils.GZipUtil;
import com.juzix.wallet.utils.JSONUtil;
import com.juzix.wallet.utils.RxUtils;
import com.juzix.wallet.utils.ToastUtil;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.reactivestreams.Publisher;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionDecoder;
import org.web3j.platon.FunctionType;
import org.web3j.protocol.core.methods.response.PlatonSendTransaction;
import org.web3j.rlp.RlpDecoder;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpString;
import org.web3j.rlp.RlpType;
import org.web3j.utils.Numeric;

import java.util.List;
import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
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
                            sbtnSendTransaction.setEnabled(false);
                            sendTransaction(transactionSignatureData, transactionAuthorizationData);
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

    @SuppressLint("CheckResult")
    private void sendTransaction(TransactionSignatureData transactionSignatureData, TransactionAuthorizationData transactionAuthorizationData) {

        getSendTransactionResult()
                .filter(new Predicate<Pair<Boolean, List<PlatonSendTransaction>>>() {
                    @Override
                    public boolean test(Pair<Boolean, List<PlatonSendTransaction>> booleanListPair) throws Exception {
                        return booleanListPair.first;
                    }
                })
                .switchIfEmpty(new SingleSource<Pair<Boolean, List<PlatonSendTransaction>>>() {
                    @Override
                    public void subscribe(SingleObserver<? super Pair<Boolean, List<PlatonSendTransaction>>> observer) {
                        observer.onError(new Throwable());
                    }
                })
                .toFlowable()
                .flatMap(new Function<Pair<Boolean, List<PlatonSendTransaction>>, Publisher<PlatonSendTransaction>>() {
                    @Override
                    public Publisher<PlatonSendTransaction> apply(Pair<Boolean, List<PlatonSendTransaction>> booleanListPair) throws Exception {
                        return Flowable.fromIterable(booleanListPair.second);
                    }
                })
                .takeLast(1)
                .compose(RxUtils.getFlowableSchedulerTransformer())
                .compose(bindToLifecycle())
                .compose(LoadingTransformer.bindToFlowableLifecycle((BaseActivity) getActivity()))
                .subscribe(new Consumer<PlatonSendTransaction>() {
                    @Override
                    public void accept(PlatonSendTransaction platonSendTransaction) throws Exception {
                        if (transactionSignatureData.getFunctionType() == FunctionType.TRANSFER) {
                            ToastUtil.showLongToast(getActivity(), getContext().getString(R.string.transfer_succeed));
                        } else if (transactionSignatureData.getFunctionType() == FunctionType.DELEGATE_FUNC_TYPE) {
                            ToastUtil.showLongToast(getActivity(), getContext().getString(R.string.delegate_success));
                        } else if (transactionSignatureData.getFunctionType() == FunctionType.WITHDREW_DELEGATE_FUNC_TYPE) {
                            ToastUtil.showLongToast(getActivity(), getContext().getString(R.string.withdraw_success));
                        } else if (transactionSignatureData.getFunctionType() == FunctionType.WITHDRAW_DELEGATE_REWARD_FUNC_TYPE) {
                            ToastUtil.showLongToast(getActivity(), getContext().getString(R.string.withdraw_delegate_reward_success));
                        }
                        afterSendTransactionSucceed(platonSendTransaction, transactionAuthorizationData);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        sbtnSendTransaction.setEnabled(true);

                        if (transactionSignatureData.getFunctionType() == FunctionType.TRANSFER) {
                            ToastUtil.showLongToast(getActivity(), getContext().getString(R.string.transfer_failed));
                        } else if (transactionSignatureData.getFunctionType() == FunctionType.DELEGATE_FUNC_TYPE) {
                            ToastUtil.showLongToast(getActivity(), getContext().getString(R.string.delegate_failed));
                        } else if (transactionSignatureData.getFunctionType() == FunctionType.WITHDREW_DELEGATE_FUNC_TYPE) {
                            ToastUtil.showLongToast(getActivity(), getContext().getString(R.string.withdraw_failed));
                        } else if (transactionSignatureData.getFunctionType() == FunctionType.WITHDRAW_DELEGATE_REWARD_FUNC_TYPE) {
                            ToastUtil.showLongToast(getActivity(), getContext().getString(R.string.withdraw_delegate_reward_failed));
                        }
                    }
                });
    }

    private Single<List<PlatonSendTransaction>> getSendTransactionResultList() {
        return Flowable
                .fromIterable(transactionSignatureData.getSignedDatas())
                .map(new Function<String, PlatonSendTransaction>() {
                    @Override
                    public PlatonSendTransaction apply(String signedMessage) throws Exception {
                        return TransactionManager.getInstance().sendTransactionReturnPlatonSendTransaction(signedMessage);
                    }
                })
                .toList();
    }

    private Single<Pair<Boolean, List<PlatonSendTransaction>>> getSendTransactionResult() {

        return Flowable
                .fromIterable(transactionSignatureData.getSignedDatas())
                .map(new Function<String, PlatonSendTransaction>() {
                    @Override
                    public PlatonSendTransaction apply(String signedMessage) throws Exception {
                        return TransactionManager.getInstance().sendTransactionReturnPlatonSendTransaction(signedMessage);
                    }
                })
                .toList()
                .map(new Function<List<PlatonSendTransaction>, Pair<Boolean, List<PlatonSendTransaction>>>() {
                    @Override
                    public Pair<Boolean, List<PlatonSendTransaction>> apply(List<PlatonSendTransaction> platonSendTransactions) throws Exception {
                        return new Pair<Boolean, List<PlatonSendTransaction>>(getSendTransactionResult(platonSendTransactions), platonSendTransactions);
                    }
                });
    }

    private boolean getSendTransactionResult(List<PlatonSendTransaction> platonSendTransactions) {
        boolean succeed = false;

        for (PlatonSendTransaction platonSendTransaction : platonSendTransactions) {
            if (!TextUtils.isEmpty(platonSendTransaction.getTransactionHash())) {
                succeed = true;
                break;
            }
            continue;
        }

        return succeed;
    }


    private int checkSignature(TransactionAuthorizationData transactionAuthorizationData) {

        //发送地址是否有效
        boolean isFromAddressValid = checkFromAddress(transactionAuthorizationData);

        if (!isFromAddressValid) {
            return CODE_WALLET_MISMATCH;
        }
        //链id是否有效
        boolean isChainIdValid = checkChainId(transactionAuthorizationData);
        //交易类型是否有效
        boolean isFunctionTypeValid = checkFunctionType(transactionAuthorizationData);

        if (!isChainIdValid || !isFunctionTypeValid) {
            return CODE_WALLET_MISMATCH;
        }

        return CODE_VALID_SIGNATURE;
    }

    private boolean checkFromAddress(TransactionAuthorizationData transactionAuthorizationData) {

        if (transactionAuthorizationData == null) {
            Wallet wallet = WalletManager.getInstance().getWalletByAddress(transactionSignatureData.getFrom());
            return !wallet.isNull();
        }

        TransactionAuthorizationBaseData transactionAuthorizationBaseData = transactionAuthorizationData.getBaseDataList().get(0);

        return TextUtils.equals(transactionAuthorizationBaseData.getFrom(), transactionSignatureData.getFrom());
    }

    private boolean checkChainId(TransactionAuthorizationData transactionAuthorizationData) {
        if (transactionAuthorizationData == null) {
            return TextUtils.equals(NodeManager.getInstance().getChainId(), transactionSignatureData.getChainId());
        }

        TransactionAuthorizationBaseData transactionAuthorizationBaseData = transactionAuthorizationData.getBaseDataList().get(0);
        return TextUtils.equals(transactionAuthorizationBaseData.getChainId(), transactionSignatureData.getChainId());
    }

    private boolean checkFunctionType(TransactionAuthorizationData transactionAuthorizationData) {

        if (transactionAuthorizationData == null) {
            return true;
        }

        TransactionAuthorizationBaseData transactionAuthorizationBaseData = transactionAuthorizationData.getBaseDataList().get(0);

        return transactionAuthorizationBaseData.getFunctionType() == transactionSignatureData.getFunctionType();
    }

    private void afterSendTransactionSucceed(PlatonSendTransaction platonSendTransaction, TransactionAuthorizationData transactionAuthorizationData) {
        dismiss();
        List<String> signedDatas = transactionSignatureData.getSignedDatas();
        Transaction transaction = buildTransaction(transactionAuthorizationData, platonSendTransaction.getTransactionHash(), signedDatas.get(signedDatas.size() - 1));
        if (transactionSignatureData.getFunctionType() == FunctionType.TRANSFER) {
            //跳转至交易记录页签
            transaction.setTxType(String.valueOf(TransactionType.TRANSFER.getTxTypeValue()));
            afterTransferSucceed(transaction);
        } else {
            transaction.setTxType(getTxTypeByFunctionType(transactionSignatureData.getFunctionType()));
            TransactionManager.getInstance().getTransactionByLoop(transaction);
            if (sendTransactionSucceedListener != null) {
                sendTransactionSucceedListener.onSendTransactionSucceed(transaction);
            }
        }
    }

    private String getTxTypeByFunctionType(int functionType) {
        switch (functionType) {
            case FunctionType.DELEGATE_FUNC_TYPE:
                return String.valueOf(TransactionType.DELEGATE.getTxTypeValue());
            case FunctionType.WITHDREW_DELEGATE_FUNC_TYPE:
                return String.valueOf(TransactionType.UNDELEGATE.getTxTypeValue());
            case FunctionType.WITHDRAW_DELEGATE_REWARD_FUNC_TYPE:
                return String.valueOf(TransactionType.CLAIM_REWARDS.getTxTypeValue());
            default:
                return String.valueOf(TransactionType.UNKNOWN.getTxTypeValue());
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


    private Transaction buildTransaction(TransactionAuthorizationData transactionAuthorizationData, String hash, String signedMessage) {
        RawTransaction rawTransaction = TransactionDecoder.decode(signedMessage);
        String amount = null;
        String data = rawTransaction.getData();
        String nodeId = decodeNodeId(data);
        String contractAmount = decodeContractAmount(data);
        if (transactionAuthorizationData != null) {
            amount = transactionAuthorizationData.getTransactionAuthorizationDetail().getAmount();
        } else {
            if (transactionSignatureData.getFunctionType() == FunctionType.TRANSFER) {
                amount = rawTransaction.getValue().toString(10);
            } else if (transactionSignatureData.getFunctionType() == FunctionType.WITHDRAW_DELEGATE_REWARD_FUNC_TYPE) {
                amount = transactionSignatureData.getClaimRewardAmount();
            } else {
                amount = contractAmount;
            }
        }
        return new Transaction.Builder()
                .hash(hash)
                .from(transactionSignatureData.getFrom())
                .to(rawTransaction.getTo())
                .senderWalletName(getSenderName(transactionSignatureData.getFrom()))
                .value(amount)
                .unDelegation(amount)
                .chainId(transactionSignatureData.getChainId())
                .timestamp(System.currentTimeMillis())
                .txReceiptStatus(TransactionStatus.PENDING.ordinal())
                .actualTxCost(BigIntegerUtil.mul(rawTransaction.getGasLimit(), rawTransaction.getGasPrice()))
                .unDelegation(amount)
                .nodeName(transactionSignatureData.getNodeName())
                .nodeId(nodeId)
                .totalReward(transactionSignatureData.getClaimRewardAmount())
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

    private String decodeContractAmount(String hex) {
        String contractAmount = null;
        try {
            contractAmount = Numeric.decodeQuantity(decodeAmount(hex)).toString(10);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contractAmount;
    }

    private String decodeNodeId(String hex) {

        RlpList rlp = RlpDecoder.decode(Numeric.hexStringToByteArray(hex));

        List<RlpType> typeList = rlp.getValues();

        if (typeList == null || typeList.isEmpty()) {
            return null;
        }

        List<RlpType> rlpList = ((RlpList) (typeList.get(0))).getValues();

        if (rlpList == null || rlpList.size() < 3) {
            return null;
        }

        List<RlpType> rlpTypeList = RlpDecoder.decode(((RlpString) rlpList.get(2)).getBytes()).getValues();

        if (rlpTypeList == null || rlpTypeList.isEmpty()) {
            return null;
        }

        return ((RlpString) rlpTypeList.get(0)).asString();
    }

    private String decodeAmount(String hex) {

        RlpList rlp = RlpDecoder.decode(Numeric.hexStringToByteArray(hex));

        List<RlpType> typeList = rlp.getValues();

        if (typeList == null || typeList.isEmpty()) {
            return null;
        }

        List<RlpType> rlpList = ((RlpList) (typeList.get(0))).getValues();

        if (rlpList == null || rlpList.size() < 4) {
            return null;
        }

        List<RlpType> rlpTypeList = RlpDecoder.decode(((RlpString) rlpList.get(3)).getBytes()).getValues();

        if (rlpTypeList == null || rlpTypeList.isEmpty()) {
            return null;
        }

        return ((RlpString) rlpTypeList.get(0)).asString();
    }

    public interface OnSendTransactionSucceedListener {

        void onSendTransactionSucceed(Transaction transaction);
    }
}
