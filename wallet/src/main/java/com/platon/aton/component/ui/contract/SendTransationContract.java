package com.platon.aton.component.ui.contract;

import com.platon.aton.entity.Wallet;
import com.platon.framework.base.BaseViewImp;
import com.platon.framework.base.IPresenter;

/**
 * @author matrixelement
 */
public class SendTransationContract {

    public interface View extends BaseViewImp {

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

        void setProgress(float progress);

        String getGasLimit();

        void setGasLimit(String gasLimit);

        void showGasLimitError(boolean isShow);

        boolean isShowAdvancedFunction();

        String getTransactionRemark();
    }

    public interface Presenter extends IPresenter<View> {

        void init();

        void fetchDefaultWalletInfo();

        void transferAllBalance();

        void calculateFee();

        void calculateFeeAndTime(float progress);

        boolean checkToAddress(String toAddress);

        boolean checkTransferAmount(String transferAmount);

        void submit();

        void updateSendTransactionButtonStatus();

        void checkAddressBook(String address);

        void saveWallet(String name, String address);

        void updateAssetsTab(int tabIndex);

        void setGasLimit(String gasLimit);
    }
}
