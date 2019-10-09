package com.juzix.wallet.component.ui.presenter;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import com.juzhen.framework.network.ApiRequestBody;
import com.juzhen.framework.network.ApiResponse;
import com.juzhen.framework.network.ApiSingleObserver;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.CustomObserver;
import com.juzix.wallet.app.CustomThrowable;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.DelegateContract;
import com.juzix.wallet.component.ui.dialog.InputWalletPasswordDialogFragment;
import com.juzix.wallet.component.ui.dialog.SelectWalletDialogFragment;
import com.juzix.wallet.component.ui.dialog.TransactionAuthorizationDialogFragment;
import com.juzix.wallet.component.ui.dialog.TransactionSignatureDialogFragment;
import com.juzix.wallet.config.AppSettings;
import com.juzix.wallet.engine.DelegateManager;
import com.juzix.wallet.engine.NodeManager;
import com.juzix.wallet.engine.ServerUtils;
import com.juzix.wallet.engine.WalletManager;
import com.juzix.wallet.engine.Web3jManager;
import com.juzix.wallet.entity.AccountBalance;
import com.juzix.wallet.entity.DelegateHandle;
import com.juzix.wallet.entity.TransactionAuthorizationBaseData;
import com.juzix.wallet.entity.TransactionAuthorizationData;
import com.juzix.wallet.entity.TransactionAuthorizationDelegateData;
import com.juzix.wallet.entity.TransactionStatus;
import com.juzix.wallet.entity.Wallet;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.RxUtils;
import com.juzix.wallet.utils.ToastUtil;

