package com.platon.aton.component.ui.contract;

import android.graphics.Bitmap;

import com.platon.aton.component.ui.base.IPresenter;
import com.platon.aton.component.ui.base.IView;
import com.platon.aton.entity.Wallet;

public class UserQRCodeContract {

    public interface View extends IView {

        Wallet getWalletFromIntent();

        void showWalletName(String walletName);

        void showWalletAddress(String walletAddress);

        void showQRCode(Bitmap bitmap, int size);
    }

    public interface Presenter extends IPresenter<View> {
        void update();

        void saveQRCode(android.view.View view);
    }
}