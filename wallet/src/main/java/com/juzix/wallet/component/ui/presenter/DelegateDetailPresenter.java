package com.juzix.wallet.component.ui.presenter;

import com.juzhen.framework.network.ApiErrorCode;
import com.juzhen.framework.network.ApiRequestBody;
import com.juzhen.framework.network.ApiResponse;
import com.juzhen.framework.network.ApiSingleObserver;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.DelegateDetailContract;
import com.juzix.wallet.db.entity.DelegateAddressEntity;
import com.juzix.wallet.db.sqlite.DelegateDao;
import com.juzix.wallet.engine.NodeManager;
import com.juzix.wallet.engine.ServerUtils;
import com.juzix.wallet.entity.DelegateDetail;
import com.juzix.wallet.entity.DelegateInfo;
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
    public void loadDelegateDetailData(int beginSequence, String direction) {
        getDelegateDetailData(beginSequence, walletAddress, Constants.VoteConstants.LIST_SIZE, direction);
    }

    @Override
    public void MoveOut(DelegateDetail detail) {
        //移除操作，需要保存到数据库
        Single.fromCallable(new Callable<DelegateAddressEntity>() {
            @Override
            public DelegateAddressEntity call() throws Exception {
                DelegateAddressEntity entity = new DelegateAddressEntity();
                entity.setAddress(detail.getNoadeId());
                return entity;
            }
        }).map(new Function<DelegateAddressEntity, Boolean>() {
            @Override
            public Boolean apply(DelegateAddressEntity entity) throws Exception {
                return DelegateDao.insertDelegateNodeAddressInfo(entity);
            }
        }).subscribeOn(Schedulers.io());

    }

    private void getDelegateDetailData(int beginSequence, String walletAddress, int listSize, String direction) {
        getView().showWalletInfo(walletAddress, walletName, walletIcon);

        ServerUtils.getCommonApi().getDelegateDetailList(NodeManager.getInstance().getChainId(), ApiRequestBody.newBuilder()
                .put("walletAddrs", walletAddress)
                .put("beginSequence", beginSequence)
                .put("listSize", listSize)
                .put("direction", direction).build())
                .zipWith(Single.fromCallable(new Callable<List<DelegateAddressEntity>>() {
                    @Override
                    public List<DelegateAddressEntity> call() throws Exception {
                        return DelegateDao.getDelegateAddressInfoList();
                    }
                }), new BiFunction<Response<ApiResponse<List<DelegateDetail>>>, List<DelegateAddressEntity>, Response<ApiResponse<List<DelegateDetail>>>>() {
                    @Override
                    public Response<ApiResponse<List<DelegateDetail>>> apply(Response<ApiResponse<List<DelegateDetail>>> apiResponseResponse, List<DelegateAddressEntity> delegateAddressEntities) throws Exception {
                        DelegateDetail detail = new DelegateDetail();
                        List<DelegateDetail> delegateDetails = apiResponseResponse.body().getData();
                        if (apiResponseResponse == null || !apiResponseResponse.isSuccessful()) {
                            return Response.success(new ApiResponse(ApiErrorCode.NETWORK_ERROR));
                        } else {
                            delegateDetails.removeAll(getDelegateList(delegateAddressEntities));
//                        Flowable.fromIterable(delegateAddressEntities).map(new Function<DelegateAddressEntity, DelegateDetail>() {
//
//                            @Override
//                            public DelegateDetail apply(DelegateAddressEntity entity) throws Exception {
//                                detail.setNoadeId(entity.getAddress());
//                                return detail;
//                            }
//                        }).toList()
//                                .blockingGet()

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

    public List<DelegateDetail> getDelegateList(List<DelegateAddressEntity> entityList) {
        DelegateDetail detail = new DelegateDetail();
        return Flowable.fromIterable(entityList).map(new Function<DelegateAddressEntity, DelegateDetail>() {

            @Override
            public DelegateDetail apply(DelegateAddressEntity entity) throws Exception {

                detail.setNoadeId(entity.getAddress());
                return detail;
            }
        }).toList().blockingGet();

    }
}
