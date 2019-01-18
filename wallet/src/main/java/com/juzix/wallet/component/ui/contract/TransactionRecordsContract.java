package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.TransactionEntity;

import java.util.ArrayList;

/**
 * @author matrixelement
 */
public class TransactionRecordsContract {

    public interface View extends IView {
        void showTransactions(ArrayList<TransactionEntity> transactionEntities);
    }

    public interface Presenter extends IPresenter<View> {
        void fetchTransactions();
        void refreshRecords();
    }
}
