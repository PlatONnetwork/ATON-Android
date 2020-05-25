package com.platon.aton.component.ui.presenter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;

import com.platon.aton.BuildConfig;
import com.platon.aton.R;
import com.platon.aton.app.CustomThrowable;
import com.platon.aton.app.LoadingTransformer;
import com.platon.aton.component.ui.contract.SendTransationContract;
import com.platon.aton.component.ui.dialog.CommonTipsDialogFragment;
import com.platon.aton.component.ui.dialog.InputWalletPasswordDialogFragment;
import com.platon.aton.component.ui.dialog.OnDialogViewClickListener;
import com.platon.aton.component.ui.dialog.SendTransactionDialogFragment;
import com.platon.aton.component.ui.dialog.TransactionAuthorizationDialogFragment;
import com.platon.aton.component.ui.dialog.TransactionSignatureDialogFragment;
import com.platon.aton.component.ui.view.MainActivity;
import com.platon.aton.db.entity.AddressEntity;
import com.platon.aton.db.entity.TransactionRecordEntity;
import com.platon.aton.db.sqlite.AddressDao;
import com.platon.aton.db.sqlite.TransactionRecordDao;
import com.platon.aton.engine.AppConfigManager;
import com.platon.aton.engine.NodeManager;
import com.platon.aton.engine.ServerUtils;
import com.platon.aton.engine.TransactionManager;
import com.platon.aton.engine.WalletManager;
import com.platon.aton.engine.Web3jManager;
import com.platon.aton.entity.AccountBalance;
import com.platon.aton.entity.AddressMatchingResultType;
import com.platon.aton.entity.EstimateGasResult;
import com.platon.aton.entity.GasProvider;
import com.platon.aton.entity.RPCErrorCode;
import com.platon.aton.entity.Transaction;
import com.platon.aton.entity.TransactionAuthorizationBaseData;
import com.platon.aton.entity.TransactionAuthorizationData;
import com.platon.aton.entity.Wallet;
import com.platon.aton.utils.AddressFormatUtil;
import com.platon.aton.utils.AmountUtil;
import com.platon.aton.utils.BigDecimalUtil;
import com.platon.aton.utils.BigIntegerUtil;
import com.platon.aton.utils.DateUtil;
import com.platon.aton.utils.JZWalletUtil;
import com.platon.aton.utils.NumberParserUtils;
import com.platon.aton.utils.RxUtils;
import com.platon.aton.utils.StringUtil;
import com.platon.framework.app.Constants;
import com.platon.framework.base.BasePresenter;
import com.platon.framework.network.ApiRequestBody;
import com.platon.framework.network.ApiResponse;
import com.platon.framework.network.ApiSingleObserver;
import com.platon.framework.network.NetConnectivity;
import com.platon.framework.utils.LogUtils;
import com.platon.framework.utils.PreferenceTool;
import com.trello.rxlifecycle2.android.FragmentEvent;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.platon.FunctionType;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;


/**
 * @author matrixelement
 */
public class SendTransactionPresenter extends BasePresenter<SendTransationContract.View> implements SendTransationContract.Presenter {

    private final static int DEFAULT_PERCENT = 0;
    private final static BigInteger DEFAULT_EXCHANGE_RATE = BigInteger.valueOf(1000000000000000000L);
    /**
     * 默认gasLimit
     */
    private final static BigInteger DEFAULT_GAS_LIMIT = DefaultGasProvider.GAS_LIMIT;
    /**
     * 默认最小gasPrice
     */
    private final static BigInteger DEFAULT_MIN_GASPRICE = new BigInteger(AppConfigManager.getInstance().getMinGasPrice());
    /**
     * 当前gasLimit
     */
    private BigInteger gasLimit = DEFAULT_GAS_LIMIT;
    /**
     * 最小值gasPrice
     */
    private BigInteger minGasPrice = DEFAULT_MIN_GASPRICE;
    /**
     * 最高gasPrice
     */
    private BigInteger maxGasPrice = DEFAULT_MIN_GASPRICE.multiply(BigInteger.valueOf(6));
    /**
     * 当前gasPrice
     */
    private BigInteger gasPrice;
    /**
     * 当前滑动百分比
     */
    private float progress = DEFAULT_PERCENT;
    /**
     * 当前nonce
     */
    private BigInteger nonce;

