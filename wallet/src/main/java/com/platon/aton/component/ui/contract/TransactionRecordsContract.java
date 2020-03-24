package com.platon.aton.component.ui.contract;

import com.platon.aton.component.ui.IContext;
import com.platon.aton.entity.Transaction;

import java.util.List;

/**
 * @author matrixelement
 */
public class TransactionRecordsContract {

    public interface View extends IContext {

        void finishLoadMore();

        void finishRefresh();

        void notifyDataSetChanged(List<Transaction> oldTransactionList, List<Transaction> newTransactionList, boolean isWalletChanged);
    }

    public interface Presenter extends IPresenter<View> {

        void fetchTransactions(String direction, List<String> addressList, boolean isWalletChanged);
    }
}
