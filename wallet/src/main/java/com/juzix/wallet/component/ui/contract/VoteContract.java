package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.CandidateEntity;
import com.juzix.wallet.entity.IndividualWalletEntity;

/**
 * @author matrixelement
 */
public class VoteContract {

    public interface View extends IView {
        CandidateEntity getCandidateFromIntent();
        String getVotes();
        void showVoteInfo(int iconRes, String name, String publicKey);
        void showPayInfo(double price, double expectedPay);
        void updateSelectOwner(IndividualWalletEntity walletEntity);
        void setNextButtonEnable(boolean enabled);
        void showVotes(long votes);
    }

    public interface Presenter extends IPresenter<View> {
        void init();
        void showSelectWalletDialogFragment();
        void updateSelectOwner(IndividualWalletEntity walletEntity);
        void setVotes(String text);
        void addVotes();
        void subVotes();
        void submit();
    }
}
