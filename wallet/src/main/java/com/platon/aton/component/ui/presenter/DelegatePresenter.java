package com.platon.aton.component.ui.presenter;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.platon.aton.BuildConfig;
import com.platon.aton.R;
import com.platon.aton.app.CustomThrowable;
import com.platon.aton.app.LoadingTransformer;
import com.platon.aton.component.ui.contract.DelegateContract;
import com.platon.aton.component.ui.dialog.DelegateSelectWalletDialogFragment;
import com.platon.aton.component.ui.dialog.InputWalletPasswordDialogFragment;
import com.platon.aton.component.ui.dialog.TransactionAuthorizationDialogFragment;
import com.platon.aton.component.ui.dialog.TransactionSignatureDialogFragment;
import com.platon.aton.engine.DelegateManager;
import com.platon.aton.engine.NodeManager;
import com.platon.aton.engine.ServerUtils;
import com.platon.aton.engine.WalletManager;
import com.platon.aton.entity.AccountBalance;
import com.platon.aton.entity.DelegateItemInfo;
import com.platon.aton.entity.EstimateGasResult;
import com.platon.aton.entity.GasProvider;
import com.platon.aton.entity.RPCErrorCode;
import com.platon.aton.entity.Transaction;
import com.platon.aton.entity.TransactionAuthorizationBaseData;
import com.platon.aton.entity.TransactionAuthorizationData;
import com.platon.aton.entity.TransactionType;
import com.platon.aton.entity.Wallet;
import com.platon.aton.utils.AmountUtil;
import com.platon.aton.utils.BigDecimalUtil;
import com.platon.aton.utils.BigIntegerUtil;
import com.platon.aton.utils.NumberParserUtils;
import com.platon.aton.utils.RxUtils;
import com.platon.framework.base.BasePresenter;
import com.platon.framework.network.ApiRequestBody;
import com.platon.framework.network.ApiResponse;
import com.platon.framework.network.ApiSingleObserver;

