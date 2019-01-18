package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.SharedTransactionEntity;
import com.juzix.wallet.entity.SharedWalletEntity;
import com.juzix.wallet.entity.TransactionEntity;

import java.util.List;

/**
 * @author matrixelement
 */
public class SharedWalletDetailContract {

    public interface View extends IView {

        SharedWalletEntity getSharedWalletFromIntent();

        void updateWalletInfo(SharedWalletEntity walletEntity);

        void notifyTransactionListChanged(List<TransactionEntity> transactionEntityList, String walletAddress);

        void notifyTransactionChanged(SharedTransactionEntity transactionEntity, String walletAddress);

        void hideSendButton();
    }

    public interface Presenter extends IPresenter<View> {
        int REQUEST_CODE_SIGNING = 100;

        void fetchWalletDetail();

        void enterTransactionDetailActivity(TransactionEntity transactionEntity);

        void enterReceiveTransactionActivity();
    }
}
