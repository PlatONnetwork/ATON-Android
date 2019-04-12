package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.SharedTransactionEntity;
import com.juzix.wallet.entity.TransactionEntity;
import com.juzix.wallet.entity.WalletEntity;

import java.util.List;

/**
 * @author matrixelement
 */
public class TransactionsContract {

    public interface View extends IView {

        WalletEntity getWalletFromIntent();

        void notifyTransactionListChanged(List<TransactionEntity> transactionEntityList);

        void notifyItem(SharedTransactionEntity transactionEntity);
    }

    public interface Presenter extends IPresenter<View> {

        void updateWalletEntity();

        void fetchWalletTransactionList();

        void enterTransactionDetailActivity(TransactionEntity transactionEntity);
    }
}
