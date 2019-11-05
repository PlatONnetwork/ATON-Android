package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;

public class ImportObservedContract {

    public interface View extends IView {
        void showQRCode(String QRCode);

        void enableImportObservedWallet(boolean isCan);

        String getWalletAddress();

        void enablePaste(boolean enabled);

    }


    public interface Presenter extends IPresenter<View> {

        void parseQRCode(String QRCode);

        void IsImportObservedWallet(String content);

        void importWalletAddress(String walletAddress);

        void checkPaste();
    }
}
