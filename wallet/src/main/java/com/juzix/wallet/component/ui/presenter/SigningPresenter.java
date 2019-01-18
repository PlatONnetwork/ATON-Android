package com.juzix.wallet.component.ui.presenter;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.view.View;

import com.juzhen.framework.network.NetConnectivity;
import com.juzix.wallet.R;
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
import io.reactivex.functions.Consumer;

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

    private void showInputWalletPasswordDialogFragment(SharedTransactionEntity sharedTransactionEntity,int type, String password, BigInteger gasPrice, BigInteger gasLimit) {
        InputWalletPasswordDialogFragment.newInstance(password).setOnConfirmClickListener(new InputWalletPasswordDialogFragment.OnConfirmClickListener() {
            @Override
            public void onConfirmClick(String password) {
                validPassword(sharedTransactionEntity,type, password, gasPrice, gasLimit);
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
                .subscribe(new Consumer<BigInteger>() {
                    @Override
                    public void accept(BigInteger gasPrice) throws Exception {
                        double feeAmount = BigDecimalUtil.mul(gasPrice.doubleValue(), SharedWalletTransactionManager.INVOKE_GAS_LIMIT.doubleValue());
                        feeAmount = BigDecimalUtil.div(feeAmount, DEFAULT_WEI);
                        ExecuteContractDialogFragment.newInstance(feeAmount, type).setOnSubmitClickListener(new ExecuteContractDialogFragment.OnSubmitClickListener() {
                            @Override
                            public void onSubmitClick() {
                                showInputWalletPasswordDialogFragment(transactionEntity,type, "", gasPrice, SharedWalletTransactionManager.INVOKE_GAS_LIMIT);
                            }
                        }).show(currentActivity().getSupportFragmentManager(), "sendTransation");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    private void validPassword(SharedTransactionEntity sharedTransactionEntity,int type, String password, BigInteger gasPrice, BigInteger gasLimit) {

        SharedWalletTransactionManager.getInstance()
                .validPassword(password, individualWalletEntity.getKey())
                .compose(LoadingTransformer.bindToLifecycle(getView().currentActivity()))
                .subscribe(new Consumer<Credentials>() {
                    @Override
                    public void accept(Credentials credentials) throws Exception {
                        getView().updateSigningStatus(transactionEntity.getOwnerWalletAddress(), TransactionResult.OPERATION_SIGNING);
                        sendTransaction(sharedTransactionEntity,type, password, gasPrice, gasLimit);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        CommonDialogFragment.createCommonTitleWithOneButton(string(R.string.validPasswordError), string(R.string.enterAgainTips), string(R.string.back), new OnDialogViewClickListener() {
                            @Override
                            public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                                showInputWalletPasswordDialogFragment(sharedTransactionEntity,type, password, gasPrice, gasLimit);
                            }
                        }).show(currentActivity().getSupportFragmentManager(), "showPasswordError");
                    }
                });


    }

    private void sendTransaction(SharedTransactionEntity sharedTransactionEntity,int type, String password, BigInteger gasPrice, BigInteger gasLimit) {

        new Thread() {
            @Override
            public void run() {
                int code = -1;
                if (type == CONFIRM) {
                    code = SharedWalletTransactionManager.getInstance().confirmTransaction(sharedTransactionEntity,password,
                            individualWalletEntity.getKey(),
                            transactionEntity.getContractAddress(),
                            transactionEntity.getTransactionId(),
                            gasPrice);
                } else if (type == REFUSE) {
                    code = SharedWalletTransactionManager.getInstance().revokeTransaction(sharedTransactionEntity,password,
                            individualWalletEntity.getKey(),
                            transactionEntity.getContractAddress(),
                            transactionEntity.getTransactionId(),
                            gasPrice);
                }
                Message msg = null;
                switch (code) {
                    case SharedWalletTransactionManager.CODE_ERROR_PASSWORD:
                        msg = mHandler.obtainMessage();
                        msg.what = MSG_PASSWORD_ERROR;
                        Bundle bundle = new Bundle();
                        bundle.putString("type", String.valueOf(type));
                        bundle.putString("gasPrice", gasPrice.toString());
                        bundle.putString("gasLimit", gasLimit.toString());
                        bundle.putString("password", password);
                        msg.setData(bundle);
                        mHandler.sendMessage(msg);
                        break;
                    case SharedWalletTransactionManager.CODE_ERROR_CONFIRM_TRANSACTION:
                        mHandler.sendEmptyMessage(MSG_CONFIRM_TRANSACTION_FAILED);
                        break;
                    case SharedWalletTransactionManager.CODE_ERROR_REVOKE_TRANSACTION:
                        mHandler.sendEmptyMessage(MSG_REOKE_TRANSACTION_FAILED);
                        break;
                    case SharedWalletTransactionManager.CODE_OK:
                        msg = mHandler.obtainMessage();
                        msg.what = MSG_SUCCESS;
                        msg.arg1 = type;
                        mHandler.sendMessage(msg);
                        break;
                }
            }
        }.start();
    }

    private static final int MSG_GET_GAS_OK = 1;
    private static final int MSG_SUCCESS = 0;
    private static final int MSG_SHOW_BALANCE = 1;
    private static final int MSG_PASSWORD_ERROR = 2;
    private static final int MSG_CONFIRM_TRANSACTION_FAILED = 3;
    private static final int MSG_REOKE_TRANSACTION_FAILED = 4;
    private static final int MSG_GET_GAS_FAILED = -4;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SUCCESS:
                    if (isViewAttached()) {
                        getView().updateSigningStatus(transactionEntity.getOwnerWalletAddress(), msg.arg1 == CONFIRM ? TransactionResult.OPERATION_APPROVAL : TransactionResult.OPERATION_REVOKE);
                        getView().signFinished();
                    }
                    break;
                case MSG_CONFIRM_TRANSACTION_FAILED:
                    if (isViewAttached()) {
                        dismissLoadingDialogImmediately();
                        showLongToast(string(R.string.transfer_failed));
                    }
                    break;
                case MSG_REOKE_TRANSACTION_FAILED:
                    if (isViewAttached()) {
                        dismissLoadingDialogImmediately();
                        showLongToast(string(R.string.transfer_failed));
                    }
                    break;
            }
        }
    };
}
