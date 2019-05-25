package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.SingleVoteEntity;
import com.juzix.wallet.entity.Transaction;

/**
 * @author matrixelement
 */
public class VoteTransactionDetailContract {

    public interface View extends IView {

        void setTransactionDetailInfo(Transaction transaction);
    }

    public interface Presenter extends IPresenter<View> {

        void showVotedTransactionDetail();

    }
}
