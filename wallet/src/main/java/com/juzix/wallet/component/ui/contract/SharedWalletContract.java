package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.SharedWalletEntity;

import java.util.List;

/**
 * @author matrixelement
 */
public class SharedWalletContract {

    public interface View extends IView {

        void notifyWalletListChanged(List<SharedWalletEntity> walletEntityList);

        void updateItem(SharedWalletEntity walletEntity);

        void updateWalletBalance(double balance);
    }

    public interface Presenter extends IPresenter<View> {

        void fetchSharedWalletList();

        void createWallet();

        void addWallet();

    }


}
