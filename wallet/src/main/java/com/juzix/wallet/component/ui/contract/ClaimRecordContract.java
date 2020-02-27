package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.component.ui.presenter.Direction;
import com.juzix.wallet.entity.ClaimRewardRecord;

import java.util.List;

public class ClaimRecordContract {

    public interface View extends IView {

        void getRewardTransactionsResult(List<ClaimRewardRecord> newClaimRewardRecordList);

        void finishLoadMore();

        void finishRefresh();

    }

    public interface Presenter extends IPresenter<ClaimRecordContract.View> {

        void getRewardTransactions(@Direction String direction);
    }
}
