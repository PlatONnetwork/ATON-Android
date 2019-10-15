package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.Wallet;

/**
 * @author matrixelement
 */
public class SendTransationContract {

    public interface View extends IView {

        void updateWalletBalance(String balance);

        void setToAddress(String toAddress);

        void setTransferAmount(double amount);

        void setTransferFeeAmount(String feeAmount);

        String getTransferAmount();

        String getToAddress();

        Wallet getWalletEntityFromIntent();

        void showToAddressError(String errMsg);

        void showAmountError(String errMsg);

        void setSendTransactionButtonEnable(boolean enable);

        void setSendTransactionButtonVisible(boolean isVisible);

        void setSaveAddressButtonEnable(boolean enable);

        void resetView(String feeAmount);

        void showSaveAddressDialog();
    }

    public interface Presenter extends IPresenter<View> {

        void init();

        void fetchDefaultWalletInfo();

        void transferAllBalance();

        void calculateFee();

        void calculateFeeAndTime(int progress);

        boolean checkToAddress(String toAddress);

        boolean checkTransferAmount(String transferAmount);

        void submit();

        void updateSendTransactionButtonStatus();

        void checkAddressBook(String address);

        void saveWallet(String name, String address);

        void updateAssetsTab(int tabIndex);
    }
}
