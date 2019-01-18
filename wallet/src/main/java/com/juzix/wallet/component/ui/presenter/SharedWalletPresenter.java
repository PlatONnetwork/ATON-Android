package com.juzix.wallet.component.ui.presenter;


import com.juzix.wallet.R;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.SharedWalletContract;
import com.juzix.wallet.component.ui.view.AddSharedWalletActivity;
import com.juzix.wallet.component.ui.view.CreateSharedWalletActivity;
import com.juzix.wallet.engine.IndividualWalletManager;
import com.juzix.wallet.engine.SharedWalletTransactionManager;
import com.juzix.wallet.engine.SharedWalletManager;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.entity.SharedWalletEntity;
import com.juzix.wallet.utils.BigDecimalUtil;

import java.util.ArrayList;

/**
 * @author matrixelement
 */
public class SharedWalletPresenter extends BasePresenter<SharedWalletContract.View> implements SharedWalletContract.Presenter {

    private final static String TAG = SharedWalletPresenter.class.getSimpleName();
    public SharedWalletPresenter(SharedWalletContract.View view) {
        super(view);
    }

    @Override
    public void fetchSharedWalletList() {
        ArrayList<SharedWalletEntity> walletEntityList = SharedWalletManager.getInstance().getWalletList();
        double totalBalance = 0.0D;
        for (SharedWalletEntity walletEntity : walletEntityList) {
            totalBalance = BigDecimalUtil.add(totalBalance, walletEntity.getBalance());
        }
        if (isViewAttached()){
            getView().updateWalletBalance(totalBalance);
            getView().notifyWalletListChanged(walletEntityList);
        }
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
