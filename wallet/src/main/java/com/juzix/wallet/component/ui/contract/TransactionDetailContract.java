package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.Transaction;

import java.util.List;

/**
 * @author matrixelement
 */
public class TransactionDetailContract {

    public interface View extends IView {

        Transaction getTransactionFromIntent();

        List<String> getAddressListFromIntent();

        void setTransactionDetailInfo(Transaction transaction, List<String> queryAddressList, String senderWalletName);

    }

    public interface Presenter extends IPresenter<View> {

        void loadData();

        void updateTransactionDetailInfo(Transaction transaction);

    }
}
