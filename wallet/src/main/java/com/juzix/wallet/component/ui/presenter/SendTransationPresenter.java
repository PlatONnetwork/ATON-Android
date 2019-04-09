package com.juzix.wallet.component.ui.presenter;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;

import com.juzhen.framework.network.NetConnectivity;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.CustomThrowable;
import com.juzix.wallet.app.LoadingTransformer;
import com.juzix.wallet.app.SchedulersTransformer;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.SendTransationContract;
import com.juzix.wallet.component.ui.dialog.CommonTipsDialogFragment;
import com.juzix.wallet.component.ui.dialog.InputWalletPasswordDialogFragment;
import com.juzix.wallet.component.ui.dialog.OnDialogViewClickListener;
import com.juzix.wallet.component.ui.dialog.SendTransactionDialogFragment;
import com.juzix.wallet.component.ui.view.AssetsFragment;
import com.juzix.wallet.component.ui.view.MainActivity;
import com.juzix.wallet.db.entity.AddressInfoEntity;
import com.juzix.wallet.db.entity.IndividualTransactionInfoEntity;
import com.juzix.wallet.db.entity.SharedTransactionInfoEntity;
import com.juzix.wallet.db.sqlite.AddressInfoDao;
import com.juzix.wallet.db.sqlite.IndividualTransactionInfoDao;
import com.juzix.wallet.engine.IndividualWalletManager;
import com.juzix.wallet.engine.IndividualWalletTransactionManager;
import com.juzix.wallet.engine.SharedWalletManager;
import com.juzix.wallet.engine.SharedWalletTransactionManager;
import com.juzix.wallet.engine.Web3jManager;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.entity.SharedWalletEntity;
import com.juzix.wallet.entity.WalletEntity;
import com.juzix.wallet.event.EventPublisher;
import com.juzix.wallet.utils.AddressFormatUtil;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.JZWalletUtil;
import com.trello.rxlifecycle2.android.FragmentEvent;

import org.reactivestreams.Publisher;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.SingleSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;


/**
 * @author matrixelement
 */
public class SendTransationPresenter extends BasePresenter<SendTransationContract.View> implements SendTransationContract.Presenter {

    private final static String TAG = SendTransationPresenter.class.getSimpleName();

    private final static double DEFAULT_PERCENT = 0;
    private final static long DEAULT_GAS_LIMIT = 210000;
    private final static double MIN_GAS_PRICE_WEI = 1E9;
    private final static double MAX_GAS_PRICE_WEI = 1E10;
    private final static double D_GAS_PRICE_WEI = MAX_GAS_PRICE_WEI - MIN_GAS_PRICE_WEI;
    private static final int REFRESH_TIME = 5000;
    private Disposable mDisposable;

    private WalletEntity walletEntity;
    private IndividualWalletEntity individualWalletEntity;
    private String toAddress;

    private double gasPrice = MIN_GAS_PRICE_WEI;
    private long gasLimit = DEAULT_GAS_LIMIT;
    private double feeAmount;
    private double percent = DEFAULT_PERCENT;

    public SendTransationPresenter(SendTransationContract.View view) {
        super(view);
    }

    @Override
    public void init() {
        if (isViewAttached()) {
            if (!TextUtils.isEmpty(toAddress)) {
                getView().setToAddress(toAddress);
            }
        }
    }

    @Override
    public void fetchDefaultWalletInfo() {
        walletEntity = MainActivity.sInstance.getSelectedWallet();
        if (walletEntity == null) {
            return;
        }
        String address = walletEntity.getPrefixAddress();
        if (walletEntity instanceof SharedWalletEntity) {
            SharedWalletEntity sharedWalletEntity = (SharedWalletEntity) walletEntity;
            individualWalletEntity = IndividualWalletManager.getInstance().getWalletByAddress(sharedWalletEntity.getCreatorAddress());
        }

        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        mDisposable = Single.fromCallable(new Callable<Double>() {
            @Override
            public Double call() throws Exception {
                return Web3jManager.getInstance().getBalance(address);
            }
        })
                .compose(bindUntilEvent(FragmentEvent.STOP))
                .compose(new SchedulersTransformer())
                .repeatWhen(new Function<Flowable<Object>, Publisher<?>>() {
                    @Override
                    public Publisher<?> apply(Flowable<Object> objectFlowable) throws Exception {
                        return objectFlowable.delay(REFRESH_TIME, TimeUnit.MILLISECONDS);
                    }
                })
                .subscribe(new Consumer<Double>() {
                    @Override
                    public void accept(Double balance) throws Exception {
                        if (isViewAttached()) {
                            walletEntity.setBalance(balance);
                            getView().updateWalletInfo(walletEntity);
                            calculateFeeAndTime(percent);
                        }
                    }
                });
    }


