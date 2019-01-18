package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;

public class ImportIndividualMnemonicPhraseContract {

    public interface View extends IView {
        String getKeystoreFromIntent();
        void showQRCode(String QRCode);
        void showMnemonicPhraseError(String text, boolean isVisible);
        void showNameError(String text, boolean isVisible);
        void showPasswordError(String text, boolean isVisible);
    }

    public interface Presenter extends IPresenter<ImportIndividualMnemonicPhraseContract.View> {
        void init();
        void parseQRCode(String QRCode);
        void importMnemonic(String phrase, String name, String password, String repeatPassword);
    }
}
