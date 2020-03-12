package com.platon.aton.component.ui.presenter;

import com.platon.framework.network.ApiRequestBody;
import com.platon.framework.network.ApiResponse;
import com.platon.framework.network.ApiSingleObserver;
import com.platon.aton.app.LoadingTransformer;
import com.platon.aton.component.ui.base.BasePresenter;
import com.platon.aton.component.ui.contract.ValidatorsDetailContract;
import com.platon.aton.engine.ServerUtils;
import com.platon.aton.entity.DelegateItemInfo;
import com.platon.aton.entity.VerifyNodeDetail;
import com.platon.aton.utils.RxUtils;

public class ValidatorsDetailPresenter extends BasePresenter<ValidatorsDetailContract.View> implements ValidatorsDetailContract.Presenter {

    private VerifyNodeDetail mVerifyNodeDetail;

    public ValidatorsDetailPresenter(ValidatorsDetailContract.View view) {
        super(view);
    }


    @Override
    public void loadValidatorsDetailData() {
        if (isViewAttached()) {

            String nodeId = getView().getNodeIdFromIntent();

            ServerUtils.getCommonApi()
                    .getNodeCandidateDetail(ApiRequestBody.newBuilder()
                            .put("nodeId", nodeId)
                            .build())
                    .compose(bindToLifecycle())
                    .compose(RxUtils.getSingleSchedulerTransformer())
                    .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                    .subscribe(new ApiSingleObserver<VerifyNodeDetail>() {
                        @Override
                        public void onApiSuccess(VerifyNodeDetail verifyNodeDetail) {
                            if (isViewAttached()) {
                                mVerifyNodeDetail = verifyNodeDetail;
                                getView().showValidatorsDetailData(verifyNodeDetail);
                            }
                        }

                        @Override
                        public void onApiFailure(ApiResponse response) {
                            if (isViewAttached()) {
                                getView().showValidatorsDetailData(null);
                            }
                        }
                    });
        }
    }


    @Override
    public DelegateItemInfo getDelegateDetail() {
        DelegateItemInfo delegateDetail = new DelegateItemInfo();
        if (mVerifyNodeDetail != null) {
            delegateDetail.setNodeName(mVerifyNodeDetail.getName());
            delegateDetail.setNodeId(mVerifyNodeDetail.getNodeId());
            delegateDetail.setUrl(mVerifyNodeDetail.getUrl());
        }
        return delegateDetail;
    }


}
