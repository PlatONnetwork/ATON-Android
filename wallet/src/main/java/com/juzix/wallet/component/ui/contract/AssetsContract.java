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

        void showTotalBalance(double totalBalance);

        void showWalletList(Wallet selectedWallet);

        void showWalletInfo(Wallet walletEntity);

        void showContent(boolean isContentEmpty);

        void showCurrentItem(int index);

        void showFreeBalance(double balance);

        void showLockBalance(double balance);

        void setArgument(Wallet entity);

        void notifyItemChanged(int position);

        void notifyAllChanged();

        void finishRefresh();

        void finishLoadMore();
    }

    public interface Presenter extends IPresenter<View> {

        List<Wallet> getRecycleViewDataSource();

        void fetchWalletList();

        void clickRecycleViewItem(Wallet walletEntity);

        void backupWallet();

        void fetchWalletsBalance();

    }
}
