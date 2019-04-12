package com.juzix.wallet.component.ui.presenter;

import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.IndividualTransactionDetailContract;
import com.juzix.wallet.entity.IndividualTransactionEntity;


/**
 * @author matrixelement
 */
public class IndividualTransactionDetailPresenter extends BasePresenter<IndividualTransactionDetailContract.View> implements IndividualTransactionDetailContract.Presenter {

    private IndividualTransactionEntity mTransactionEntity;
    private String mQueryAddress;

    public IndividualTransactionDetailPresenter(IndividualTransactionDetailContract.View view) {
        super(view);
        mTransactionEntity = view.getTransactionFromIntent();
        mQueryAddress = view.getAddressFromIntent();
    }


}
