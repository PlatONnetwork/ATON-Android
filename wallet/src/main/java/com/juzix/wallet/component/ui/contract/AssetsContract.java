package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.WalletEntity;

import java.util.ArrayList;

/**
 * @author matrixelement
 */
public class AssetsContract {

    public interface View extends IView {
        void showTotalBalance(double totalBalance);
        void showWalletList(WalletEntity selectedWallet);
        void showWalletTab(WalletEntity walletEntity, int tabIndex);
        void showWalletInfo(WalletEntity walletEntity);
        void showEmptyView(boolean isEmpty);
        void showCurrentItem(int index);
        void showBalance(double balance);
        int getTabIndex();
        void setArgument(WalletEntity entity);
        void notifyWalletChanged(int position);
        void notifyAllChanged();
    }

    public interface Presenter extends IPresenter<View> {
        void init();
        void start();
        void scanQRCode();
        ArrayList<WalletEntity> getRecycleViewDataSource();
        void fetchWalletList();
        void clickRecycleViewItem(WalletEntity walletEntity);
        void createIndividualWallet();
        void createSharedWallet();
        void importIndividualWallet();
        void addSharedWallet();
        void backupWallet();
        boolean needBackup(WalletEntity walletEntity);
        void updateUnreadMessage(String contractAddress, boolean hasUnreadMessage);
    }
}
