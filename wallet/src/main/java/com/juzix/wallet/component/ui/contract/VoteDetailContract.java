package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.BatchVoteTransactionEntity;
import com.juzix.wallet.entity.BatchVoteTransactionWrapEntity;
import com.juzix.wallet.entity.VoteDetailItemEntity;

import java.util.List;

/**
 * @author matrixelement
 */
public class VoteDetailContract {
    public static class Entity {
        public String name;
        public String candidateId;
        public long createTime;
        public long validVotes;
        public long invalidVotes;
        public String ticketPrice;
        public double voteStaked;
        public double voteUnstaked;
        public double profit;
        public String walletAddress;
        public String walletName;
        public long expirTime;
    }

    public interface View extends IView {

        BatchVoteTransactionWrapEntity getBatchVoteWrapTransactionFromIntent();

        void showNodeDetailInfo(BatchVoteTransactionEntity batchVoteTransactionEntity);

        void notifyDataSetChanged(List<VoteDetailItemEntity> voteDetailItemEntityList);

    }

    public interface Presenter extends IPresenter<View> {

        void loadData();

    }
}
