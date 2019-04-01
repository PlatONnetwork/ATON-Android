package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.entity.SharedTransactionEntity;
import com.juzix.wallet.entity.TransactionResult;

import java.util.List;

/**
 * @author matrixelement
 */
public class SigningContract {

    public interface View extends IView {

        SharedTransactionEntity getTransactionFromIntent();

        IndividualWalletEntity getIndividualWalletFromIntent();

        void setTransactionDetailInfo(SharedTransactionEntity transactionEntity, String statusDesc);

        void showTransactionResult(List<TransactionResult> transactionResultList);

        void enableButtons(boolean enabaled);

        void updateSigningStatus(String address, TransactionResult.Status status);
    }

    public interface Presenter extends IPresenter<View> {
        void init();

        void fetchTransactionDetail();

        void confirm();

        void revoke();

//        void getBalance();
    }
}
