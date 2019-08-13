package com.juzix.wallet.component.ui.presenter;

import com.juzhen.framework.network.ApiRequestBody;
import com.juzhen.framework.network.ApiResponse;
import com.juzhen.framework.network.ApiSingleObserver;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.VoteDetailContract;
import com.juzix.wallet.engine.NodeManager;
import com.juzix.wallet.engine.WalletManager;
import com.juzix.wallet.engine.ServerUtils;
import com.juzix.wallet.entity.VotedCandidate;
import com.juzix.wallet.schedulers.BaseSchedulerProvider;
import com.juzix.wallet.schedulers.SchedulerProvider;
import com.juzix.wallet.schedulers.SchedulerTestProvider;
import com.juzix.wallet.utils.RxUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * @author matrixelement
 */
public class VoteDetailPresenter extends BasePresenter<VoteDetailContract.View> implements VoteDetailContract.Presenter {

    private String mNodeId;
    private String mNodeName;
    private BaseSchedulerProvider schedulerProvider;

    public VoteDetailPresenter(VoteDetailContract.View view) {
        super(view);
        schedulerProvider = SchedulerProvider.getInstance();
        mNodeId = getView().getCandidateIdFromIntent();
        mNodeName = getView().getCandidateNameFromIntent();
    }

    public VoteDetailPresenter(VoteDetailContract.View view, SchedulerTestProvider schedulerProvider) {
        super(view);
        this.schedulerProvider = schedulerProvider;
    }


    @Override
    public void loadVoteDetailData(int beginSequence) {
        getView().showNodeInfo(mNodeName,mNodeId);
        List<String> walletAddressList = WalletManager.getInstance().getAddressList();
        getVoteDetailListData(beginSequence, Constants.VoteConstants.LIST_SIZE, mNodeId, Constants.VoteConstants.REQUEST_DIRECTION, walletAddressList.toArray(new String[walletAddressList.size()]));
    }

    /**
     * @param beginSequence //起始序号 (必填) 客户端首次进入页面时传-1，-1：代表最新记录
     * @param listSize      //列表大小 (必填)
     * @param nodeId        //节点ID
     * @param direction     //方向 (必填) old：朝最旧记录方向 ,客户端写死
     * @param addressList   //地址列表
     */
    public void getVoteDetailListData(int beginSequence, int listSize, String nodeId, String direction, String[] addressList) {

        ServerUtils.getCommonApi().getVotedCandidateDetailList(NodeManager.getInstance().getChainId(), ApiRequestBody.newBuilder()
                .put("beginSequence", beginSequence)
                .put("listSize", listSize)
                .put("nodeId", nodeId)
                .put("direction", direction)
                .put("walletAddrs", addressList)
                .build())
                .compose(bindToLifecycle())
                .compose(RxUtils.getSingleSchedulerTransformer())
                .subscribe(new ApiSingleObserver<List<VotedCandidate>>() {
                    @Override
                    public void onApiSuccess(List<VotedCandidate> entityList) {
                        if (entityList != null && entityList.size() > 0) {
                            getView().getVoteDetailListDataSuccess(buildSortVoteDetailList(entityList));
                        }else {
                             getView().getVoteDetailListDataSuccess(entityList);
                        }
                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {
                        if(isViewAttached()){
                            getView().getVoteDetailListDataFailed();
                        }
                    }
                });


    }

    /**
     * 排序后返回的集合
     *
     * @param entityList
     * @return
     */
    private List<VotedCandidate> buildSortVoteDetailList(List<VotedCandidate> entityList) {
        Collections.sort(entityList, new Comparator<VotedCandidate>() {
            @Override
            public int compare(VotedCandidate o1, VotedCandidate o2) {
                return o2.getSequence() - o1.getSequence();
            }
        });
        return entityList;
    }

}
