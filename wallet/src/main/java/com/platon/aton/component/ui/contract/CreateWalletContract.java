package com.platon.aton.component.ui.contract;

import com.platon.aton.component.ui.IContext;
import com.platon.aton.entity.WalletType;
import com.platon.framework.base.BaseViewImp;
import com.platon.framework.base.IPresenter;

public class CreateWalletContract {

    public interface View extends BaseViewImp {

        void showNameError(String text, boolean isVisible);

        void showPasswordError(String text, boolean isVisible);

        void showWalletNumber(int walletNum);
    }

    public interface Presenter extends IPresenter<View> {

        void loadDBWalletNumber();
        void createWallet(String name, String password, String repeatPassword, @WalletType int walletType);
    }
}
