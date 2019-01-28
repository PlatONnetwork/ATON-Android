package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;

import java.util.List;

/**
 * @author matrixelement
 */
public class MyVoteContract {

    public static class Entity {
        public String avatar;
        public String candidateName;
        public String candidateId;
        public String region;
        public double voteStaked;
        public long   validVotes;
        public long   invalidVotes;
        public double profit;
    }

    public interface View extends IView {
        void showTicketInfo(double voteStaked, long validVotes, long invalidVotes, double profit);

        void updateTickets(List<Entity> entityList);
    }

    public interface Presenter extends IPresenter<View> {

        void start();

        void refresh();

        void enterVoteDetailActivity(MyVoteContract.Entity entity);

        void enterVoteActivity(MyVoteContract.Entity entity);
    }
}
