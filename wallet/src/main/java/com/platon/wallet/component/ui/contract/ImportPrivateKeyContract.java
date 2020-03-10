package com.platon.wallet.component.ui.contract;

import com.platon.wallet.component.ui.base.IPresenter;
import com.platon.wallet.component.ui.base.IView;

public class ImportPrivateKeyContract {

    public interface View extends IView {
        String getKeystoreFromIntent();
        void showQRCode(String QRCode);
        void showPrivateKeyError(String text, boolean isVisible);
        void showNameError(String text, boolean isVisible);
        void showPasswordError(String text, boolean isVisible);
        void enablePaste(boolean enabled);
    }

    public interface Presenter extends IPresenter<View> {
        void init();
        void parseQRCode(String QRCode);
        void importPrivateKey(String privateKey, String name, String password, String repeatPassword);
        void checkPaste();
        boolean isExists(String walletName);
    }
}
