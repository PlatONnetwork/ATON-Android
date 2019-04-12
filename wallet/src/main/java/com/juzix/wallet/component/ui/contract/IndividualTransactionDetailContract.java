package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.IndividualTransactionEntity;

/**
 * @author matrixelement
 */
public class IndividualTransactionDetailContract {

    public interface View extends IView {

        IndividualTransactionEntity getTransactionFromIntent();

        String getAddressFromIntent();

        void setTransactionDetailInfo(IndividualTransactionEntity transactionEntity, String address);
    }

    public interface Presenter extends IPresenter<View> {

    }
}
