package com.juzix.wallet.component.ui.presenter;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.juzhen.framework.network.ApiRequestBody;
import com.juzhen.framework.network.ApiResponse;
import com.juzhen.framework.network.ApiSingleObserver;
import com.juzix.wallet.BuildConfig;
import com.juzix.wallet.R;
import com.juzix.wallet.app.CustomObserver;
import com.juzix.wallet.app.CustomThrowable;
import com.juzix.wallet.app.LoadingTransformer;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.DelegateContract;
import com.juzix.wallet.component.ui.dialog.DelegateSelectWalletDialogFragment;
import com.juzix.wallet.component.ui.dialog.InputWalletPasswordDialogFragment;
import com.juzix.wallet.component.ui.dialog.TransactionAuthorizationDialogFragment;
import com.juzix.wallet.component.ui.dialog.TransactionSignatureDialogFragment;
import com.juzix.wallet.engine.AppConfigManager;
import com.juzix.wallet.engine.DelegateManager;
import com.juzix.wallet.engine.NodeManager;
import com.juzix.wallet.engine.ServerUtils;
import com.juzix.wallet.engine.TransactionManager;
import com.juzix.wallet.engine.WalletManager;
import com.juzix.wallet.entity.DelegateHandle;
import com.juzix.wallet.entity.DelegateItemInfo;
import com.juzix.wallet.entity.RPCErrorCode;
import com.juzix.wallet.entity.Transaction;
import com.juzix.wallet.entity.TransactionAuthorizationBaseData;
import com.juzix.wallet.entity.TransactionAuthorizationData;
import com.juzix.wallet.entity.TransactionType;
import com.juzix.wallet.entity.Wallet;
import com.juzix.wallet.utils.AmountUtil;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.BigIntegerUtil;
import com.juzix.wallet.utils.NumberParserUtils;
import com.juzix.wallet.utils.RxUtils;

import org.web3j.crypto.Credentials;
import org.web3j.platon.ContractAddress;
import org.web3j.platon.FunctionType;
import org.web3j.platon.StakingAmountType;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.gas.GasProvider;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import retrofit2.Response;

public class DelegatePresenter extends BasePresenter<DelegateContract.View> implements DelegateContract.Presenter {

    private final static BigInteger DEFAULT_EXCHANGE_RATE = BigInteger.valueOf(1000000000000000000L);

    private Wallet mWallet;
    private DelegateItemInfo mDelegateDetail;

    //    private String feeAmount;
    private GasProvider mGasProvider = new DefaultGasProvider();
    private boolean isAll = false;//是否点击全部

    private String minDelegation = AppConfigManager.getInstance().getMinDelegation();

    public DelegatePresenter(DelegateContract.View view) {
        super(view);
        mDelegateDetail = view.getDelegateDetailFromIntent();
        if (mDelegateDetail != null) {
            if (TextUtils.isEmpty(mDelegateDetail.getWalletAddress())) {
                mWallet = WalletManager.getInstance().getSelectedWallet();
            } else {
                mWallet = WalletManager.getInstance().getWalletEntityByWalletAddress(mDelegateDetail.getWalletAddress());
            }
        }
    }

    public String getWalletAddress() {
        if (mWallet != null) {
            return mWallet.getPrefixAddress();
        }
        return null;
    }

    @Override
    public void showSelectWalletDialogFragment() {
        DelegateSelectWalletDialogFragment.newInstance(mWallet != null ? mWallet.getUuid() : "", false)
                .setOnItemClickListener(new DelegateSelectWalletDialogFragment.OnItemClickListener() {
                    @Override
                    public void onItemClick(Wallet wallet) {
                        ServerUtils.getCommonApi().getGasProvider(ApiRequestBody.newBuilder()
                                .put("from", wallet.getPrefixAddress())
                                .put("txType", FunctionType.DELEGATE_FUNC_TYPE)
                                .put("nodeId", mDelegateDetail.getNodeId())
                                .build())
                                .compose(RxUtils.bindToLifecycle(getView()))
                                .compose(RxUtils.getSingleSchedulerTransformer())
                                .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                                .subscribe(new ApiSingleObserver<com.juzix.wallet.entity.GasProvider>() {
                                    @Override
                                    public void onApiSuccess(com.juzix.wallet.entity.GasProvider gasProvider) {
                                        if (isViewAttached()) {
                                            mWallet = wallet;
                                            getView().showSelectedWalletInfo(mWallet);
                                            getView().clearInputDelegateAmount();
                                            if (mDelegateDetail != null && mWallet != null) {
                                                checkIsCanDelegate(mWallet.getPrefixAddress(), mDelegateDetail.getNodeId()); //0.7.3修改
                                            }
                                            mGasProvider = gasProvider.toSdkGasProvider();
                                            getView().showFeeAmount(BigIntegerUtil.mul(mGasProvider.getGasLimit(), mGasProvider.getGasPrice()));

                                        }
                                    }

                                    @Override
                                    public void onApiFailure(ApiResponse response) {
                                        super.onApiFailure(response);
                                        if (isViewAttached()) {
                                            showLongToast(R.string.msg_connect_timeout);
                                        }
                                    }
                                });
                    }
                })
                .show(currentActivity().getSupportFragmentManager(), "showSelectWalletDialog");

    }


