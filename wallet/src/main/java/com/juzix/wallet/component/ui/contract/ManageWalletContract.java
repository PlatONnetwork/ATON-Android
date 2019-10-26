package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.Wallet;

import org.web3j.crypto.Credentials;

public class ManageWalletContract {

    public interface View extends IView {
        int TYPE_DELETE_WALLET       = -1;
        int TYPE_MODIFY_NAME        = 1;
        int TYPE_EXPORT_PRIVATE_KEY = 2;
        int TYPE_EXPORT_KEYSTORE    = 3;

        Wallet getWalletEntityFromIntent();

        void showWalletName(String name);

//        void showWalletAddress(String address);

        void showErrorDialog(String title, String content, int type, Wallet walletEntity);

//        void showWalletAvatar(String avatar);

        void showModifyNameDialog(String name);

        void showPasswordDialog(int type, Wallet walletEntity);

        void enableBackup(boolean enabled);

        void enableDelete(boolean enabled);

        void showWalletInfo(Wallet wallet);
    }

    public interface Presenter extends IPresenter<View> {
        void showIndividualWalletInfo();

        void validPassword(int type, Credentials credentials);

        void deleteWallet();

        void modifyName(String name);

        void backup();

        boolean isExists(String walletName);

        void deleteObservedWallet();


    }
}
