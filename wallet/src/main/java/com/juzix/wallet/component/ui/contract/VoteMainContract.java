package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.CandidateEntity;
import com.juzix.wallet.entity.IndividualWalletEntity;

import java.util.List;

/**
 * @author matrixelement
 */
public class VoteMainContract {

    public interface View extends IView {

        IndividualWalletEntity getWalletFromIntent();

        void setTicketInfo(double voteRatio, long votedTicketCount, String ticketPrice);

        void notifyDataSetChanged(List<CandidateEntity> candidateList);
    }

    public interface Presenter extends IPresenter<View> {
        int SORT_DEFAULT  = 0;
        int SORT_REWARD   = 1;
        int SORT_LOCATION = 2;

        void start();

        void destroy();

        void sort(int type);

        void search(String keyword);

        void voteTicket(CandidateEntity candidateEntity);
    }
}
