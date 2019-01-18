package com.juzix.wallet.component.ui.presenter;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.View;

import com.juzhen.framework.network.NetConnectivity;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.SendIndividualTransationContract;
import com.juzix.wallet.component.ui.dialog.CommonDialogFragment;
import com.juzix.wallet.component.ui.dialog.InputWalletPasswordDialogFragment;
import com.juzix.wallet.component.ui.dialog.OnDialogViewClickListener;
import com.juzix.wallet.component.ui.dialog.SelectIndividualWalletDialogFragment;
import com.juzix.wallet.component.ui.dialog.SendTransationDialogFragment;
import com.juzix.wallet.db.entity.IndividualTransactionInfoEntity;
import com.juzix.wallet.db.sqlite.IndividualTransactionInfoDao;
import com.juzix.wallet.engine.IndividualWalletManager;
import com.juzix.wallet.engine.IndividualWalletTransactionManager;
import com.juzix.wallet.engine.Web3jManager;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.event.EventPublisher;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.ToastUtil;

import org.web3j.crypto.WalletUtils;

import java.math.RoundingMode;
import java.util.UUID;

/**
 * @author matrixelement
 */
public class SendIndividualTransationPresenter extends BasePresenter<SendIndividualTransationContract.View> implements SendIndividualTransationContract.Presenter {

    private final static String TAG = SendIndividualTransationPresenter.class.getSimpleName();

    private final static double DEFAULT_PERCENT = 0;
    private final static long DEAULT_GAS_LIMIT = 210000;
    private final static double MIN_GAS_PRICE_WEI = 1E9;
    private final static double MAX_GAS_PRICE_WEI = 1E10;
    private final static double D_GAS_PRICE_WEI = MAX_GAS_PRICE_WEI - MIN_GAS_PRICE_WEI;

    private IndividualWalletEntity walletEntity;

    private double gasPrice = MIN_GAS_PRICE_WEI;
    private long gasLimit = DEAULT_GAS_LIMIT;
    private double feeAmount;
    private double percent = DEFAULT_PERCENT;

    public SendIndividualTransationPresenter(SendIndividualTransationContract.View view) {
        super(view);
        walletEntity = view.getWalletEntityFromIntent();
    }

    @Override
    public void init() {
        if (isViewAttached()) {
            String toAddress = getView().getToAddressFromIntent();
            if (!TextUtils.isEmpty(toAddress)) {
                getView().setToAddress(toAddress);
            }
        }
    }

    @Override
    public void updateSendWalletInfoAndFee(IndividualWalletEntity walletEntity) {

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
        new Thread() {
            @Override
            public void run() {
                walletEntity.setBalance(Web3jManager.getInstance().getBalance(walletEntity.getPrefixAddress()));
                mHandler.sendEmptyMessage(MSG_UPDATEWALLETINFO);
            }
        }.start();
    }


    @Override
    public void transferAllBalance() {

        if (isViewAttached() && walletEntity != null) {

            getView().setTransferAmount(BigDecimalUtil.sub(walletEntity.getBalance(), feeAmount));
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

            if (toAddress.equals(walletEntity.getPrefixAddress())) {
                showLongToast(R.string.can_not_send_to_itself);
                return;
            }

            SendTransationDialogFragment.newInstance(transferAmount, toAddress, feeAmount).setOnSubmitClickListener(new SendTransationDialogFragment.OnSubmitClickListener() {
                @Override
                public void onSubmitClick() {
                    showInputWalletPasswordDialogFragment("", transferAmount, toAddress);
                }
            }).show(currentActivity().getSupportFragmentManager(), "sendTransation");

        }
    }

    @Override
    public void showSelectWalletDialogFragment() {
        if (isViewAttached()) {
            SelectIndividualWalletDialogFragment.newInstance(walletEntity == null ? "" : walletEntity.getUuid()).show(currentActivity().getSupportFragmentManager(), SelectIndividualWalletDialogFragment.SELECT_TRANSACTION_WALLET);
        }
    }

    @Override
    public void updateSendTransactionButtonStatus() {

        if (isViewAttached()) {

            String transferAmount = getView().getTransferAmount();

            boolean isToAddressNotEmpty = !TextUtils.isEmpty(getView().getToAddress());
            boolean isTransferAmountValid = !TextUtils.isEmpty(transferAmount) && NumberParserUtils.parseDouble(transferAmount) > 0 && isBalanceEnough(transferAmount);

            getView().setSendTransactionButtonEnable(isToAddressNotEmpty && isTransferAmountValid);
        }

    }

