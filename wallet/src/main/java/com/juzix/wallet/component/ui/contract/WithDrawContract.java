package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.Wallet;

public class WithDrawContract {
    public interface View extends IView {

        void showSelectedWalletInfo(Wallet individualWalletEntity);
    }

    public interface Presenter extends IPresenter<View> {

        //选择钱包
        void showSelectWalletDialogFragment();

        void showWalletInfo();

    }

}
