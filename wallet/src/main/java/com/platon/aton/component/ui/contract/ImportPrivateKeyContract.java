package com.platon.aton.component.ui.contract;

import com.platon.framework.base.BaseViewImp;
import com.platon.framework.base.IPresenter;

public class ImportPrivateKeyContract {

    public interface View extends BaseViewImp {
        String getKeystoreFromIntent();
        void showQRCode(String QRCode);
        void showPrivateKeyError(String text, boolean isVisible);
        void showNameError(String text, boolean isVisible);
        void showPasswordError(String text, boolean isVisible);
        void enablePaste(boolean enabled);
        void showWalletNumber(int walletNum);
    }

    public interface Presenter extends IPresenter<View> {
        void init();
        void parseQRCode(String QRCode);
        void importPrivateKey(String privateKey, String name, String password, String repeatPassword);
        void checkPaste();
        boolean isExists(String walletName);
        void loadDBWalletNumber();
    }
}
