package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.IndividualWalletEntity;

/**
 * @author matrixelement
 */
public class UnlockWithPasswordContract {

    public interface View extends IView {

        void updateWalletInfo(IndividualWalletEntity walletEntity);
    }

    public interface Presenter extends IPresenter<View> {

        int getSelectedPostion();

        void setSelectedPostion(int selectedPostion);

        void init();

        void unlock(String password);
    }
}
