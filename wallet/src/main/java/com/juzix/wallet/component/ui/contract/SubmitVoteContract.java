package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.Wallet;

/**
 * @author matrixelement
 */
public class SubmitVoteContract {

    public interface View extends IView {

        String getCandidateIdFromIntent();

        String getCandidateNameFromIntent();

        void showNodeInfo(String nodeName, String nodeId);

        void showSelectedWalletInfo(Wallet individualWalletEntity);

        void showVotePayInfo(double ticketPrice, double ticketPayAmount);

        String getTicketNum();

        void setTicketNum(int ticketNum);

    }

    public interface Presenter extends IPresenter<View> {

        void showVoteInfo();

        void showVotePayInfo();

        void submitVote();

        void showSelectWalletDialogFragment();

        void updateVotePayInfo();

    }
}
