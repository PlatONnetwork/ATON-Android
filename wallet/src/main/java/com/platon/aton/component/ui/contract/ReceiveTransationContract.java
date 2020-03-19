package com.platon.aton.component.ui.contract;

import android.graphics.Bitmap;

import com.platon.aton.component.ui.base.IPresenter;
import com.platon.aton.component.ui.base.IView;
import com.platon.aton.entity.Wallet;

/**
 * @author matrixelement
 */
public class ReceiveTransationContract {

    public interface View extends IView {

        Wallet getWalletFromIntent();

        void setWalletInfo(Wallet walletEntity);

        void setWalletAddressQrCode(Bitmap bitmap);

        android.view.View shareView(String name, String address, Bitmap bitmap);
    }

    public interface Presenter extends IPresenter<View> {

        void loadData();

        void shareView();

        void copy();
    }
}
