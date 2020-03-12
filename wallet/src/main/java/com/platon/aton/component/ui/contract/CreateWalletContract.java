package com.platon.aton.component.ui.contract;

import com.platon.aton.component.ui.base.IPresenter;
import com.platon.aton.component.ui.base.IView;

public class CreateWalletContract {

    public interface View extends IView {

        void showNameError(String text, boolean isVisible);

        void showPasswordError(String text, boolean isVisible);
    }

    public interface Presenter extends IPresenter<View> {
        void createWallet(String name, String password, String repeatPassword);
    }
}
