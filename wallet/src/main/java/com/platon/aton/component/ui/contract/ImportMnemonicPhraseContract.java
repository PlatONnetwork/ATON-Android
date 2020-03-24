package com.platon.aton.component.ui.contract;

import com.platon.aton.component.ui.IContext;
import com.platon.aton.component.ui.base.IView;

import java.util.List;

public class ImportMnemonicPhraseContract {

    public interface View extends IContext {
        String getKeystoreFromIntent();
        void showMnemonicWords(List<String> words);
        void showMnemonicPhraseError(String text, boolean isVisible);
        void showNameError(String text, boolean isVisible);
        void showPasswordError(String text, boolean isVisible);
    }

    public interface Presenter extends IPresenter<ImportMnemonicPhraseContract.View> {
        void init();
        void parseQRCode(String QRCode);
        void importMnemonic(String phrase, String name, String password, String repeatPassword);
        boolean isExists(String walletName);
    }
}