import org.web3j.crypto.Credentials;
import org.web3j.platon.ContractAddress;
import org.web3j.platon.FunctionType;
import org.web3j.platon.StakingAmountType;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.PlatonSendTransaction;
import org.web3j.tx.PlatOnContract;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.GasProvider;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IllegalFormatCodePointException;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import retrofit2.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DelegatePresenter extends BasePresenter<DelegateContract.View> implements DelegateContract.Presenter {
    private Wallet mWallet;
    private String mNodeAddress;
    private String mNodeName;
    private String mNodeIcon;
    private int tag;//获取是从哪个页面跳转到委托页
    private String feeAmount;
    private String mWalletAddress;
    private List<String> walletAddressList = new ArrayList<>();
    private BigInteger gas_Price; //调web3j获取gasprice
    private BigInteger gas_limit;
    private boolean isAll = false;//是否点击全部

    public DelegatePresenter(DelegateContract.View view) {
        super(view);
        mNodeAddress = view.getNodeAddressFromIntent();
        mNodeName = view.getNodeNameFromIntent();
        mNodeIcon = view.getNodeIconFromIntent();
        mWalletAddress = view.getWalletAddressFromIntent();
        tag = view.getJumpTagFromIntent();
        AppSettings.getInstance().setFromDelegateOrValidators(String.valueOf(tag));
    }

    @Override
    public void showSelectWalletDialogFragment() {
        SelectWalletDialogFragment.newInstance(mWallet != null ? mWallet.getUuid() : "", true)
                .setOnItemClickListener(new SelectWalletDialogFragment.OnItemClickListener() {
                    @Override
                    public void onItemClick(Wallet walletEntity) {
                        if (isViewAttached()) {
                            mWallet = walletEntity;
                            getView().showSelectedWalletInfo(walletEntity);
                            getBalanceByWalletAddress(walletEntity.getPrefixAddress());//获取钱包余额信息
                        }
                    }
                })
                .show(currentActivity().getSupportFragmentManager(), "showSelectWalletDialog");

    }

    @Override
    public void showWalletInfo() {
        getView().showNodeInfo(mNodeAddress, mNodeName, mNodeIcon);
        showDefaultWalletInfo();
        getWalletBalance();
    }

    @Override
    public String checkDelegateAmount(String delegateAmount) {
        double amount = NumberParserUtils.parseDouble(delegateAmount);
        //检查委托的数量
        String errMsg = null;
        if (TextUtils.isEmpty(delegateAmount)) {
            errMsg = string(R.string.transfer_amount_cannot_be_empty);
        } else if (amount < 10) {
            //按钮不可点击,并且下方提示
            getView().showTips(true);
            updateDelegateButtonState();
        } else {
            getView().showTips(false);
            updateDelegateButtonState();
        }
        return delegateAmount;

    }

    @Override
    public void updateDelegateButtonState() {
        if (isViewAttached()) {
            String withdrawAmount = getView().getDelegateAmount(); //获取输入的委托数量
            boolean isAmountValid = !TextUtils.isEmpty(withdrawAmount) && NumberParserUtils.parseDouble(withdrawAmount) >= 10;
            getView().setDelegateButtonState(isAmountValid);
        }

    }

    private void showDefaultWalletInfo() { //这里如果从验证列表进来显示第一大于0的钱包，从委托详情进来，是哪个钱包就显示哪个钱包的信息
        if (!TextUtils.isEmpty(mWalletAddress)) {
            mWallet = WalletManager.getInstance().getWalletEntityByWalletAddress(mWalletAddress);
        } else {
            mWallet = WalletManager.getInstance().getFirstValidIndividualWalletBalance();
        }

//        mWallet = WalletManager.getInstance().getFirstValidIndividualWalletBalance();
        if (isViewAttached() && mWallet != null) {
            getView().showSelectedWalletInfo(mWallet);
        }
    }


    /**
     * 获取所有钱包的余额(可用余额+锁仓余额)
     */
    private void getWalletBalance() {
        walletAddressList.clear();
        if (!TextUtils.isEmpty(mWalletAddress)) {
            walletAddressList.add(mWalletAddress);
        } else {
            walletAddressList.addAll(WalletManager.getInstance().getAddressList());
        }

//        List<String> walletAddressList = WalletManager.getInstance().getAddressList();
        ServerUtils.getCommonApi().getAccountBalance(ApiRequestBody.newBuilder()
                .put("addrs", walletAddressList.toArray(new String[walletAddressList.size()]))
                .build())
                .compose(RxUtils.bindToLifecycle(getView()))
                .compose(RxUtils.getSingleSchedulerTransformer())
                .subscribe(new ApiSingleObserver<List<AccountBalance>>() {
                    @Override
                    public void onApiSuccess(List<AccountBalance> accountBalances) {
                        if (isViewAttached()) {
                            if (null != accountBalances && accountBalances.size() > 0) {
                                getView().getWalletBalanceList(accountBalances);
                            }

                        }
                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {

                    }
                });

    }


    //获取钱包余额信息根据选择的钱包地址
    private void getBalanceByWalletAddress(String address) {
        ServerUtils.getCommonApi().getAccountBalance(ApiRequestBody.newBuilder()
                .put("addrs", new String[]{address})
                .build())
                .compose(RxUtils.bindToLifecycle(getView()))
                .compose(RxUtils.getSingleSchedulerTransformer())
                .subscribe(new ApiSingleObserver<List<AccountBalance>>() {
                    @Override
                    public void onApiSuccess(List<AccountBalance> accountBalances) {
                        if (isViewAttached()) {
                            getView().getWalletBalanceList(accountBalances);
                        }
                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {

                    }
                });


    }

    /**
     * 检测是否可以委托
     */
    @Override
    public void checkIsCanDelegate() {
        if (mWallet == null) {
            return;
        }
        ServerUtils.getCommonApi().getIsDelegateInfo(ApiRequestBody.newBuilder()
                .put("addr", mWallet.getPrefixAddress())
                .put("nodeId", mNodeAddress)
                .build())
                .compose(RxUtils.bindToLifecycle(getView()))
                .compose(RxUtils.getSingleSchedulerTransformer())
                .subscribe(new ApiSingleObserver<DelegateHandle>() {
                    @Override
                    public void onApiSuccess(DelegateHandle delegateHandle) {
                        if (isViewAttached()) {
                            if (null != delegateHandle) {
                                getView().showIsCanDelegate(delegateHandle);
                            }

                        }

                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {

                    }
                });
    }

    @Override
    public void getGas() {
        DelegateManager.getInstance().getGasPrice()
                .compose(RxUtils.bindToLifecycle(getView()))
                .compose(RxUtils.getSingleSchedulerTransformer())
                .subscribe(new Consumer<BigInteger>() {
                    @Override
                    public void accept(BigInteger gasPrice) throws Exception {
                        if (isViewAttached()) {
                            gas_Price = gasPrice;
                            getView().showGas(gasPrice);
                        }
                    }
                });
    }

    //获取手续费
    @SuppressLint("CheckResult")
    @Override
    public void getGasPrice(String gasPrice, String chooseType) {
        String inputAmount = getView().getDelegateAmount();//输入的数量
        if (TextUtils.isEmpty(inputAmount) || TextUtils.equals(inputAmount, ".")) {
            getView().showGasPrice("0.00");
            return;
        } else {
            if (NumberParserUtils.parseDouble(inputAmount) < 0) {
                getView().showGasPrice("0.00");
                return;
            }
        }

        Web3j web3j = Web3jManager.getInstance().getWeb3j();
        org.web3j.platon.contracts.DelegateContract delegateContract = org.web3j.platon.contracts.DelegateContract.load(web3j);
        StakingAmountType stakingAmountType = TextUtils.equals(chooseType, "balance") ? StakingAmountType.FREE_AMOUNT_TYPE : StakingAmountType.RESTRICTING_AMOUNT_TYPE;
        if (!isAll) {
            Log.d("gasprovide", "============" + "表示不是点击的全部");
            delegateContract.getDelegateGasProvider(mNodeAddress, stakingAmountType, Convert.toVon(inputAmount, Convert.Unit.LAT).toBigInteger())
                    .subscribe(new Subscriber<GasProvider>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(GasProvider gasProvider) {
                            gas_limit = gasProvider.getGasLimit();
                            Log.d("gaslimit", "========getGasPrice========" + "limit ===" + gas_limit + "gasprice ======" + gas_Price);
                            BigDecimal mul = BigDecimalUtil.mul(gasProvider.getGasLimit().toString(), gas_Price.toString());
                            feeAmount = NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(mul.toString(), "1E18"));
                            getView().showGasPrice(feeAmount);
                        }
                    });
        } else {
            isAll = false;
            Log.d("gasprovide", "============" + "1111111111111表示点击的全部");
        }
    }


    //点击全部的时候，需要获取一次手续费
    public void getAllPrice(String gasPrice, String amount, String chooseType) {
        isAll = true;
        Web3j web3j = Web3jManager.getInstance().getWeb3j();
        org.web3j.platon.contracts.DelegateContract delegateContract = org.web3j.platon.contracts.DelegateContract.load(web3j);
        StakingAmountType stakingAmountType = TextUtils.equals(chooseType, "balance") ? StakingAmountType.FREE_AMOUNT_TYPE : StakingAmountType.RESTRICTING_AMOUNT_TYPE;

        delegateContract.getDelegateGasProvider(mNodeAddress, stakingAmountType, new BigInteger(Convert.toVon(amount, Convert.Unit.LAT).toBigInteger().toString().replaceAll("0", "1")))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GasProvider>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(GasProvider gasProvider) {
                        gas_limit = gasProvider.getGasLimit();
                        Log.d("gaslimit", "========getAllPrice========" + "limit" + gas_limit + "gasprice ========" + gas_Price);
                        Log.d("gasprovide", "========getAllPrice======" + gasProvider.getGasLimit());
                        BigDecimal mul = BigDecimalUtil.mul(gasProvider.getGasLimit().toString(), gas_Price.toString());
                        feeAmount = NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(mul.toString(), "1E18"));
                        getView().showAllGasPrice(feeAmount);
                    }
                });
    }

    @SuppressLint("CheckResult")
    @Override
    public void submitDelegate(String type) {

        Single.fromCallable(new Callable<Double>() {
            @Override
            public Double call() throws Exception {
                //输入数量+手续费
                return BigDecimalUtil.add(getView().getDelegateAmount(), getView().getGasPrice()).doubleValue();
            }
        }).zipWith(ServerUtils.getCommonApi().getIsDelegateInfo(ApiRequestBody.newBuilder()
                        .put("addr", mWallet.getPrefixAddress())
                        .put("nodeId", mNodeAddress)
                        .build()),
                new BiFunction<Double, Response<ApiResponse<DelegateHandle>>, String>() {
                    @Override
                    public String apply(Double amount, Response<ApiResponse<DelegateHandle>> apiResponseResponse) throws Exception {
                        if (apiResponseResponse == null || !apiResponseResponse.isSuccessful()) {
                            return "";
                        } else {
                            DelegateHandle data = apiResponseResponse.body().getData();
                            String isDelegate = "0"; //0 表示false,1 表示true
                            if (!data.isCanDelegation()) {
                                isDelegate = "0";
                            } else {
                                isDelegate = "1";
                            }

                            String message = data.getMessage();

                            return amount + "&" + isDelegate + "&" + message;
                        }

                    }
                })
                .compose(bindToLifecycle())
                .compose(RxUtils.getSingleSchedulerTransformer())
//                .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String result) throws Exception {
                        if (isViewAttached()) {

                            double amount = NumberParserUtils.parseDouble(result.split("&", 3)[0]);
                            String delegateState = result.split("&", 3)[1];
                            String msg = result.split("&", 3)[2];

                            double chooseAmount = NumberParserUtils.parseDouble(getView().getChooseBalance()); //选择的余额
                            if (chooseAmount < amount) {
                                //余额不足
                                showLongToast(R.string.insufficient_balance_unable_to_delegate);
                                return;
                            }

                            //不能委托
                            if (TextUtils.equals(delegateState, "0")) {
                                //表示不能委托
                                if (TextUtils.equals(msg, "1")) { //不能委托原因：解除委托金额大于0
                                    showLongToast(R.string.delegate_no_click);
                                    return;
                                } else { //节点已退出或退出中
                                    showLongToast(R.string.the_Validator_has_exited_and_cannot_be_delegated);
                                    return;
                                }
                            }

                            String inputAmount = getView().getDelegateAmount();

                            if (mWallet.isObservedWallet()) {
                                showTransactionAuthorizationDialogFragment(mNodeAddress, mNodeName, StakingAmountType.FREE_AMOUNT_TYPE.getValue(), inputAmount, mWallet.getPrefixAddress(), ContractAddress.DELEGATE_CONTRACT_ADDRESS, gas_limit.toString(10), gas_Price.toString(10));
                            } else {
                                showInputPasswordDialogFragment(inputAmount, type);
                            }
                        }


                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (isViewAttached()) {
                            if (throwable instanceof CustomThrowable) {
                                CustomThrowable customThrowable = (CustomThrowable) throwable;
                                showLongToast(customThrowable.getDetailMsgRes());
                            } else {
                                showLongToast(R.string.delegate_failed);
                            }
                        }
                    }
                });

    }


    @SuppressLint("CheckResult")
    private void delegate(Credentials credentials, String inputAmount, String address, String type) {
        //这里调用新的方法，传入GasProvider
        GasProvider gasProvider = new ContractGasProvider(gas_Price, gas_limit);
        DelegateManager.getInstance().delegate(credentials, inputAmount, address, type, gasProvider)
                .compose(RxUtils.getSingleSchedulerTransformer())
                .subscribe(new Consumer<PlatonSendTransaction>() {
                    @Override
                    public void accept(PlatonSendTransaction platonSendTransaction) throws Exception {
                        if (isViewAttached()) {
                            if (!TextUtils.isEmpty(platonSendTransaction.getTransactionHash())) {
                                getView().transactionSuccessInfo(platonSendTransaction.getTransactionHash() ,mWallet.getPrefixAddress(), ContractAddress.DELEGATE_CONTRACT_ADDRESS, String.valueOf(FunctionType.DELEGATE_FUNC_TYPE), inputAmount, feeAmount, mNodeName, mNodeAddress
                                        , TransactionStatus.PENDING.ordinal());
                            } else {
                                ToastUtil.showLongToast(getContext(), platonSendTransaction.getError().getMessage());
                            }

                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (isViewAttached()) {
                            showLongToast(R.string.delegate_failed);
                        }
                    }
                });

    }

    private void showInputPasswordDialogFragment(String inputAmount, String type) {
        InputWalletPasswordDialogFragment
                .newInstance(mWallet)
                .setOnWalletPasswordCorrectListener(new InputWalletPasswordDialogFragment.OnWalletPasswordCorrectListener() {
                    @Override
                    public void onWalletPasswordCorrect(Credentials credentials) {
                        delegate(credentials, inputAmount, mNodeAddress, type);
                    }
                })
                .show(currentActivity().getSupportFragmentManager(), "inputWalletPasssword");
    }

    private void showTransactionAuthorizationDialogFragment(String nodeId, String nodeName, int stakingAmountType, String transactionAmount, String from, String to, String gasLimit, String gasPrice) {

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
                            TransactionAuthorizationData transactionAuthorizationData = new TransactionAuthorizationData(Arrays.asList(new TransactionAuthorizationBaseData.Builder(FunctionType.DELEGATE_FUNC_TYPE)
                                    .setAmount(BigDecimalUtil.mul(transactionAmount,"1E18").toPlainString())
                                    .setChainId(NodeManager.getInstance().getChainId())
                                    .setNonce(nonce.toString(10))
                                    .setFrom(from)
                                    .setTo(to)
                                    .setGasLimit(gasLimit)
                                    .setGasPrice(gasPrice)
                                    .setNodeId(nodeId)
                                    .setNodeName(nodeName)
                                    .setStakingAmountType(stakingAmountType)
                                    .build()), System.currentTimeMillis() / 1000);
                            TransactionAuthorizationDialogFragment.newInstance(transactionAuthorizationData)
                                    .setOnNextBtnClickListener(new TransactionAuthorizationDialogFragment.OnNextBtnClickListener() {
                                        @Override
                                        public void onNextBtnClick() {
                                            TransactionSignatureDialogFragment.newInstance(transactionAuthorizationData.getTimeStamp())
                                                    .setOnSendTransactionSucceedListener(new TransactionSignatureDialogFragment.OnSendTransactionSucceedListener() {
                                                        @Override
                                                        public void onSendTransactionSucceed(String hash) {
                                                            if (isViewAttached()) {
                                                                getView().transactionSuccessInfo(hash ,mWallet.getPrefixAddress(), ContractAddress.DELEGATE_CONTRACT_ADDRESS,  String.valueOf(FunctionType.DELEGATE_FUNC_TYPE), transactionAmount, feeAmount, mNodeName, mNodeAddress
                                                                        , TransactionStatus.PENDING.ordinal());
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
