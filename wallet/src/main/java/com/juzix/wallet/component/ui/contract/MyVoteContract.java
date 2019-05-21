package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.VoteSummaryEntity;
import com.juzix.wallet.entity.VotedCandidateEntity;

import java.util.List;

/**
 * @author matrixelement
 */
public class MyVoteContract {

    public interface View extends IView {

        /**
         * 获取我的投票页面数据 显示
         *
         * @param entityList
         */
        void showMyVoteListData(List<VotedCandidateEntity> entityList);

        /**
         * 获取数据失败
         */
        void showMyVoteListDataFailed();


        /**
         * 显示我的投票页面头部数据
         *
         * @param voteSummaryEntityList
         */
        void showBatchVoteSummary(List<VoteSummaryEntity> voteSummaryEntityList);
    }

    public interface Presenter extends IPresenter<View> {
        /**
         * 加载数据
         */
        void loadMyVoteData();
    }
}
