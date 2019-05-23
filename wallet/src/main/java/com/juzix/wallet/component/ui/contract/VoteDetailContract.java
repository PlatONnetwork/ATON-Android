package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.VotedCandidate;

import java.util.List;

/**
 * @author matrixelement
 */
public class VoteDetailContract {

    public interface View extends IView {

        String getCandidateIdFromIntent();

        String getCandidateNameFromIntent();

        /**
         * 获取投票详情列表数据 成功
         *
         * @param list
         */
        void getVoteDetailListDataSuccess(List<VotedCandidate> list);


        /**
         * 获取数据失败
         */
        void getVoteDetailListDataFailed();

        void showNodeInfo(String nodeName, String nodeId);

    }

    public interface Presenter extends IPresenter<View> {

        /**
         * 加载数据
         */
        void loadVoteDetailData(int beginSequence);




    }
}
