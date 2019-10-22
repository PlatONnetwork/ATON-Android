package com.juzix.wallet.component.ui.presenter;

import android.text.TextUtils;

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
import com.juzix.wallet.engine.WalletManager;
import com.juzix.wallet.entity.AccountBalance;
import com.juzix.wallet.entity.DelegateDetail;
import com.juzix.wallet.entity.DelegateInfo;
import com.juzix.wallet.utils.RxUtils;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

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

    @Override
    public void moveOut(DelegateDetail detail) {
        Single
                .fromCallable(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return DelegateDetailDao.insertDelegateNodeAddressInfo(detail.toDelegateDetailEntity());
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    @Override
    public DelegateInfo getDelegateInfo() {
        return mDelegateInfo;
    }

    private void getDelegateDetailData() {
        if (mDelegateInfo == null) {
            return;
        }
        ServerUtils.getCommonApi().getDelegateDetailList(ApiRequestBody.newBuilder()
                .put("addr", mDelegateInfo.getWalletAddress())
                .build())
                .flatMap(new Function<Response<ApiResponse<List<DelegateDetail>>>, SingleSource<Response<ApiResponse<List<DelegateDetail>>>>>() {
                    @Override
                    public SingleSource<Response<ApiResponse<List<DelegateDetail>>>> apply(Response<ApiResponse<List<DelegateDetail>>> apiResponseResponse) throws Exception {
                        if (apiResponseResponse == null || !apiResponseResponse.isSuccessful()) {
                            return Single.just(Response.success(new ApiResponse(ApiErrorCode.NETWORK_ERROR)));
                        } else {
                            List<DelegateDetail> detailList = apiResponseResponse.body().getData();
                            return Flowable.fromIterable(detailList)
                                    .map(new Function<DelegateDetail, DelegateDetail>() {
                                        @Override
                                        public DelegateDetail apply(DelegateDetail delegateDetail) throws Exception {
                                            delegateDetail.setWalletAddress(mDelegateInfo.getWalletAddress()); //给每个对象赋值钱包地址
                                            return delegateDetail;
                                        }
                                    }).toList().map(new Function<List<DelegateDetail>, Response<ApiResponse<List<DelegateDetail>>>>() {
                                        @Override
                                        public Response<ApiResponse<List<DelegateDetail>>> apply(List<DelegateDetail> delegateDetails) throws Exception {
                                            return Response.success(new ApiResponse<>(ApiErrorCode.SUCCESS, delegateDetails));
                                        }
                                    });
                        }
                    }
                }).flatMap(new Function<Response<ApiResponse<List<DelegateDetail>>>, SingleSource<Response<ApiResponse<List<DelegateDetail>>>>>() {

            @Override
            public SingleSource<Response<ApiResponse<List<DelegateDetail>>>> apply(Response<ApiResponse<List<DelegateDetail>>> apiResponseResponse) throws Exception {
                List<DelegateDetail> list = apiResponseResponse.body().getData();
                return Flowable.fromIterable(list)
                        .filter(new Predicate<DelegateDetail>() {
                            @Override
                            public boolean test(DelegateDetail delegateDetail) throws Exception {
                                DelegateDetailEntity entity = DelegateDetailDao.getEntityWithAddressAndNodeId(delegateDetail.getWalletAddress(), delegateDetail.getNodeId());
                                if (entity != null) {
                                    if (TextUtils.equals(entity.getDelegationBlockNum(), delegateDetail.getDelegationBlockNum())) { //数据库中拿到的对象的块高和列表中的块高做比较
                                        return false;
                                    } else {
                                        DelegateDetailDao.deleteDelegateDetailEntityByAddressAndNodeId(delegateDetail.getNodeId(), delegateDetail.getWalletAddress());
                                        return true;
                                    }
                                } else {
                                    return true;
                                }
                            }
                        })
                        .toList()
                        .map(new Function<List<DelegateDetail>, Response<ApiResponse<List<DelegateDetail>>>>() {
                            @Override
                            public Response<ApiResponse<List<DelegateDetail>>> apply(List<DelegateDetail> delegateDetails) throws Exception {
                                return Response.success(new ApiResponse<>(ApiErrorCode.SUCCESS, delegateDetails));
                            }
                        });
            }
        })
                .compose(RxUtils.bindToLifecycle(getView()))
                .compose(RxUtils.getSingleSchedulerTransformer())
                .subscribe(new ApiSingleObserver<List<DelegateDetail>>() {
                    @Override
                    public void onApiSuccess(List<DelegateDetail> detailList) {
                        if (isViewAttached()) {

                            getView().showDelegateDetailData(detailList);

//                            //获取数据库的对象和当前列表的对象做比较
//                            if (getList().size() > 0) { //表示数据库已经存了移除的数据
//                                boolean isFind = false;
//                                DelegateDetail detail = null;
//
//
//                                DelegateDetailEntity entity = getList().get(0);  //其实数据库中只有一条数据
//                                for (DelegateDetail delegateDetail : detailList) {
//                                    if (TextUtils.equals(entity.getAddress(), delegateDetail.getWalletAddress())
//                                            && TextUtils.equals(entity.getNodeId(), delegateDetail.getNodeId())
//                                            && TextUtils.equals(entity.getStakingBlockNum(), delegateDetail.getStakingBlockNum())) {
//                                        isFind = true;
//                                        detail = delegateDetail;
//                                        break;
//                                    }
//                                }
//
//                                if (!isFind) {
//                                    getView().showDelegateDetailData(detailList);
//                                } else {
//                                    //移除集合的对象
//                                    deleteDelegateDetailEntity(); //从数据库中移除
//                                    detailList.remove(detail);
//                                    getView().showDelegateDetailData(detailList);
//                                }
//
//                            } else {
//                                getView().showDelegateDetailData(detailList);
//                            }

                        }

                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {
                        getView().showDelegateDetailFailed();
                    }
                });


//        ServerUtils.getCommonApi().getDelegateDetailList(NodeManager.getInstance().getChainId(), ApiRequestBody.newBuilder()
//                .put("addr", walletAddress)
//                .build())
//                .zipWith(Single.fromCallable(new Callable<List<DelegateDetailEntity>>() {
//                    @Override
//                    public List<DelegateDetailEntity> call() throws Exception {
//                        return DelegateDetailDao.getDelegateAddressInfoList();
//                    }
//                }), new BiFunction<Response<ApiResponse<List<DelegateDetail>>>, List<DelegateDetailEntity>, Response<ApiResponse<List<DelegateDetail>>>>() {
//                    @Override
//                    public Response<ApiResponse<List<DelegateDetail>>> apply(Response<ApiResponse<List<DelegateDetail>>> apiResponseResponse, List<DelegateDetailEntity> EntityList) throws Exception {
//                        DelegateDetail detail = new DelegateDetail();
//                        List<DelegateDetail> delegateDetails = apiResponseResponse.body().getData();
//                        if (apiResponseResponse == null || !apiResponseResponse.isSuccessful()) {
//                            return Response.success(new ApiResponse(ApiErrorCode.NETWORK_ERROR));
//                        } else {
//                            delegateDetails.removeAll(getDelegateList(EntityList));//把相同块高的移除，不显示到列表中
//                            return Response.success(new ApiResponse(ApiErrorCode.SUCCESS, delegateDetails));
//                        }
//
//                    }
//                })
//                .compose(bindToLifecycle())
//                .compose(RxUtils.getSingleSchedulerTransformer())
//                .subscribe(new ApiSingleObserver<List<DelegateDetail>>() {
//                    @Override
//                    public void onApiSuccess(List<DelegateDetail> detailList) {
//                        if (isViewAttached()) {
//                            getView().showDelegateDetailData(detailList);
//                        }
//                    }
//
//                    @Override
//                    public void onApiFailure(ApiResponse response) {
//                        getView().showDelegateDetailFailed();
//                    }
//                });


    }

    //从数据库读取数据
    public List<DelegateDetailEntity> getList() {
        return Single.fromCallable(new Callable<List<DelegateDetailEntity>>() {

            @Override
            public List<DelegateDetailEntity> call() throws Exception {
                return DelegateDetailDao.getDelegateAddressInfoList();
            }
        }).subscribeOn(Schedulers.io())
                .blockingGet();
    }

    //从数据库删除
    public void deleteDelegateDetailEntity() {
        Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return DelegateDetailDao.deleteDelegateDetailEntity();
            }
        }).subscribeOn(Schedulers.io()).blockingGet();
    }
}
