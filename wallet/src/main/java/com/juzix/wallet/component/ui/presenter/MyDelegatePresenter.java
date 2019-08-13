package com.juzix.wallet.component.ui.presenter;

import com.juzhen.framework.network.ApiErrorCode;
import com.juzhen.framework.network.ApiRequestBody;
import com.juzhen.framework.network.ApiResponse;
import com.juzhen.framework.network.ApiSingleObserver;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.MyDelegateContract;
import com.juzix.wallet.engine.BaseApi;
import com.juzix.wallet.engine.NodeManager;
import com.juzix.wallet.engine.ServerUtils;
import com.juzix.wallet.engine.WalletManager;
import com.juzix.wallet.engine.Web3jManager;
import com.juzix.wallet.entity.AccountBalance;
import com.juzix.wallet.entity.DelegateInfo;
import com.juzix.wallet.entity.MyDelegate;
import com.juzix.wallet.entity.VotedCandidate;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.RxUtils;
import com.juzix.wallet.utils.StringUtil;
import com.trello.rxlifecycle2.android.FragmentEvent;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class MyDelegatePresenter extends BasePresenter<MyDelegateContract.View> implements MyDelegateContract.Presenter {
    public MyDelegatePresenter(MyDelegateContract.View view) {
        super(view);
    }


    @Override
    public void loadMyDelegateData() {
        List<String> walletAddressList = WalletManager.getInstance().getAddressList();
        getMyDelegateData(walletAddressList.toArray(new String[walletAddressList.size()]));
    }

    private void getMyDelegateData(String[] addressList) {
        ServerUtils.getCommonApi().getMyDelegateList(NodeManager.getInstance().getChainId(), ApiRequestBody.newBuilder().
                put("walletAddrs", addressList).build())
                .compose(bindUntilEvent(FragmentEvent.STOP))
                .compose(RxUtils.getSingleSchedulerTransformer())
//                .doOnSubscribe(new Consumer<Disposable>() {
//                    @Override
//                    public void accept(Disposable disposable) throws Exception {
//                        if (isViewAttached()) {
//                            showLoadingDialog();
//                        }
//                    }
//                })
//                .doFinally(new Action() {
//                    @Override
//                    public void run() throws Exception {
//                        if (isViewAttached()) {
//                            dismissLoadingDialogImmediately();
//                        }
//                    }
//                })
                .subscribe(new ApiSingleObserver<List<DelegateInfo>>() {
                    @Override
                    public void onApiSuccess(List<DelegateInfo> infoList) {
                        if (isViewAttached()) {
                            if (infoList != null) {
                                getView().showMyDelegateData(getWalletIconByAddress(infoList));
                                //获取总计委托金额
                                getTotalDelegateAmount(infoList);
                                //获取钱包余额
//                                getWalletBalance(infoList);
                                //获取钱包地址的数组
                                getwalletAddressGroup(infoList);
//                                getShowAccountAmount(getAccountBalance(getwalletAddressGroup(infoList).toArray(new String[getwalletAddressGroup(infoList).size()])), infoList);
                            }
                        }
                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {

                        getView().showMyDelegateDataFailed();
                    }
                });


    }

    /**
     * @param accountBalance
     * @param infoList
     */
    private void getShowAccountAmount(List<AccountBalance> accountBalance, List<DelegateInfo> infoList) {
        for (int i = 0; i < accountBalance.size(); i++) {
            for (int j = 0; j < infoList.size(); j++) {
                if (infoList.get(j).getWalletAddress() == accountBalance.get(i).getAddr()) {
                    String free = NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(accountBalance.get(j).getFree(), "1E18"));
                    String lock = NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(accountBalance.get(j).getLock(), "1E18"));
                    double totalAccount = NumberParserUtils.parseDouble(free) + NumberParserUtils.parseDouble(lock);
                    infoList.get(j).setBalance(totalAccount);
                    getView().showMyDelegateDataByPosition(j, infoList.get(j));//刷新某个position的数据
                }
            }
        }


    }

    /**
     * 获取钱包地址数组
     *
     * @param infoList
     * @return
     */
    private List<String> getwalletAddressGroup(List<DelegateInfo> infoList) {

        return Flowable.fromIterable(infoList)
                .map(new Function<DelegateInfo, String>() {
                    @Override
                    public String apply(DelegateInfo delegateInfo) throws Exception {
                        return delegateInfo.getWalletAddress();
                    }
                })
                .compose(bindUntilEvent(FragmentEvent.STOP))
                .toList()
                .blockingGet();
    }

    /**
     * 批量获取余额列表
     *
     * @param address
     * @return
     */
    public List<AccountBalance> getAccountBalance(String[] address) {

        return ServerUtils.getCommonApi().getAccountBalance(NodeManager.getInstance().getChainId(), ApiRequestBody.newBuilder()
                .put("walletAddrs", address)
                .build()).
                flatMap(new Function<Response<ApiResponse<List<AccountBalance>>>, SingleSource<Response<ApiResponse<List<AccountBalance>>>>>() {
                    @Override
                    public SingleSource<Response<ApiResponse<List<AccountBalance>>>> apply(Response<ApiResponse<List<AccountBalance>>> apiResponseResponse) throws Exception {
                        if (apiResponseResponse == null || !apiResponseResponse.isSuccessful()) {
                            return Single.just(Response.success(new ApiResponse(ApiErrorCode.NETWORK_ERROR)));
                        } else {
                            List<AccountBalance> list = apiResponseResponse.body().getData();
                            return Flowable.fromIterable(list)
                                    .map(new Function<AccountBalance, AccountBalance>() {
                                        @Override
                                        public AccountBalance apply(AccountBalance accountBalance) throws Exception {
                                            return accountBalance;
                                        }
                                    }).toList()
                                    .map(new Function<List<AccountBalance>, Response<ApiResponse<List<AccountBalance>>>>() {
                                        @Override
                                        public Response<ApiResponse<List<AccountBalance>>> apply(List<AccountBalance> entityList) throws Exception {
                                            return Response.success(new ApiResponse<>(ApiErrorCode.SUCCESS, entityList));
                                        }
                                    });
                        }
                    }
                })
                .compose(RxUtils.getSingleSchedulerTransformer())
                .map(new Function<Response<ApiResponse<List<AccountBalance>>>, List<AccountBalance>>() {
                    @Override
                    public List<AccountBalance> apply(Response<ApiResponse<List<AccountBalance>>> apiResponseResponse) throws Exception {
                        return apiResponseResponse.body().getData();
                    }
                })
                .blockingGet();

    }


    private void getTotalDelegateAmount(List<DelegateInfo> infoList) {
        Flowable.fromIterable(infoList)
                .map(new Function<DelegateInfo, Double>() {
                    @Override
                    public Double apply(DelegateInfo delegateInfo) throws Exception {
                        return NumberParserUtils.parseDouble(NumberParserUtils.parseDouble(NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(delegateInfo.getDelegate(), "1E18"))));
                    }
                })
                .reduce(new BiFunction<Double, Double, Double>() {
                    @Override
                    public Double apply(Double aDouble, Double aDouble2) throws Exception {
                        return aDouble + aDouble2;
                    }
                })
                .toObservable()
                .compose(bindUntilEvent(FragmentEvent.STOP))
                .subscribe(new Consumer<Double>() {
                    @Override
                    public void accept(Double aDouble) throws Exception {
                        if (isViewAttached()) {
                            getView().showTotalDelegate(aDouble);
                        }
                    }
                });
    }


    //根据钱包地址获取钱包的头像和名称并赋值
    public List<DelegateInfo> getWalletIconByAddress(List<DelegateInfo> infoList) {

        if (infoList.size() > 0 || infoList != null) {
            return Flowable.fromIterable(infoList)
                    .map(new Function<DelegateInfo, DelegateInfo>() {
                        @Override
                        public DelegateInfo apply(DelegateInfo delegateInfo) throws Exception {
                            delegateInfo.setWalletIcon(WalletManager.getInstance().getWalletIconByWalletAddress(delegateInfo.getWalletAddress()));
                            delegateInfo.setWalletName(WalletManager.getInstance().getWalletNameByWalletAddress(delegateInfo.getWalletAddress()));
                            return delegateInfo;
                        }
                    }).toList().blockingGet();
        }


        return null;

    }


    private void getWalletBalance(List<DelegateInfo> delegateInfoList) {
        Flowable.range(0, delegateInfoList.size())
                .map(new Function<Integer, DelegateInfo>() {
                    @Override
                    public DelegateInfo apply(Integer integer) throws Exception {
                        return delegateInfoList.get(integer);
                    }
                })
                .map(new Function<DelegateInfo, DelegateInfo>() {
                    @Override
                    public DelegateInfo apply(DelegateInfo delegateInfo) throws Exception {

                        double balance = Web3jManager.getInstance().getBalance(delegateInfo.getWalletAddress());

                        delegateInfo.setBalance(balance);

                        return delegateInfo;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<DelegateInfo>() {
                    @Override
                    public void accept(DelegateInfo delegateInfo) throws Exception {
                        if (isViewAttached()) {
                            getView().showMyDelegateDataByPosition(delegateInfoList.indexOf(delegateInfo), delegateInfo);
                        }
                    }
                });

    }


}
