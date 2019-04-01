package com.juzix.wallet.component.ui.presenter;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.View;

import com.juzhen.framework.network.NetConnectivity;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.CustomThrowable;
import com.juzix.wallet.app.LoadingTransformer;
import com.juzix.wallet.app.SchedulersTransformer;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.SendSharedTransationContract;
import com.juzix.wallet.component.ui.dialog.CommonDialogFragment;
import com.juzix.wallet.component.ui.dialog.OnDialogViewClickListener;
import com.juzix.wallet.component.ui.dialog.SelectSharedWalletDialogFragment;
import com.juzix.wallet.db.entity.SharedTransactionInfoEntity;
import com.juzix.wallet.engine.IndividualWalletManager;
import com.juzix.wallet.engine.SharedWalletTransactionManager;
import com.juzix.wallet.engine.Web3jManager;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.entity.SharedWalletEntity;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.JZWalletUtil;
import com.juzix.wallet.utils.ToastUtil;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.SingleSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * @author matrixelement
 */
public class SendSharedTransationPresenter extends BasePresenter<SendSharedTransationContract.View> implements SendSharedTransationContract.Presenter {

    private final static String TAG = SendSharedTransationPresenter.class.getSimpleName();
    private final static double DEFAULT_PERCENT = 0;
    private final static long DEAULT_GAS_LIMIT = SharedWalletTransactionManager.INVOKE_GAS_LIMIT.longValue();
    private static final double DEFAULT_WEI = 1E18;
    private final static double MIN_GAS_PRICE_WEI = 1E9;
    private final static double MAX_GAS_PRICE_WEI = 1E10;
    private final static double D_GAS_PRICE_WEI = MAX_GAS_PRICE_WEI - MIN_GAS_PRICE_WEI;
    private double gasPrice = MIN_GAS_PRICE_WEI;
    private long gasLimit = DEAULT_GAS_LIMIT;
    private double percent = DEFAULT_PERCENT;
    private double feeAmount;
    private IndividualWalletEntity individualWalletEntity;
    private SharedWalletEntity walletEntity;

    public SendSharedTransationPresenter(SendSharedTransationContract.View view) {
        super(view);
        walletEntity = view.getSharedWalletFromIntent();
    }

    @Override
    public void updateSendWalletInfoAndFee(SharedWalletEntity walletEntity) {

        this.walletEntity = walletEntity;

        if (isViewAttached() && walletEntity != null) {

            getView().updateWalletInfo(walletEntity);

            calculateFee();
        }
    }

    @Override
    public void fetchDefaultWalletInfo() {
        if (walletEntity == null) {
            return;
        }
        individualWalletEntity = IndividualWalletManager.getInstance().getWalletByAddress(walletEntity.getAddress());
        if (individualWalletEntity != null && walletEntity.isOwner()) {
            getView().setSendTransactionButtonVisible(true);
        } else {
            getView().setSendTransactionButtonVisible(false);
        }

        Single.fromCallable(new Callable<Double>() {
            @Override
            public Double call() throws Exception {
                return Web3jManager.getInstance().getBalance(walletEntity.getPrefixAddress());
            }
        })
                .compose(new SchedulersTransformer())
                .compose(bindToLifecycle())
                .subscribe(new Consumer<Double>() {
                    @Override
                    public void accept(Double balance) throws Exception {
                        walletEntity.setBalance(balance);
                        if (isViewAttached()) {
                            getView().updateWalletInfo(walletEntity);
                            calculateFeeAndTime(percent);

                        }
                    }
                });
    }


    @Override
    public void transferAllBalance() {

        if (isViewAttached() && walletEntity != null) {

            getView().setTransferAmount(walletEntity.getBalance());
        }
    }

    @Override
    public void inputTransferAmount(String transferAmount) {
        if (isViewAttached() && walletEntity != null) {
            getView().setTransferAmountTextColor(NumberParserUtils.parseDouble(transferAmount) > walletEntity.getBalance());
        }
    }

    @Override
    public void calculateFee() {
        String toAddress = getView().getToAddress();
        if (TextUtils.isEmpty(toAddress) || walletEntity == null || TextUtils.isEmpty(walletEntity.getPrefixAddress())) {
            return;
        }
        updateFeeAmount(percent);
    }

    @Override
    public void calculateFeeAndTime(double percent) {

        this.percent = percent;

        updateFeeAmount(percent);

        updateTransferTime(percent);

    }

    @Override
    public boolean checkToAddress(String toAddress) {

        String errMsg = null;

        if (TextUtils.isEmpty(toAddress)) {
            errMsg = string(R.string.address_cannot_be_empty);
        } else {
            if (!WalletUtils.isValidAddress(toAddress)) {
                errMsg = string(R.string.address_format_error);
            }
        }

        getView().showToAddressError(errMsg);

        return TextUtils.isEmpty(errMsg);
    }

