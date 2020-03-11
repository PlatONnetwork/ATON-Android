package com.platon.aton.component.ui.presenter;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.View;

import com.platon.framework.network.ApiErrorCode;
import com.platon.framework.network.ApiRequestBody;
import com.platon.framework.network.ApiResponse;
import com.platon.framework.network.ApiSingleObserver;
import com.platon.framework.util.NumberParserUtils;
import com.platon.aton.R;
import com.platon.aton.app.CustomObserver;
import com.platon.aton.app.CustomThrowable;
import com.platon.aton.app.LoadingTransformer;
import com.platon.aton.component.ui.base.BasePresenter;
import com.platon.aton.component.ui.contract.WithDrawContract;
import com.platon.aton.component.ui.dialog.InputWalletPasswordDialogFragment;
import com.platon.aton.component.ui.dialog.SelectDelegationsDialogFragment;
import com.platon.aton.component.ui.dialog.TransactionAuthorizationDialogFragment;
import com.platon.aton.component.ui.dialog.TransactionSignatureDialogFragment;
import com.platon.aton.engine.AppConfigManager;
import com.platon.aton.engine.DelegateManager;
import com.platon.aton.engine.NodeManager;
import com.platon.aton.engine.ServerUtils;
import com.platon.aton.engine.WalletManager;
import com.platon.aton.engine.Web3jManager;
import com.platon.aton.entity.AccountBalance;
import com.platon.aton.entity.DelegateItemInfo;
import com.platon.aton.entity.DelegationValue;
import com.platon.aton.entity.RPCErrorCode;
import com.platon.aton.entity.Transaction;
import com.platon.aton.entity.TransactionAuthorizationBaseData;
import com.platon.aton.entity.TransactionAuthorizationData;
import com.platon.aton.entity.TransactionType;
import com.platon.aton.entity.Wallet;
import com.platon.aton.entity.WithDrawBalance;
import com.platon.aton.utils.AmountUtil;
import com.platon.aton.utils.BigDecimalUtil;
import com.platon.aton.utils.RxUtils;

