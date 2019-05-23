package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.Wallet;

/**
 * @author matrixelement
 */
public class UnlockWithPasswordContract {

    public interface View extends IView {

        void updateWalletInfo(Wallet walletEntity);
    }

    public interface Presenter extends IPresenter<View> {

        Wallet getSelectedWallet();

        void setSelectWallet(Wallet wallet);

        void init();

        void unlock(String password);
    }
}
