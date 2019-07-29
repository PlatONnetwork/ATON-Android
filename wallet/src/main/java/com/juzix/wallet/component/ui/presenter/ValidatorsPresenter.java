package com.juzix.wallet.component.ui.presenter;

import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.ValidatorsContract;

public class ValidatorsPresenter extends BasePresenter<ValidatorsContract.View> implements ValidatorsContract.Presenter {
    public ValidatorsPresenter(ValidatorsContract.View view) {
        super(view);

    }

}
