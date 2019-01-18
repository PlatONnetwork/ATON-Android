package com.juzix.wallet.component.ui.contract;

import android.graphics.Bitmap;
import android.view.View;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.SharedWalletEntity;

/**
 * @author matrixelement
 */
public class SharedReceiveTransationContract {

    public interface View extends IView {

        SharedWalletEntity getWalletFromIntent();

        void setWalletInfo(SharedWalletEntity walletInfo);

        void showWarnDialogFragment();

        void setWalletAddressQrCode(Bitmap bitmap);

        android.view.View shareView(String name, String address, Bitmap bitmap);
    }

    public interface Presenter extends IPresenter<View> {

        void loadData();

        void shareView();
    }
}
