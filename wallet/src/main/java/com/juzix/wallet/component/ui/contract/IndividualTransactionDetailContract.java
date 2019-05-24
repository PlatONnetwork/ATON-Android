package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.IndividualTransactionEntity;
import com.juzix.wallet.entity.Transaction;

/**
 * @author matrixelement
 */
public class IndividualTransactionDetailContract {

    public interface View extends IView {

        Transaction getTransactionFromIntent();

        String getAddressFromIntent();

        void setTransactionDetailInfo(Transaction transaction, String queryAddress, String senderWalletName);

    }

    public interface Presenter extends IPresenter<View> {

        void loadData();

        void updateTransactionDetailInfo(Transaction transaction);

    }
}
