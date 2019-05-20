package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.WalletEntity;

import java.util.List;

/**
 * @author matrixelement
 */
public class AssetsContract {

    public interface View extends IView {
        void showTotalBalance(double totalBalance);

        void showWalletList(WalletEntity selectedWallet);

        void showWalletInfo(WalletEntity walletEntity);

        void showEmptyView(boolean isEmpty);

        void showCurrentItem(int index);

        void showBalance(double balance);

        void setArgument(WalletEntity entity);

        void notifyItemChanged(int position);

        void notifyAllChanged();
    }

    public interface Presenter extends IPresenter<View> {

        void start();

        List<WalletEntity> getRecycleViewDataSource();

        void fetchWalletList();

        void clickRecycleViewItem(WalletEntity walletEntity);

        void backupWallet();

        boolean needBackup(WalletEntity walletEntity);

    }
}
