package com.juzix.wallet.component.ui.presenter;

import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.IndividualWalletManagerContract;
import com.juzix.wallet.engine.IndividualWalletManager;

public class IndividualWalletManagerPresenter extends BasePresenter<IndividualWalletManagerContract.View> implements IndividualWalletManagerContract.Presenter{

    public IndividualWalletManagerPresenter(IndividualWalletManagerContract.View view) {
        super(view);
    }

    @Override
    public void refresh() {
        getView().showList(IndividualWalletManager.getInstance().getWalletList());
    }
}
