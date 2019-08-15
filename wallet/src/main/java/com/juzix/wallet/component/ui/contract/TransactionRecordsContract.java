package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.Transaction;

import java.util.List;

/**
 * @author matrixelement
 */
public class TransactionRecordsContract {

    public interface View extends IView {

        void finishLoadMore();

        void finishRefresh();

        void notifyItemRangeInserted(List<Transaction> transactionList, int positionStart, int itemCount);

        void showTransactions(List<Transaction> transactionList);
    }

    public interface Presenter extends IPresenter<View> {

        void fetchTransactions(String direction,List<String> addressList);
    }
}