    private void sendTransaction(String password, String transferAmount, String toAddress) {
        String to = toAddress;
        String from = walletEntity.getPrefixAddress();
        String amount = transferAmount;
        long time = System.currentTimeMillis();
        showLoadingDialog();
        new Thread() {
            @Override
            public void run() {
                String privateKey = IndividualWalletManager.getInstance().exportPrivateKey(walletEntity, password);
                if (TextUtils.isEmpty(privateKey)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("password", password);
                    bundle.putString("transferAmount", transferAmount);
                    bundle.putString("toAddress", toAddress);
                    Message msg = mHandler.obtainMessage();
                    msg.what = MSG_PASSWORD_FAILED;
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                    return;
                }
                String hash = IndividualWalletTransactionManager.getInstance().sendTransaction(privateKey, from, to, amount, NumberParserUtils.parseLong(BigDecimalUtil.parseString(gasPrice)), gasLimit);
                if (TextUtils.isEmpty(hash)) {
                    mHandler.sendEmptyMessage(MSG_TRANSFER_FAILED);
                } else {
                    IndividualTransactionInfoDao.getInstance().insertTransaction(new IndividualTransactionInfoEntity.Builder()
                            .uuid(UUID.randomUUID().toString())
                            .hash(hash)
                            .createTime(System.currentTimeMillis())
                            .from(walletEntity.getPrefixAddress())
                            .to(toAddress)
                            .walletName(walletEntity.getName())
                            .build());
                    mHandler.sendEmptyMessage(MSG_OK);
                    EventPublisher.getInstance().sendIndividualTransactionSucceedEvent();
                }
            }
        }.start();
    }

//    private void sendTransaction(String password, String transferAmount, String toAddress) {
//
//        String memo = getView().getTransactionMemo();
//
//        Flowable.fromCallable(() -> IndividualWalletManager.getInstance().exportPrivateKey(walletEntity, password)).onErrorReturn(throwable -> "").flatMap((Function<String, Publisher<String>>) privateKey -> {
//            if (TextUtils.isEmpty(privateKey)) {
//                return Flowable.just(string(R.string.validPasswordError));
//            } else {
////                long gasLimit = this.gasLimit;
////                long gasLimit = 10 * DEAULT_GAS_LIMIT;
////                double gasPrice = NewSharedWalletTransactionManager.GAS_PRICE.doubleValue();
////                long gasLimit = NewSharedWalletTransactionManager.GAS_LIMIT.longValue();
//                return walletTransactionService.sendTransaction(privateKey, walletEntity.getPrefixAddress(), toAddress, transferAmount, memo, NumberParserUtils.parseLong(BigDecimalUtil.parseString(gasPrice)), gasLimit);
//            }
//        }).compose(((BaseActivity) getView()).bindToLifecycle()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnSubscribe(new Consumer<Subscription>() {
//            @Override
//            public void accept(Subscription subscription) throws Exception {
//                if (isViewAttached()) {
//                    showLoadingDialog();
//                }
//            }
//        }).doOnTerminate(() -> {
//            if (isViewAttached()) {
//                dismissLoadingDialogImmediately();
//            }
//        }).subscribe(hash -> {
//
//            if (string(R.string.validPasswordError).equals(hash)) {
//                if (isViewAttached()) {
//                    CommonDialogFragment.createCommonTitleWithOneButton(string(R.string.validPasswordError), string(R.string.enterAgainTips), string(R.string.back), new OnDialogViewClickListener() {
//                        @Override
//                        public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
//                            showInputWalletPasswordDialogFragment(password, transferAmount, toAddress);
//                        }
//                    }).show(currentActivity().getSupportFragmentManager(), "showPasswordError");
//                }
//            } else {
//                IndividualTransactionInfoDao.getInstance().insertTransaction(new IndividualTransactionInfoEntity.Builder()
//                        .uuid(UUID.randomUUID().toString())
//                        .hash(hash)
//                        .createTime(System.currentTimeMillis())
//                        .from(walletEntity.getPrefixAddress())
//                        .to(toAddress)
//                        .walletName(walletEntity.getName())
//                        .memo(memo)
//                        .build());
//
//                EventPublisher.getInstance().sendIndividualTransactionSucceedEvent();
//
//                if (isViewAttached()) {
//                    ToastUtil.showLongToast(currentActivity(), string(R.string.transfer_succeed));
//                    ((BaseActivity) getView()).finish();
//                }
//            }
//        }, throwable -> {
//            if (isViewAttached()) {
//                ToastUtil.showLongToast(currentActivity(), string(R.string.transfer_failed));
//            }
//        });
//    }

    private void showInputWalletPasswordDialogFragment(String password, String transferAmount, String toAddress) {
        InputWalletPasswordDialogFragment.newInstance(password).setOnConfirmClickListener(new InputWalletPasswordDialogFragment.OnConfirmClickListener() {
            @Override
            public void onConfirmClick(String password) {
                sendTransaction(password, transferAmount, toAddress);
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

    private void updateTransferTime(double percent) {

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

    private static final int MSG_OK = 1;
    private static final int MSG_UPDATEWALLETINFO = 2;
    private static final int MSG_UPDATE_FEE = 3;
    private static final int MSG_PASSWORD_FAILED = -1;
    private static final int MSG_TRANSFER_FAILED = -3;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_OK:
                    if (isViewAttached()) {
                        showLongToast(string(R.string.transfer_succeed));
                        dismissLoadingDialogImmediately();
                        currentActivity().finish();
                    }
                    break;
                case MSG_PASSWORD_FAILED:
                    dismissLoadingDialogImmediately();
                    if (isViewAttached()) {
                        CommonDialogFragment.createCommonTitleWithOneButton(string(R.string.validPasswordError), string(R.string.enterAgainTips), string(R.string.back), new OnDialogViewClickListener() {
                            @Override
                            public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                                Bundle bundle = msg.getData();
                                String password = bundle.getString("password");
                                String transferAmount = bundle.getString("transferAmount");
                                String toAddress = bundle.getString("toAddress");
                                showInputWalletPasswordDialogFragment(password, transferAmount, toAddress);
                            }
                        }).show(currentActivity().getSupportFragmentManager(), "showPasswordError");
                    }
                    break;
                case MSG_TRANSFER_FAILED:
                    dismissLoadingDialogImmediately();
                    if (isViewAttached()) {
                        ToastUtil.showLongToast(currentActivity(), string(R.string.transfer_failed));
                    }
                    break;

                case MSG_UPDATEWALLETINFO:
                    if (isViewAttached()) {
                        getView().updateWalletInfo(walletEntity);
                        calculateFeeAndTime(percent);
                    }
                    break;

                case MSG_UPDATE_FEE:
                    updateFeeAmount(percent);
                    break;
            }
        }
    };

}
