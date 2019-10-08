package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.Wallet;

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
    }

    public interface Presenter extends IPresenter<View> {

        List<Wallet> getRecycleViewDataSource();

        void fetchWalletList();

        void clickRecycleViewItem(Wallet walletEntity);

        void backupWallet();

        void fetchWalletsBalance();

    }
}
