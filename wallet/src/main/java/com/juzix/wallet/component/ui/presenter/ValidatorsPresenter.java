package com.juzix.wallet.component.ui.presenter;

import android.text.TextUtils;

import com.juzhen.framework.network.ApiErrorCode;
import com.juzhen.framework.network.ApiResponse;
import com.juzhen.framework.network.ApiSingleObserver;
import com.juzix.wallet.app.LoadingTransformer;
import com.juzix.wallet.component.ui.SortType;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.ValidatorsContract;
import com.juzix.wallet.engine.ServerUtils;
import com.juzix.wallet.entity.NodeStatus;
import com.juzix.wallet.entity.VerifyNode;
import com.juzix.wallet.utils.RxUtils;
import com.trello.rxlifecycle2.android.FragmentEvent;

import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import retrofit2.Response;


public class ValidatorsPresenter extends BasePresenter<ValidatorsContract.View> implements ValidatorsContract.Presenter {

    private List<VerifyNode> mVerifyNodeList = new ArrayList<>();
    private List<VerifyNode> mOldVerifyNodeList = new ArrayList<>();

    public ValidatorsPresenter(ValidatorsContract.View view) {
        super(view);
    }

    @Override
    public void loadValidatorsData(@NodeStatus String nodeStatus, SortType sortType, String keywords, boolean isRefresh) {

        getVerifyNodeList(mVerifyNodeList, isRefresh)
                .compose(bindUntilEvent(FragmentEvent.STOP))
                .compose(RxUtils.getSingleSchedulerTransformer())
                .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                .subscribe(new ApiSingleObserver<List<VerifyNode>>() {
                    @Override
                    public void onApiSuccess(List<VerifyNode> nodeList) {
                        if (isViewAttached()) {
                            List<VerifyNode> newVerifyNodeList = getVerifyNodeList(nodeList, nodeStatus, sortType, keywords);
                            getView().loadValidatorsDataResult(mOldVerifyNodeList, newVerifyNodeList);
                            mOldVerifyNodeList = newVerifyNodeList;
                            mVerifyNodeList = nodeList;
                        }
                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {
                        if (isViewAttached()) {
                            List<VerifyNode> oldVerifyNodeList = getVerifyNodeList(mVerifyNodeList, nodeStatus, sortType, keywords);
                            getView().loadValidatorsDataResult(oldVerifyNodeList, null);
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



