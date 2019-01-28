package com.juzix.wallet.component.ui.presenter;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;

import com.juzhen.framework.network.NetConnectivity;
import com.juzix.wallet.R;
import com.juzix.wallet.app.CustomThrowable;
import com.juzix.wallet.app.LoadingTransformer;
import com.juzix.wallet.app.SchedulersTransformer;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.SigningContract;
import com.juzix.wallet.component.ui.dialog.CommonDialogFragment;
import com.juzix.wallet.component.ui.dialog.ExecuteContractDialogFragment;
import com.juzix.wallet.component.ui.dialog.InputWalletPasswordDialogFragment;
import com.juzix.wallet.component.ui.dialog.OnDialogViewClickListener;
import com.juzix.wallet.engine.IndividualWalletManager;
import com.juzix.wallet.engine.SharedWalletTransactionManager;
import com.juzix.wallet.engine.Web3jManager;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.entity.SharedTransactionEntity;
import com.juzix.wallet.entity.TransactionResult;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.ToastUtil;

import org.web3j.crypto.Credentials;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

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
public class SigningPresenter extends BasePresenter<SigningContract.View> implements SigningContract.Presenter {

    private static final double DEFAULT_WEI = 1E18;
    private static final int CONFIRM = 1;
    private static final int REFUSE = 2;
    private SharedTransactionEntity transactionEntity;
    private IndividualWalletEntity individualWalletEntity;

    public SigningPresenter(SigningContract.View view) {
        super(view);
    }

    @Override
    public void init() {
        transactionEntity = getView().getTransactionFromIntent();
        individualWalletEntity = IndividualWalletManager.getInstance().getWalletByAddress(transactionEntity.getOwnerWalletAddress());
    }

    @Override
    public void fetchTransactionDetail() {
        if (isViewAttached() && transactionEntity != null) {
            ArrayList<TransactionResult> resultList = transactionEntity.getTransactionResult();
            ArrayList<TransactionResult> confirmList = new ArrayList<>();
            ArrayList<TransactionResult> revokeList = new ArrayList<>();
            ArrayList<TransactionResult> undeterminedList = new ArrayList<>();
            String walletAddress = transactionEntity.getContractAddress();
            boolean subTransactoined = true;
            int len = resultList.size();
            for (int i = 0; i < len; i++) {
                TransactionResult result = resultList.get(i);
                int operation = result.getOperation();
                if (walletAddress.contains(result.getAddress()) && operation != TransactionResult.OPERATION_UNDETERMINED) {
                    subTransactoined = false;
                }
                switch (operation) {
                    case TransactionResult.OPERATION_APPROVAL:
                        confirmList.add(result);
                        break;
                    case TransactionResult.OPERATION_REVOKE:
                        revokeList.add(result);
                        break;
                    case TransactionResult.OPERATION_UNDETERMINED:
                        undeterminedList.add(result);
                        break;
                }
            }
            if (!resultList.isEmpty()) {
                resultList.clear();
            }
            if (!confirmList.isEmpty()) {
                resultList.addAll(confirmList);
            }
            if (!revokeList.isEmpty()) {
                resultList.addAll(revokeList);
            }
            if (!undeterminedList.isEmpty()) {
                resultList.addAll(undeterminedList);
            }

            String statusDesc = string(R.string.transactionConfirmation) + "(" + confirmList.size() + "/" + transactionEntity.getRequiredSignNumber() + ")";
            getView().setTransactionDetailInfo(transactionEntity, statusDesc);
            getView().showTransactionResult(resultList);

            if (individualWalletEntity != null && isWaittingSigned(transactionEntity.getTransactionResult()) && subTransactoined) {
                getView().enableButtons(true);
            } else {
                getView().enableButtons(false);
            }
        }
    }

