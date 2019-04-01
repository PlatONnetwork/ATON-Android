package com.juzix.wallet.component.ui.contract;

import android.graphics.Bitmap;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.entity.WalletEntity;

/**
 * @author matrixelement
 */
public class ReceiveTransationContract {

    public interface View extends IView {

        WalletEntity getWalletFromIntent();

        void setWalletInfo(WalletEntity walletEntity);

        void showWarnDialogFragment();

        void setWalletAddressQrCode(Bitmap bitmap);

        android.view.View shareView(String name, String address, Bitmap bitmap);
    }

    public interface Presenter extends IPresenter<View> {

        void loadData();

        void shareView();
    }
}
