package com.juzix.wallet.component.ui.presenter;

import com.juzhen.framework.network.ApiRequestBody;
import com.juzhen.framework.network.ApiResponse;
import com.juzhen.framework.network.ApiSingleObserver;
import com.juzix.wallet.R;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.MyDelegateContract;
import com.juzix.wallet.engine.BaseApi;
import com.juzix.wallet.engine.NodeManager;
import com.juzix.wallet.engine.ServerUtils;
import com.juzix.wallet.engine.WalletManager;
import com.juzix.wallet.engine.Web3jManager;
import com.juzix.wallet.entity.DelegateInfo;
import com.juzix.wallet.entity.MyDelegate;
import com.juzix.wallet.utils.RxUtils;
import com.trello.rxlifecycle2.android.FragmentEvent;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

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
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        if (isViewAttached()) {
                            showLoadingDialog();
                        }
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        if (isViewAttached()) {
                            dismissLoadingDialogImmediately();
                        }
                    }
                })
                .subscribe(new ApiSingleObserver<MyDelegate>() {
                    @Override
                    public void onApiSuccess(MyDelegate myDelegate) {
                        if (isViewAttached()) {
                            if (myDelegate != null) {
                                getView().showTotalDelegate(myDelegate);
                                getView().showMyDelegateData(getWalletIconByAddress(myDelegate.getDelegateInfoList()));
                                //获取钱包余额
                                getWalletBalance(myDelegate.getDelegateInfoList());
                            }
                        }
                    }


                    @Override
                    public void onApiFailure(ApiResponse response) {

                        getView().showMyDelegateDataFailed();
                    }
                });


    }


    //根据钱包地址获取钱包的头像并赋值
    public List<DelegateInfo> getWalletIconByAddress(List<DelegateInfo> infoList) {

        if (infoList.size() > 0 || infoList != null) {
            return Flowable.fromIterable(infoList)
                    .map(new Function<DelegateInfo, DelegateInfo>() {
                        @Override
                        public DelegateInfo apply(DelegateInfo delegateInfo) throws Exception {
                            delegateInfo.setWalletIcon(WalletManager.getInstance().getWalletIconByWalletAddress(delegateInfo.getWalletAddress()));
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
                })

        ;

    }


}
