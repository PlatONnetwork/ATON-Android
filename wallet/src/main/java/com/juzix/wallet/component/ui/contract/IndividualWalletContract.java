package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.IndividualWalletEntity;

import java.util.List;

/**
 * @author matrixelement
 */
public class IndividualWalletContract {

    public interface View extends IView {

        void notifyWalletListChanged(List<IndividualWalletEntity> walletEntityList);

        void updateItem(IndividualWalletEntity walletEntity);

        void updateWalletBalance(double balance);
    }

    public interface Presenter extends IPresenter<View> {

        void fetchIndividualWalletList();

    }


}
