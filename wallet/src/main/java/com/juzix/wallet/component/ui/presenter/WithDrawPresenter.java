package com.juzix.wallet.component.ui.presenter;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import com.jakewharton.rxbinding2.view.RxView;
import com.juzhen.framework.network.ApiRequestBody;
import com.juzhen.framework.network.ApiResponse;
import com.juzhen.framework.network.ApiSingleObserver;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.CustomObserver;
import com.juzix.wallet.app.LoadingTransformer;
import com.juzix.wallet.component.adapter.WithDrawPopWindowAdapter;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.WithDrawContract;
import com.juzix.wallet.component.ui.dialog.InputWalletPasswordDialogFragment;
import com.juzix.wallet.component.ui.dialog.TransactionAuthorizationDialogFragment;
import com.juzix.wallet.component.ui.dialog.TransactionSignatureDialogFragment;
import com.juzix.wallet.engine.AppConfigManager;
import com.juzix.wallet.engine.DelegateManager;
import com.juzix.wallet.engine.NodeManager;
import com.juzix.wallet.engine.ServerUtils;
import com.juzix.wallet.engine.WalletManager;
import com.juzix.wallet.engine.Web3jManager;
import com.juzix.wallet.entity.DelegateDetail;
import com.juzix.wallet.entity.Transaction;
import com.juzix.wallet.entity.TransactionAuthorizationBaseData;
import com.juzix.wallet.entity.TransactionAuthorizationData;
import com.juzix.wallet.entity.TransactionType;
import com.juzix.wallet.entity.Wallet;
import com.juzix.wallet.entity.WithDrawBalance;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.RxUtils;
import com.juzix.wallet.utils.ToastUtil;

