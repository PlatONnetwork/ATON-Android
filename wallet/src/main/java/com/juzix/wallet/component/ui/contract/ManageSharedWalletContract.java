package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.OwnerEntity;
import com.juzix.wallet.entity.SharedWalletEntity;

import java.util.ArrayList;

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

        void showMember(ArrayList<OwnerEntity> addressEntityList);

        void showOwner(String individualWalletName, String individualWalletAddress);

        void showErrorDialog(String title, String content);

        void dimissErrorDialog();

        void showModifyWalletNameDialog();

        void dimissModifyWalletNameDialog();

        void showModifyMemberNameDialog(int memberIndex);

        void dimissModifyMemberNameDialog();

        void showPasswordDialog(int type, int index);

        void dimissPasswordDialog();
    }

    public interface Presenter extends IPresenter<View> {

        boolean checkIndividualWallet();

        void start();

        void modifyWalletName(String name);

        void modifyMemberName(int memberIndex, String name);

        void validPassword(int type, String password, int index);

        void deleteAction(int type);

        void deleteWallet();
    }
}
