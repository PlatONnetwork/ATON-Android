package com.juzix.wallet.component.ui.presenter;

import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.ValidatorsDetailContract;

public class ValidatorsDetailPresenter extends BasePresenter<ValidatorsDetailContract.View> implements ValidatorsDetailContract.Presenter {
    public ValidatorsDetailPresenter(ValidatorsDetailContract.View view) {

        super(view);
    }

}