import org.web3j.crypto.Credentials;
import org.web3j.platon.ContractAddress;
import org.web3j.platon.FunctionType;
import org.web3j.platon.StakingAmountType;
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
    private EstimateGasResult mEstimateGasResult;
    /**
     * 是否点击全部
     */
    private boolean isAll = false;

    public String getWalletAddress() {
        if (mWallet != null) {
            return mWallet.getPrefixAddress();
        }
        return null;
    }

    @Override
    public void init(DelegateItemInfo delegateItemInfo) {
        this.mDelegateDetail = delegateItemInfo;
        if (mDelegateDetail != null) {
            if (TextUtils.isEmpty(mDelegateDetail.getWalletAddress())) {
                mWallet = WalletManager.getInstance().getSelectedWallet();
            } else {
                mWallet = WalletManager.getInstance().getWalletByWalletAddress(mDelegateDetail.getWalletAddress());
            }
        }
    }

    @Override
    public void showSelectWalletDialogFragment() {
        DelegateSelectWalletDialogFragment.newInstance(mWallet != null ? mWallet.getUuid() : "", false)
                .setOnItemClickListener(new DelegateSelectWalletDialogFragment.OnItemClickListener() {
                    @Override
                    public void onItemClick(Wallet wallet) {
                        getEstimateGas(wallet.getPrefixAddress(), mDelegateDetail.getNodeId());
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
                getEstimateGas(mWallet.getPrefixAddress(), mDelegateDetail.getNodeId());
            }
        }
    }

    @Override
    public void checkDelegateAmount(String delegateAmount) {
        if (mEstimateGasResult != null) {
            double amount = NumberParserUtils.parseDouble(delegateAmount);
            String minDelegationAmount = NumberParserUtils.getPrettyNumber(AmountUtil.convertVonToLat(mEstimateGasResult.getMinDelegation()));
            //检查委托的数量
            if (TextUtils.isEmpty(delegateAmount)) {
                getView().showTips(false, minDelegationAmount);
            } else if (amount < NumberParserUtils.parseDouble(NumberParserUtils.getPrettyBalance(AmountUtil.convertVonToLat(mEstimateGasResult.getMinDelegation())))) {
                //按钮不可点击,并且下方提示
                getView().showTips(true, minDelegationAmount);
            } else {
                getView().showTips(false, minDelegationAmount);
            }
            updateDelegateButtonState();
        }
    }

    @Override
    public void updateDelegateButtonState() {
        if (isViewAttached() && mEstimateGasResult != null) {
            //获取输入的委托数量
            String withdrawAmount = getView().getDelegateAmount();
            boolean isAmountValid = !TextUtils.isEmpty(withdrawAmount) && NumberParserUtils.parseDouble(withdrawAmount) >= NumberParserUtils.parseDouble(NumberParserUtils.getPrettyBalance(AmountUtil.convertVonToLat(mEstimateGasResult.getMinDelegation())));
            getView().setDelegateButtonState(isAmountValid);
        }

    }

    /**
     * 获取手续费
     *
     * @param stakingAmountType
     */
    @SuppressLint("CheckResult")
    @Override
    public void getGasProvider(StakingAmountType stakingAmountType) {

        //输入的数量
        String inputAmount = getView().getDelegateAmount();

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
                getView().showFeeAmount(mEstimateGasResult.getFeeAmount());
            } else {
                isAll = false;
            }
        }

    }


    //点击全部的时候，需要获取一次手续费
    public void getAllPrice(StakingAmountType stakingAmountType, String amount, boolean keepBalance) {
        isAll = true;
        if (mDelegateDetail != null && mEstimateGasResult != null) {
            String feeAmount = mEstimateGasResult.getFeeAmount();
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
     * @param estimateGasResult
     * @param stakingAmountType
     * @return
     */
    private String checkDelegateParam(EstimateGasResult estimateGasResult, StakingAmountType stakingAmountType) {

        BigDecimal feeAmount = new BigDecimal(getView().getFeeAmount()).multiply(new BigDecimal(DEFAULT_EXCHANGE_RATE));
        BigDecimal delegateAmount = new BigDecimal(getView().getDelegateAmount()).multiply(new BigDecimal(DEFAULT_EXCHANGE_RATE));
        BigDecimal freeAmount = new BigDecimal(estimateGasResult.getFree());
        BigDecimal lockAmount = new BigDecimal(estimateGasResult.getLock());

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
        return "";
    }

    @SuppressLint("CheckResult")
    @Override
    public void submitDelegate(StakingAmountType stakingAmountType) {

        if (mWallet != null && mDelegateDetail != null && mEstimateGasResult != null) {
            if (mWallet.isObservedWallet()) {
                showTransactionAuthorizationDialogFragment(mDelegateDetail.getNodeId(), mDelegateDetail.getNodeName(), stakingAmountType, mEstimateGasResult.getGasProvider(), getView().getDelegateAmount(), mWallet.getPrefixAddress(), ContractAddress.DELEGATE_CONTRACT_ADDRESS, mEstimateGasResult.getNonce());
            } else {
                showInputPasswordDialogFragment(getView().getDelegateAmount(), mDelegateDetail.getNodeId(), mDelegateDetail.getNodeName(), stakingAmountType, mEstimateGasResult.getGasProvider(), mEstimateGasResult.getNonce());
            }
        }

    }

    private String getMessageDescByErrorCode(int errorCode) {
        switch (errorCode) {
            case 3006:
                return string(R.string.the_validator_has_exited_and_cannot_be_delegated);
            case 3007:
                return string(R.string.tips_not_delegate);
            case 3004:
                return string(R.string.tips_not_balance);
            case 3008:
                return string(R.string.validators_details_tips);
            default:
                return "";
        }
    }

    /**
     * 估算gas
     *
     * @param from
     * @param nodeId
     * @return
     */
    private Single<Response<ApiResponse<EstimateGasResult>>> estimateGas(String from, String nodeId) {
        return ServerUtils.getCommonApi().estimateGas(ApiRequestBody.newBuilder()
                .put("from", from)
                .put("nodeId", nodeId)
                .put("txType", TransactionType.DELEGATE.getTxTypeValue())
                .build());
    }

    @SuppressLint("CheckResult")
    private void delegate(Credentials credentials, String inputAmount, String nodeId, String nodeName, StakingAmountType stakingAmountType, GasProvider gasProvider, String nonce) {
        //这里调用新的方法，传入GasProvider
        DelegateManager.getInstance().delegate(credentials, ContractAddress.DELEGATE_CONTRACT_ADDRESS, inputAmount, nodeId, nodeName, String.valueOf(TransactionType.DELEGATE.getTxTypeValue()), stakingAmountType, gasProvider.toSdkGasProvider(), nonce)
                .toObservable()
                .compose(RxUtils.getSchedulerTransformer())
                .compose(RxUtils.getLoadingTransformer(currentActivity()))
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
                                    showLongToast(string(R.string.msg_transaction_exception, customThrowable.getErrCode()));
                                } else {
                                    String errMsg = getMessageDescByErrorCode(customThrowable.getErrCode());
                                    if (TextUtils.isEmpty(errMsg)) {
                                        showLongToast(string(R.string.msg_server_exception, customThrowable.getErrCode()));
                                    } else {
                                        showLongToast(errMsg);
                                    }
                                }
                                getEstimateGas(credentials.getAddress(), nodeId);
                            }
                        }
                    }
                });

    }

    private void getEstimateGas(String prefixAddress, String nodeId) {

        estimateGas(prefixAddress, nodeId)
                .compose(RxUtils.bindToLifecycle(getView()))
                .compose(RxUtils.getSingleSchedulerTransformer())
                .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                .subscribe(new ApiSingleObserver<EstimateGasResult>() {
                    @Override
                    public void onApiSuccess(EstimateGasResult estimateGasResult) {
                        if (isViewAttached()) {

                            mEstimateGasResult = estimateGasResult;

                            WalletManager.getInstance().updateAccountBalance(new AccountBalance(prefixAddress, estimateGasResult.getFree(), estimateGasResult.getLock()));

                            mWallet = WalletManager.getInstance().getWalletByAddress(prefixAddress);

                            getView().showSelectedWalletInfo(mWallet);

                            getView().clearInputDelegateAmount();

                            getView().showFeeAmount(estimateGasResult.getFeeAmount());

                            getView().showIsCanDelegate(estimateGasResult);

                            getView().showDelegateResult(estimateGasResult.getMinDelegation());


                        }
                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {
                        super.onApiFailure(response);
                        if (isViewAttached()) {
                            getView().showIsCanDelegate(EstimateGasResult.getNullInstance());

                            getView().showDelegateException(response.getErrorCode());
                        }
                    }
                });
    }

    private void showInputPasswordDialogFragment(String inputAmount, String nodeAddress, String nodeName, StakingAmountType stakingAmountType, GasProvider gasProvider, String nonce) {
        InputWalletPasswordDialogFragment
                .newInstance(mWallet)
                .setOnWalletPasswordCorrectListener(new InputWalletPasswordDialogFragment.OnWalletPasswordCorrectListener() {
                    @Override
                    public void onWalletPasswordCorrect(Credentials credentials) {
                        delegate(credentials, inputAmount, nodeAddress, nodeName, stakingAmountType, gasProvider, nonce);
                    }
                })
                .show(currentActivity().getSupportFragmentManager(), "inputWalletPasssword");
    }

    private void showTransactionAuthorizationDialogFragment(String nodeId, String nodeName, StakingAmountType stakingAmountType, GasProvider gasProvider, String transactionAmount, String from, String to, String nonce) {

        TransactionAuthorizationData transactionAuthorizationData = new TransactionAuthorizationData(Arrays.asList(new TransactionAuthorizationBaseData.Builder(FunctionType.DELEGATE_FUNC_TYPE)
                .setAmount(BigDecimalUtil.mul(transactionAmount, "1E18").toPlainString())
                .setChainId(NodeManager.getInstance().getChainId())
                .setNonce(nonce)
                .setFrom(from)
                .setTo(to)
                .setGasLimit(gasProvider.getGasLimit())
                .setGasPrice(gasProvider.getGasPrice())
                .setNodeId(nodeId)
                .setNodeName(nodeName)
                .setStakingAmountType(stakingAmountType.getValue())
                .setRemark("")
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
