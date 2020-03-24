package com.platon.aton.component.ui.contract;

import com.platon.aton.component.ui.IContext;
import com.platon.aton.entity.Wallet;

/**
 * @author matrixelement
 */
public class UnlockWithPasswordContract {

    public interface View extends IContext {

        void updateWalletInfo(Wallet walletEntity);
    }

    public interface Presenter extends IPresenter<View> {

        Wallet getSelectedWallet();

        void setSelectWallet(Wallet wallet);

        void init();

        void unlock(String password);
    }
}
