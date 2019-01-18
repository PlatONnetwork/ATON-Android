package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.IndividualWalletEntity;

public class ManageIndividualWalletContract {

    public interface View extends IView {
        int TYPE_DELETE_WALLET       = -1;
        int TYPE_MODIFY_NAME        = 1;
        int TYPE_EXPORT_PRIVATE_KEY = 2;
        int TYPE_EXPORT_KEYSTORE    = 3;

        IndividualWalletEntity getWalletEntityFromIntent();

        void showWalletName(String name);

        void showWalletAddress(String address);

        void showErrorDialog(String title, String content);

        void showWalletAvatar(String avatar);

        void dimissErrorDialog();

        void showModifyNameDialog();

        void dimissModifyNameDialog();

        void showPasswordDialog(int type);

        void dimissPasswordDialog();
    }

    public interface Presenter extends IPresenter<View> {
        void start();

        void validPassword(int type, String password);

        void deleteWallet();

        void modifyName(String name);

        void exportPrivateKey();

        void exportKeystore();
    }
}
