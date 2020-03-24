package com.platon.aton.component.ui.contract;

import com.platon.aton.component.ui.IContext;

public class ImportObservedContract {

    public interface View extends IContext {
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
