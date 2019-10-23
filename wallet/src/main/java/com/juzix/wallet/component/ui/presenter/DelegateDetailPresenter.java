package com.juzix.wallet.component.ui.presenter;


import com.juzhen.framework.network.ApiRequestBody;
import com.juzhen.framework.network.ApiResponse;
import com.juzhen.framework.network.ApiSingleObserver;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.DelegateDetailContract;
import com.juzix.wallet.engine.ServerUtils;
import com.juzix.wallet.entity.DelegateDetail;
import com.juzix.wallet.entity.DelegateInfo;
import com.juzix.wallet.utils.RxUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;

public class DelegateDetailPresenter extends BasePresenter<DelegateDetailContract.View> implements DelegateDetailContract.Presenter {

    private DelegateInfo mDelegateInfo;

    public DelegateDetailPresenter(DelegateDetailContract.View view) {
        super(view);
        mDelegateInfo = view.getDelegateInfoFromIntent();
    }


    @Override
    public void loadDelegateDetailData() {
        if (isViewAttached()) {
            getView().showWalletInfo(mDelegateInfo);
        }
        getDelegateDetailData();
    }

    private void getDelegateDetailData() {
        if (mDelegateInfo == null) {
            return;
        }
        ServerUtils.getCommonApi().getDelegateDetailList(ApiRequestBody.newBuilder()
                .put("addr", mDelegateInfo.getWalletAddress())
                .build())
                .compose(RxUtils.bindToLifecycle(getView()))
                .compose(RxUtils.getSingleSchedulerTransformer())
                .subscribe(new ApiSingleObserver<List<DelegateDetail>>() {
                    @Override
                    public void onApiSuccess(List<DelegateDetail> detailList) {
                        if (isViewAttached()) {
                            getView().showDelegateDetailData(setWalletAddress(detailList));
                        }
                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {
                        getView().showDelegateDetailFailed();
                    }
                });
    }
    public  List<DelegateDetail> setWalletAddress(List<DelegateDetail> detailList){
        if (detailList != null && detailList.size() > 0) {
            return Flowable.fromIterable(detailList)
                    .map(new Function<DelegateDetail, DelegateDetail>() {
                        @Override
                        public DelegateDetail apply(DelegateDetail detail) throws Exception {
                           detail.setWalletAddress(mDelegateInfo.getWalletAddress());
                            return detail;
                        }
                    }).toList().blockingGet();
        }
        return new ArrayList<>();
    }

}
