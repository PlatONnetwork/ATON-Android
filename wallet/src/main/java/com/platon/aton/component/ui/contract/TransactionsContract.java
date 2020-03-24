package com.platon.aton.component.ui.contract;

import com.platon.aton.component.ui.IContext;
import com.platon.aton.component.ui.base.IView;
import com.platon.aton.entity.Transaction;

import java.util.List;

/**
 * @author matrixelement
 */
public class TransactionsContract {

    public interface View extends IContext {

        void notifyDataSetChanged(List<Transaction> oldTransactionList, List<Transaction> newTransactionList, String queryAddress, boolean loadLatestData);
    }

    public interface Presenter extends IPresenter<View> {

        void loadLatestData();

        void loadNew(String direction);

        void deleteTransaction(Transaction transaction);

        void addNewTransaction(Transaction transaction);
    }

}
