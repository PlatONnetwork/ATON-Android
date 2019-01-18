package com.juzix.wallet.component.ui.presenter;

import com.juzix.wallet.R;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.SharedWalletManagerContract;
import com.juzix.wallet.component.ui.view.AddSharedWalletActivity;
import com.juzix.wallet.component.ui.view.CreateSharedWalletActivity;
import com.juzix.wallet.engine.IndividualWalletManager;
import com.juzix.wallet.engine.SharedWalletManager;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.utils.BigDecimalUtil;

import java.util.ArrayList;

public class SharedWalletManagerPresenter extends BasePresenter<SharedWalletManagerContract.View> implements SharedWalletManagerContract.Presenter{

    public SharedWalletManagerPresenter(SharedWalletManagerContract.View view) {
        super(view);
    }

    @Override
    public void refresh() {
        getView().showList(SharedWalletManager.getInstance().getWalletList());
    }

    @Override
    public void createWallet() {
        ArrayList<IndividualWalletEntity> walletEntityList = IndividualWalletManager.getInstance().getWalletList();
        if (walletEntityList.isEmpty()){
            showLongToast(R.string.noWalletTips);
            return;
        }
        double totalBalance = 0.0D;
        for (IndividualWalletEntity walletEntity : walletEntityList) {
            totalBalance = BigDecimalUtil.add(totalBalance, walletEntity.getBalance());
        }
        if (totalBalance <= 0){
            showLongToast(R.string.insufficientBalanceTips);
            return;
        }
        CreateSharedWalletActivity.actionStart(currentActivity());
    }

    @Override
    public void addWallet() {
        ArrayList<IndividualWalletEntity> walletEntityList = IndividualWalletManager.getInstance().getWalletList();
        if (walletEntityList.isEmpty()){
            showLongToast(R.string.noWalletTips);
            return;
        }
        AddSharedWalletActivity.actionStart(currentActivity());
    }
}
