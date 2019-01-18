package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.IndividualWalletEntity;

import java.util.ArrayList;

public class IndividualWalletManagerContract {

    public interface View extends IView {
        void showList(ArrayList<IndividualWalletEntity> walletEntityList);
    }

    public interface Presenter extends IPresenter<View> {
        void refresh();
    }
}