    @Override
    public void transferAllBalance() {
        if (isViewAttached() && walletEntity != null) {
            getView().setTransferAmount(BigDecimalUtil.sub(walletEntity.getBalance(), feeAmount));
        }
    }

    @Override
    public void calculateFee() {
        String toAddress = getView().getToAddress();
        String address = walletEntity.getPrefixAddress();
        if (TextUtils.isEmpty(toAddress) || walletEntity == null || TextUtils.isEmpty(address)) {
            return;
        }
        updateFeeAmount(percent);
    }

    @Override
    public void calculateFeeAndTime(double percent) {
        this.percent = percent;
        updateFeeAmount(percent);
        updateGasPrice(percent);
    }

    @Override
    public boolean checkToAddress(String toAddress) {
        String errMsg = null;
        if (TextUtils.isEmpty(toAddress)) {
            errMsg = string(R.string.address_cannot_be_empty);
        } else {
            if (!WalletUtils.isValidAddress(toAddress)) {
                errMsg = string(R.string.receive_address_error);
            }
        }
        getView().showToAddressError(errMsg);
        return TextUtils.isEmpty(errMsg);
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
            if (toAddress.equals(address)) {
                showLongToast(R.string.can_not_send_to_itself);
                return;
            }
            if (walletEntity instanceof SharedWalletEntity) {
                if (individualWalletEntity == null || !((SharedWalletEntity) walletEntity).isOwner()) {
                    CommonTipsDialogFragment.createDialogWithTitleAndOneButton(ContextCompat.getDrawable(getContext(), R.drawable.icon_dialog_tips), string(R.string.txn_init_failed_title), string(R.string.txn_init_failed_content), string(R.string.understood), new OnDialogViewClickListener() {
                        @Override
                        public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                        }
                    }).show(currentActivity().getSupportFragmentManager(), "showError");
                    return;
                }
            }
            String fromWallet = walletEntity.getName();
            String fromAddress = address;
            String fee = NumberParserUtils.getPrettyBalance(feeAmount);
            String executor = walletEntity instanceof SharedWalletEntity ? individualWalletEntity.getName() : "";
            String walletName = IndividualWalletManager.getInstance().getWalletNameByWalletAddress(toAddress);
            if (TextUtils.isEmpty(walletName)){
                walletName = SharedWalletManager.getInstance().getSharedWalletNameByContractAddress(toAddress);
            }
            if (TextUtils.isEmpty(walletName)){
                walletName = AddressFormatUtil.formatAddress(toAddress);
            }
            SendTransactionDialogFragment
                    .newInstance(string(R.string.send_transation), NumberParserUtils.getPrettyBalance(transferAmount), buildSendTransactionInfo(fromWallet, fromAddress, walletName, fee, executor))
                    .setOnConfirmBtnClickListener(new SendTransactionDialogFragment.OnConfirmBtnClickListener() {
                        @Override
                        public void onConfirmBtnClick() {
                            showInputWalletPasswordDialogFragment(transferAmount, toAddress);
                        }
                    })
                    .show(currentActivity().getSupportFragmentManager(), "sendTransaction");

        }
    }

    @Override
    public void updateSendTransactionButtonStatus() {
        if (isViewAttached()) {
            String transferAmount = getView().getTransferAmount();
            String toAddress = getView().getToAddress();
            boolean isToAddressFormatCorrect = !TextUtils.isEmpty(toAddress) && WalletUtils.isValidAddress(toAddress);
            boolean isTransferAmountValid = !TextUtils.isEmpty(transferAmount) && NumberParserUtils.parseDouble(transferAmount) > 0 && isBalanceEnough(transferAmount);
            getView().setSendTransactionButtonEnable(isToAddressFormatCorrect && isTransferAmountValid);
        }

    }

    @Override
    public void saveWallet(String name, String address) {
        String[] avatarArray = getContext().getResources().getStringArray(R.array.wallet_avatar);
        String avatar = avatarArray[new Random().nextInt(avatarArray.length)];
        getView().setSaveAddressButtonEnable(!AddressInfoDao.getInstance().insertAddressInfo(new AddressInfoEntity(UUID.randomUUID().toString(), address, name, avatar)));
    }

    @Override
    public void updateAssetsTab(int tabIndex) {
        if (isViewAttached()) {
            if (tabIndex != AssetsFragment.TAB2) {
                resetData();
                getView().resetView(BigDecimalUtil.parseString(feeAmount));
            }
        }
    }

    @Override
    public void checkAddressBook(String address) {
        if (TextUtils.isEmpty(address)) {
            getView().setSaveAddressButtonEnable(false);
            return;
        }
        if (JZWalletUtil.isValidAddress(address)) {
            getView().setSaveAddressButtonEnable(!AddressInfoDao.getInstance().isExist(address));
        } else {
            getView().setSaveAddressButtonEnable(false);
        }
    }

    private Single<String> sendTransaction(String privateKey, String fromAddress, String toAddress, String transferAmount, long gasPrice, long gasLimit) {

        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(SingleEmitter<String> emitter) throws Exception {
                String transactionHash = IndividualWalletTransactionManager.getInstance().sendTransaction(privateKey, fromAddress, toAddress, transferAmount, NumberParserUtils.parseLong(BigDecimalUtil.parseString(gasPrice)), gasLimit);
                if (TextUtils.isEmpty(transactionHash)) {
                    emitter.onError(new CustomThrowable(CustomThrowable.CODE_ERROR_TRANSFER_FAILED));
                } else {
                    emitter.onSuccess(transactionHash);
                }
            }
        });
    }

    private void sendTransaction(Credentials credentials, String transferAmount, String toAddress) {
        sendTransaction(Numeric.toHexStringNoPrefix(credentials.getEcKeyPair().getPrivateKey()), walletEntity.getPrefixAddress(), toAddress, transferAmount, NumberParserUtils.parseLong(BigDecimalUtil.parseString(gasPrice)), gasLimit)
                .map(new Function<String, IndividualTransactionInfoEntity>() {
                    @Override
                    public IndividualTransactionInfoEntity apply(String hash) throws Exception {
                        return new IndividualTransactionInfoEntity.Builder()
                                .uuid(UUID.randomUUID().toString())
                                .hash(hash)
                                .createTime(System.currentTimeMillis())
                                .from(walletEntity.getPrefixAddress())
                                .to(toAddress)
                                .walletName(walletEntity.getName())
                                .build();
                    }
                })
                .doOnSuccess(new Consumer<IndividualTransactionInfoEntity>() {
                    @Override
                    public void accept(IndividualTransactionInfoEntity individualTransactionInfoEntity) throws Exception {
                        IndividualTransactionInfoDao.getInstance().insertTransaction(individualTransactionInfoEntity);
                        EventPublisher.getInstance().sendUpdateIndividualWalletTransactionEvent();
                    }
                })
                .compose(new SchedulersTransformer())
                .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                .compose(bindToLifecycle())
                .subscribe(new Consumer<IndividualTransactionInfoEntity>() {
                    @Override
                    public void accept(IndividualTransactionInfoEntity transactionInfoEntity) throws Exception {
                        if (isViewAttached()) {
                            showLongToast(string(R.string.transfer_succeed));
                            reset();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (isViewAttached()) {
                            if (throwable instanceof CustomThrowable) {
                                CustomThrowable exception = (CustomThrowable) throwable;
                                if (exception.getErrCode() == CustomThrowable.CODE_ERROR_TRANSFER_FAILED) {
                                    showLongToast(string(R.string.transfer_failed));
                                }
                            }
                        }
                    }
                });
    }

    private Single<Credentials> checkBalance(IndividualWalletEntity walletEntity, Credentials credentials, BigInteger gasPrice) {

        return Single.create(new SingleOnSubscribe<Credentials>() {
            @Override
            public void subscribe(SingleEmitter<Credentials> emitter) throws Exception {
                double balance = Web3jManager.getInstance().getBalance(walletEntity.getPrefixAddress());
                if (balance < feeAmount) {
                    emitter.onError(new CustomThrowable(CustomThrowable.CODE_ERROR_NOT_SUFFICIENT_BALANCE));
                } else {
                    emitter.onSuccess(credentials);
                }
            }
        });
    }


    private void validPassword(Credentials credentials, String transferAmount, String toAddress) {
        BigInteger submitGasPrice = BigInteger.valueOf(NumberParserUtils.parseLong(BigDecimalUtil.parseString(gasPrice)));
        checkBalance(individualWalletEntity, credentials, submitGasPrice)
                .flatMap(new Function<Credentials, SingleSource<SharedTransactionInfoEntity>>() {
                    @Override
                    public SingleSource<SharedTransactionInfoEntity> apply(Credentials credentials) throws Exception {
                        return SharedWalletTransactionManager.getInstance()
                                .submitTransaction(credentials, (SharedWalletEntity) walletEntity, toAddress, transferAmount, "", submitGasPrice, feeAmount);
                    }
                })
                .map(new Function<SharedTransactionInfoEntity, SharedTransactionInfoEntity>() {
                    @Override
                    public SharedTransactionInfoEntity apply(SharedTransactionInfoEntity sharedTransactionInfoEntity) throws Exception {
                        return sharedTransactionInfoEntity;
                    }
                })
                .doOnSuccess(new Consumer<SharedTransactionInfoEntity>() {
                    @Override
                    public void accept(SharedTransactionInfoEntity sharedTransactionInfoEntity) throws Exception {
                        EventPublisher.getInstance().sendUpdateSharedWalletTransactionEvent();
                    }
                })
                .compose(new SchedulersTransformer())
                .compose(bindToLifecycle())
                .compose(LoadingTransformer.bindToSingleLifecycle(getView().currentActivity()))
                .subscribe(new Consumer<SharedTransactionInfoEntity>() {
                    @Override
                    public void accept(SharedTransactionInfoEntity sharedTransactionInfoEntity) {
                        if (isViewAttached()) {
                            showLongToast(R.string.transfer_succeed);
                            reset();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (isViewAttached()) {
                            if (throwable instanceof CustomThrowable) {
                                CustomThrowable customThrowable = (CustomThrowable) throwable;
                                if (customThrowable.getErrCode() == CustomThrowable.CODE_ERROR_NOT_SUFFICIENT_BALANCE) {
                                    showLongToast(R.string.insufficient_balance);
                                }
                            } else {
                                showLongToast(R.string.transfer_failed);
                            }
                        }
                    }
                });
    }

    private void showInputWalletPasswordDialogFragment(String transferAmount, String toAddress) {
        InputWalletPasswordDialogFragment.newInstance(walletEntity instanceof SharedWalletEntity ? individualWalletEntity : (IndividualWalletEntity) walletEntity).setOnWalletPasswordCorrectListener(new InputWalletPasswordDialogFragment.OnWalletPasswordCorrectListener() {
            @Override
            public void onWalletPasswordCorrect(Credentials credentials) {
                if (walletEntity instanceof IndividualWalletEntity) {
                    sendTransaction(credentials, transferAmount, toAddress);
                } else {
                    validPassword(credentials, transferAmount, toAddress);
                }
            }
        }).show(currentActivity().getSupportFragmentManager(), "inputPassword");
    }

    private void updateFeeAmount(double percent) {
        double minFee = getMinFee();
        double maxFee = getMaxFee();
        double dValue = maxFee - minFee;
        feeAmount = BigDecimalUtil.add(minFee, BigDecimalUtil.mul(percent, dValue), 8, RoundingMode.CEILING);
        if (isViewAttached()) {
            getView().setTransferFeeAmount(BigDecimalUtil.parseString(feeAmount));
        }
    }

    private void updateGasPrice(double percent) {
        gasPrice = BigDecimalUtil.add(MIN_GAS_PRICE_WEI, BigDecimalUtil.mul(percent, D_GAS_PRICE_WEI));
    }

    private boolean isBalanceEnough(String transferAmount) {
        double usedAmount = BigDecimalUtil.add(NumberParserUtils.parseDouble(transferAmount), feeAmount);
        if (walletEntity != null) {
            return walletEntity.getBalance() >= usedAmount;
        }
        return false;
    }

    private double getMinFee() {
        return BigDecimalUtil.div(BigDecimalUtil.mul(gasLimit, MIN_GAS_PRICE_WEI), 1E18);
    }

    private double getMaxFee() {
        return BigDecimalUtil.div(BigDecimalUtil.mul(gasLimit, MAX_GAS_PRICE_WEI), 1E18);
    }

    private Map<String, String> buildSendTransactionInfo(String fromWallet, String fromAddress, String recipient, String fee, String executor) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put(string(R.string.payment_info), string(R.string.send_energon));
        map.put(string(R.string.from_wallet), fromWallet);
        if (!TextUtils.isEmpty(executor)) {
            map.put(string(R.string.execute_wallet), executor);
        }
        map.put(string(R.string.recipient_wallet), recipient);
        map.put(string(R.string.fee), fee);
        return map;
    }

    private void resetData() {
        toAddress = "";
        gasPrice = MIN_GAS_PRICE_WEI;
        gasLimit = DEAULT_GAS_LIMIT;
        percent = DEFAULT_PERCENT;
        feeAmount = getMinFee();
    }

    private void reset() {
        Single.fromCallable(new Callable<Double>() {
            @Override
            public Double call() throws Exception {
                return feeAmount;
            }
        })
                .delay(1000, TimeUnit.MILLISECONDS)
                .compose(new SchedulersTransformer())
                .subscribe(new Consumer<Double>() {
                    @Override
                    public void accept(Double o) throws Exception {
                        if (isViewAttached()) {
                            EventPublisher.getInstance().sendUpdateWalletListEvent();
                            resetData();
                            getView().resetView(BigDecimalUtil.parseString(o));
                            MainActivity.actionStart(getContext(), MainActivity.TAB_PROPERTY, AssetsFragment.TAB1);
                        }
                    }
                });
    }

}
