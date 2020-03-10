package com.platon.wallet.component.ui.contract;

import com.platon.wallet.component.ui.base.IPresenter;
import com.platon.wallet.component.ui.base.IView;
import com.platon.wallet.entity.Transaction;

import java.util.List;

/**
 * @author matrixelement
 */
public class TransactionRecordsContract {

    public interface View extends IView {

        void finishLoadMore();

        void finishRefresh();

        void notifyDataSetChanged(List<Transaction> oldTransactionList, List<Transaction> newTransactionList, boolean isWalletChanged);
    }

    public interface Presenter extends IPresenter<View> {

        void fetchTransactions(String direction, List<String> addressList, boolean isWalletChanged);
    }
}
