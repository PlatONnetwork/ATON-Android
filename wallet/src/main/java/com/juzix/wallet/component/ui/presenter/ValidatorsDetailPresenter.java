package com.juzix.wallet.component.ui.presenter;

import com.juzhen.framework.network.ApiRequestBody;
import com.juzhen.framework.network.ApiResponse;
import com.juzhen.framework.network.ApiSingleObserver;
import com.juzix.wallet.app.LoadingTransformer;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.ValidatorsDetailContract;
import com.juzix.wallet.engine.ServerUtils;
import com.juzix.wallet.entity.DelegateItemInfo;
import com.juzix.wallet.entity.DelegateNodeDetail;
import com.juzix.wallet.entity.VerifyNodeDetail;
import com.juzix.wallet.utils.RxUtils;

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