    @Override
    public void checkToAddressAndUpdateFee(String toAddress) {
        if (checkToAddress(toAddress)) {
            calculateFee();
        }
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
                ToastUtil.showLongToast(currentActivity(), string(R.string.network_error));
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

            if (toAddress.equals(walletEntity.getPrefixAddress())) {
                showLongToast(R.string.can_not_send_to_itself);
                return;
            }

//            SendTransactionDialogFragment.newInstance(transferAmount, toAddress, feeAmount).setOnSubmitClickListener(new SendTransactionDialogFragment.OnSubmitClickListener() {
//                @Override
//                public void onSubmitClick() {
                    showInputWalletPasswordDialogFragment("", transferAmount, toAddress);
//                }
//            }).show(currentActivity().getSupportFragmentManager(), "sendTransation");

        }
    }

    @Override
    public void showSelectWalletDialogFragment() {
        if (isViewAttached()) {
            SelectSharedWalletDialogFragment.newInstance(walletEntity == null ? "" : walletEntity.getUuid()).show(currentActivity().getSupportFragmentManager(), "selectWallet");
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

    private Single<Credentials> validPassword(String password, String keyJson) {
        return Single.create(new SingleOnSubscribe<Credentials>() {
            @Override
            public void subscribe(SingleEmitter<Credentials> emitter) throws Exception {
                Credentials credentials = JZWalletUtil.getCredentials(password, keyJson);
                if (credentials == null) {
                    emitter.onError(new CustomThrowable(CustomThrowable.CODE_ERROR_PASSWORD));
                } else {
                    emitter.onSuccess(credentials);
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


    private void validPassword(String password, String transferAmount, String toAddress) {

        String memo = getView().getTransactionMemo();
        String keyJson = individualWalletEntity.getKey();
        BigInteger submitGasPrice = BigInteger.valueOf(NumberParserUtils.parseLong(BigDecimalUtil.parseString(gasPrice)));

        validPassword(password, keyJson)
                .flatMap(new Function<Credentials, SingleSource<Credentials>>() {
                    @Override
                    public SingleSource<Credentials> apply(Credentials credentials) throws Exception {
                        return checkBalance(individualWalletEntity, credentials, submitGasPrice);
                    }
                })
                .flatMap(new Function<Credentials, SingleSource<SharedTransactionInfoEntity>>() {
                    @Override
                    public SingleSource<SharedTransactionInfoEntity> apply(Credentials credentials) throws Exception {
                        return SharedWalletTransactionManager.getInstance()
                                .submitTransaction(credentials, walletEntity, toAddress, transferAmount, memo, submitGasPrice, feeAmount);
                    }
                })
                .map(new Function<SharedTransactionInfoEntity, SharedTransactionInfoEntity>() {
                    @Override
                    public SharedTransactionInfoEntity apply(SharedTransactionInfoEntity sharedTransactionInfoEntity) throws Exception {
                        return sharedTransactionInfoEntity;
                    }
                })
                .compose(new SchedulersTransformer())
                .compose(LoadingTransformer.bindToSingleLifecycle(getView().currentActivity()))
                .compose(bindToLifecycle())
                .subscribe(new Consumer<SharedTransactionInfoEntity>() {
                    @Override
                    public void accept(SharedTransactionInfoEntity sharedTransactionInfoEntity) {
                        if (isViewAttached()) {
                            showLongToast(R.string.transfer_succeed);
                            currentActivity().finish();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (isViewAttached()) {
                            if (throwable instanceof CustomThrowable) {
                                CustomThrowable customThrowable = (CustomThrowable) throwable;
                                if (customThrowable.getErrCode() == CustomThrowable.CODE_ERROR_PASSWORD) {
                                    CommonDialogFragment.createCommonTitleWithOneButton(string(R.string.validPasswordError), string(R.string.enterAgainTips), string(R.string.back), new OnDialogViewClickListener() {
                                        @Override
                                        public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                                            showInputWalletPasswordDialogFragment(password, transferAmount, toAddress);
                                        }
                                    }).show(currentActivity().getSupportFragmentManager(), "showPasswordError");
                                } else if (customThrowable.getErrCode() == CustomThrowable.CODE_ERROR_NOT_SUFFICIENT_BALANCE) {
                                    showLongToast(R.string.insufficient_balance);
                                }
                            } else {
                                showLongToast(R.string.transfer_failed);
                            }
                        }
                    }
                });
    }

    private void showInputWalletPasswordDialogFragment(String password, String transferAmount, String toAddress) {
//        InputWalletPasswordDialogFragment.newInstance(password).setOnConfirmClickListener(new InputWalletPasswordDialogFragment.OnConfirmClickListener() {
//            @Override
//            public void onConfirmClick(String password) {
//                validPassword(password, transferAmount, toAddress);
//            }
//        }).show(currentActivity().getSupportFragmentManager(), "inputPassword");
    }

    private void updateFeeAmount(double percent) {

        double minFee = getMinFee();
        double maxFee = getMaxFee();
        double dValue = BigDecimalUtil.sub(maxFee, minFee);

        feeAmount = BigDecimalUtil.add(minFee, BigDecimalUtil.mul(percent, dValue), 8, RoundingMode.CEILING);

        if (isViewAttached()) {
            getView().setTransferFeeAmount(BigDecimalUtil.parseString(feeAmount));
        }
    }

    private void updateTransferTime(double percent) {

        gasPrice = BigDecimalUtil.add(MIN_GAS_PRICE_WEI, BigDecimalUtil.mul(percent, D_GAS_PRICE_WEI));

    }

    private boolean isBalanceEnough(String transferAmount) {
        if (walletEntity != null) {
            return walletEntity.getBalance() >= NumberParserUtils.parseDouble(transferAmount);
        }
        return false;
    }

    private double getMinFee() {
        return BigDecimalUtil.div(BigDecimalUtil.mul(gasLimit, MIN_GAS_PRICE_WEI), 1E18);
    }

    private double getMaxFee() {
        return BigDecimalUtil.div(BigDecimalUtil.mul(gasLimit, MAX_GAS_PRICE_WEI), 1E18);
    }
}
