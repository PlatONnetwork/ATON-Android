package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.WalletEntity;
import com.umeng.commonsdk.debug.W;

import java.util.ArrayList;

/**
 * @author matrixelement
 */
public class MainContract {

    public interface View extends IView {
    }

    public interface Presenter extends IPresenter<View> {
        void init();
        WalletEntity getSelectedWallet();
        void setSelectedWallet(WalletEntity walletEntity);
    }
}
