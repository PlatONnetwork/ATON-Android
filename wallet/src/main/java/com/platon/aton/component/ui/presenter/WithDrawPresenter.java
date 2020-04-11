package com.platon.aton.component.ui.presenter;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.View;

import com.platon.aton.BuildConfig;
import com.platon.aton.R;
import com.platon.aton.app.CustomObserver;
import com.platon.aton.app.CustomThrowable;
import com.platon.aton.app.LoadingTransformer;
import com.platon.aton.component.ui.contract.WithDrawContract;
import com.platon.aton.component.ui.dialog.InputWalletPasswordDialogFragment;
import com.platon.aton.component.ui.dialog.SelectDelegationsDialogFragment;
import com.platon.aton.component.ui.dialog.TransactionAuthorizationDialogFragment;
import com.platon.aton.component.ui.dialog.TransactionSignatureDialogFragment;
import com.platon.aton.engine.DelegateManager;
import com.platon.aton.engine.NodeManager;
import com.platon.aton.engine.ServerUtils;
import com.platon.aton.engine.WalletManager;
import com.platon.aton.entity.AccountBalance;
import com.platon.aton.entity.DelegateItemInfo;
import com.platon.aton.entity.DelegationValue;
import com.platon.aton.entity.GasProvider;
import com.platon.aton.entity.RPCErrorCode;
import com.platon.aton.entity.Transaction;
import com.platon.aton.entity.TransactionAuthorizationBaseData;
import com.platon.aton.entity.TransactionAuthorizationData;
import com.platon.aton.entity.TransactionType;
import com.platon.aton.entity.Wallet;
import com.platon.aton.entity.WithDrawBalance;
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
import org.web3j.utils.Convert;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;

/**
 * @author ziv
 */
public class WithDrawPresenter extends BasePresenter<WithDrawContract.View> implements WithDrawContract.Presenter {

    private DelegateItemInfo mDelegateDetail;
    private Wallet mWallet;
    private DelegationValue mDelegationValue;
    private List<WithDrawBalance> list = new ArrayList<>();
    private WithDrawBalance mWithDrawBalance = null;

    @Override
    public void init(DelegateItemInfo delegateItemInfo) {
        mDelegateDetail = delegateItemInfo;
        if (mDelegateDetail != null) {
            if (TextUtils.isEmpty(mDelegateDetail.getWalletAddress())) {
                mWallet = WalletManager.getInstance().getFirstSortedWallet();
            } else {
                mWallet = WalletManager.getInstance().getWalletByAddress(mDelegateDetail.getWalletAddress());
            }
        }
    }

    public String getWalletAddress() {
        if (mWallet != null) {
            return mWallet.getPrefixAddress();
        }
        return null;
    }

    public List<WithDrawBalance> getWithDrawBalanceList() {
        return list;
    }

    public WithDrawBalance getWithDrawBalance() {
        return mWithDrawBalance;
    }

    public double getMinDelegationAmount() {
        return NumberParserUtils.parseDouble(AmountUtil.convertVonToLat(mDelegationValue.getMinDelegation()));
    }

    @Override
    public void showWalletInfo() {
        if (isViewAttached()) {
            if (mWallet != null) {
                getView().showSelectedWalletInfo(mWallet);
            }
            if (mDelegateDetail != null) {
                getView().showNodeInfo(mDelegateDetail);
            }
        }
    }


    @Override
    public void checkWithDrawAmount(String withdrawAmount) {
        if (mDelegationValue != null) {
            //检查赎回的数量
            String minDelegation = mDelegationValue.getMinDelegation();
            String minDelegationAmount = AmountUtil.convertVonToLat(mDelegationValue.getMinDelegation());
            boolean isWithdrawAmountBiggerThanMinDelegation = BigDecimalUtil.isNotSmaller(withdrawAmount, minDelegationAmount);

            getView().showTips(!isWithdrawAmountBiggerThanMinDelegation, NumberParserUtils.getPrettyNumber(minDelegationAmount));

            if (mWithDrawBalance != null && mWithDrawBalance.isDelegated()) {
                String leftWithdrawAmount = BigDecimalUtil.sub(mWithDrawBalance.getDelegated(), Convert.toVon(BigDecimalUtil.toBigDecimal(withdrawAmount), Convert.Unit.LAT).toPlainString()).toPlainString();
                boolean isLeftWithdrawAmountSmallerThanMinDelegation = BigDecimalUtil.isBiggerThanZero(leftWithdrawAmount) && !BigDecimalUtil.isNotSmaller(leftWithdrawAmount, minDelegation);
                if (isLeftWithdrawAmountSmallerThanMinDelegation) {
                    getView().setAllAmountDelegate();
                }
            }
        }
    }

