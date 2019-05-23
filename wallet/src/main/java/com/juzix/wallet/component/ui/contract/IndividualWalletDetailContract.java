package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.Transaction;
import com.juzix.wallet.entity.Wallet;

import java.util.List;

/**
 * @author matrixelement
 */
public class IndividualWalletDetailContract {

    public interface View extends IView {

        Wallet getWalletEntityFromIntent();

        void updateWalletInfo(Wallet walletEntity);

        void notifyTransactionListChanged(List<Transaction> transactionEntityList, String walletAddress);
    }

    public interface Presenter extends IPresenter<View> {

        void fetchWalletDetail();

        void fetchWalletTransactionList();

        void enterTransactionDetailActivity(Transaction transactionEntity);

        void enterReceiveTransactionActivity();

        void enterVoteActivity();
    }
}