    private boolean isWaittingSigned(List<TransactionResult> transactionResultList) {

        if (transactionResultList == null || transactionResultList.isEmpty()) {
            return false;
        }

        for (TransactionResult result : transactionResultList) {
            if (individualWalletEntity.getPrefixAddress().equals(result.getPrefixAddress()) && result.getOperation() == TransactionResult.OPERATION_UNDETERMINED) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void confirm() {
        if (isViewAttached()) {
            start(CONFIRM);
        }
    }

    @Override
    public void revoke() {
        if (isViewAttached()) {
            start(REFUSE);
        }
    }

    private void start(int type) {
        if (!NetConnectivity.getConnectivityManager().isConnected()) {
            ToastUtil.showLongToast(currentActivity(), string(R.string.network_error));
            return;
        }

        getGase(type);
    }

    private void showInputWalletPasswordDialogFragment(SharedTransactionEntity sharedTransactionEntity, int type, String password, BigInteger gasPrice, double feeAmount) {
        InputWalletPasswordDialogFragment.newInstance(password).setOnConfirmClickListener(new InputWalletPasswordDialogFragment.OnConfirmClickListener() {
            @Override
            public void onConfirmClick(String password) {
                validPassword(sharedTransactionEntity, type, password, gasPrice, feeAmount);
            }
        }).show(currentActivity().getSupportFragmentManager(), "inputPassword");
    }

    private void getGase(int type) {

        Single
                .fromCallable(new Callable<BigInteger>() {
                    @Override
                    public BigInteger call() throws Exception {
                        BigInteger gasPrice = Web3jManager.getInstance().getWeb3j().ethGasPrice().send().getGasPrice();
                        return gasPrice;
                    }
                })
                .compose(new SchedulersTransformer())
                .compose(LoadingTransformer.bindToLifecycle(getView().currentActivity()))
                .compose(bindToLifecycle())
                .subscribe(new Consumer<BigInteger>() {
                    @Override
                    public void accept(BigInteger gasPrice) throws Exception {
                        final double feeAmount = BigDecimalUtil.div(BigDecimalUtil.mul(gasPrice.doubleValue(), SharedWalletTransactionManager.INVOKE_GAS_LIMIT.doubleValue()), DEFAULT_WEI);
                        ExecuteContractDialogFragment.newInstance(feeAmount, type).setOnSubmitClickListener(new ExecuteContractDialogFragment.OnSubmitClickListener() {
                            @Override
                            public void onSubmitClick() {
                                showInputWalletPasswordDialogFragment(transactionEntity, type, "", gasPrice, feeAmount);
                            }
                        }).show(currentActivity().getSupportFragmentManager(), "sendTransation");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    private Single<Credentials> validPassword(String password, String keyJson) {
        return Single.create(new SingleOnSubscribe<Credentials>() {
            @Override
            public void subscribe(SingleEmitter<Credentials> emitter) throws Exception {
                Credentials credentials = SharedWalletTransactionManager.getInstance().credentials(password, keyJson);
                if (credentials == null) {
                    emitter.onError(new CustomThrowable(CustomThrowable.CODE_ERROR_PASSWORD));
                } else {
                    emitter.onSuccess(credentials);
                }
            }
        });
    }

    private Single<Credentials> checkBalance(IndividualWalletEntity walletEntity, Credentials credentials, double feeAmount) {
        return Single.create(new SingleOnSubscribe<Credentials>() {
            @Override
            public void subscribe(SingleEmitter<Credentials> emitter) throws Exception {
                if (walletEntity.getBalance() < feeAmount) {
                    emitter.onError(new CustomThrowable(CustomThrowable.CODE_ERROR_NOT_SUFFICIENT_BALANCE));
                } else {
                    emitter.onSuccess(credentials);
                }
            }
        });
    }

    private void validPassword(SharedTransactionEntity sharedTransactionEntity, int type, String password, BigInteger gasPrice, double feeAmount) {

        validPassword(password, individualWalletEntity.getKey())
                .flatMap(new Function<Credentials, SingleSource<Credentials>>() {
                    @Override
                    public SingleSource<Credentials> apply(Credentials credentials) throws Exception {
                        return checkBalance(individualWalletEntity, credentials, feeAmount);
                    }
                })
                .compose(new SchedulersTransformer())
                .compose(LoadingTransformer.bindToLifecycle(getView().currentActivity()))
                .compose(bindToLifecycle())
                .subscribe(new Consumer<Credentials>() {
                    @Override
                    public void accept(Credentials credentials) throws Exception {
                        sendTransaction(sharedTransactionEntity, credentials, type, gasPrice);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (throwable instanceof CustomThrowable) {
                            CustomThrowable execption = (CustomThrowable) throwable;
                            if (execption.getErrCode() == CustomThrowable.CODE_ERROR_PASSWORD) {
                                if (isViewAttached()) {
                                    CommonDialogFragment.createCommonTitleWithOneButton(string(R.string.validPasswordError), string(R.string.enterAgainTips), string(R.string.back), new OnDialogViewClickListener() {
                                        @Override
                                        public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                                            showInputWalletPasswordDialogFragment(sharedTransactionEntity, type, password, gasPrice, feeAmount);
                                        }
                                    }).show(currentActivity().getSupportFragmentManager(), "showPasswordError");
                                }
                            } else if (execption.getErrCode() == CustomThrowable.CODE_ERROR_NOT_SUFFICIENT_BALANCE) {
                                if (isViewAttached()) {
                                    showLongToast(execption.getDetailMsgRes());
                                }
                            }
                        }
                    }
                });


    }

    private void sendTransaction(SharedTransactionEntity sharedTransactionEntity, Credentials credentials, int type, BigInteger gasPrice) {

        SharedWalletTransactionManager.getInstance()
                .sendTransaction(sharedTransactionEntity, credentials, transactionEntity.getContractAddress(), transactionEntity.getTransactionId(), gasPrice, type)
                .compose(new SchedulersTransformer())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        if (isViewAttached()) {
                            getView().updateSigningStatus(transactionEntity.getOwnerWalletAddress(), TransactionResult.OPERATION_SIGNING);
                        }
                    }
                })
                .subscribe(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if (isViewAttached()) {
                            getView().updateSigningStatus(transactionEntity.getOwnerWalletAddress(), type == CONFIRM ? TransactionResult.OPERATION_APPROVAL : TransactionResult.OPERATION_REVOKE);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (isViewAttached()) {
                            getView().updateSigningStatus(transactionEntity.getOwnerWalletAddress(), TransactionResult.OPERATION_UNDETERMINED);
                            showLongToast(string(R.string.transfer_failed));
                        }
                    }
                });
    }
}