import org.web3j.crypto.Credentials;
import org.web3j.platon.ContractAddress;
import org.web3j.platon.FunctionType;
import org.web3j.platon.contracts.DelegateContract;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.gas.GasProvider;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class WithDrawPresenter extends BasePresenter<WithDrawContract.View> implements WithDrawContract.Presenter {

    private DelegateDetail mDelegateDetail;
    private Wallet mWallet;

    private List<WithDrawBalance> list = new ArrayList<>();

    private int tag;
    private String feeAmount;
    private BigInteger gasPrice = DefaultGasProvider.GAS_PRICE; //调web3j获取gasprice
    private BigInteger gasLimit = DefaultGasProvider.GAS_LIMIT;

    private String minDelegation = AppConfigManager.getInstance().getMinDelegation();

    public WithDrawPresenter(WithDrawContract.View view) {
        super(view);
        mDelegateDetail = view.getDelegateDetailFromIntent();
        if (mDelegateDetail != null) {
            mWallet = WalletManager.getInstance().getWalletEntityByWalletAddress(mDelegateDetail.getWalletAddress());
        }
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
        double amount = NumberParserUtils.parseDouble(withdrawAmount);
        if (TextUtils.isEmpty(withdrawAmount)) {
            getView().showTips(false);
        } else if (amount < NumberParserUtils.parseDouble(NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(minDelegation, "1E18")))) {
            //按钮不可点击,并且下方提示
            getView().showTips(true);
        } else {
            getView().showTips(false);
        }

        updateWithDrawButtonState();
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
        ServerUtils.getCommonApi().getWithDrawBalance(ApiRequestBody.newBuilder()
                .put("addr", mDelegateDetail.getWalletAddress())
                .put("nodeId", mDelegateDetail.getNodeId())
                .build())
                .compose(RxUtils.getSingleSchedulerTransformer())
                .compose(bindToLifecycle())
                .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                .subscribe(new ApiSingleObserver<List<WithDrawBalance>>() {
                    @Override
                    public void onApiSuccess(List<WithDrawBalance> balanceList) {
                        if (isViewAttached()) {
                            list.clear();
                            list.addAll(balanceList);

                            double releasedSum = 0; //待赎回
                            double delegatedSum = 0;//已委托
                            if (null != balanceList && balanceList.size() > 0) {
                                for (WithDrawBalance balance : balanceList) {
                                    delegatedSum += NumberParserUtils.parseDouble(balance.getShowDelegated());
                                    releasedSum += NumberParserUtils.parseDouble(balance.getShowReleased());
                                }
                            }

                            getView().showBalanceType(delegatedSum, releasedSum);

                            if (delegatedSum + releasedSum <= 0) {
                                getView().finishDelayed();
                            }

                        }

                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {

                    }
                });
    }

    /**
     * 获取
     */
    @Override
    public void getGas() {
        DelegateManager.getInstance().getGasPrice()
                .compose(RxUtils.bindToLifecycle(getView()))
                .compose(RxUtils.getSingleSchedulerTransformer())
                .subscribe(new Consumer<BigInteger>() {
                    @Override
                    public void accept(BigInteger bigInteger) throws Exception {
                        if (isViewAttached()) {
                            WithDrawPresenter.this.gasPrice = gasPrice;
                            getView().showGas(gasPrice);
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

        if (mDelegateDetail == null || list.isEmpty()) {
            return;
        }

        DelegateContract
                .load(Web3jManager.getInstance().getWeb3j())
                .getUnDelegateGasProvider(mDelegateDetail.getNodeId(), new BigInteger(list.get(0).getStakingBlockNum()), Convert.toVon(input, Convert.Unit.LAT).toBigInteger())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<GasProvider>() {
                    @Override
                    public void call(GasProvider gasProvider) {
                        gasLimit = gasProvider.getGasLimit();
                        feeAmount = getFeeAmount(gasProvider, gasPrice);
                        getView().showWithDrawGasPrice(feeAmount);
                    }
                });
    }

    private String getFeeAmount(GasProvider gasProvider, String gasPrice) {

        return NumberParserUtils.getPrettyBalance(BigDecimalUtil.mul(gasProvider.getGasLimit().toString(10), gasPrice).doubleValue());

    }

    @SuppressLint("CheckResult")
    @Override
    public void submitWithDraw(String type) {
        if (mDelegateDetail == null) {
            return;
        }

        if (isViewAttached()) {
            double chooseNum = NumberParserUtils.parseDouble(getView().getChooseType());
            double inputPutAmount = NumberParserUtils.parseDouble(getView().getInputAmount());
            if (chooseNum < inputPutAmount) {
                showLongToast(R.string.withdraw_operation_tips);
                return;
            }

            if (mWallet.isObservedWallet()) {
                showTransactionAuthorizationDialogFragment(mDelegateDetail.getNodeId(), mDelegateDetail.getNodeName(), getView().getInputAmount(), mWallet.getPrefixAddress(), ContractAddress.DELEGATE_CONTRACT_ADDRESS, gasLimit.toString(10), gasPrice.toString(10), type);
            } else {
                InputWalletPasswordDialogFragment
                        .newInstance(mWallet)
                        .setOnWalletPasswordCorrectListener(new InputWalletPasswordDialogFragment.OnWalletPasswordCorrectListener() {
                            @Override
                            public void onWalletPasswordCorrect(Credentials credentials) {
                                withDrawIterate(credentials, mDelegateDetail.getNodeId(), mDelegateDetail.getNodeName(), getView().getInputAmount(), type);
                            }
                        })
                        .show(currentActivity().getSupportFragmentManager(), "inputWalletPasssword");
            }
        }
    }


    /**
     * @param credentials
     * @param nodeId
     * @param withdrawAmount 输入数量
     * @param type           从  已委托 或 未锁定委托 提取：  只操作记录最新的那个委托对象（快高最大）
     *                       从  已解除委托 提取：  查看记录中所有 已解锁余额非0的记录。  会存在调用多次底层的情况。
     */
    public void withDrawIterate(Credentials credentials, String nodeId, String nodeName, String withdrawAmount, String type) {
        String stakingBlockNum = "0";
        if (TextUtils.equals(type, WithDrawPopWindowAdapter.TAG_DELEGATED)) { //已委托
            for (int i = 0; i < list.size(); i++) {
                if (!TextUtils.isEmpty(list.get(i).getDelegated())) {
                    stakingBlockNum = list.get(i).getStakingBlockNum(); //这里其实就是第一条，因为最新的块高排在前面
                    withdraw(credentials, nodeId, nodeName, stakingBlockNum, withdrawAmount, type);
                    return;
                }
            }
        } else { //表示选择的是已解除委托
            for (int i = 0; i < list.size(); i++) {
                WithDrawBalance balance = list.get(i);
                if (!TextUtils.isEmpty(balance.getReleased())) {
                    stakingBlockNum = balance.getStakingBlockNum();
                    tag++;
                    withdraw(credentials, nodeId, nodeName, stakingBlockNum, NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(balance.getReleased(), "1E18")), type); //这里传入的数量应该是记录中每个对象不为0的数量（而不是显示的全部数量，因为这里要循环执行多次）
                }
            }


        }
    }


    //操作赎回
    @SuppressLint("CheckResult")
    public void withdraw(Credentials credentials, String nodeId, String nodeName, String blockNum, String withdrawAmount, String type) {
        GasProvider gasProvider = new ContractGasProvider(gasPrice, gasLimit);
        DelegateManager.getInstance().withdraw(credentials, ContractAddress.DELEGATE_CONTRACT_ADDRESS, nodeId, nodeName, feeAmount, blockNum, withdrawAmount, String.valueOf(TransactionType.UNDELEGATE.getTxTypeValue()), gasProvider)
                .compose(RxUtils.getSchedulerTransformer())
                .compose(RxUtils.getLoadingTransformer(currentActivity()))
                .subscribe(new CustomObserver<Transaction>() {
                    @Override
                    public void accept(Transaction transaction) {
                        if (isViewAttached()) {
                            //操作成功，跳转到交易详情，当前页面关闭
                            if (TextUtils.equals(type, WithDrawPopWindowAdapter.TAG_DELEGATED)) {
                                getView().withDrawSuccessInfo(transaction);
                            } else {
                                if (tag == list.size()) {
                                    getView().withDrawSuccessInfo(transaction);
                                }
                            }
                        }
                    }

                    @Override
                    public void accept(Throwable throwable) {
                        super.accept(throwable);
                        if (isViewAttached()) {
                            if (isViewAttached()) {
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


    private void showTransactionAuthorizationDialogFragment(String nodeId, String nodeName, String transactionAmount, String from, String to, String gasLimit, String gasPrice, String type) {

        Observable
                .fromCallable(new Callable<BigInteger>() {
                    @Override
                    public BigInteger call() throws Exception {
                        return Web3jManager.getInstance().getNonce(from);
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
                            if (!TextUtils.isEmpty(throwable.getMessage())) {
                                ToastUtil.showLongToast(currentActivity(), throwable.getMessage());
                            }
                        }
                    }
                });
    }

}