import org.web3j.crypto.Credentials;
import org.web3j.platon.ContractAddress;
import org.web3j.platon.FunctionType;
import org.web3j.tx.gas.GasProvider;
import org.web3j.utils.Convert;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class WithDrawPresenter extends BasePresenter<WithDrawContract.View> implements WithDrawContract.Presenter {

    private DelegateItemInfo mDelegateDetail;
    private Wallet mWallet;

    private List<WithDrawBalance> list = new ArrayList<>();
    private WithDrawBalance mWithDrawBalance = null;

    private String feeAmount;

    private String minDelegation = AppConfigManager.getInstance().getMinDelegation();

    public WithDrawPresenter(WithDrawContract.View view) {
        super(view);
        mDelegateDetail = view.getDelegateDetailFromIntent();
        if (mDelegateDetail != null) {
            if (TextUtils.isEmpty(mDelegateDetail.getWalletAddress())) {
                mWallet = WalletManager.getInstance().getSelectedWallet();
            } else {
                mWallet = WalletManager.getInstance().getWalletByWalletAddress(mDelegateDetail.getWalletAddress());
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
        return NumberParserUtils.parseDouble(BigDecimalUtil.div(minDelegation, "1E18"));
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
        //检查赎回的数量
        String minDelegationAmount = BigDecimalUtil.div(minDelegation, "1E18");
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

    @Override
    public void updateWithDrawButtonState() {
        if (isViewAttached()) {
            String withdrawAmount = getView().getWithDrawAmount();
            boolean isAmountValid = !TextUtils.isEmpty(withdrawAmount) && NumberParserUtils.parseDouble(withdrawAmount) >= NumberParserUtils.parseDouble(NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(minDelegation, "1E18")));
            getView().setWithDrawButtonState(isAmountValid);
        }
    }

    @Override
    public void getBalanceType() {
        if (mDelegateDetail == null) {
            return;
        }
        ServerUtils.getCommonApi().getAccountBalance(ApiRequestBody.newBuilder()
                .put("addrs", Arrays.asList(mWallet.getPrefixAddress()))
                .build())
                .map(new Function<Response<ApiResponse<List<AccountBalance>>>, List<AccountBalance>>() {
                    @Override
                    public List<AccountBalance> apply(Response<ApiResponse<List<AccountBalance>>> apiResponseResponse) {
                        if (apiResponseResponse != null && apiResponseResponse.isSuccessful() && apiResponseResponse.body().getResult() == ApiErrorCode.SUCCESS) {
                            return apiResponseResponse.body().getData();
                        } else {
                            return new ArrayList<>();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(new Consumer<List<AccountBalance>>() {
                    @Override
                    public void accept(List<AccountBalance> accountBalances) {
                        if (isViewAttached() && !accountBalances.isEmpty()) {
                            AccountBalance accountBalance = accountBalances.get(0);
                            WalletManager.getInstance().updateAccountBalance(accountBalance);
                            mWallet = WalletManager.getInstance().getWalletByAddress(accountBalance.getPrefixAddress());
                            showWalletInfo();
                        }
                    }
                })
                .observeOn(Schedulers.io())
                .flatMap(new Function<List<AccountBalance>, SingleSource<Response<ApiResponse<DelegationValue>>>>() {
                    @Override
                    public SingleSource<Response<ApiResponse<DelegationValue>>> apply(List<AccountBalance> accountBalances) throws Exception {
                        return ServerUtils.getCommonApi().getDelegationValue(ApiRequestBody.newBuilder()
                                .put("addr", mWallet.getPrefixAddress())
                                .put("nodeId", mDelegateDetail.getNodeId())
                                .build());
                    }
                })
                .compose(RxUtils.getSingleSchedulerTransformer())
                .compose(bindToLifecycle())
                .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                .subscribe(new ApiSingleObserver<DelegationValue>() {

                    @Override
                    public void onApiSuccess(DelegationValue delegationValue) {
                        if (isViewAttached()) {

                            list = delegationValue.getWithDrawBalanceList();

                            mWithDrawBalance = delegationValue.getDefaultShowWithDrawBalance();

                            minDelegation = delegationValue.getMinDelegation();

                            if (mWithDrawBalance != null) {

                                getView().showGas(mWithDrawBalance.getGasProvider());

                                double releasedSum = delegationValue.getReleasedSumAmount(); //待赎回
                                double delegatedSum = delegationValue.getDelegatedSumAmount();//已委托

                                getView().showMinDelegationInfo(NumberParserUtils.getPrettyNumber(BigDecimalUtil.div(minDelegation, "1E18")));
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

        if (mDelegateDetail == null || list.isEmpty() || mWithDrawBalance == null) {
            return;
        }

        feeAmount = getFeeAmount(mWithDrawBalance.getGasProvider());
        getView().showWithDrawGasPrice(feeAmount);
    }

    @Override
    public void showSelectDelegationsDialogFragment() {
        if (list != null && list.size() > 1) {
            SelectDelegationsDialogFragment.newInstance(list, list.indexOf(mWithDrawBalance))
                    .setOnInvalidDelegationsClickListener(new SelectDelegationsDialogFragment.OnInvalidDelegationsClickListener() {
                        @Override
                        public void onInvalidDelegationsClick(WithDrawBalance withDrawBalance) {
                            mWithDrawBalance = withDrawBalance;
                            getView().showGas(mWithDrawBalance.getGasProvider());
                            getView().showWithdrawBalance(mWithDrawBalance);
                        }
                    })
                    .show(currentActivity().getSupportFragmentManager(), "showSelectDelegationsDialogFragment");
        }
    }

    private String getFeeAmount(GasProvider gasProvider) {
        return BigDecimalUtil.mul(gasProvider.getGasLimit().toString(10), gasProvider.getGasPrice().toString(10)).toPlainString();
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
                showTransactionAuthorizationDialogFragment(mDelegateDetail.getNodeId(), mDelegateDetail.getNodeName(), getView().getInputAmount(), mWallet.getPrefixAddress(), ContractAddress.DELEGATE_CONTRACT_ADDRESS, gasProvider.getGasLimit().toString(10), gasProvider.getGasPrice().toString(10));
            } else {
                InputWalletPasswordDialogFragment
                        .newInstance(mWallet)
                        .setOnWalletPasswordCorrectListener(new InputWalletPasswordDialogFragment.OnWalletPasswordCorrectListener() {
                            @Override
                            public void onWalletPasswordCorrect(Credentials credentials) {
                                withdraw(credentials, gasProvider, mDelegateDetail.getNodeId(), mDelegateDetail.getNodeName(), mWithDrawBalance.getStakingBlockNum(), getView().getInputAmount());
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
    public void withdraw(Credentials credentials, GasProvider gasProvider, String nodeId, String nodeName, String blockNum, String withdrawAmount) {
        DelegateManager.getInstance().withdrawDelegate(credentials, ContractAddress.DELEGATE_CONTRACT_ADDRESS, nodeId, nodeName, feeAmount, blockNum, withdrawAmount, String.valueOf(TransactionType.UNDELEGATE.getTxTypeValue()), gasProvider)
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
                            if (throwable instanceof CustomThrowable && ((CustomThrowable) throwable).getErrCode() == RPCErrorCode.CONNECT_TIMEOUT) {
                                showLongToast(R.string.msg_connect_timeout);
                            } else {
                                showLongToast(R.string.withdraw_failed);
                            }
                        }
                    }
                });

    }

    private List<TransactionAuthorizationBaseData> buildTransactionAuthorizationBaseDataList(final BigInteger nonce, String nodeId, String nodeName, String transactionAmount, String from, String to, String gasLimit, String gasPrice) {
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
                                .setGasLimit(gasLimit)
                                .setGasPrice(gasPrice)
                                .setNodeId(nodeId)
                                .setNodeName(nodeName)
                                .setStakingBlockNum(list.get(position).getStakingBlockNum())
                                .build();
                    }
                })
                .toList()
                .blockingGet();
    }


    private void showTransactionAuthorizationDialogFragment(String nodeId, String nodeName, String transactionAmount, String from, String to, String gasLimit, String gasPrice) {

        Observable
                .fromCallable(new Callable<BigInteger>() {
                    @Override
                    public BigInteger call() throws Exception {
                        return Web3jManager.getInstance().getNonce(from);
                    }
                })
                .filter(new Predicate<BigInteger>() {
                    @Override
                    public boolean test(BigInteger bigInteger) throws Exception {
                        return !bigInteger.equals(Web3jManager.NONE_NONCE);
                    }
                })
                .switchIfEmpty(new Observable<BigInteger>() {
                    @Override
                    protected void subscribeActual(Observer<? super BigInteger> observer) {
                        observer.onError(new CustomThrowable(RPCErrorCode.CONNECT_TIMEOUT));
                    }
                })
                .compose(RxUtils.getSchedulerTransformer())
                .compose(bindToLifecycle())
                .compose(RxUtils.getLoadingTransformer(currentActivity()))
                .subscribe(new CustomObserver<BigInteger>() {
                    @Override
                    public void accept(BigInteger nonce) {
                        if (isViewAttached()) {
                            TransactionAuthorizationData transactionAuthorizationData = new TransactionAuthorizationData(buildTransactionAuthorizationBaseDataList(nonce, nodeId, nodeName, transactionAmount, from, to, gasLimit, gasPrice), System.currentTimeMillis() / 1000);
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

                    @Override
                    public void accept(Throwable throwable) {
                        super.accept(throwable);
                        if (isViewAttached()) {
                            if (throwable instanceof CustomThrowable && ((CustomThrowable) throwable).getErrCode() == RPCErrorCode.CONNECT_TIMEOUT) {
                                showLongToast(R.string.msg_connect_timeout);
                            } else {
                                if (!TextUtils.isEmpty(throwable.getMessage())) {
                                    showLongToast(throwable.getMessage());
                                }
                            }
                        }
                    }
                });
    }

}
