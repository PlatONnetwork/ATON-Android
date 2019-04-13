package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.BatchVoteTransactionWrapEntity;
import com.juzix.wallet.entity.VoteSummaryEntity;

import java.util.List;

/**
 * @author matrixelement
 */
public class MyVoteContract {

    public interface View extends IView {

        void showBatchVoteSummary(List<VoteSummaryEntity> voteSummaryEntityList);

        void showBatchVoteTransactionList(List<BatchVoteTransactionWrapEntity> batchVoteTransactionWrapEntityList);
    }

    public interface Presenter extends IPresenter<View> {

        void loadData();

        void voteTicket(String candidateId);
    }
}
