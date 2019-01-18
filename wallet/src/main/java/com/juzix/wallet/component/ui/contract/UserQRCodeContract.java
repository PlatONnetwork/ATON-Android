package com.juzix.wallet.component.ui.contract;

import android.graphics.Bitmap;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.WalletEntity;

public class UserQRCodeContract {

    public interface View extends IView {

        WalletEntity getWalletFromIntent();

        void showWalletName(String walletName);

        void showWalletAddress(String walletAddress);

        void showQRCode(Bitmap bitmap, int size);
    }

    public interface Presenter extends IPresenter<View> {
        void update();

        void saveQRCode(android.view.View view);
    }
}
