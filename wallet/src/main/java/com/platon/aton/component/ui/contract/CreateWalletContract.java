package com.platon.aton.component.ui.contract;

import com.platon.aton.component.ui.IContext;
import com.platon.framework.base.BaseViewImp;
import com.platon.framework.base.IPresenter;

public class CreateWalletContract {

    public interface View extends BaseViewImp {

        void showNameError(String text, boolean isVisible);

        void showPasswordError(String text, boolean isVisible);
    }

    public interface Presenter extends IPresenter<View> {
        void createWallet(String name, String password, String repeatPassword);
    }
}
