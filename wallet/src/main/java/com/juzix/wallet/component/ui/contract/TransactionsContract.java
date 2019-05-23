package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.Transaction;

import java.util.List;

/**
 * @author matrixelement
 */
public class TransactionsContract {

    public interface View extends IView {

        void notifyTransactionListChanged(List<Transaction> transactionEntityList);
    }

    public interface Presenter extends IPresenter<View> {

    }
}
