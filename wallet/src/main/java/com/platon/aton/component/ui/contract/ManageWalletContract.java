package com.platon.aton.component.ui.contract;

import com.platon.aton.component.ui.base.IPresenter;
import com.platon.aton.component.ui.base.IView;
import com.platon.aton.entity.Wallet;

import org.web3j.crypto.Credentials;

public class ManageWalletContract {

    public interface View extends IView {
        int TYPE_DELETE_WALLET       = -1;
        int TYPE_MODIFY_NAME        = 1;
        int TYPE_EXPORT_PRIVATE_KEY = 2;
        int TYPE_EXPORT_KEYSTORE    = 3;

        Wallet getWalletEntityFromIntent();

        void showWalletName(String name);

        void showErrorDialog(String title, String content, int type, Wallet walletEntity);

        void showModifyNameDialog(String name);

        void showPasswordDialog(int type, Wallet walletEntity);

        void showWalletInfo(Wallet wallet);
    }

    public interface Presenter extends IPresenter<View> {
        void showWalletInfo();

        void validPassword(int type, Credentials credentials);

        void deleteWallet();

        void modifyName(String name);

        void backup();

        boolean isExists(String walletName);

        void deleteObservedWallet();


    }
}
