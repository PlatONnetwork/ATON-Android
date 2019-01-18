package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.IndividualWalletEntity;

/**
 * @author matrixelement
 */
public class SendIndividualTransationContract {

    public interface View extends IView {

        void updateWalletInfo(IndividualWalletEntity walletEntity);

        void setToAddress(String toAddress);

        void setTransferAmount(double amount);

        void setTransferAmountTextColor(boolean isBiggerThanBalance);

        void setTransferFeeAmount(String feeAmount);

        void setTransferTime(String transferTime);

        String getTransferAmount();

        String getToAddress();

        IndividualWalletEntity getWalletEntityFromIntent();

        String getToAddressFromIntent();

        void showToAddressError(String errMsg);

        void showAmountError(String errMsg);

        void setSendTransactionButtonEnable(boolean enable);
    }

    public interface Presenter extends IPresenter<View> {

        void init();

        void updateSendWalletInfoAndFee(IndividualWalletEntity walletEntity);

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
