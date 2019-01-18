package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.SharedWalletEntity;

/**
 * @author matrixelement
 */
public class SendSharedTransationContract {

    public interface View extends IView {

        SharedWalletEntity getSharedWalletFromIntent();

        void updateWalletInfo(SharedWalletEntity walletEntity);

        void setToAddress(String toAddress);

        void setTransferAmount(double amount);

        void setTransferAmountTextColor(boolean isBiggerThanBalance);

        void setTransferFeeAmount(String feeAmount);

        void setTransferTime(String transferTime);

        String getTransferAmount();

        String getToAddress();

        void showToAddressError(String errMsg);

        void showAmountError(String errMsg);

        String getTransactionMemo();

        void setSendTransactionButtonEnable(boolean enable);

        void setSendTransactionButtonVisible(boolean isVisible);
    }

    public interface Presenter extends IPresenter<View> {

        void init();

        void updateSendWalletInfoAndFee(SharedWalletEntity walletEntity);

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
