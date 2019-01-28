package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.IndividualTransactionEntity;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.entity.TransactionEntity;
import com.juzix.wallet.entity.WalletEntity;

import java.util.List;

/**
 * @author matrixelement
 */
public class IndividualWalletDetailContract {

    public interface View extends IView {

        IndividualWalletEntity getWalletEntityFromIntent();

        void updateWalletInfo(IndividualWalletEntity walletEntity);

        void notifyTransactionListChanged(List<TransactionEntity> transactionEntityList, String walletAddress);
    }

    public interface Presenter extends IPresenter<View> {

        void fetchWalletDetail();

        void fetchWalletTransactionList();

        void enterTransactionDetailActivity(TransactionEntity transactionEntity);

        void enterReceiveTransactionActivity();

        void enterVoteActivity();
    }
}
