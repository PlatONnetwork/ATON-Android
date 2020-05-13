package com.platon.aton.component.ui.contract;

import android.graphics.Bitmap;

import com.platon.aton.component.ui.IContext;
import com.platon.aton.entity.Wallet;
import com.platon.framework.base.BaseViewImp;
import com.platon.framework.base.IPresenter;

/**
 * @author matrixelement
 */
public class ReceiveTransationContract {

    public interface View extends BaseViewImp {

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
