package com.platon.aton.component.ui.presenter;

import android.text.TextUtils;

import com.platon.framework.network.ApiErrorCode;
import com.platon.framework.network.ApiResponse;
import com.platon.framework.network.ApiSingleObserver;
import com.platon.aton.component.ui.SortType;
import com.platon.aton.component.ui.base.BasePresenter;
import com.platon.aton.component.ui.contract.ValidatorsContract;
import com.platon.aton.engine.ServerUtils;
import com.platon.aton.entity.NodeStatus;
import com.platon.aton.entity.VerifyNode;
import com.platon.aton.utils.RxUtils;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.functions.Predicate;
import retrofit2.Response;


public class ValidatorsPresenter extends BasePresenter<ValidatorsContract.View> implements ValidatorsContract.Presenter {

    private List<VerifyNode> mVerifyNodeList = new ArrayList<>();
    private List<VerifyNode> mOldVerifyNodeList = new ArrayList<>();

    public ValidatorsPresenter(ValidatorsContract.View view) {
        super(view);
    }

    @Override
    public void loadValidatorsData(@NodeStatus String nodeStatus, SortType sortType, String keywords, boolean isRefresh, boolean isRefreshAll) {

        getVerifyNodeList(mVerifyNodeList, isRefresh)
                .compose(bindUntilEvent(FragmentEvent.STOP))
                .compose(RxUtils.getSingleSchedulerTransformer())
                .subscribe(new ApiSingleObserver<List<VerifyNode>>() {
                    @Override
                    public void onApiSuccess(List<VerifyNode> nodeList) {
                        if (isViewAttached()) {
                            List<VerifyNode> newVerifyNodeList = getVerifyNodeList(nodeList, nodeStatus, sortType, keywords);
                            getView().loadValidatorsDataResult(mOldVerifyNodeList, newVerifyNodeList, isRefreshAll);
                            mOldVerifyNodeList = newVerifyNodeList;
                            mVerifyNodeList = nodeList;
                        }
                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {
                        if (isViewAttached()) {
                            List<VerifyNode> oldVerifyNodeList = getVerifyNodeList(mVerifyNodeList, nodeStatus, sortType, keywords);
                            getView().loadValidatorsDataResult(oldVerifyNodeList, null, isRefreshAll);
                        }
                    }
                });
    }

    private Single<Response<ApiResponse<List<VerifyNode>>>> getVerifyNodeList(List<VerifyNode> verifyNodeList, boolean isRefresh) {
        return isRefresh || verifyNodeList.isEmpty() ? ServerUtils.getCommonApi().getVerifyNodeList() : Single.just(Response.success(new ApiResponse<List<VerifyNode>>(ApiErrorCode.SUCCESS, verifyNodeList)));
    }

    /**
     * @param allVerifyNodeList 所有节点列表
     * @param nodeStatus        节点状态，活跃中的节点包含共识中的节点
     * @param sortType          排序类型
     * @param keywords          搜索关键字
     * @return
     */
    private List<VerifyNode> getVerifyNodeList(List<VerifyNode> allVerifyNodeList, @NodeStatus String nodeStatus, SortType sortType, String keywords) {

        List<VerifyNode> verifyNodeList = new ArrayList<>();

        if (allVerifyNodeList == null || allVerifyNodeList.isEmpty()) {
            return verifyNodeList;
        }

        return Flowable.fromIterable(allVerifyNodeList)
                .filter(new Predicate<VerifyNode>() {
                    @Override
                    public boolean test(VerifyNode verifyNode) throws Exception {
                        return (TextUtils.equals(nodeStatus, NodeStatus.ALL) || TextUtils.equals(nodeStatus, verifyNode.getNodeStatus())) && !TextUtils.isEmpty(verifyNode.getName()) && verifyNode.getName().contains(keywords);
                    }
                })
                .toSortedList(sortType.getComparator())
                .blockingGet();

    }

}



