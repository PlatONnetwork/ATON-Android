package com.platon.aton.component.ui.contract;

import com.platon.aton.component.ui.view.AssetsFragment;
import com.platon.aton.entity.Transaction;
import com.platon.aton.entity.Wallet;
import com.platon.framework.base.BaseViewImp;
import com.platon.framework.base.IPresenter;

import java.util.List;

/**
 * @author matrixelement
 */
public class AssetsContract {

    public interface View extends BaseViewImp {

        void showTotalBalance(String totalBalance);

        void showWalletList(List<Wallet> walletList,Wallet selectedWallet);

        void setSelectedWallet(Wallet selectedWallet);

        void showWalletInfo(Wallet walletEntity);

        void showContent(boolean isContentEmpty);

        void showCurrentItem(int index);

        void showFreeBalance(String balance);

        void showLockBalance(String balance);

        void setArgument(Wallet entity);

        void finishRefresh();

        void resetView();
    }

    public interface Presenter extends IPresenter<View> {

        List<Wallet> getRecycleViewDataSource();

        void fetchWalletList();

        void clickRecycleViewItem(Wallet walletEntity);

        void backupWallet();

        void fetchWalletsBalance();

        void afterSendTransactionSucceed(Transaction transaction);

    }
}