    private String feeAmount;
    //刷新时间
    private Wallet walletEntity;
    private String toAddress;
    //钱包余额
    private String balance = "";

    @Override
    public void init() {
        if (isViewAttached()) {
            if (!TextUtils.isEmpty(toAddress)) {
                getView().setToAddress(toAddress);
            }
        }
    }

    public String getSenderAddress() {
        return walletEntity != null ? walletEntity.getPrefixAddress() : "";
    }

    @Override
    public void fetchDefaultWalletInfo() {
        walletEntity = WalletManager.getInstance().getSelectedWallet();
        if (walletEntity == null) {
            return;
        }

        getAccountBalance(walletEntity);
        getEstimateGas(walletEntity);
        //getGasPrice();
    }

    private void getAccountBalance(Wallet wallet) {

        ServerUtils
                .getCommonApi()
                .getAccountBalance(ApiRequestBody.newBuilder()
                        .put("addrs", Arrays.asList(wallet.getPrefixAddress()))
                        .build())
                .compose(bindUntilEvent(FragmentEvent.STOP))
                .compose(RxUtils.getSingleSchedulerTransformer())
                .subscribe(new ApiSingleObserver<List<AccountBalance>>() {

                    @Override
                    public void onApiSuccess(List<AccountBalance> accountBalances) {
                        if (isViewAttached() && accountBalances != null && !accountBalances.isEmpty()) {
                            //String balance = accountBalances.get(0).getShowFreeBalace();
                            balance = accountBalances.get(0).getFree();
                            String balanceNew = AmountUtil.getPrettyBalance(balance, 8);
                            getView().updateWalletBalance(balanceNew);
                        }
                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {

                    }
                });
    }

    public void getEstimateGas(Wallet mWallet) {

        ServerUtils.getCommonApi().estimateGas(ApiRequestBody.newBuilder()
                .put("from", mWallet.getPrefixAddress())
                .put("txType", FunctionType.TRANSFER)
                .build())
                .compose(RxUtils.bindToLifecycle(getView()))
                .compose(RxUtils.getSingleSchedulerTransformer())
                .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                .subscribe(new ApiSingleObserver<EstimateGasResult>() {
                    @Override
                    public void onApiSuccess(EstimateGasResult gasProvider) {
                        if (isViewAttached()) {
                            GasProvider mGasProvider = gasProvider.getGasProvider();
                            gasLimit = BigIntegerUtil.toBigInteger(gasProvider.getGasLimit());
                            nonce = BigIntegerUtil.toBigInteger(gasProvider.getNonce());
                            initGasPrice(BigIntegerUtil.toBigInteger(mGasProvider.getGasPrice()));
                            setDefaultProgress();
                            setDefaultFeeAmount();
                            setDefaultGasLimit();

                           // getView().showFeeAmount(BigIntegerUtil.mul(mGasProvider.getGasLimit(), mGasProvider.getGasPrice()));
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


    private Disposable getGasPrice() {
        return Web3jManager
                .getInstance()
                .getGasPrice()
                .compose(RxUtils.getSingleSchedulerTransformer())
                .subscribe(new Consumer<BigInteger>() {
                    @Override
                    public void accept(BigInteger bigInteger) throws Exception {
                        if (isViewAttached()) {
                            initGasPrice(bigInteger);
                            setDefaultProgress();
                            setDefaultFeeAmount();
                            setDefaultGasLimit();
                        }
                    }
                });
    }


    @Override
    public void transferAllBalance() {
        if (isViewAttached() && walletEntity != null) {
            if (BigDecimalUtil.isBigger(String.valueOf(feeAmount), BigDecimalUtil.div(walletEntity.getFreeBalance(), DEFAULT_EXCHANGE_RATE.toString(10)))) {
                getView().setTransferAmount(0D);
            } else {
                getView().setTransferAmount(BigDecimalUtil.sub(NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(walletEntity.getFreeBalance(), DEFAULT_EXCHANGE_RATE.toString(10))), String.valueOf(feeAmount)).doubleValue());
            }
        }
    }

    @Override
    public void calculateFee() {
        String toAddress = getView().getToAddress();
        String address = walletEntity.getPrefixAddress();
        if (TextUtils.isEmpty(toAddress) || walletEntity == null || TextUtils.isEmpty(address)) {
            return;
        }
        updateFeeAmount(progress);
    }

    @Override
    public void calculateFeeAndTime(float progress) {
        updateGasPrice(progress);
        updateFeeAmount(progress);
    }

    @Override
    public boolean checkToAddress(String toAddress) {
        String errMsg = null;
        if (TextUtils.isEmpty(toAddress)) {
            errMsg = string(R.string.address_cannot_be_empty);
        }else if(!JZWalletUtil.isValidAddress(toAddress)){
            errMsg = string(R.string.receive_address_error);
        }else {
            int result = JZWalletUtil.isValidAddressMatchingNet(toAddress);
            if(result == AddressMatchingResultType.ADDRESS_MAINNET_MATCHING){

            }else if(result == AddressMatchingResultType.ADDRESS_MAINNET_MISMATCHING){

               errMsg = string(R.string.receive_address_match_mainnet_error);
            }else if(result == AddressMatchingResultType.ADDRESS_TESTNET_MATCHING){


            }else if(result == AddressMatchingResultType.ADDRESS_TESTNET_MISMATCHING){

               errMsg = string(R.string.receive_address_match_testnet_error);
            }
        }
        getView().showToAddressError(errMsg);
        return TextUtils.isEmpty(errMsg);
    }


    public boolean checkToAddressNotSelf(String toAddress,String address){

        if (toAddress.equalsIgnoreCase(address)) {
            showLongToast(R.string.can_not_send_to_itself);
            return false;
        }
        return true;
    }

    @Override
    public boolean checkTransferAmount(String transferAmount) {

        String errMsg = null;

        if (TextUtils.isEmpty(transferAmount)) {
            errMsg = string(R.string.transfer_amount_cannot_be_empty);
        } else {
            if (!isBalanceEnough(transferAmount)) {
                errMsg = string(R.string.insufficient_balance);
            }
        }

        getView().showAmountError(errMsg);

        return TextUtils.isEmpty(errMsg);
    }

    public void checkTransferAmountDueToGasLimitChanged(String transferAmount) {
        getView().showAmountError(!TextUtils.isEmpty(transferAmount) && !isBalanceEnough(transferAmount) ? string(R.string.insufficient_balance) : "");
    }

    @Override
    public void submit() {
        if (isViewAttached()) {
            if (!NetConnectivity.getConnectivityManager().isConnected()) {
                showLongToast(R.string.network_error);
                return;
            }
            String transferAmount = getView().getTransferAmount();
            String toAddress = getView().getToAddress();
            if (!checkToAddress(toAddress)) {
                return;
            }
            if (!checkTransferAmount(transferAmount)) {
                return;
            }
            String address = walletEntity.getPrefixAddress();
            if(!checkToAddressNotSelf(toAddress,address)){
                return;
            }

            String remark = getView().getTransactionRemark();

            long currentTime = System.currentTimeMillis();

            if (!TransactionManager.getInstance().isAllowSendTransaction(address, currentTime)) {
                showLongToast(string(R.string.msg_wait_finished_transaction_tips, DateUtil.millisecondToMinutes(TransactionManager.getInstance().getSendTransactionTimeInterval(address, currentTime))));
                return;
            }

            if (BigDecimalUtil.isNotSmaller(transferAmount, PreferenceTool.getString(Constants.Preference.KEY_REMINDER_THRESHOLD_AMOUNT, "1000"))) {
                CommonTipsDialogFragment.createDialogWithTwoButton(ContextCompat.getDrawable(currentActivity(), R.drawable.icon_dialog_tips),
                        string(R.string.msg_large_transaction_reminder, StringUtil.formatBalanceWithoutMinFraction(PreferenceTool.getString(Constants.Preference.KEY_REMINDER_THRESHOLD_AMOUNT, "1000"))),
                        string(R.string.confirm), new OnDialogViewClickListener() {
                            @Override
                            public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                                sendTransaction(toAddress, transferAmount, remark);
                            }
                        }, string(R.string.cancel), new OnDialogViewClickListener() {
                            @Override
                            public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                                fragment.dismiss();
                            }
                        }).show(currentActivity().getSupportFragmentManager(), "showLargeTransactionReminderDialogFragment");
            } else {
                sendTransaction(toAddress, transferAmount, remark);
            }

        }
    }

    @Override
    public void updateSendTransactionButtonStatus() {
        if (isViewAttached()) {
            String transferAmount = getView().getTransferAmount();
            String toAddress = getView().getToAddress();
            String gasLimit = getView().getGasLimit();

            boolean isToAddressFormatCorrect = !TextUtils.isEmpty(toAddress) && JZWalletUtil.isValidAddress(toAddress);
            boolean isTransferAmountValid = !TextUtils.isEmpty(transferAmount) && NumberParserUtils.parseDouble(transferAmount) > 0 && isBalanceEnough(transferAmount);
            boolean isGasLimitValid = BigIntegerUtil.toBigInteger(gasLimit).compareTo(DEFAULT_GAS_LIMIT) >= 0;
            boolean isToAddressMatchNet = false;
            int result = JZWalletUtil.isValidAddressMatchingNet(toAddress);
            if(result == AddressMatchingResultType.ADDRESS_MAINNET_MATCHING){
                isToAddressMatchNet = true;
            }else if(result == AddressMatchingResultType.ADDRESS_MAINNET_MISMATCHING){
                isToAddressMatchNet = false;
            }else if(result == AddressMatchingResultType.ADDRESS_TESTNET_MATCHING){
                isToAddressMatchNet = true;
            }else if(result == AddressMatchingResultType.ADDRESS_TESTNET_MISMATCHING){
                isToAddressMatchNet = false;
            }
            getView().setSendTransactionButtonEnable(isToAddressFormatCorrect && isToAddressMatchNet && isTransferAmountValid && isGasLimitValid);
        }

    }

    @Override
    public void saveWallet(String name, String address) {
        String[] avatarArray = getContext().getResources().getStringArray(R.array.wallet_avatar);
        String avatar = avatarArray[new Random().nextInt(avatarArray.length)];
        boolean success = AddressDao.insertAddressInfo(new AddressEntity(UUID.randomUUID().toString(), address, name, avatar));
        getView().setSaveAddressButtonEnable(!success);
        if (success) {
            showLongToast(string(R.string.save_successfully));
        }
    }

    @Override
    public void updateAssetsTab(int tabIndex) {
        if (isViewAttached()) {
//            if (tabIndex != AssetsFragment.MainTab.SEND_TRANSACTION) {
//                getView().resetView(feeAmount);
//            } else {
//                getGasPrice();
//            }
        }
    }

    @Override
    public void setGasLimit(String gasLimit) {
        this.gasLimit = BigIntegerUtil.toBigInteger(gasLimit);
        feeAmount = NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(BigIntegerUtil.mul(this.gasLimit, this.gasPrice), DEFAULT_EXCHANGE_RATE.toString(10)));
        boolean isGasLimitValid = BigIntegerUtil.toBigInteger(gasLimit).compareTo(DEFAULT_GAS_LIMIT) >= 0;
        if (isViewAttached()) {
            getView().setTransferFeeAmount(feeAmount);
            getView().showGasLimitError(!isGasLimitValid);
            checkTransferAmountDueToGasLimitChanged(getView().getTransferAmount());
            updateSendTransactionButtonStatus();
        }
    }

    @Override
    public void checkAddressBook(String address) {
        //保存地址到地址簿
     /*   if (TextUtils.isEmpty(address)) {
            getView().setSaveAddressButtonEnable(false);
            return;
        }
        if (JZWalletUtil.isValidAddress(address)) {
            getView().setSaveAddressButtonEnable(!AddressDao.isExist(address));
        } else {
            getView().setSaveAddressButtonEnable(false);
        }*/
    }

    @SuppressLint("CheckResult")
    private void sendTransaction(String toAddress, String transferAmount, String remark) {

        TransactionRecordEntity transactionRecordEntity = new TransactionRecordEntity(System.currentTimeMillis(), walletEntity.getPrefixAddress(), toAddress, transferAmount, NodeManager.getInstance().getChainId());

        if (PreferenceTool.getBoolean(Constants.Preference.KEY_RESEND_REMINDER, true)) {
            Single
                    .fromCallable(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return TransactionRecordDao.isResendTransaction(transactionRecordEntity);
                        }
                    })
                    .compose(RxUtils.getSingleSchedulerTransformer())
                    .compose(bindToLifecycle())
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            if (aBoolean) {
                                showResendTransactionReminderDialogFragment(transactionRecordEntity, remark);
                            } else {
                                showTransactionInfoDialogFragment(transactionRecordEntity, remark);
                            }
                        }
                    });
        } else {
            showTransactionInfoDialogFragment(transactionRecordEntity, remark);
        }

    }

    private void showResendTransactionReminderDialogFragment(TransactionRecordEntity transactionRecordEntity, String remark) {

        CommonTipsDialogFragment.createDialogWithTwoButton(ContextCompat.getDrawable(currentActivity(), R.drawable.icon_dialog_tips),
                string(R.string.msg_resend_transaction_reminder, StringUtil.formatBalanceWithoutMinFraction(PreferenceTool.getString(Constants.Preference.KEY_REMINDER_THRESHOLD_AMOUNT, "1000"))),
                string(R.string.confirm), new OnDialogViewClickListener() {
                    @Override
                    public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                        showTransactionInfoDialogFragment(transactionRecordEntity, remark);
                    }
                }, string(R.string.cancel), new OnDialogViewClickListener() {
                    @Override
                    public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                        fragment.dismiss();
                    }
                }).show(currentActivity().getSupportFragmentManager(), "showResendTransactionReminderDialogFragment");
    }

    @SuppressLint("CheckResult")
    private void showTransactionInfoDialogFragment(TransactionRecordEntity transactionRecordEntity, String remark) {

        String fromWallet = String.format("%s(%s)", walletEntity.getName(), AddressFormatUtil.formatTransactionAddress(walletEntity.getPrefixAddress()));
        String fee = NumberParserUtils.getPrettyBalance(feeAmount);

        getWalletNameFromAddress(transactionRecordEntity.getTo())
                .compose(RxUtils.getSingleSchedulerTransformer())
                .compose(bindToLifecycle())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        if (isViewAttached()) {
                            SendTransactionDialogFragment
                                    .newInstance(string(R.string.send_transaction), NumberParserUtils.getPrettyBalance(transactionRecordEntity.getValue()), buildSendTransactionInfo(fromWallet, s, fee))
                                    .setOnConfirmBtnClickListener(new SendTransactionDialogFragment.OnConfirmBtnClickListener() {
                                        @Override
                                        public void onConfirmBtnClick() {
                                            if (WalletManager.getInstance().getSelectedWallet().isObservedWallet()) {
                                                showTransactionAuthorizationDialogFragment(transactionRecordEntity, gasLimit.toString(10), gasPrice.toString(10),nonce, remark);
                                            } else {
                                                showInputWalletPasswordDialogFragment(transactionRecordEntity, feeAmount, remark);
                                            }
                                        }
                                    })
                                    .show(currentActivity().getSupportFragmentManager(), "sendContractTransaction");
                        }
                    }
                });
    }

    /**
     * 根据当前gasPrice获取当前进度
     *
     * @return
     */
    private float getDefaultProgress() {
        String progress = BigDecimalUtil.div(gasPrice.subtract(minGasPrice).toString(10), maxGasPrice.subtract(minGasPrice).toString(10));
        return BigDecimalUtil.convertsToFloat(progress);
    }


    /**
     * gasPrice为当前的gasPrice，默认为推荐值(链上获取值的二分之一)
     * 如果推荐值的二分之一小于最小值，则推荐值为最小值，否则为推荐值
     *
     * @param recommendedGasPrice
     */
    private void initGasPrice(BigInteger recommendedGasPrice) {

        BigInteger recommendedMinGasPrice = recommendedGasPrice.divide(BigInteger.valueOf(2));
        //推荐的最小值是否大于默认的最小值
        boolean isRecommendedMinGasPriceBiggerThanDefaultMinGasPrice = recommendedMinGasPrice.compareTo(DEFAULT_MIN_GASPRICE) > 0;
        //如果推荐的最小值大于默认的最小值，则最小值取推荐的最小值，否则为默认的最小值
        minGasPrice = isRecommendedMinGasPriceBiggerThanDefaultMinGasPrice ? recommendedMinGasPrice : DEFAULT_MIN_GASPRICE;
        //如果推荐的最小值大于默认的最小值，则推荐值取推荐值，否则取默认的最小值
        gasPrice = isRecommendedMinGasPriceBiggerThanDefaultMinGasPrice ? recommendedGasPrice : DEFAULT_MIN_GASPRICE;
        //最大值为最小值的6倍
        maxGasPrice = gasPrice.multiply(BigInteger.valueOf(6));
    }

    /**
     * 最大值与最小值的差值
     *
     * @return
     */
    private BigInteger getDGasPrice() {
        return maxGasPrice.subtract(minGasPrice);
    }

    /**
     * 获取手续费的中间差值
     *
     * @return
     */
    private BigInteger getDFeeAmount() {
        return getMaxFeeAmount().subtract(getMinFeeAmount());
    }

    /**
     * 获取最小的手续费
     *
     * @return
     */
    private BigInteger getMinFeeAmount() {
        return minGasPrice.multiply(gasLimit);
    }

    /**
     * 获取最大的手续费
     *
     * @return
     */
    private BigInteger getMaxFeeAmount() {
        return maxGasPrice.multiply(gasLimit);
    }

    @SuppressLint("CheckResult")
    private void sendTransaction(TransactionRecordEntity transactionRecordEntity, ECKeyPair ecKeyPair, String feeAmount, String remark) {

        try{
            TransactionManager
                    .getInstance()
                    .sendTransferTransaction(ecKeyPair, transactionRecordEntity.getFrom(), transactionRecordEntity.getTo(), walletEntity.getName(), Convert.toVon(transactionRecordEntity.getValue(), Convert.Unit.LAT), Convert.toVon(feeAmount, Convert.Unit.LAT), gasPrice, gasLimit,nonce, remark)
                    .compose(RxUtils.getSingleSchedulerTransformer())
                    .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                    .compose(bindToLifecycle())
                    .subscribe(new Consumer<Transaction>() {
                        @Override
                        public void accept(Transaction transaction) {
                            if (isViewAttached()) {
                                insertAndDeleteTransactionRecord(transactionRecordEntity);
                                backToTransactionListWithDelay();
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) {
                            if (isViewAttached()) {
                                if (throwable instanceof CustomThrowable) {
                                    CustomThrowable customThrowable = (CustomThrowable) throwable;
                                    if (customThrowable.getErrCode() == RPCErrorCode.CONNECT_TIMEOUT) {
                                        showLongToast(R.string.msg_connect_timeout);
                                    } else if (customThrowable.getErrCode() == CustomThrowable.CODE_TX_NONCE_TOO_LOW  ||
                                            customThrowable.getErrCode() == CustomThrowable.CODE_TX_GAS_LOW){
                                        if(WalletManager.getInstance().getSelectedWallet().isObservedWallet()){
                                            showLongToast(R.string.msg_expired_qr_code);
                                        }else{
                                            showLongToast(string(R.string.msg_transaction_exception, customThrowable.getErrCode()));
                                        }
                                    } else {
                                        //showLongToast(string(R.string.msg_server_exception, customThrowable.getErrCode()));
                                        showLongToast(customThrowable.getDetailMsgRes());
                                    }
                                    // refresh estimate gas
                                    getEstimateGas(WalletManager.getInstance().getSelectedWallet());
                                }
                            }
                        }
                    });

        }catch (Exception e){
            showLongToast(R.string.failed);
            LogUtils.e("tag",e.fillInStackTrace());
        }

    }

    /**
     * 延迟指定时间后返回交易列表页
     */
    @SuppressLint("CheckResult")
    private void backToTransactionListWithDelay() {
        Single
                .timer(1000, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        if (isViewAttached()) {
                            getView().resetView(feeAmount);
                            MainActivity.actionStart(getContext(), MainActivity.MainTab.TAB_ASSETS);
                        }
                    }
                });
    }

    private Single<Credentials> checkBalance(Wallet walletEntity, Credentials credentials, BigInteger gasPrice) {

        return Single.create(new SingleOnSubscribe<Credentials>() {
            @Override
            public void subscribe(SingleEmitter<Credentials> emitter) throws Exception {
                double balance = Web3jManager.getInstance().getBalance(walletEntity.getPrefixAddress());
                if (balance < NumberParserUtils.parseDouble(feeAmount)) {
                    emitter.onError(new CustomThrowable(CustomThrowable.CODE_ERROR_NOT_SUFFICIENT_BALANCE));
                } else {
                    emitter.onSuccess(credentials);
                }
            }
        });
    }


    private void showInputWalletPasswordDialogFragment(TransactionRecordEntity transactionRecordEntity, String feeAmount, String remark) {
        InputWalletPasswordDialogFragment.newInstance(walletEntity).setOnWalletPasswordCorrectListener(new InputWalletPasswordDialogFragment.OnWalletPasswordCorrectListener() {
            @Override
            public void onWalletPasswordCorrect(Credentials credentials) {
                sendTransaction(transactionRecordEntity, credentials.getEcKeyPair(), feeAmount, remark);
            }
        }).show(currentActivity().getSupportFragmentManager(), "inputPassword");
    }

    private void showTransactionAuthorizationDialogFragment(TransactionRecordEntity transactionRecordEntity, String gasLimit, String gasPrice,BigInteger nonce, String remark) {

        if (isViewAttached()) {
            TransactionAuthorizationData transactionAuthorizationData = new TransactionAuthorizationData(Arrays.asList(new TransactionAuthorizationBaseData.Builder(FunctionType.TRANSFER)
                    .setAmount(BigDecimalUtil.mul(transactionRecordEntity.getValue(), "1E18").toPlainString())
                    .setChainId(NodeManager.getInstance().getChainId())
                    .setNonce(nonce.toString(10))
                    .setFrom(transactionRecordEntity.getFrom())
                    .setTo(transactionRecordEntity.getTo())
                    .setGasLimit(gasLimit)
                    .setGasPrice(gasPrice)
                    .setRemark(remark)
                    .build()), transactionRecordEntity.getTimeStamp() / 1000, BuildConfig.QRCODE_VERSION_CODE);
            //(观察钱包)冷钱包确认
            TransactionAuthorizationDialogFragment.newInstance(transactionAuthorizationData)
                    .setOnNextBtnClickListener(new TransactionAuthorizationDialogFragment.OnNextBtnClickListener() {
                        @Override
                        public void onNextBtnClick() {
                           //(观察钱包)读取授权签名数据
                            TransactionSignatureDialogFragment.newInstance(transactionAuthorizationData)
                                    .setOnSendTransactionSucceedListener(new TransactionSignatureDialogFragment.OnSendTransactionSucceedListener() {
                                        @Override
                                        public void onSendTransactionSucceed(Transaction transaction) {
                                            if (isViewAttached()) {
                                                insertAndDeleteTransactionRecord(transactionRecordEntity);
                                                backToTransactionListWithDelay();
                                            }
                                        }
                                    })
                                    .show(currentActivity().getSupportFragmentManager(), TransactionSignatureDialogFragment.TAG);
                        }
                    })
                    .show(currentActivity().getSupportFragmentManager(), "showTransactionAuthorizationDialog");
        }


       /* TransactionManager.getInstance().getNonce(transactionRecordEntity.getFrom())
                .toObservable()
                .compose(RxUtils.getSchedulerTransformer())
                .compose(bindToLifecycle())
                .compose(RxUtils.getLoadingTransformer(currentActivity()))
                .subscribe(new CustomObserver<BigInteger>() {
                    @Override
                    public void accept(BigInteger nonce) {
                        if (isViewAttached()) {
                            TransactionAuthorizationData transactionAuthorizationData = new TransactionAuthorizationData(Arrays.asList(new TransactionAuthorizationBaseData.Builder(FunctionType.TRANSFER)
                                    .setAmount(BigDecimalUtil.mul(transactionRecordEntity.getValue(), "1E18").toPlainString())
                                    .setChainId(NodeManager.getInstance().getChainId())
                                    .setNonce(nonce.toString(10))
                                    .setFrom(transactionRecordEntity.getFrom())
                                    .setTo(transactionRecordEntity.getTo())
                                    .setGasLimit(gasLimit)
                                    .setGasPrice(gasPrice)
                                    .setRemark(remark)
                                    .build()), transactionRecordEntity.getTimeStamp() / 1000, BuildConfig.QRCODE_VERSION_CODE);
                            TransactionAuthorizationDialogFragment.newInstance(transactionAuthorizationData)
                                    .setOnNextBtnClickListener(new TransactionAuthorizationDialogFragment.OnNextBtnClickListener() {
                                        @Override
                                        public void onNextBtnClick() {
                                            TransactionSignatureDialogFragment.newInstance(transactionAuthorizationData)
                                                    .setOnSendTransactionSucceedListener(new TransactionSignatureDialogFragment.OnSendTransactionSucceedListener() {
                                                        @Override
                                                        public void onSendTransactionSucceed(Transaction transaction) {
                                                            if (isViewAttached()) {
                                                                insertAndDeleteTransactionRecord(transactionRecordEntity);
                                                                backToTransactionListWithDelay();
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
                });*/
    }

    private void insertAndDeleteTransactionRecord(TransactionRecordEntity transactionRecordEntity) {
        if (PreferenceTool.getBoolean(Constants.Preference.KEY_RESEND_REMINDER, true)) {
            Single
                    .fromCallable(new Callable<Boolean>() {

                        @Override
                        public Boolean call() throws Exception {
                            return TransactionRecordDao.insertTransactionRecord(transactionRecordEntity) && TransactionRecordDao.deleteTimeoutTransactionRecord();
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .subscribe();
        }
    }

    private void updateFeeAmount(float progress) {

        BigInteger minFeeAmount = getMinFeeAmount();

        BigInteger dFeeAmount = getDFeeAmount();

        BigDecimal subFeeAmount = BigDecimalUtil.mul(dFeeAmount.toString(10), String.valueOf(progress)).add(new BigDecimal(minFeeAmount.toString(10)));

        feeAmount = NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(subFeeAmount.toPlainString(), DEFAULT_EXCHANGE_RATE.toString(10)));

        if (isViewAttached()) {
            getView().setTransferFeeAmount(feeAmount);
        }
    }

    private void setDefaultProgress() {
        progress = getDefaultProgress();
        if (isViewAttached()) {
            getView().setProgress(progress);
        }
    }

    private void setDefaultFeeAmount() {

        feeAmount = NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(gasPrice.multiply(gasLimit).toString(10), DEFAULT_EXCHANGE_RATE.toString(10)));

        if (isViewAttached()) {
            getView().setTransferFeeAmount(feeAmount);
        }
    }

    private void setDefaultGasLimit() {
        //this.gasLimit = DEFAULT_GAS_LIMIT;
        if (isViewAttached()) {
            getView().setGasLimit(gasLimit.toString(10));
        }
    }

    private void updateGasPrice(float progress) {
        gasPrice = minGasPrice.add(BigDecimalUtil.mul(getDGasPrice().toString(10), String.valueOf(progress)).toBigInteger());
    }


    private boolean isBalanceEnough(String transferAmount) {
        double usedAmount = BigDecimalUtil.add(transferAmount, feeAmount).doubleValue();
        if (!"".equals(balance) && balance != null) {
           // walletEntity.getFreeBalance()
            return BigDecimalUtil.isNotSmaller(balance, BigDecimalUtil.mul(String.valueOf(usedAmount), DEFAULT_EXCHANGE_RATE.toString(10)).toPlainString());
        }
        return false;
    }

    private Map<String, String> buildSendTransactionInfo(String fromWallet, String recipient, String fee) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put(String.format("%s:", string(R.string.txt_info)), string(R.string.send_energon));
        map.put(String.format("%s:", string(R.string.from_wallet)), fromWallet);
        map.put(String.format("%s:", string(R.string.recipient_address)), recipient);
        map.put(String.format("%s:", string(R.string.fee)), string(R.string.amount_with_unit, fee));
        return map;
    }

    private Single<String> getWalletNameFromAddress(String address) {
        return Single.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {

                String walletName = WalletManager.getInstance().getWalletNameByWalletAddress(address);
                return TextUtils.isEmpty(walletName) ? walletName : String.format("%s(%s)", walletName, AddressFormatUtil.formatTransactionAddress(address));
            }
        }).filter(new Predicate<String>() {
            @Override
            public boolean test(String s) throws Exception {
                return !TextUtils.isEmpty(s);
            }
        }).switchIfEmpty(new SingleSource<String>() {
            @Override
            public void subscribe(SingleObserver<? super String> observer) {
                String addressName = AddressDao.getAddressNameByAddress(address);
                observer.onSuccess(TextUtils.isEmpty(addressName) ? addressName : String.format("%s(%s)", addressName, AddressFormatUtil.formatTransactionAddress(address)));
            }
        }).filter(new Predicate<String>() {
            @Override
            public boolean test(String s) throws Exception {
                return !TextUtils.isEmpty(s);
            }
        }).defaultIfEmpty(address)
                .toSingle();
    }

}
