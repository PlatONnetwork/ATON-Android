package com.juzix.wallet.component.ui.presenter;

import android.text.TextUtils;

import com.juzhen.framework.network.ApiRequestBody;
import com.juzhen.framework.network.ApiResponse;
import com.juzhen.framework.network.ApiSingleObserver;
import com.juzix.wallet.app.LoadingTransformer;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.ValidatorsDetailContract;
import com.juzix.wallet.engine.ServerUtils;
import com.juzix.wallet.engine.WalletManager;
import com.juzix.wallet.entity.AccountBalance;
import com.juzix.wallet.entity.VerifyNodeDetail;
import com.juzix.wallet.utils.RxUtils;

import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

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
                            if (verifyNodeDetail != null) {
                                getView().showValidatorsDetailData(verifyNodeDetail);
                            }
                        }
                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {
                        if (isViewAttached()) {
                            getView().showValidatorsDetailFailed();
                        }
                    }
                });

    }


}