    @Override
    public void showWalletInfo() {
        if (isViewAttached()) {
            if (mDelegateDetail != null) {
                getView().showNodeInfo(mDelegateDetail);
            }
            if (mWallet != null) {
                getView().showSelectedWalletInfo(mWallet);
            }
            if (mDelegateDetail != null && mWallet != null) {
                checkIsCanDelegate(mWallet.getPrefixAddress(), mDelegateDetail.getNodeId()); //0.7.3修改
            }
        }
    }

    @Override
    public void checkDelegateAmount(String delegateAmount) {
        double amount = NumberParserUtils.parseDouble(delegateAmount);
        String minDelegationAmount = NumberParserUtils.getPrettyNumber(BigDecimalUtil.div(minDelegation, "1E18"));
        //检查委托的数量
        if (TextUtils.isEmpty(delegateAmount)) {
            getView().showTips(false, minDelegationAmount);
        } else if (amount < NumberParserUtils.parseDouble(NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(minDelegation, "1E18")))) {
            //按钮不可点击,并且下方提示
            getView().showTips(true, minDelegationAmount);
        } else {
            getView().showTips(false, minDelegationAmount);
        }
        updateDelegateButtonState();
    }

    @Override
    public void updateDelegateButtonState() {
        if (isViewAttached()) {
            String withdrawAmount = getView().getDelegateAmount(); //获取输入的委托数量
            boolean isAmountValid = !TextUtils.isEmpty(withdrawAmount) && NumberParserUtils.parseDouble(withdrawAmount) >= NumberParserUtils.parseDouble(NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(minDelegation, "1E18")));
            getView().setDelegateButtonState(isAmountValid);
        }

    }

    /**
     * 检测是否可以委托
     */
    @Override
    public void checkIsCanDelegate(String walletAddress, String nodeAddress) {
        ServerUtils.getCommonApi().getIsDelegateInfo(ApiRequestBody.newBuilder()
                .put("addr", walletAddress)
                .put("nodeId", nodeAddress)
                .build())
                .compose(RxUtils.bindToLifecycle(getView()))
                .compose(RxUtils.getSingleSchedulerTransformer())
                .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                .subscribe(new ApiSingleObserver<DelegateHandle>() {
                    @Override
                    public void onApiSuccess(DelegateHandle delegateHandle) {
                        if (isViewAttached()) {
                            if (null != delegateHandle) {
                                minDelegation = delegateHandle.getMinDelegation();
                                getView().showIsCanDelegate(delegateHandle);
                            }

                        }
                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {
                        if (isViewAttached()) {
                            getView().showIsCanDelegate(DelegateHandle.getNullInstance());
                        }
                    }
                });
    }

    @SuppressLint("CheckResult")
    @Override
    public void getGas() {

        ServerUtils.getCommonApi().getGasProvider(ApiRequestBody.newBuilder()
                .put("from", mWallet.getPrefixAddress())
                .put("txType", FunctionType.DELEGATE_FUNC_TYPE)
                .put("nodeId", mDelegateDetail.getNodeId())
                .build())
                .compose(RxUtils.bindToLifecycle(getView()))
                .compose(RxUtils.getSingleSchedulerTransformer())
                .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                .subscribe(new ApiSingleObserver<com.juzix.wallet.entity.GasProvider>() {
                    @Override
                    public void onApiSuccess(com.juzix.wallet.entity.GasProvider gasProvider) {
                        if (isViewAttached()) {
                            mGasProvider = gasProvider.toSdkGasProvider();
                            getView().showFeeAmount(BigIntegerUtil.mul(mGasProvider.getGasLimit(), mGasProvider.getGasPrice()));
                        }
                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {
                        super.onApiFailure(response);
                        if (isViewAttached()) {
                            showLongToast(R.string.msg_connect_timeout);
                        }
                    }
                });
    }

    //获取手续费
    @SuppressLint("CheckResult")
    @Override
    public void getGasProvider(StakingAmountType stakingAmountType) {

        String inputAmount = getView().getDelegateAmount();//输入的数量

        if (TextUtils.isEmpty(inputAmount) || TextUtils.equals(inputAmount, ".")) {
            getView().showFeeAmount("0.00");
            return;
        } else {
            if (NumberParserUtils.parseDouble(inputAmount) < 0) {
                getView().showFeeAmount("0.00");
                return;
            }
        }

        if (mDelegateDetail != null && !TextUtils.isEmpty(mDelegateDetail.getNodeId())) {
            if (!isAll) {
                getView().showFeeAmount(getFeeAmount(mGasProvider.getGasPrice(), mGasProvider.getGasLimit()));
            } else {
                isAll = false;
            }
        }

    }


    //点击全部的时候，需要获取一次手续费
    public void getAllPrice(StakingAmountType stakingAmountType, String amount, boolean keepBalance) {
        isAll = true;
        if (mDelegateDetail != null) {
            String feeAmount = getFeeAmount(mGasProvider.getGasPrice(), mGasProvider.getGasLimit());
            BigInteger delegateAmount = stakingAmountType == StakingAmountType.FREE_AMOUNT_TYPE ? BigDecimalUtil.sub(Convert.toVon(amount, Convert.Unit.LAT).toBigInteger().toString(10), feeAmount).toBigInteger() : Convert.toVon(amount, Convert.Unit.LAT).toBigInteger();
            BigInteger actualDelegateAmount = keepBalance ? delegateAmount.subtract(Convert.toVon("0.1", Convert.Unit.LAT).toBigInteger()) : delegateAmount;
            getView().showAllFeeAmount(stakingAmountType, BigIntegerUtil.toString(actualDelegateAmount), feeAmount);
        }
    }

    private String getDelegateAmount(String preDelegateAmount, String feeAmount) {
        return BigDecimalUtil.sub(preDelegateAmount, feeAmount).toPlainString();
    }

    /**
     * 获取手续费
     *
     * @param gasPrice
     * @param gasLimit
     * @return
     */
    private String getFeeAmount(BigInteger gasPrice, BigInteger gasLimit) {
        return BigDecimalUtil.mul(BigIntegerUtil.toString(gasPrice), BigIntegerUtil.toString(gasLimit)).toPlainString();
    }

    /**
     * 获取gasPrice，手续费除以gasLimit
     *
     * @param feeAmount 手续费
     * @param gasLimit
     * @return
     */
    private BigInteger getGasPrice(String feeAmount, BigInteger gasLimit) {
        return BigIntegerUtil.toBigInteger(BigDecimalUtil.div(AmountUtil.getPrettyFee(feeAmount, 8), BigIntegerUtil.toString(gasLimit)));
    }

    /**
     * 检查余额是否充足
     * 1.当用自由金额委托时，判断自由金额是否大于委托金额与手续费的和
     * 2.当用锁仓金额委托时，手续费是从自由金额扣除的，这时候就需要考虑两个条件
     * 1.锁仓金额是否够委托金额
     * 2.自由金额是否够手续费
     *
     * @param delegateHandle
     * @param stakingAmountType
     * @return
     */
    private String checkDelegateParam(DelegateHandle delegateHandle, StakingAmountType stakingAmountType) {

        BigDecimal feeAmount = new BigDecimal(getView().getFeeAmount()).multiply(new BigDecimal(DEFAULT_EXCHANGE_RATE));
        BigDecimal delegateAmount = new BigDecimal(getView().getDelegateAmount()).multiply(new BigDecimal(DEFAULT_EXCHANGE_RATE));
        BigDecimal freeAmount = new BigDecimal(delegateHandle.getFree());
        BigDecimal lockAmount = new BigDecimal(delegateHandle.getLock());

        //自由金额是否够委托金额与手续费的和
        boolean isFreeAmountNotEnoughDelegateAmount = freeAmount.compareTo(feeAmount.add(delegateAmount)) < 0;
        //自由金额是否够手续费
        boolean isFreeAmountNotEnoughFeeAmount = freeAmount.compareTo(feeAmount) < 0;
        //锁仓金额是否够委托金额
        boolean isLockAmountNotEnough = lockAmount.compareTo(delegateAmount) < 0;
        //金额不足
        boolean isNotEnough = stakingAmountType == StakingAmountType.FREE_AMOUNT_TYPE ? isFreeAmountNotEnoughDelegateAmount : isFreeAmountNotEnoughFeeAmount || isLockAmountNotEnough;

        if (isNotEnough) {
            return string(R.string.insufficient_balance_unable_to_delegate);
        }
        return delegateHandle.getMessageDesc(getContext());
    }

    @SuppressLint("CheckResult")
    @Override
    public void submitDelegate(StakingAmountType stakingAmountType) {

        if (mWallet != null && mDelegateDetail != null) {
            getIsDelegateInfo(mWallet.getPrefixAddress(), mDelegateDetail.getNodeId())
                    .compose(RxUtils.getSingleSchedulerTransformer())
                    .compose(bindToLifecycle())
                    .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                    .subscribe(new ApiSingleObserver<DelegateHandle>() {
                        @Override
                        public void onApiSuccess(DelegateHandle delegateHandle) {
                            if (isViewAttached()) {
                                minDelegation = delegateHandle.getMinDelegation();
                                String errMsg = checkDelegateParam(delegateHandle, stakingAmountType);
                                if (!TextUtils.isEmpty(errMsg)) {
                                    showLongToast(errMsg);
                                } else {
                                    if (mWallet.isObservedWallet()) {
                                        showTransactionAuthorizationDialogFragment(mDelegateDetail.getNodeId(), mDelegateDetail.getNodeName(), stakingAmountType, getView().getDelegateAmount(), mWallet.getPrefixAddress(), ContractAddress.DELEGATE_CONTRACT_ADDRESS, mGasProvider.getGasLimit().toString(10), mGasProvider.getGasPrice().toString(10));
                                    } else {
                                        showInputPasswordDialogFragment(getView().getDelegateAmount(), mDelegateDetail.getNodeId(), mDelegateDetail.getNodeName(), stakingAmountType);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onApiFailure(ApiResponse response) {
                            if (isViewAttached()) {
                                if (!TextUtils.isEmpty(response.getErrMsg(getContext()))) {
                                    showLongToast(response.getErrMsg(getContext()));
                                } else {
                                    showLongToast(R.string.delegate_failed);
                                }
                            }
                        }
                    });
        }

    }

    private Single<Response<ApiResponse<DelegateHandle>>> getIsDelegateInfo(String walletAddress, String nodeAddress) {
        return ServerUtils.getCommonApi().getIsDelegateInfo(ApiRequestBody.newBuilder()
                .put("addr", walletAddress)
                .put("nodeId", nodeAddress)
                .build());
    }


    @SuppressLint("CheckResult")
    private void delegate(Credentials credentials, String inputAmount, String nodeId, String nodeName, StakingAmountType stakingAmountType) {
        //这里调用新的方法，传入GasProvider
        DelegateManager.getInstance().delegate(credentials, ContractAddress.DELEGATE_CONTRACT_ADDRESS, inputAmount, nodeId, nodeName, String.valueOf(TransactionType.DELEGATE.getTxTypeValue()), stakingAmountType, mGasProvider)
                .compose(RxUtils.getSingleSchedulerTransformer())
                .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                .subscribe(new Consumer<Transaction>() {
                    @Override
                    public void accept(Transaction transaction) throws Exception {
                        if (isViewAttached()) {
                            getView().showTransactionSuccessInfo(transaction);
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (isViewAttached()) {

                            if (throwable instanceof CustomThrowable) {
                                CustomThrowable customThrowable = (CustomThrowable) throwable;
                                if (customThrowable.getErrCode() == RPCErrorCode.CONNECT_TIMEOUT) {
                                    showLongToast(R.string.msg_connect_timeout);
                                } else if (customThrowable.getErrCode() == CustomThrowable.CODE_TX_KNOWN_TX) {
                                    showLongToast(R.string.msg_transaction_repeatedly_exception);
                                } else if (customThrowable.getErrCode() == CustomThrowable.CODE_TX_NONCE_TOO_LOW ||
                                        customThrowable.getErrCode() == CustomThrowable.CODE_TX_GAS_LOW) {
                                    showLongToast(R.string.msg_transaction_exception);
                                } else {
                                    showLongToast(R.string.msg_server_exception);
                                }
                            }
                        }
                    }
                });

    }

    private void showInputPasswordDialogFragment(String inputAmount, String nodeAddress, String nodeName, StakingAmountType stakingAmountType) {
        InputWalletPasswordDialogFragment
                .newInstance(mWallet)
                .setOnWalletPasswordCorrectListener(new InputWalletPasswordDialogFragment.OnWalletPasswordCorrectListener() {
                    @Override
                    public void onWalletPasswordCorrect(Credentials credentials) {
                        delegate(credentials, inputAmount, nodeAddress, nodeName, stakingAmountType);
                    }
                })
                .show(currentActivity().getSupportFragmentManager(), "inputWalletPasssword");
    }

    private void showTransactionAuthorizationDialogFragment(String nodeId, String nodeName, StakingAmountType stakingAmountType, String transactionAmount, String from, String to, String gasLimit, String gasPrice) {

        TransactionManager.getInstance().getNonce(from)
                .toObservable()
                .compose(RxUtils.getSchedulerTransformer())
                .compose(bindToLifecycle())
                .compose(RxUtils.getLoadingTransformer(currentActivity()))
                .subscribe(new CustomObserver<BigInteger>() {
                    @Override
                    public void accept(BigInteger nonce) {
                        if (isViewAttached()) {
                            TransactionAuthorizationData transactionAuthorizationData = new TransactionAuthorizationData(Arrays.asList(new TransactionAuthorizationBaseData.Builder(FunctionType.DELEGATE_FUNC_TYPE)
                                    .setAmount(BigDecimalUtil.mul(transactionAmount, "1E18").toPlainString())
                                    .setChainId(NodeManager.getInstance().getChainId())
                                    .setNonce(nonce.toString(10))
                                    .setFrom(from)
                                    .setTo(to)
                                    .setGasLimit(gasLimit)
                                    .setGasPrice(gasPrice)
                                    .setNodeId(nodeId)
                                    .setNodeName(nodeName)
                                    .setStakingAmountType(stakingAmountType.getValue())
                                    .build()), System.currentTimeMillis() / 1000, BuildConfig.QRCODE_VERSION_CODE);
                            TransactionAuthorizationDialogFragment.newInstance(transactionAuthorizationData)
                                    .setOnNextBtnClickListener(new TransactionAuthorizationDialogFragment.OnNextBtnClickListener() {
                                        @Override
                                        public void onNextBtnClick() {
                                            TransactionSignatureDialogFragment.newInstance(transactionAuthorizationData)
                                                    .setOnSendTransactionSucceedListener(new TransactionSignatureDialogFragment.OnSendTransactionSucceedListener() {
                                                        @Override
                                                        public void onSendTransactionSucceed(Transaction transaction) {
                                                            if (isViewAttached()) {
                                                                getView().showTransactionSuccessInfo(transaction);
                                                            }
                                                        }
                                                    })
                                                    .show(currentActivity().getSupportFragmentManager(), TransactionSignatureDialogFragment.TAG);
                                        }
                                    })
                                    .show(currentActivity().getSupportFragmentManager(), "showTransactionAuthorizationDialog");
                        }
                    }

                    @Override
                    public void accept(Throwable throwable) {
                        super.accept(throwable);
                        if (isViewAttached()) {

                            if (throwable instanceof CustomThrowable) {
                                CustomThrowable customThrowable = (CustomThrowable) throwable;
                                if (customThrowable.getErrCode() == RPCErrorCode.CONNECT_TIMEOUT) {
                                    showLongToast(R.string.msg_connect_timeout);
                                } else if (customThrowable.getErrCode() == CustomThrowable.CODE_TX_KNOWN_TX) {
                                    showLongToast(R.string.msg_transaction_repeatedly_exception);
                                } else if (customThrowable.getErrCode() == CustomThrowable.CODE_TX_NONCE_TOO_LOW ||
                                        customThrowable.getErrCode() == CustomThrowable.CODE_TX_GAS_LOW) {
                                    showLongToast(R.string.msg_expired_qr_code);
                                } else {
                                    showLongToast(R.string.msg_server_exception);
                                }
                            }
                        }
                    }
                });
    }
}
