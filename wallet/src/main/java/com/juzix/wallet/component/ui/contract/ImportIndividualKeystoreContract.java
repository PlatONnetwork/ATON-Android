package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;

public class ImportIndividualKeystoreContract {

    public interface View extends IView {
        String getKeystoreFromIntent();
        void showQRCode(String QRCode);
        void showKeystoreError(String text, boolean isVisible);
        void showNameError(String text, boolean isVisible);
        void showPasswordError(String text, boolean isVisible);
        void enablePaste(boolean enabled);
    }

    public interface Presenter extends IPresenter<View> {
        void init();
        void parseQRCode(String QRCode);
        void importKeystore(String keystore, String name, String password);
        void checkPaste();
        boolean isExists(String walletName);
    }
}
