package com.platon.aton.component.ui.contract;

import com.platon.aton.entity.Wallet;
import com.platon.framework.base.BaseViewImp;
import com.platon.framework.base.IPresenter;

import java.util.ArrayList;

/**
 * @author matrixelement
 */
public class WalletManagerHDManagerContract {

    public interface View extends BaseViewImp {

        void notifyWalletListChanged();

        void showWalletList();

        void showEmpty();

        void showModifyNameDialog(String name);

        void showWalletName(String name);
    }

    public interface Presenter extends IPresenter<View> {

        ArrayList<Wallet> getDataSource();

        void fetchHDWalletList(String parentId);

        void sortWalletList();


        void startAction(int position);

        void modifyName(String name,String uuid);

        boolean isExists(String walletName,String uuid);
    }

}
