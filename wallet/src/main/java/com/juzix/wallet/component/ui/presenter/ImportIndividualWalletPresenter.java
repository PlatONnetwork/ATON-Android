package com.juzix.wallet.component.ui.presenter;

import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.ImportIndividualWalletContract;

public class ImportIndividualWalletPresenter extends BasePresenter<ImportIndividualWalletContract.View> implements ImportIndividualWalletContract.Presenter {

    public ImportIndividualWalletPresenter(ImportIndividualWalletContract.View view) {
        super(view);
    }
}
