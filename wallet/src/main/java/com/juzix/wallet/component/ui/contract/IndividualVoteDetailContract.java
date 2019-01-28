package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.SingleVoteEntity;

/**
 * @author matrixelement
 */
public class IndividualVoteDetailContract {

    public interface View extends IView {

        String getTransactionUuidFromIntent();

        void setTransactionDetailInfo(SingleVoteEntity voteEntity);
    }

    public interface Presenter extends IPresenter<View> {

        void fetchTransactionDetail();

    }
}
