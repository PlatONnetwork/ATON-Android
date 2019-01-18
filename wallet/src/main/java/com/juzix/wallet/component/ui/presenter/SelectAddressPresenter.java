package com.juzix.wallet.component.ui.presenter;

import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.SelectAddressContract;

/**
 * @author matrixelement
 */
public class SelectAddressPresenter extends BasePresenter<SelectAddressContract.View> implements SelectAddressContract.Presenter {

    public SelectAddressPresenter(SelectAddressContract.View view) {
        super(view);
    }
}
