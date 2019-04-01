package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;

public class CreateIndividualWalletContract {

    public interface View extends IView {
        void showNameError(String text, boolean isVisible);
        void showPasswordError(String text, boolean isVisible);
    }

    public interface Presenter extends IPresenter<View> {
        void createWallet(String name, String password, String repeatePassword);
        boolean isExists(String walletName);
    }
}
