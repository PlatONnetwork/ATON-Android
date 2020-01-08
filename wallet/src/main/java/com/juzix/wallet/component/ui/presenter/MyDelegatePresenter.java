package com.juzix.wallet.component.ui.presenter;

import com.juzhen.framework.network.ApiRequestBody;
import com.juzhen.framework.network.ApiResponse;
import com.juzhen.framework.network.ApiSingleObserver;
import com.juzix.wallet.app.CustomObserver;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.MyDelegateContract;
import com.juzix.wallet.engine.Optional;
import com.juzix.wallet.engine.ServerUtils;
import com.juzix.wallet.engine.WalletManager;
import com.juzix.wallet.entity.DelegateInfo;
import com.juzix.wallet.utils.RxUtils;
import com.trello.rxlifecycle2.android.FragmentEvent;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.SingleSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import retrofit2.Response;

public class MyDelegatePresenter extends BasePresenter<MyDelegateContract.View> implements MyDelegateContract.Presenter {

    private Disposable mDisposable;


    public MyDelegatePresenter(MyDelegateContract.View view) {
        super(view);
    }


    @Override
    public void loadMyDelegateData() {

        mDisposable = ServerUtils.getCommonApi().getMyDelegateList(ApiRequestBody.newBuilder().
                put("walletAddrs", WalletManager.getInstance().getAddressList())
                .build())
                .map(new Function<Response<ApiResponse<List<DelegateInfo>>>, Optional<List<DelegateInfo>>>() {
                    @Override
                    public Optional<List<DelegateInfo>> apply(Response<ApiResponse<List<DelegateInfo>>> apiResponseResponse) throws Exception {
                        return apiResponseResponse != null && apiResponseResponse.isSuccessful() && apiResponseResponse.body() != null ? new Optional<List<DelegateInfo>>(apiResponseResponse.body().getData()) : new Optional<List<DelegateInfo>>(null);
                    }
                })
                .toFlowable()
                .filter(new Predicate<Optional<List<DelegateInfo>>>() {
                    @Override
                    public boolean test(Optional<List<DelegateInfo>> listOptional) throws Exception {
                        return !listOptional.isEmpty();
                    }
                })
                .switchIfEmpty(new Publisher<Optional<List<DelegateInfo>>>() {
                    @Override
                    public void subscribe(Subscriber<? super Optional<List<DelegateInfo>>> s) {
                        s.onError(new Throwable());
                    }
                })
                .flatMap(new Function<Optional<List<DelegateInfo>>, Publisher<DelegateInfo>>() {
                    @Override
                    public Publisher<DelegateInfo> apply(Optional<List<DelegateInfo>> listOptional) throws Exception {
                        return Flowable.fromIterable(listOptional.get());
                    }
                })
                .map(new Function<DelegateInfo, DelegateInfo>() {
                    @Override
                    public DelegateInfo apply(DelegateInfo delegateInfo) throws Exception {
                        delegateInfo.setWalletIcon(WalletManager.getInstance().getWalletIconByWalletAddress(delegateInfo.getWalletAddress()));
                        delegateInfo.setWalletName(WalletManager.getInstance().getWalletNameByWalletAddress(delegateInfo.getWalletAddress()));
                        return delegateInfo;
                    }
                })
                .toList()
                .compose(RxUtils.bindToParentLifecycleUtilEvent(getView(), FragmentEvent.STOP))
                .compose(RxUtils.getSingleSchedulerTransformer())
                .subscribe(new Consumer<List<DelegateInfo>>() {
                    @Override
                    public void accept(List<DelegateInfo> delegateInfos) throws Exception {
                        if (isViewAttached()) {
                            getView().showMyDelegateData(delegateInfos);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (isViewAttached()) {
                            getView().showMyDelegateData(null);
                        }
                    }
                });

    }

    @Override
    public void detachView() {
        super.detachView();
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }
}
