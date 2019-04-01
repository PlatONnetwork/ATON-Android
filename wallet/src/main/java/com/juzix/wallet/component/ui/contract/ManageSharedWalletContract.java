package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.entity.OwnerEntity;
import com.juzix.wallet.entity.SharedWalletEntity;

import org.web3j.crypto.Credentials;

import java.util.ArrayList;
import java.util.List;

/**
 * @author matrixelement
 */
public class ManageSharedWalletContract {

    public interface View extends IView {
        int TYPE_DELETE_WALLET      = -1;
        int TYPE_MODIFY_WALLET_NAME = 1;
        int TYPE_MODIFY_MEMBER_NAME = 2;

        SharedWalletEntity getWalletEntityFromIntent();

        void showWallet(SharedWalletEntity walletEntity);

        void showMember(List<OwnerEntity> addressEntityList);

        void showErrorDialog(String title, String content, int type, IndividualWalletEntity walletEntity);

        void showModifyWalletNameDialog();

        void showModifyMemberNameDialog(int memberIndex);

        void showPasswordDialog(int type, int index, IndividualWalletEntity walletEntity);

        void updateWalletName(String walletName);

        void updateWalletMemberName(String newMemberName, int position);
    }

    public interface Presenter extends IPresenter<View> {

        void showWalletInfo();

        void modifyWalletName(String name);

        void modifyMemberName(int memberIndex, String name);

        void validPassword(int type, Credentials credentials, int index);

        void deleteAction(int type);

        void deleteWallet();

        boolean isExists(String walletName);
    }
}
