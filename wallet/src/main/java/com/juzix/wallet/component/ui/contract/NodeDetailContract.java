package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.CandidateDetailEntity;
import com.juzix.wallet.entity.CandidateEntity;

/**
 * @author matrixelement
 */
public class NodeDetailContract {

    public interface View extends IView {

        /**
         * @return
         */
        String getCandidateIdFromIntent();

        /**
         * 展示节点详情信息
         *
         * @param candidateDetailEntity
         */
        void showNodeDetailInfo(CandidateDetailEntity candidateDetailEntity);
    }

    public interface Presenter extends IPresenter<View> {

        /**
         * 获取实时的票龄
         */
        void getNodeDetailInfo();

        /**
         * 投票
         */
        void voteTicket();
    }
}
