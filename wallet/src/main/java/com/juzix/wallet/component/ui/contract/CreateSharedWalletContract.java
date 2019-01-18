package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.IndividualWalletEntity;

/**
 * @author matrixelement
 */
public class CreateSharedWalletContract {

    public interface View extends IView {

        int REQUEST_CODE_CREATE_SHARED_WALLET_SECOND_STEP = 100;

        void updateSelectOwner(IndividualWalletEntity walletEntity);

        void setNextButtonEnable(boolean enable);

        String getWalletName();

        void showWalletNameError(String errMsg);

        int getSharedOwners();

        int getRequiredSignatures();
    }

    public interface Presenter extends IPresenter<View> {

        void init();

        void showSelectWalletDialogFragment();

        void updateSelectOwner(IndividualWalletEntity walletEntity);

        void updateWalletName(String walletName);

        void next();

        boolean checkWalletName(String walletName);
    }
}
