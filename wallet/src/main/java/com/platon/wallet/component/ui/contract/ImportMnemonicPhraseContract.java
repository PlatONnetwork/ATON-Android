package com.platon.wallet.component.ui.contract;

import com.platon.wallet.component.ui.base.IPresenter;
import com.platon.wallet.component.ui.base.IView;

import java.util.List;

public class ImportMnemonicPhraseContract {

    public interface View extends IView {
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
