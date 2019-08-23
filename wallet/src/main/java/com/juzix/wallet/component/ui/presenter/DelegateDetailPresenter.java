package com.juzix.wallet.component.ui.presenter;

import com.juzhen.framework.network.ApiErrorCode;
import com.juzhen.framework.network.ApiRequestBody;
import com.juzhen.framework.network.ApiResponse;
import com.juzhen.framework.network.ApiSingleObserver;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.DelegateDetailContract;
import com.juzix.wallet.db.entity.DelegateDetailEntity;
import com.juzix.wallet.db.sqlite.DelegateDetailDao;
import com.juzix.wallet.engine.NodeManager;
import com.juzix.wallet.engine.ServerUtils;
import com.juzix.wallet.entity.DelegateDetail;
import com.juzix.wallet.utils.RxUtils;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class DelegateDetailPresenter extends BasePresenter<DelegateDetailContract.View> implements DelegateDetailContract.Presenter {
    private String walletAddress;
    private String walletName;
    private String walletIcon;

    public DelegateDetailPresenter(DelegateDetailContract.View view) {
        super(view);
        walletAddress = getView().getWalletAddressFromIntent();
        walletName = getView().getWalletNameFromIntent();
        walletIcon = getView().getWalletIconFromIntent();
    }


    @Override
    public void loadDelegateDetailData() {
        getDelegateDetailData(walletAddress);
    }

    @Override
    public void MoveOut(DelegateDetail detail) {
        //移除操作，需要保存到数据库
        Single.fromCallable(new Callable<DelegateDetailEntity>() {
            @Override
            public DelegateDetailEntity call() throws Exception {
                DelegateDetailEntity entity = new DelegateDetailEntity();
                entity.setAddress(walletAddress);
                entity.setNodeId(detail.getNodeId());
                entity.setStakingBlockNum(detail.getStakingBlockNum());
                return entity;
            }
        }).map(new Function<DelegateDetailEntity, Boolean>() {
            @Override
            public Boolean apply(DelegateDetailEntity entity) throws Exception {

                return DelegateDetailDao.insertDelegateNodeAddressInfo(entity);
            }
        }).subscribeOn(Schedulers.io());

    }

    private void getDelegateDetailData(String walletAddress) {
        getView().showWalletInfo(walletAddress, walletName, walletIcon);

        ServerUtils.getCommonApi().getDelegateDetailList(NodeManager.getInstance().getChainId(), ApiRequestBody.newBuilder()
                .put("addr", walletAddress)
                .build())
                .zipWith(Single.fromCallable(new Callable<List<DelegateDetailEntity>>() {
                    @Override
                    public List<DelegateDetailEntity> call() throws Exception {
                        return DelegateDetailDao.getDelegateAddressInfoList();
                    }
                }), new BiFunction<Response<ApiResponse<List<DelegateDetail>>>, List<DelegateDetailEntity>, Response<ApiResponse<List<DelegateDetail>>>>() {
                    @Override
                    public Response<ApiResponse<List<DelegateDetail>>> apply(Response<ApiResponse<List<DelegateDetail>>> apiResponseResponse, List<DelegateDetailEntity> EntityList) throws Exception {
                        DelegateDetail detail = new DelegateDetail();
                        List<DelegateDetail> delegateDetails = apiResponseResponse.body().getData();
                        if (apiResponseResponse == null || !apiResponseResponse.isSuccessful()) {
                            return Response.success(new ApiResponse(ApiErrorCode.NETWORK_ERROR));
                        } else {

                            delegateDetails.removeAll(getDelegateList(EntityList));
                            return Response.success(new ApiResponse(ApiErrorCode.SUCCESS, delegateDetails));
                        }

                    }
                })
                .compose(bindToLifecycle())
                .compose(RxUtils.getSingleSchedulerTransformer())
                .subscribe(new ApiSingleObserver<List<DelegateDetail>>() {
                    @Override
                    public void onApiSuccess(List<DelegateDetail> detailList) {
                        if (isViewAttached()) {
                            getView().showDelegateDetailData(detailList);
                        }
                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {
                        getView().showDelegateDetailFailed();
                    }
                });


    }

    public List<DelegateDetail> getDelegateList(List<DelegateDetailEntity> entityList) {
        DelegateDetail detail = new DelegateDetail();
        return Flowable.fromIterable(entityList)
                .map(new Function<DelegateDetailEntity, DelegateDetail>() {
                    @Override
                    public DelegateDetail apply(DelegateDetailEntity entity) throws Exception {
                        detail.setNodeId(entity.getAddress());
                        return detail;
                    }
                }).toList().blockingGet();

    }
}
