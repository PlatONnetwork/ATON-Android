package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.SharedTransactionEntity;
import com.juzix.wallet.entity.SharedWalletEntity;
import com.juzix.wallet.entity.TransactionResult;

import java.util.ArrayList;

/**
 * @author matrixelement
 */
public class SharedTransactionDetailContract {

    public interface View extends IView {

        SharedTransactionEntity getTransactionFromIntent();

        void setTransactionDetailInfo(SharedTransactionEntity transactionEntity);

        void showTransactionResult(ArrayList<TransactionResult> transactionResultList);

        void showHash(String hash);

        void visibleHash(boolean visible);
    }

    public interface Presenter extends IPresenter<View> {

        void fetchTransactionDetail();

    }
}
