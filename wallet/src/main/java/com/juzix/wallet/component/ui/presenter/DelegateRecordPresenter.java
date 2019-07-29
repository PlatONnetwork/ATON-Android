package com.juzix.wallet.component.ui.presenter;

import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.DelegateRecordContract;

public class DelegateRecordPresenter extends BasePresenter<DelegateRecordContract.View> implements DelegateRecordContract.Presentet {
    public DelegateRecordPresenter(DelegateRecordContract.View view) {
        super(view);
    }
}
