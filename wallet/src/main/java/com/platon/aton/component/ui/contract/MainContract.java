package com.platon.aton.component.ui.contract;

import com.platon.aton.component.ui.IContext;
import com.platon.aton.component.ui.SortType;
import com.platon.aton.entity.NodeStatus;
import com.platon.aton.entity.Wallet;
import com.platon.aton.entity.WalletTypeSearch;
import com.platon.framework.base.BaseViewImp;
import com.platon.framework.base.IPresenter;

import java.util.List;

/**
 * @author matrixelement
 */
public class MainContract {

    public interface View extends BaseViewImp {

        void exitApp();
        void notifyDataSetChanged();
    }

    public interface Presenter extends IPresenter<View> {

        void checkVersion();

        void loadData(@WalletTypeSearch int walletTypeSearch, String keywords);

        List<Wallet> getDataSource();

        void updateSelectedWalletnotifyData(Wallet selectedWallet);
    }
}