    @Override
    public void updateWithDrawButtonState() {
        if (isViewAttached() && mDelegationValue != null) {
            String withdrawAmount = getView().getWithDrawAmount();
            boolean isAmountValid = !TextUtils.isEmpty(withdrawAmount) && NumberParserUtils.parseDouble(withdrawAmount) >= NumberParserUtils.parseDouble(NumberParserUtils.getPrettyBalance(AmountUtil.convertVonToLat(mDelegationValue.getMinDelegation())));
            getView().setWithDrawButtonState(isAmountValid);
        }
    }

    @Override
    public void getBalanceType() {
        if (mDelegateDetail == null) {
            return;
        }

        ServerUtils.getCommonApi().getDelegationValue(ApiRequestBody.newBuilder()
                .put("addr", mWallet.getPrefixAddress())
                .put("nodeId", mDelegateDetail.getNodeId())
                .build())
                .compose(RxUtils.getSingleSchedulerTransformer())
                .compose(bindToLifecycle())
                .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                .subscribe(new ApiSingleObserver<DelegationValue>() {
                    @Override
                    public void onApiSuccess(DelegationValue delegationValue) {
                        if (isViewAttached()) {

                            mDelegationValue = delegationValue;

                            AccountBalance accountBalance = new AccountBalance(mWallet.getPrefixAddress(), delegationValue.getFree(), delegationValue.getLock());
                            WalletManager.getInstance().updateAccountBalance(accountBalance);

                            mWallet = WalletManager.getInstance().getWalletByAddress(accountBalance.getPrefixAddress());

                            showWalletInfo();

                            list = delegationValue.getWithDrawBalanceList();

                            mWithDrawBalance = delegationValue.getDefaultShowWithDrawBalance();

                            if (mWithDrawBalance != null) {

                                getView().showGas(mWithDrawBalance.getGasProvider().toSdkGasProvider());

                                double releasedSum = delegationValue.getReleasedSumAmount(); //待赎回
                                double delegatedSum = delegationValue.getDelegatedSumAmount();//已委托

                                getView().showMinDelegationInfo(NumberParserUtils.getPrettyNumber(AmountUtil.convertVonToLat(delegationValue.getMinDelegation())));
                                getView().showWithdrawBalance(mWithDrawBalance);
                                getView().showsSelectDelegationsBtnVisibility(list != null && list.size() > 1 ? View.VISIBLE : View.GONE);

                                if (delegatedSum + releasedSum <= 0) {
                                    getView().finishDelayed();
                                }
                            }

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


    /**
     * 获取手续费
     */
    @Override
    public void getWithDrawGasPrice(String gasPrice) {
        String input = getView().getInputAmount();
        if (TextUtils.isEmpty(input) || TextUtils.equals(input, ".")) {
            getView().showWithDrawGasPrice("0.00");
            return;
        }

        if (mDelegateDetail == null || list.isEmpty() || mWithDrawBalance == null || mDelegationValue == null) {
            return;
        }

        getView().showWithDrawGasPrice(getFeeAmount(mWithDrawBalance.getGasProvider()));
    }

    @Override
    public void showSelectDelegationsDialogFragment() {
        if (list != null && list.size() > 1) {
            SelectDelegationsDialogFragment.newInstance(list, list.indexOf(mWithDrawBalance))
                    .setOnInvalidDelegationsClickListener(new SelectDelegationsDialogFragment.OnInvalidDelegationsClickListener() {
                        @Override
                        public void onInvalidDelegationsClick(WithDrawBalance withDrawBalance) {
                            mWithDrawBalance = withDrawBalance;
                            getView().showGas(mWithDrawBalance.getGasProvider().toSdkGasProvider());
                            getView().showWithdrawBalance(mWithDrawBalance);
                        }
                    })
                    .show(currentActivity().getSupportFragmentManager(), "showSelectDelegationsDialogFragment");
        }
    }

    private String getFeeAmount(GasProvider gasProvider) {
        return BigDecimalUtil.mul(gasProvider.getGasLimit(), gasProvider.getGasPrice()).toPlainString();
    }

    @SuppressLint("CheckResult")
    @Override
    public void submitWithDraw() {
        if (mDelegateDetail == null || mWithDrawBalance == null) {
            return;
        }

        if (isViewAttached()) {
            String amount = mWithDrawBalance.isDelegated() ? mWithDrawBalance.getDelegated() : mWithDrawBalance.getReleased();
            if (BigDecimalUtil.isBigger(getView().getInputAmount(), AmountUtil.formatAmountText(amount).replace(",", ""))) {
                showLongToast(R.string.withdraw_operation_tips);
                return;
            }

            GasProvider gasProvider = mWithDrawBalance.getGasProvider();

            if (mWallet.isObservedWallet()) {
                showTransactionAuthorizationDialogFragment(gasProvider, mDelegateDetail.getNodeId(), mDelegateDetail.getNodeName(), getView().getInputAmount(), mWallet.getPrefixAddress(), ContractAddress.DELEGATE_CONTRACT_ADDRESS, mDelegationValue.getNonce());
            } else {
                InputWalletPasswordDialogFragment
                        .newInstance(mWallet)
                        .setOnWalletPasswordCorrectListener(new InputWalletPasswordDialogFragment.OnWalletPasswordCorrectListener() {
                            @Override
                            public void onWalletPasswordCorrect(Credentials credentials) {
                                withdraw(credentials, gasProvider, mDelegateDetail.getNodeId(), mDelegateDetail.getNodeName(), mWithDrawBalance.getStakingBlockNum(), getView().getInputAmount(), mDelegationValue.getNonce());
                            }
                        })
                        .show(currentActivity().getSupportFragmentManager(), "inputWalletPasssword");
            }
        }
    }

    /**
     * 操作赎回
     *
     * @param credentials
     * @param gasProvider
     * @param nodeId
     * @param nodeName
     * @param blockNum
     * @param withdrawAmount
     */
    @SuppressLint("CheckResult")
    public void withdraw(Credentials credentials, GasProvider gasProvider, String nodeId, String nodeName, String blockNum, String withdrawAmount, String nonce) {
        DelegateManager.getInstance()
                .withdrawDelegate(credentials, ContractAddress.DELEGATE_CONTRACT_ADDRESS, nodeId, nodeName, getFeeAmount(gasProvider), blockNum, withdrawAmount, String.valueOf(TransactionType.UNDELEGATE.getTxTypeValue()), gasProvider.toSdkGasProvider(), nonce)
                .toObservable()
                .compose(RxUtils.getSchedulerTransformer())
                .compose(RxUtils.getLoadingTransformer(currentActivity()))
                .subscribe(new CustomObserver<Transaction>() {
                    @Override
                    public void accept(Transaction transaction) {
                        if (isViewAttached()) {
                            //操作成功，跳转到交易详情，当前页面关闭
                            getView().withDrawSuccessInfo(transaction);
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
                                    showLongToast(string(R.string.msg_transaction_exception, customThrowable.getErrCode()));
                                } else {
                                    showLongToast(string(R.string.msg_server_exception, customThrowable.getErrCode()));
                                }
                            }
                            //重新刷新
                            getBalanceType();
                        }
                    }
                });

    }

    private List<TransactionAuthorizationBaseData> buildTransactionAuthorizationBaseDataList(final BigInteger nonce, String nodeId, String nodeName, String transactionAmount, String from, String to, com.platon.aton.entity.GasProvider gasProvider) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }

        return Flowable
                .range(0, list.size())
                .map(new Function<Integer, TransactionAuthorizationBaseData>() {
                    @Override
                    public TransactionAuthorizationBaseData apply(Integer position) throws Exception {
                        return new TransactionAuthorizationBaseData.Builder(FunctionType.WITHDREW_DELEGATE_FUNC_TYPE)
                                .setAmount(BigDecimalUtil.mul(transactionAmount, "1E18").toPlainString())
                                .setChainId(NodeManager.getInstance().getChainId())
                                .setNonce(nonce.add(BigInteger.valueOf(position)).toString(10))
                                .setFrom(from)
                                .setTo(to)
                                .setGasLimit(gasProvider.getGasLimit())
                                .setGasPrice(gasProvider.getGasPrice())
                                .setNodeId(nodeId)
                                .setNodeName(nodeName)
                                .setRemark("")
                                .setStakingBlockNum(list.get(position).getStakingBlockNum())
                                .build();
                    }
                })
                .toList()
                .blockingGet();
    }


    private void showTransactionAuthorizationDialogFragment(GasProvider gasProvider, String nodeId, String nodeName, String transactionAmount, String from, String to, String nonce) {

        TransactionAuthorizationData transactionAuthorizationData = new TransactionAuthorizationData(buildTransactionAuthorizationBaseDataList(BigIntegerUtil.toBigInteger(nonce), nodeId, nodeName, transactionAmount, from, to, gasProvider), System.currentTimeMillis() / 1000, BuildConfig.QRCODE_VERSION_CODE);
        TransactionAuthorizationDialogFragment.newInstance(transactionAuthorizationData)
                .setOnNextBtnClickListener(new TransactionAuthorizationDialogFragment.OnNextBtnClickListener() {
                    @Override
                    public void onNextBtnClick() {
                        TransactionSignatureDialogFragment.newInstance(transactionAuthorizationData)
                                .setOnSendTransactionSucceedListener(new TransactionSignatureDialogFragment.OnSendTransactionSucceedListener() {
                                    @Override
                                    public void onSendTransactionSucceed(Transaction transaction) {
                                        if (isViewAttached()) {
                                            getView().withDrawSuccessInfo(transaction);
                                        }
                                    }
                                })
                                .show(currentActivity().getSupportFragmentManager(), TransactionSignatureDialogFragment.TAG);
                    }
                })
                .show(currentActivity().getSupportFragmentManager(), "showTransactionAuthorizationDialog");
    }

}
