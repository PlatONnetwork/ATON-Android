package com.platon.wallet.component.ui.presenter;

import com.platon.framework.network.ApiRequestBody;
import com.platon.framework.network.ApiResponse;
import com.platon.framework.network.ApiSingleObserver;
import com.platon.wallet.app.LoadingTransformer;
import com.platon.wallet.component.ui.base.BasePresenter;
import com.platon.wallet.component.ui.contract.ValidatorsDetailContract;
import com.platon.wallet.engine.ServerUtils;
import com.platon.wallet.entity.DelegateItemInfo;
import com.platon.wallet.entity.VerifyNodeDetail;
import com.platon.wallet.utils.RxUtils;

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
