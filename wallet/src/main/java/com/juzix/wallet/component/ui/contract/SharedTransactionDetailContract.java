package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.SharedTransactionEntity;
import com.juzix.wallet.entity.TransactionResult;

import java.util.List;

/**
 * @author matrixelement
 */
public class SharedTransactionDetailContract {

    public interface View extends IView {

        SharedTransactionEntity getTransactionFromIntent();

        String getAddressFromIntent();

        void setTransactionDetailInfo(SharedTransactionEntity transactionEntity, String queryAddress);

        void showTransactionResult(List<TransactionResult> transactionResultList,SharedTransactionEntity transactionEntity);
    }

    public interface Presenter extends IPresenter<View> {

        void fetchTransactionDetail();

    }
}
