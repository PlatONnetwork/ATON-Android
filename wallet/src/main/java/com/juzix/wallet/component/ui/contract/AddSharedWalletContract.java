package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.IndividualWalletEntity;

/**
 * @author matrixelement
 */
public class AddSharedWalletContract {

    public interface View extends IView {

        void setSelectOwner(IndividualWalletEntity walletEntity);

        void showWalletNameError(String errorMsg);

        void showWalletAddressError(String errorMsg);

        void setAddSharedWalletBtnEnable(boolean enable);

        String getWalletName();

        String getWalletAddress();
    }

    public interface Presenter extends IPresenter<View> {

        void init();

        void updateSelectOwner(IndividualWalletEntity walletEntity);

        boolean checkWalletName(String walletName);

        boolean checkWalletAddress(String walletAddress);

        void showSelectOwnerDialogFragment();

        void checkAddSharedWalletBtnEnable();

        void addWallet(String name, String address);

        boolean isExists(String walletName);
    }
}
