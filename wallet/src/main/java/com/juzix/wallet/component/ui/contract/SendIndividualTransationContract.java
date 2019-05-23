package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.Wallet;

/**
 * @author matrixelement
 */
public class SendIndividualTransationContract {

    public interface View extends IView {

        void updateWalletInfo(Wallet walletEntity);

        void setToAddress(String toAddress);

        void setTransferAmount(double amount);

        void setTransferAmountTextColor(boolean isBiggerThanBalance);

        void setTransferFeeAmount(String feeAmount);

        void setTransferTime(String transferTime);

        String getTransferAmount();

        String getToAddress();

        Wallet getWalletEntityFromIntent();

        String getToAddressFromIntent();

        void showToAddressError(String errMsg);

        void showAmountError(String errMsg);

        void setSendTransactionButtonEnable(boolean enable);
    }

    public interface Presenter extends IPresenter<View> {

        void init();

        void updateSendWalletInfoAndFee(Wallet walletEntity);

        void fetchDefaultWalletInfo();

        void transferAllBalance();

        void inputTransferAmount(String transferAmount);

        void calculateFee();

        void calculateFeeAndTime(double percent);

        boolean checkToAddress(String toAddress);

        void checkToAddressAndUpdateFee(String toAddress);

        boolean checkTransferAmount(String transferAmount);

        void submit();

        void showSelectWalletDialogFragment();

        void updateSendTransactionButtonStatus();

    }
}
