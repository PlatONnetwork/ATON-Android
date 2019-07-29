package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.Wallet;

public class DelegateContract {

    public interface View extends IView {
        
        void showSelectedWalletInfo(Wallet individualWalletEntity);

    }

    public interface Presenter extends IPresenter<View> {
        void showSelectWalletDialogFragment();

        void showWalletInfo();

    }
}
