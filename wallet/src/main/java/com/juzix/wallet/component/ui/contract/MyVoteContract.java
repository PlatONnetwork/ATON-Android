package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.VoteSummary;
import com.juzix.wallet.entity.VotedCandidate;

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
        void showMyVoteListData(List<VotedCandidate> entityList);

        /**
         * 获取数据失败
         */
        void showMyVoteListDataFailed();


        /**
         * 显示我的投票页面头部数据
         *
         * @param voteSummaryEntityList
         */
        void showBatchVoteSummary(List<VoteSummary> voteSummaryEntityList);

        /**
         * 显示我的投票页面头部数据（没投票的时候）
         */

        void  showNoVoteSummary(List<VoteSummary> voteSummaryEntityList);

    }

    public interface Presenter extends IPresenter<View> {
        /**
         * 加载数据
         */
        void loadMyVoteData();
    }
}
