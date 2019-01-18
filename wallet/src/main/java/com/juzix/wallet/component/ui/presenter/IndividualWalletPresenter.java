package com.juzix.wallet.component.ui.presenter;

import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.IndividualWalletContract;
import com.juzix.wallet.engine.IndividualWalletManager;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.utils.BigDecimalUtil;

import java.util.ArrayList;

/**
 * @author matrixelement
 */
public class IndividualWalletPresenter extends BasePresenter<IndividualWalletContract.View> implements IndividualWalletContract.Presenter {

    private final static String TAG = IndividualWalletPresenter.class.getSimpleName();

    public IndividualWalletPresenter(IndividualWalletContract.View view) {
        super(view);
    }

    @Override
    public void fetchIndividualWalletList() {
        ArrayList<IndividualWalletEntity> walletEntityList = IndividualWalletManager.getInstance().getWalletList();
        double totalBalance = 0.0D;
        for (IndividualWalletEntity walletEntity : walletEntityList) {
            double balance = walletEntity.getBalance();
            totalBalance = BigDecimalUtil.add(totalBalance, balance);
            walletEntity.setBalance(balance);
        }
        if (isViewAttached()){
            getView().updateWalletBalance(totalBalance);
            getView().notifyWalletListChanged(walletEntityList);
        }
    }
}
