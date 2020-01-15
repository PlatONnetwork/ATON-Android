package com.juzix.wallet.component.ui.presenter;


import com.juzhen.framework.network.ApiRequestBody;
import com.juzhen.framework.network.ApiResponse;
import com.juzhen.framework.network.ApiSingleObserver;
import com.juzix.wallet.app.LoadingTransformer;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.DelegateDetailContract;
import com.juzix.wallet.engine.ServerUtils;
import com.juzix.wallet.entity.ClaimRewardRecord;
import com.juzix.wallet.entity.DelegateItemInfo;
import com.juzix.wallet.entity.DelegateNodeDetail;
import com.juzix.wallet.entity.DelegateInfo;
import com.juzix.wallet.utils.RxUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;

public class DelegateDetailPresenter extends BasePresenter<DelegateDetailContract.View> implements DelegateDetailContract.Presenter {

    private DelegateNodeDetail mDelegateNodeDetail;

    public DelegateDetailPresenter(DelegateDetailContract.View view) {
        super(view);

    }

    @Override
    public void loadDelegateDetailData() {
        if (isViewAttached()) {
            DelegateInfo delegateInfo = getView().getDelegateInfoFromIntent();
            if (delegateInfo != null) {
                getView().showWalletInfo(delegateInfo);
                getDelegateDetailData(delegateInfo);
            }
        }
    }

    private void getDelegateDetailData(DelegateInfo delegateInfo) {
        ServerUtils.getCommonApi().getDelegateDetailList(ApiRequestBody.newBuilder()
                .put("addr", delegateInfo.getWalletAddress())
                .build())
                .compose(RxUtils.bindToLifecycle(getView()))
                .compose(RxUtils.getSingleSchedulerTransformer())
                .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                .subscribe(new ApiSingleObserver<DelegateNodeDetail>() {
                    @Override
                    public void onApiSuccess(DelegateNodeDetail delegateNodeDetail) {
                        if (isViewAttached()) {
                            getView().showDelegateDetailData(getDelegateItemInfoList(), delegateNodeDetail.getDelegateItemInfoList());
                            getView().showWalletDelegatedInfo(delegateNodeDetail.getAvailableDelegationBalance(), delegateNodeDetail.getDelegated());
                            mDelegateNodeDetail = delegateNodeDetail;
                        }
                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {
                        if (isViewAttached()) {
                            getView().showDelegateDetailData(getDelegateItemInfoList(), null);
                        }
                    }
                });
    }

    private List<DelegateItemInfo> getDelegateItemInfoList() {
        if (mDelegateNodeDetail == null) {
            return new ArrayList<>();
        }

        return mDelegateNodeDetail.getDelegateItemInfoList();
    }

}
