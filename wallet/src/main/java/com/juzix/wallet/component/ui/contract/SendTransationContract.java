package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.WalletEntity;

/**
 * @author matrixelement
 */
public class SendTransationContract {

    public interface View extends IView {

        void updateWalletInfo(WalletEntity walletEntity);

        void setToAddress(String toAddress);

        void setTransferAmount(double amount);

        void setTransferFeeAmount(String feeAmount);

        String getTransferAmount();

        String getToAddress();

        WalletEntity getWalletEntityFromIntent();

        void showToAddressError(String errMsg);

        void showAmountError(String errMsg);

        void setSendTransactionButtonEnable(boolean enable);

        void setSendTransactionButtonVisible(boolean isVisible);

        void setSaveAddressButtonEnable(boolean enable);

        void resetView(double feeAmount);

        void showSaveAddressDialog();
    }

    public interface Presenter extends IPresenter<View> {

        void init();

        void fetchDefaultWalletInfo();

        void transferAllBalance();

        void calculateFee();

        void calculateFeeAndTime(double percent);

        boolean checkToAddress(String toAddress);

        boolean checkTransferAmount(String transferAmount);

        void submit();

        void updateSendTransactionButtonStatus();

        void checkAddressBook(String address);

        void saveWallet(String name, String address);

        void updateAssetsTab(int tabIndex);
    }
}
