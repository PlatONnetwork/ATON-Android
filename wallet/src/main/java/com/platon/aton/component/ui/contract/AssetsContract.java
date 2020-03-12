package com.platon.aton.component.ui.contract;

import com.platon.aton.component.ui.base.IPresenter;
import com.platon.aton.component.ui.base.IView;
import com.platon.aton.component.ui.view.AssetsFragment;
import com.platon.aton.entity.Transaction;
import com.platon.aton.entity.Wallet;

import java.util.List;

/**
 * @author matrixelement
 */
public class AssetsContract {

    public interface View extends IView {

        void showTotalBalance(String totalBalance);

        void showWalletList(Wallet selectedWallet);

        void showWalletInfo(Wallet walletEntity);

        void showContent(boolean isContentEmpty);

        void showCurrentItem(int index);

        void showFreeBalance(String balance);

        void showLockBalance(String balance);

        void setArgument(Wallet entity);

        void finishRefresh();

        void showTab(@AssetsFragment.MainTab int tab);

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
