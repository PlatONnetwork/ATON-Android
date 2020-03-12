package com.platon.aton.component.ui.presenter;


import android.text.TextUtils;

import com.platon.framework.network.ApiErrorCode;
import com.platon.framework.network.ApiRequestBody;
import com.platon.framework.network.ApiResponse;
import com.platon.framework.network.ApiSingleObserver;
import com.platon.aton.app.LoadingTransformer;
import com.platon.aton.component.ui.base.BasePresenter;
import com.platon.aton.component.ui.contract.DelegateDetailContract;
import com.platon.aton.engine.ServerUtils;
import com.platon.aton.entity.DelegateItemInfo;
import com.platon.aton.entity.DelegateNodeDetail;
import com.platon.aton.entity.DelegateInfo;
import com.platon.aton.utils.RxUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import retrofit2.Response;

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
                .map(new Function<Response<ApiResponse<DelegateNodeDetail>>, DelegateNodeDetail>() {
                    @Override
                    public DelegateNodeDetail apply(Response<ApiResponse<DelegateNodeDetail>> apiResponseResponse) throws Exception {
                        return apiResponseResponse != null && apiResponseResponse.isSuccessful() && apiResponseResponse.body() != null ? apiResponseResponse.body().getData() : null;
                    }
                })
                .flatMap(new Function<DelegateNodeDetail, Single<Response<ApiResponse<DelegateNodeDetail>>>>() {

                    @Override
                    public Single<Response<ApiResponse<DelegateNodeDetail>>> apply(DelegateNodeDetail delegateNodeDetail) throws Exception {
                        delegateNodeDetail.setDelegateItemInfoList(buildDelegateItemInfoList(delegateNodeDetail.getDelegateItemInfoList(), delegateInfo.getWalletAddress()));
                        return Single.just(Response.success(new ApiResponse<>(ApiErrorCode.SUCCESS, delegateNodeDetail)));
                    }
                })
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

    private List<DelegateItemInfo> buildDelegateItemInfoList(List<DelegateItemInfo> delegateInfoList, String walletAddress) {
        if (delegateInfoList == null || delegateInfoList.isEmpty() || TextUtils.isEmpty(walletAddress)) {
            return delegateInfoList;
        }

        return Flowable
                .fromIterable(delegateInfoList)
                .map(new Function<DelegateItemInfo, DelegateItemInfo>() {
                    @Override
                    public DelegateItemInfo apply(DelegateItemInfo delegateItemInfo) throws Exception {
                        delegateItemInfo.setWalletAddress(walletAddress);
                        return delegateItemInfo;
                    }
                })
                .toList()
                .blockingGet();
    }

    private List<DelegateItemInfo> getDelegateItemInfoList() {
        if (mDelegateNodeDetail == null) {
            return new ArrayList<>();
        }

        return mDelegateNodeDetail.getDelegateItemInfoList();
    }

}
