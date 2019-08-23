package com.juzix.wallet.component.ui.presenter;

import com.juzhen.framework.network.ApiRequestBody;
import com.juzhen.framework.network.ApiResponse;
import com.juzhen.framework.network.ApiSingleObserver;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.ValidatorsDetailContract;
import com.juzix.wallet.engine.NodeManager;
import com.juzix.wallet.engine.ServerUtils;
import com.juzix.wallet.entity.VerifyNodeDetail;
import com.juzix.wallet.utils.RxUtils;

public class ValidatorsDetailPresenter extends BasePresenter<ValidatorsDetailContract.View> implements ValidatorsDetailContract.Presenter {
    private String mNodeId;

    public ValidatorsDetailPresenter(ValidatorsDetailContract.View view) {
        super(view);
        mNodeId = getView().getNodeIdFromIntent();
    }


    @Override
    public void loadValidatorsDetailData() {
       getValidatorsDetailData(mNodeId);

    }

    private void getValidatorsDetailData(String nodeId) {
        ServerUtils.getCommonApi().getNodeCandidateDetail(NodeManager.getInstance().getChainId(), ApiRequestBody.newBuilder()
                .put("nodeId", nodeId)
                .build())
                .compose(bindToLifecycle())
                .compose(RxUtils.getSingleSchedulerTransformer())
                .subscribe(new ApiSingleObserver<VerifyNodeDetail>() {
                    @Override
                    public void onApiSuccess(VerifyNodeDetail verifyNodeDetail) {
                        if (isViewAttached()) {
                            if (verifyNodeDetail != null) {
                                getView().showValidatorsDetailData(verifyNodeDetail);
                            }
                        }
                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {
                        getView().showValidatorsDetailFailed();
                    }
                });

    }


}
