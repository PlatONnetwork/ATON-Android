package com.platon.aton.component.ui.contract;

import com.platon.aton.entity.Wallet;
import com.platon.framework.base.BaseViewImp;
import com.platon.framework.base.IPresenter;

import java.util.ArrayList;

/**
 * @author matrixelement
 */
public class WalletManagerContract {

    public interface View extends BaseViewImp {

        void notifyWalletListChanged();

        void showWalletList();

        void showEmpty();
    }

    public interface Presenter extends IPresenter<View> {

        ArrayList<Wallet> getDataSource();

        void fetchWalletList();

        void sortWalletList();

        void backupWallet(int position);

        void startAction(int position);
    }

}
