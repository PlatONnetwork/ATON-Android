package com.juzix.wallet.component.ui.presenter;

import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.DelegateContract;
import com.juzix.wallet.component.ui.dialog.SelectWalletDialogFragment;
import com.juzix.wallet.engine.WalletManager;
import com.juzix.wallet.entity.Wallet;

public class DelegatePresenter extends BasePresenter<DelegateContract.View> implements DelegateContract.Presenter {
    private Wallet mWallet;

    public DelegatePresenter(DelegateContract.View view) {
        super(view);

    }


    @Override
    public void showSelectWalletDialogFragment() {

        SelectWalletDialogFragment.newInstance(mWallet != null ? mWallet.getUuid() : "", true)
                .setOnItemClickListener(new SelectWalletDialogFragment.OnItemClickListener() {
                    @Override
                    public void onItemClick(Wallet walletEntity) {
                        if (isViewAttached()) {
                            mWallet = walletEntity;
                            getView().showSelectedWalletInfo(walletEntity);
                        }
                    }
                })
                .show(currentActivity().getSupportFragmentManager(), "showSelectWalletDialog");

    }

    @Override
    public void showWalletInfo() {
        showSelectedWalletInfo();
    }

    private void showSelectedWalletInfo() {
        mWallet = WalletManager.getInstance().getFirstValidIndividualWalletBalance();
        if (isViewAttached() && mWallet != null) {
            getView().showSelectedWalletInfo(mWallet);
        }

    }
}
